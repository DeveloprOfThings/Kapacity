package io.github.developrofthings.kapacity

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class KapacityOperatorTest {

    //region Double Division Tests
    @Test
    fun testDoubleDivisionHappyPath() {
        val capacity = 10.megabyte
        val result = capacity / 2.0
        
        assertEquals(5.megabyte.rawBytes, result.rawBytes)
    }

    @Test
    fun testDoubleDivisionByZeroThrowsException() {
        val capacity = 10.megabyte
        
        assertFailsWith<IllegalArgumentException> {
            capacity / 0.0
        }
    }

    @Test
    fun testDoubleDivisionByNaNThrowsException() {
        val capacity = 10.megabyte
        
        assertFailsWith<IllegalArgumentException> {
            capacity / Double.NaN
        }
    }

    @Test
    fun testDoubleDivisionByInfinityThrowsException() {
        val capacity = 10.megabyte
        
        assertFailsWith<IllegalArgumentException> {
            capacity / Double.POSITIVE_INFINITY
        }
        
        assertFailsWith<IllegalArgumentException> {
            capacity / Double.NEGATIVE_INFINITY
        }
    }
    //endregion

    //region Float Division Tests
    @Test
    fun testFloatDivisionHappyPath() {
        val capacity = 10.megabyte
        val result = capacity / 2.0f
        
        assertEquals(5.megabyte.rawBytes, result.rawBytes)
    }

    @Test
    fun testFloatDivisionByZeroThrowsException() {
        val capacity = 10.megabyte
        
        assertFailsWith<IllegalArgumentException> {
            capacity / 0.0f
        }
    }

    @Test
    fun testFloatDivisionByNaNThrowsException() {
        val capacity = 10.megabyte
        
        assertFailsWith<IllegalArgumentException> {
            capacity / Float.NaN
        }
    }
    //endregion
}