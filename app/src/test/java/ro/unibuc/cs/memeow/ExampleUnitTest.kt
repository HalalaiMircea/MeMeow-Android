package ro.unibuc.cs.memeow

import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun test(){
        val jsonParser= JsonParser()
        val adress = jsonParser.parse("{\"cat\": \"caine\"}")
            .asJsonObject.get("cat").asString
        println(adress)
    }
}