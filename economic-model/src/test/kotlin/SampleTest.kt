import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestSample {
    @Test
    fun canDoubleInput() {
        val result = double(3);
        Assertions.assertEquals(6, result)
    }
}