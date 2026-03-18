package io.github.developrofthings.kapacity.io

import io.github.developrofthings.kapacity.byte
import java.nio.ByteBuffer
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ByteBufferKapacityTest {

    //region ByteBuffer.get() Tests
    @Test
    fun testGet_exactFit() {
        val buffer = ByteBuffer.wrap(byteArrayOf(1, 2, 3, 4, 5))
        val dest = ByteArray(size = 5)

        buffer.get(destination = dest, kapacity = 5.byte)

        assertContentEquals(byteArrayOf(1, 2, 3, 4, 5), dest)
        assertEquals(0, buffer.remaining()) // Buffer should be fully read
    }

    @Test
    fun testGet_limitedByKapacity() {
        val buffer = ByteBuffer.wrap(byteArrayOf(1, 2, 3, 4, 5))
        val dest = ByteArray(size = 5)

        // Buffer has 5, array holds 5, but we only ask for 3
        buffer.get(destination = dest, kapacity = 3.byte)

        // Only the first 3 slots should be filled
        assertContentEquals(byteArrayOf(1, 2, 3, 0, 0), dest)
        assertEquals(2, buffer.remaining()) // 2 bytes left in buffer
    }

    @Test
    fun testGet_limitedByArraySpace() {
        val buffer = ByteBuffer.wrap(byteArrayOf(1, 2, 3, 4, 5))
        val dest = ByteArray(size = 2)

        // Ask for 5, but array only holds 2
        buffer.get(destination = dest, kapacity = 5.byte)

        assertContentEquals(byteArrayOf(1, 2), dest)
        assertEquals(3, buffer.remaining()) // 3 bytes left in buffer
    }

    @Test
    fun testGet_limitedByBufferSpace() {
        val buffer = ByteBuffer.wrap(byteArrayOf(1, 2))
        val dest = ByteArray(size = 5)

        // Ask for 5, array holds 5, but buffer only has 2
        buffer.get(destination = dest, kapacity = 5.byte)

        assertContentEquals(byteArrayOf(1, 2, 0, 0, 0), dest)
        assertEquals(0, buffer.remaining())
    }

    @Test
    fun testGet_withOffset() {
        val buffer = ByteBuffer.wrap(byteArrayOf(9, 8, 7))
        val dest = ByteArray(size = 5)

        // Write starting at index 2
        buffer.get(destination = dest, destinationOffset = 2, kapacity = 10.byte)

        assertContentEquals(byteArrayOf(0, 0, 9, 8, 7), dest)
    }
    //endregion

    //region ByteBuffer.put() Tests
    @Test
    fun testPut_exactFit() {
        val buffer = ByteBuffer.allocate(5)
        val source = byteArrayOf(1, 2, 3, 4, 5)

        buffer.put(source = source, kapacity = 5.byte)

        buffer.flip() // Prepare buffer for reading to assert contents
        val result = ByteArray(5)
        buffer.get(result)
        assertContentEquals(byteArrayOf(1, 2, 3, 4, 5), result)
    }

    @Test
    fun testPut_limitedByKapacity() {
        val buffer = ByteBuffer.allocate(5)
        val source = byteArrayOf(1, 2, 3, 4, 5)

        // We have 5, buffer holds 5, but we only want to write 2
        buffer.put(source = source, kapacity = 2.byte)

        assertEquals(2, buffer.position()) // Position should advance by 2
    }

    @Test
    fun testPut_limitedByBufferSpace() {
        val buffer = ByteBuffer.allocate(2)
        val source = byteArrayOf(1, 2, 3, 4, 5)

        // Try to write 5, but buffer only holds 2
        buffer.put(source = source, kapacity = 5.byte)

        assertEquals(2, buffer.position()) // Should safely stop at buffer capacity
        assertEquals(0, buffer.remaining()) 
    }

    @Test
    fun testPut_limitedByArraySpace() {
        val buffer = ByteBuffer.allocate(5)
        val source = byteArrayOf(1, 2)

        // Try to write 5, but array only has 2
        buffer.put(source = source, kapacity = 5.byte)

        assertEquals(2, buffer.position()) // Should safely stop after reading the 2 available bytes
    }

    @Test
    fun testPut_withOffset() {
        val buffer = ByteBuffer.allocate(5)
        val source = byteArrayOf(0, 0, 9, 8, 7) // We want to skip the first two zeros

        // Read from source starting at index 2
        buffer.put(source = source, sourceOffset = 2, kapacity = 10.byte)

        assertEquals(3, buffer.position()) // Should write exactly 3 bytes
        
        buffer.flip()
        val result = ByteArray(3)
        buffer.get(result)
        assertContentEquals(byteArrayOf(9, 8, 7), result)
    }
    //endregion
}