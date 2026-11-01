# RFQ / リアルタイム価格配信 Kotlin移植・サービス分離計画

- **計画バージョン:** 1.1（学習資料を追加した合意済みベースライン）
- **基準確定日:** 2026-10-31
- **今回の改訂:** 『Kotlin サーバーサイドプログラミング実践開発』を学習計画に追加。技術選定・サービス境界・開発順序は維持。
- **対象:** 既存Python実装のRFQワークフローおよびリアルタイム価格配信基盤
- **基本方針:** 学習と移植を並行する。実装はユーザー主導。QuantLibによる本物のプライシングは最後。

> **この文書の使い方:** 今後のチャットでは、この文書を設計・実装計画の基準とする。変更が必要になったときは黙って前提を変えず、変更点・理由を明示し、バージョンを更新する。

## 1. 目的と成功条件

1. 既存Pythonシステムの振る舞いを、Kotlin/JVM上に段階的に移植する。
2. 『Kotlin in Action』と『Kotlin サーバーサイドプログラミング実践開発』を読みながら、言語仕様とサーバーサイド実装を実コードで試す。
3. RFQ WorkflowとPricingを**独立したサービス**に分ける。
4. 経済効果は両サービスで**CDM-inspiredの最小限の独自Kotlinモデル**を用いる。最初から公式CDM Javaモデルを全面採用しない。
5. **複数Pricing WorkerとRebalanceを初期段階から実装・テストする**。
6. QuantLibなしのFake Pricerで、RFQ操作から参考価格配信までの経路を先に完成させる。
7. 最後に実Pricerを接続し、既存Python実装との数値的な一致を検証する。

## 2. 合意済みの設計上の決定事項

### 2.1 サービス境界とフロントエンド

- **FrontendはRFQ Workflow ServiceのREST / WebSocketに接続する。Pricing Serviceへは直接接続しない。**
- 参考価格の取得はRFQの業務の一部。取得可能かどうかの業務ルール、認可、クライアント向けAPIはRFQ Serviceが管理する。
- Pricing ServiceはPricing Session、Market Data、依存解決、再評価、価格生成を担当する。
- Market Dataのティック更新による再評価はPricing Service内で完結し、RFQ UseCaseを毎回実行しない。
- 高頻度の参考価格はRFQ DBへ毎ティック永続化しない。業務上確定・保存すべき価格は別途扱う。
- RFQ Service内の価格配信アダプター（Price Gateway）がPricing Serviceからの価格更新を受信し、Frontend向けWebSocketへ中継する。必要なら将来このアダプターを別プロセス化できる。

概念的なデータ経路:

```text
React Frontend
  └── REST / WebSocket ── RFQ Workflow Service
                              ├── RFQ Domain / UseCases / PostgreSQL
                              ├── RFQ Domain Events → Kafka ─┐
                              └── Price Gateway ← Redis Pub/Sub ←┐
                                                           ┌─────┘
Pricing Service (複数Worker) ← Kafka RFQ Domain Events ────┘
  ├── Pricing Session / Ownership / Rebalance / Restore
  ├── ZeroMQ Market Data / Curve Store / Dependency Resolver
  ├── Fake Pricer → 最後にQuantLib実装へ
  └── Price Updates → Redis Pub/Sub
```

※図中の流れは概念的なもの。価格更新はPricing Service→Redis→RFQ Price Gateway→Frontend、制御系はRFQ→Kafka→Pricing。

### 2.2 Economic Model

- 最初は **CDM-inspiredな独自の最小モデル**をKotlinで実装する。これはCDMの構造を自分で理解するための意図的な選択。
- `economic-model` Gradleサブプロジェクトを用意し、RFQ ServiceとPricing Serviceで共用する（明示的なShared Kernel）。
- 対象商品はまず **JPY-TONA OIS（金利スワップ）**。既存RFQの他商品やエキゾチック商品への拡張は後段。
- 初期モデルの中心は `EconomicTerms`、`Payout`、`InterestRatePayout`、固定/変動の`RateSpecification`、Notional、Currency、Pay/Receive、計算期間・支払スケジュール、Day Countなど。
- 固定/変動の2レッグを表現できることが最初の到達点。必要になるまでCDMの全階層、Reset/Fixing/Cashflow生成、エキゾチックなPayoutは実装しない。
- Economic Modelは**純粋なKotlin**とし、Spring、DB、Kafka、QuantLibに依存させない。
- RFQのWorkflow状態、Pricing Session、MarketCurve、PricingDependencies等のサービス固有概念は共通化しない。
- **共有ライブラリとサービス間のメッセージ契約は別物**。KafkaへJVMオブジェクトを直接転送する前提にはせず、`contracts`でバージョン管理された明示的なイベントスキーマを用いる。

### 2.3 Pricing Worker / メッセージング

- RFQイベント経路は**Kafka**。原則として`key=request_id`、既存の`rfq-domain-events`と`pricing-workers` Consumer Groupを参照する。
- 複数Workerは「後で」ではなく**最初のPricing基盤の必須要件**。Worker間でPartitionとSession Ownershipを分担する。
- Partition assignment / revocation / lost / Worker停止 / 再起動 / scale-out・scale-inを扱う。
- Sessionは永続化・復元可能とし、Rebalance後に新Ownerが復元して処理継続できるようにする。
- 逐次処理を基本とし、**イベントの業務処理成功後にオフセットをコミット**する。自動的なExactly-Onceは仮定せず、重複配送への冪等性を確保する。
- 旧Ownerの計算・Publishが新Ownerの結果を破壊しない仕組み（世代番号/Fencing等）を設計・テストする。
- `request_id`→Partitionの対応とSessionのOwner判定を整合させる。ハッシュを独自に決め打ちしてKafkaの実際のPartitionと矛盾させない。
- Kafka Consumerのpoll、処理時間、Rebalanceコールバック、キャンセル・リソース解放、`max.poll.interval.ms`等を初期から考慮する。
- **Market Data経路はZeroMQ PUB/SUBを維持**。Pricing Service内で受信、最新状態の保持、Curve更新、依存するSessionの再評価を行う。
- 価格更新は**Redis Pub/Sub**経由でRFQ Service側のPrice Gatewayへ流す。

### 2.4 技術選択・コード構成

| 項目 | 基本選択 |
|---|---|
| 言語 | Kotlin / JDK 21 |
| ビルド | Gradle Kotlin DSL / Multi-project |
| フレームワーク | **Spring Boot**を第一選択（Ktorではなく本番運用を重視） |
| Architecture | 各サービスでOnion: domain / application / infrastructure / presentation |
| RFQデータ | PostgreSQL（RFQ Serviceが所有） |
| Pricing Session永続化 | 独立したStore（技術の詳細は実装時に決める） |
| RFQ Domain Events | Kafka |
| Market Data | ZeroMQ PUB/SUB |
| 価格更新バックプレーン | Redis Pub/Sub |
| Frontend接続 | RFQ ServiceのREST / WebSocket |
| Pricing Engine | 初期はFake、最後にQuantLib |
| Database migration | Flywayを候補とする |
| Kafkaライブラリ | Spring KafkaまたはKafka Java Clientを採用時に比較する |
| イベントスキーマ | 明示的にバージョン管理。Avro＋Schema Registryは候補（未確定） |

- Domain/Applicationには原則としてSpring依存を持ち込まない。Springは主にAdapter、DI/起動、HTTP、メッセージングに使う。
- コードは最初から別々に起動・テスト・将来デプロイできる構成にする。
- 原則Monorepo。共通モデルと契約は個別のGradleサブプロジェクトで管理する。

推奨初期構成:

```text
rfq-kotlin/
├── settings.gradle.kts
├── build.gradle.kts
├── economic-model/
├── contracts/
├── rfq-service/
│   └── src/{main,test}/kotlin/...  (domain/application/infrastructure/presentation)
├── pricing-service/
│   └── src/{main,test}/kotlin/...  (domain/application/infrastructure/presentation)
├── docker-compose.yml
└── docs/
    └── kotlin-migration-plan.md
```

## 3. 移植ロードマップ（実施順）

| Phase | テーマ | 到達条件（Definition of Done） |
|---|---|---|
| **0** | Gradle Multi-project / Spring Boot基盤 | 2サービスを別々に起動・テスト可能。`economic-model`が単独でテストできる。 |
| **1** | 最小Economic Model＋RFQ Domain | JPY-TONA OISの固定/変動2レッグを表現でき、RFQの基礎モデル・不変条件にテストがある。 |
| **2** | Kafka Multi-Worker＋Rebalance＋Session Restore | テスト用RFQイベント発行器で、複数Pricing WorkerによるSession生成/変更/停止、Owner移動、障害復旧、冪等性、古いOwnerの拒否を検証。Fake Pricerでよい。 |
| **3** | RFQ Workflow / PostgreSQL / Outbox | RFQ登録・変更等のUseCase、永続化、Kafkaイベント発行の整合性を検証。 |
| **4** | RFQ REST / WebSocket / Redis / Fake価格配信 | Frontend→RFQ→Kafka→Pricing→Redis→RFQ Price Gateway→Frontendの一連の経路が動く。 |
| **5** | ZeroMQ / Market Data / Dependency Resolver / Fake Curve | 更新したMarket Dataに依存するSessionを再評価・再配信。起動・再起動時の最新状態の取扱いも検証。 |
| **6** | QuantLibによる実計算 | 既存Python実装の曲線構築・JPY-TONA OISプライシングと数値比較。Par Rateを固定金利に入れたNPV≈0を再検証。 |

**Phase 2を早期に置く理由:** ユーザーの希望により、複数Worker/Rebalanceを最初から成立させる。RFQ Service側のイベント送信が未完成でも、テスト用PublisherでPricing側を先行開発できる。

**QuantLibは最後:** API、Session、Market Data、依存解決、価格配信の接続まではFake/Stubで完結する。Javaバインディング/JNI/JNAなどの連携方式はPhase 6で評価する。

## 4. 学習の進め方（2冊の役割分担とフェーズ別読書計画）

### 4.1 参考書と役割

1. **『Kotlin in Action』**：言語の主教材。型システム、クラス/インターフェース、コレクション、関数型の書き方、Null Safety、Java相互運用、Coroutineなどを深く学ぶ。**版が未確認なので章番号ではなくテーマ単位で対応付ける。**
2. **『Kotlin サーバーサイドプログラミング実践開発』**（竹端尚人、技術評論社、2021年）：サーバーサイドの補助教材。Spring Boot、REST、DI、DBアクセス、認証・認可、JUnit、Java相互運用、Ktor/Exposed/Kotest/MockK等を、実際の移植タスクと結びつけて読む。**こちらは出版社公式目次の章番号を使用する。**

共通方針：**2冊とも完読を実装開始条件にしない。**「必要な箇所を読む→ユーザー本人が実装→正常/失敗テスト→レビュー→本へ戻る」の順で繰り返す。2冊の重複箇所は主に『Kotlin in Action』で深掘りし、実践書は利用例の確認に充てる。

参考書の正式情報・目次：
- https://gihyo.jp/book/2021/978-4-297-11859-4
- 2021年刊のため、掲載されているSpring Boot / Kotlin / Java / MyBatis / テストライブラリ等の**バージョン・API・Gradle設定は現在の採用版の公式資料で確認**する。設計原則・実装の流れを学ぶ資料と位置付け、サンプルを無条件にコピーしない。

### 4.2 フェーズ別の読書計画

| Phase | 実装の焦点 | 『Kotlin in Action』で学ぶテーマ | 『サーバーサイドプログラミング実践開発』で読む章 |
|---|---|---|---|
| **0** | Gradle Multi-project / Spring Boot起動 | プロジェクト構成、基本文法、型、関数 | **第1章**（基本・環境）を必要箇所だけ復習、**第4章**（Spring Boot導入、REST、DI）を先読み |
| **1** | CDM-inspired Economic Model / RFQ Domain | `data class`、`sealed interface`、`when`、値オブジェクト、Null Safety、コレクション | **第2章**（言語機能）の必要箇所、**第8章**（JUnit）のテスト導入箇所 |
| **2** | Kafka複数Worker / Rebalance / Session Restore | Java APIとの相互運用、例外・コールバック、Coroutine基礎、ライフサイクル | **第3章**（Java連携）、**第2章11節**（Coroutine入門）、**第8章**（テスト） |
| **3** | RFQ Workflow / PostgreSQL / Outbox | 関数・コレクション、Generics、例外・結果型、インターフェース | **第4〜6章**（Spring Boot、DBアクセス、CRUD）、**第8章**（JUnit） |
| **4** | RFQ REST/WS / Redis / Fake価格配信 | ラムダ、型設計、シリアライゼーション境界、非同期I/Oの理解 | **第4章・第6章**（REST・アプリ構成）、**第7章**（認証・認可/AOP）、**第8章**（テスト） |
| **5** | ZeroMQ / Market Data / Dependency Resolver | Coroutine、Flow、キャンセル、状態管理、リソース解放 | **第2章11節**（Coroutineの導入）、**第12章**（Kotest / MockK：必要なら採用） |
| **6** | QuantLib連携 / 数値一致検証 | Java相互運用、型/例外の境界、ネイティブ呼出しの設計 | **第3章**（Java連携の復習）。QuantLibやJNI/JNA自体は別資料で確認 |

上の章対応は**読む推奨タイミング**であり、全ページの学習義務ではない。『Kotlin in Action』もこの表のテーマに合わせて柔軟に先読み・復習する。

### 4.3 第3部の扱い（採用する技術とは分離）

- **第9章 gRPC**：比較検討用。今回のRFQ→PricingはKafkaイベント方式を維持するため、導入の必須タスクにはしない。
- **第10章 Ktor**：Spring Bootとの設計比較に利用。今回の採用フレームワークは**Spring Bootのまま**。
- **第11章 Exposed**：DBアクセス技術の比較対象。採用は未確定であり、サンプルのMyBatisやMySQLも無条件には採用しない。今回のDBは引き続きPostgreSQL。
- **第12章 Kotest / MockK**：JUnitに慣れた後、テスト表現やMockが必要になった段階で比較・導入を判断する。最初から必須にしない。

### 4.4 書籍でカバーできない重要領域

KafkaのPartition Ownership/Rebalance/Commit、Redis Pub/Sub、ZeroMQ、Outboxの障害時整合性、Spring Bootでの運用監視、QuantLib連携などは、この2冊だけで仕様を確定しない。**採用するライブラリの現行公式ドキュメントと実験・障害テスト**を使う。

### 4.5 1タスクの学習ループ

1. 次の移植対象に対応する『Kotlin in Action』の言語テーマを読む。
2. Spring/DB/RESTなどを使うタスクなら実践書の該当節を読む。
3. ユーザー本人がまず最小のコードを書く。AIは最初から完成コードを渡さない。
4. 1機能を動かし、**少なくとも1つの失敗条件テスト**を加える。
5. Kotlinらしい表現とOnion Architecture/サービス境界をレビューし、必要な箇所だけ読み直す。

## 5. 実装とレビューの約束

1. **ユーザー本人が実装を書くことを基本**とする。AIは最初から完成コードを渡すのではなく、設計・不変条件・失敗条件・落とし穴・テストを問いかけてレビューする。
2. **一度に新機能1つ＋その機能の失敗条件を最低1つテスト**する。
3. 各Phaseに到達条件を設け、既存Python実装との挙動比較ができる箇所は比較する。
4. 停止・再起動・イベント重複・順序逆転・DB失敗・Kafka Rebalance・Consumer停止・旧Workerの遅延Publishなどを重視する。
5. 型やレイヤーの抽象化は必要最小限。CDMらしさのためだけに大量のクラスを先行作成しない。
6. Python実装は移植対象の参照実装とし、機能をKotlinに移す過程で意図的に設計を改善してよい。ただし仕様変更は明示する。

## 6. 最初の具体的なタスク

**Task 0:** Gradle Multi-projectの骨格を作成する。`economic-model`、`contracts`、`rfq-service`、`pricing-service`。サービスは独立起動・テスト可能であること。

**Task 1（最初のDomain実装）:** `economic-model`に、最小限のJPY-TONA OIS表現を作る。

- `EconomicTerms`に固定・変動の`InterestRatePayout`を格納できる。
- Pay/Receive、Notional、Currency、Calculation/Payment Schedule、Day Count、Fixed/Floating Rate Specを最低限表現できる。
- 最初はスケジュール自動生成、Reset/Fixing、Cashflow計算、汎用商品階層、CDM完全準拠を要求しない。
- **失敗条件テスト例:** 無効な計算期間、ゼロ/負のNotional、固定/変動レッグの必要属性欠落などの少なくとも1つを検証する。
- Spring、DB、Kafkaに依存せずにユニットテストが通る。

その後、Economic Modelを持つRFQ Domainを実装する。**Task 1の読書：『Kotlin in Action』のclass/interface/sealed/data class、実践書第2章（該当節）・第8章（JUnit）。**

## 7. 未確定・実装時に決める事項（確定済みと混同しない）

- CDM-inspiredモデルの**正確なクラス構造・命名・粒度**、既存Pythonモデルのどの部分を持ち込むか。
- 参考価格取得要求をどのRFQ Domain Event/業務状態遷移として表すか、具体的なイベントの契約。
- KafkaのPartition数や将来のShardスケール戦略。以前の4 Partition構成は**参照値**であり、将来不変の設計制約ではない。
- Session Storeの具体的な実装・永続化形式と最新状態への復元手順。
- Fencing/Ownership世代とPrice Gateway側の古い更新の排除方法。
- Spring KafkaのContainer APIとKafka Java Clientのどちらで詳細なRebalance制御を行うか。
- Kafkaイベントスキーマの技術（Avro/Protobuf/JSON Schema）、Registryの採否と進化規則。
- ZeroMQ Market Dataの最新Snapshotの復元・初期化の詳細（更新前に価格を出さない条件など）。
- QuantLibのJVM連携方式と性能・デプロイ上の評価。
- RFQの他商品、Quote登録、通知等の全機能をどのフェーズで拡充するか（最初の縦方向の完成後に範囲確定）。

## 8. 別チャット用の引き継ぎ短文

> 現在の「RFQ / リアルタイム価格配信 Kotlin移植・サービス分離計画 v1.1（2026-10-31基準）」を前提に進めてください。Kotlin + JDK 21 + Spring Boot + Gradle Multi-project、Onion Architecture。FrontendはRFQ ServiceのREST/WSのみ利用し、参考価格取得はRFQ業務としてRFQ Serviceが仲介。Pricing ServiceはKafka RFQ Domain Eventsを購読してSession管理・Market Data処理・再計算を担当。Market DataはZeroMQ、価格配信バックプレーンはRedis。Economic Modelは両サービス共通の、最小限のCDM-inspired独自Kotlinモデルから始める（まずJPY-TONA OIS）。複数Pricing Worker・Kafka Rebalance・Session Restore・冪等性・旧Owner対策を初期から実装。QuantLibは最後、先にFake PricerでEnd-to-Endを完成。『Kotlin in Action』を言語の主教材、『Kotlin サーバーサイドプログラミング実践開発』（竹端尚人、2021年）をSpring Boot/DB/API/テストの実践教材としてフェーズごとに並行して読む。2021年の書籍の設定/版は現行公式資料で確認する。ユーザー本人が1機能ずつ実装し、各機能に失敗条件テストを加える。確定方針は勝手に変えず、未確定事項はその都度検討する。

## 9. 変更管理

- **v1.0 / 2026-10-31:** サービス境界・技術選定・実装順序・ユーザー主導の開発方針を固定。
- **v1.1 / 2026-10-31:** 参考書『Kotlin サーバーサイドプログラミング実践開発』を追加。第1〜12章の適用箇所、フェーズ別の読書計画、旧版コードの注意点を明文化。**既存のアーキテクチャ・実装順序・技術選択の確定事項は変更なし。**
- 今後の変更は `v1.2`、`v2.0` などに更新し、「何を変更したか」「なぜ変更したか」「既存コードへの影響」を記録する。
