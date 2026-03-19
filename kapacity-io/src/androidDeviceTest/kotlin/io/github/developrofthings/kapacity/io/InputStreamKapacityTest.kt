package io.github.developrofthings.kapacity.io

import io.github.developrofthings.kapacity.Kapacity
import io.github.developrofthings.kapacity.byte
import java.io.ByteArrayInputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class InputStreamKapacityTest {

    @Test
    fun testReadFillsDestinationCorrectlyWithinKapacity() {
        val data = byteArrayOf(1, 2, 3, 4, 5)
        val stream = ByteArrayInputStream(data)
        val destination = ByteArray(5)

        val bytesRead = stream.read(destination, kapacity = 3.byte)

        assertEquals(3, bytesRead)
        assertEquals(1, destination[0])
        assertEquals(3, destination[2])
        assertEquals(0, destination[3]) // Remaining bytes should be untouched
    }

    @Test
    fun testReadReturnsMinusOneAtEndOfStream() {
        val stream = ByteArrayInputStream(byteArrayOf())
        val destination = ByteArray(10)

        val bytesRead = stream.read(destination, kapacity = 5.byte)

        assertEquals(-1, bytesRead)
    }

    @Test
    fun testReadCoercesLengthToAvailableSpaceInDestination() {
        val data = byteArrayOf(10, 20, 30, 40, 50)
        val stream = ByteArrayInputStream(data)
        val destination = ByteArray(3) // Smaller than the data and the requested kapacity

        // We request 5 bytes, but the buffer only has 3 slots.
        val bytesRead = stream.read(destination, kapacity = 5.byte)

        assertEquals(3, bytesRead, "Should have capped the read at the destination size")
        assertEquals(10, destination[0])
        assertEquals(30, destination[2])
    }

    @Test
    fun testReadRespectsDestinationOffsetAndCapsRemainingSpace() {
        val data = byteArrayOf(9, 9, 9, 9, 9)
        val stream = ByteArrayInputStream(data)
        val destination = ByteArray(10)

        // Start at index 8. Only 2 slots (8, 9) are left in the destination.
        // We ask for 5 bytes, but should only safely get 2.
        val bytesRead = stream.read(destination, destinationOffset = 8, kapacity = 5.byte)

        assertEquals(2, bytesRead)
        assertEquals(9, destination[8])
        assertEquals(9, destination[9])
    }

    @Test
    fun testReadReturnsZeroForNegativeKapacity() {
        val stream = ByteArrayInputStream(byteArrayOf(1))
        val destination = ByteArray(5)

        val readBytes = stream.read(destination, kapacity = (-1).byte)
        assertEquals(0, readBytes)
    }

    @Test
    fun testReadThrowsExceptionForOutOfBoundsOffset() {
        val stream = ByteArrayInputStream(byteArrayOf(1))
        val destination = ByteArray(5)

        assertFailsWith<IllegalArgumentException> {
            // Offset is larger than the array size
            stream.read(destination, destinationOffset = 6, kapacity = 1.byte)
        }

        assertFailsWith<IllegalArgumentException> {
            // Negative offset
            stream.read(destination, destinationOffset = -1, kapacity = 1.byte)
        }
    }

    // --- Tests for readKapacity() ---

    @Test
    fun testReadKapacityReturnsValidKapacityOnSuccess() {
        val data = byteArrayOf(100, 101, 102)
        val stream = ByteArrayInputStream(data)
        val destination = ByteArray(5)

        val kapacityRead = stream.readKapacity(destination, kapacity = 2.byte)

        assertEquals(2.byte, kapacityRead)
        assertEquals(100, destination[0])
        assertEquals(101, destination[1])
    }

    @Test
    fun testReadKapacityReturnsInvalidAtEndOfStream() {
        val stream = ByteArrayInputStream(byteArrayOf())
        val destination = ByteArray(5)

        val kapacityRead = stream.readKapacity(destination, kapacity = 5.byte)

        assertEquals(Kapacity.INVALID, kapacityRead)
    }
}