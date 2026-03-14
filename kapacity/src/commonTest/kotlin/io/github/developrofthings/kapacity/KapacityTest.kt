package io.github.developrofthings.kapacity

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KapacityTest {
    @Test
    fun `verify metric SI conversions from Long`() {
        assertEquals(1L, 1L.byte.rawBytes)
        assertEquals(1_000L, 1L.kilobyte.rawBytes)
        assertEquals(1_000_000L, 1L.megabyte.rawBytes)
        assertEquals(1_000_000_000L, 1L.gigabyte.rawBytes)
        assertEquals(1_000_000_000_000L, 1L.terabyte.rawBytes)
        assertEquals(1_000_000_000_000_000L, 1L.petabyte.rawBytes)
        assertEquals(1_000_000_000_000_000_000L, 1L.exabyte.rawBytes)
    }

    @Test
    fun `verify binary IEC conversions from Long`() {
        assertEquals(1L, 1L.binaryByte.rawBytes)
        assertEquals(1_024L, 1L.binaryKilobyte.rawBytes)
        assertEquals(1_048_576L, 1L.binaryMegabyte.rawBytes)
        assertEquals(1_073_741_824L, 1L.binaryGigabyte.rawBytes)
        assertEquals(1_099_511_627_776L, 1L.binaryTerabyte.rawBytes)
        assertEquals(1_125_899_906_842_624L, 1L.binaryPetabyte.rawBytes)
        assertEquals(1_152_921_504_606_846_976L, 1L.binaryExabyte.rawBytes)
    }

    @Test
    fun `verify conversions from Int`() {
        // Just checking a few to ensure the Int extensions route correctly
        assertEquals(5_000_000L, 5.megabyte.rawBytes)
        assertEquals(5_242_880L, 5.binaryMegabyte.rawBytes)
    }

    @Test
    fun `verify math operators between capacities`() {
        val base = 5.megabyte

        // Addition
        assertEquals(7_000_000L, (base + 2.megabyte).rawBytes)
        assertEquals(5_500_000L, (base + 500.kilobyte).rawBytes)

        // Subtraction
        assertEquals(3_000_000L, (base - 2.megabyte).rawBytes)

        // Division returning a scalar Long
        val ratio = 10.megabyte / 2.megabyte
        assertEquals(5L, ratio)
    }

    @Test
    fun `verify scalar math operators`() {
        val base = 10.megabyte

        // Multiply
        assertEquals(30_000_000L, (base * 3).rawBytes)
        assertEquals(50_000_000L, (base * 5L).rawBytes)

        // Divide
        assertEquals(2_000_000L, (base / 5).rawBytes)
        assertEquals(5_000_000L, (base / 2L).rawBytes)
    }

    @Test
    fun `verify negative capacities are coerced to zero`() {
        // Subtracting a larger capacity
        val underflow = 5.megabyte - 10.megabyte
        assertEquals(0L, underflow.rawBytes)

        // Subtracting scalar bytes
        val underflowScalar = 10.kilobyte - 20_000
        assertEquals(0L, underflowScalar.rawBytes)
    }

    @Test
    fun `verify comparison operators`() {
        assertTrue(1.gigabyte > 900.megabyte)
        assertTrue(1.binaryKilobyte > 1.kilobyte) // 1024 > 1000
        assertTrue(500.megabyte < 1.gigabyte)
        
        // Ensure sorting works
        val list = listOf(2.gigabyte, 500.megabyte, 1.terabyte)
        val sortedList = list.sorted()
        
        assertEquals(500.megabyte, sortedList[0])
        assertEquals(2.gigabyte, sortedList[1])
        assertEquals(1.terabyte, sortedList[2])
    }
}
