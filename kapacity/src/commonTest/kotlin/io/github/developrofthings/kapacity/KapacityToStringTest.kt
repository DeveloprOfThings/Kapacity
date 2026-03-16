package io.github.developrofthings.kapacity

import kotlin.test.Test
import kotlin.test.assertEquals

class KapacityToStringTest {

    //region Byte Edge Cases
    @Test
    fun testByteFormattingWithAbbreviation() {
        val singleByte = 1.byte
        val multipleBytes = 15.byte

        // Abbreviation should always be "B" regardless of pluralization
        assertEquals("1 B", singleByte.toString(useAbbreviation = true))
        assertEquals("15 B", multipleBytes.toString(useAbbreviation = true))
    }

    @Test
    fun testByteFormattingWithFullNames() {
        val singleByte = 1.byte
        val multipleBytes = 15.byte

        // Full names must correctly pluralize "Byte" vs "Bytes"
        assertEquals("1 Byte", singleByte.toString(useAbbreviation = false))
        assertEquals("15 Bytes", multipleBytes.toString(useAbbreviation = false))
    }

    @Test
    fun testByteFormattingWithoutSuffix() {
        val capacity = 42.byte

        // Should completely drop the suffix and the space
        assertEquals("42", capacity.toString(useUnitSuffix = false))
    }
    //endregion

    //region Metric Formatting Tests
    @Test
    fun testMetricFormattingWithAbbreviation() {
        val capacity = 1.5.megabyte // 1,500,000 bytes

        assertEquals("1.5 MB", capacity.toString(useMetric = true, useAbbreviation = true))
    }

    @Test
    fun testMetricFormattingWithFullNames() {
        val exactlyOne = 1.kilobyte // 1,000 bytes
        val multiple = 1.5.kilobyte // 1,500 bytes

        assertEquals("1 Kilobyte", exactlyOne.toString(useMetric = true, useAbbreviation = false))
        assertEquals("1.5 Kilobytes", multiple.toString(useMetric = true, useAbbreviation = false))
    }
    //endregion

    //region Binary (IEC) Formatting Tests
    @Test
    fun testBinaryFormattingWithAbbreviation() {
        val capacity = 1.5.mebibyte // 1.5 * 1,048,576 bytes

        // Binary should use the "iB" abbreviations
        assertEquals("1.5 MiB", capacity.toString(useMetric = false, useAbbreviation = true))
    }

    @Test
    fun testBinaryFormattingWithFullNames() {
        val exactlyOne = 1.kibibyte // 1,024 bytes
        val multiple = 1.5.kibibyte // 1,536 bytes

        // Since the enum name is still Kilobyte, it should print the enum's name
        assertEquals("1 Kilobyte", exactlyOne.toString(useMetric = false, useAbbreviation = false))
        assertEquals("1.5 Kilobytes", multiple.toString(useMetric = false, useAbbreviation = false))
    }
    //endregion

    //region Forced Unit Tests
    @Test
    fun testForcedUnitFormatting() {
        val capacity = 1.megabyte

        // Forcing a 1 MB capacity to print in Kilobytes
        assertEquals(
            "1,000 KB", 
            capacity.toString(unit = KapacityUnit.Kilobyte, useMetric = true, useAbbreviation = true)
        )
        
        // No suffix forced unit
        assertEquals(
            "1,000", 
            capacity.toString(unit = KapacityUnit.Kilobyte, useUnitSuffix = false)
        )
    }
    //endregion
}