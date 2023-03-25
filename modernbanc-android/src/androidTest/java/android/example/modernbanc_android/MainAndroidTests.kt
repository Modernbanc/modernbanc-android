package android.example.modernbanc_android

import android.ModernbancApiClient
import android.ModernbancInput
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MainAndroidTests {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Log.d("ModernbancInput", appContext.packageName)
        assertEquals("com.modernbanc.android.test", appContext.packageName)
        val client = ModernbancApiClient("")
        val input = ModernbancInput(appContext, client = client)
        assertNotNull(input.client)


        val isLongerThan5Characters: (String) -> Boolean = { it.length > 5 }
        input.validationFn = isLongerThan5Characters

        input.setText("Hello")
        assertFalse(input.isValid)

        input.setText("Hello world")
        assertTrue(input.isValid)

    }
}