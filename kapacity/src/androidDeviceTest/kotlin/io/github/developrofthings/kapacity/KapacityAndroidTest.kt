package io.github.developrofthings.kapacity

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class KapacityAndroidTest {

    @Test
    fun testFormatByteCount_LargeValue_HasSeparators() {
        // We test a large number to ensure DecimalFormat applies grouping.
        // We use the symbols from the current locale to know what separator to expect.
        val symbols = DecimalFormatSymbols.getInstance(Locale.getDefault())
        val separator = symbols.groupingSeparator

        val input = 1_234_567L
        val result = formatByteCount(input)

        // Assert it contains the separator (e.g., "1,234,567" or "1.234.567")
        assertTrue(result.contains(separator), "Result '$result' should contain the grouping separator '$separator'")

        // Ensure it doesn't contain any letters or unexpected characters
        assertTrue(result.all { it.isDigit() || it == separator }, "Result '$result' contains non-digit/non-separator characters")
    }

    @Test
    fun testFormatByteCount_Zero_IsFormatted() {
        assertEquals("0", formatByteCount(0L))
    }

    @Test
    fun testFormatByteCount_SmallValue_NoSeparators() {
        assertEquals("999", formatByteCount(999L))
    }

    @Test
    fun testKapacityToString_Integration() {
        // This verifies the common toString() correctly delegates to the android actual implementation
        val capacity = Kapacity.fromBytes(1000L)
        val result = capacity.toString(unit = KapacityUnit.Byte)

        // On Android, this should use the DecimalFormat pattern "#,###.###"
        // For US locale: "1,000 bytes"
        val symbols = DecimalFormatSymbols.getInstance(Locale.getDefault())
        val separator = symbols.groupingSeparator

        assertEquals("1${separator}000 bytes", result)
    }

    @Test
    fun testFormatByteCountWithDefaultLocale() {
        // Since DecimalFormat uses the default locale, we verify the output
        // matches the expected pattern for the current environment.
        val capacity = Kapacity.fromBytes(1234567L)
        val result = capacity.toString(unit = KapacityUnit.Byte)

        // We expect grouping separators if the locale supports them.
        // For US locale, this would be "1,234,567 bytes"
        // Note: formatByteCount is internal, but accessible here.
        val formatted = formatByteCount(1234567L)

        // Basic check: should contain the numbers and grouping separators (if applicable)
        // or at least have the correct length.
        assert(formatted.contains("1"))
        assert(formatted.contains("234"))
        assert(formatted.contains("567"))
    }

    @Test
    fun testFormatByteCountEdgeCases() {
        assertEquals("-9,223,372,036,854,775,807", formatByteCount(Long.MIN_VALUE)) // Assuming grouping is active
        assertEquals("0", formatByteCount(0L))
        assertEquals("1,000", formatByteCount(1000L)) // Assuming grouping is active
        assertEquals("9,223,372,036,854,775,807", formatByteCount(Long.MAX_VALUE)) // Assuming grouping is active
    }
}
