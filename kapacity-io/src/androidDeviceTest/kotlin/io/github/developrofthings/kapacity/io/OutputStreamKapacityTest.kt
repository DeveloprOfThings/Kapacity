package io.github.developrofthings.kapacity.io

import io.github.developrofthings.kapacity.byte
import java.io.ByteArrayOutputStream
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class OutputStreamKapacityTest {
    @Test
    fun testOutputStreamWrite_exactFit() {
        val stream = ByteArrayOutputStream()
        val source = byteArrayOf(1, 2, 3, 4, 5)

        stream.write(source = source, kapacity = 5.byte)

        assertContentEquals(byteArrayOf(1, 2, 3, 4, 5), stream.toByteArray())
    }

    @Test
    fun testOutputStreamWrite_limitedByKapacity() {
        val stream = ByteArrayOutputStream()
        val source = byteArrayOf(1, 2, 3, 4, 5)

        // Source has 5 bytes, but we strictly limit the write to 3 bytes
        stream.write(source = source, kapacity = 3.byte)

        assertContentEquals(byteArrayOf(1, 2, 3), stream.toByteArray())
    }

    @Test
    fun testOutputStreamWrite_limitedBySourceSpace() {
        val stream = ByteArrayOutputStream()
        val source = byteArrayOf(1, 2, 3)

        // We ask to write 5 bytes, but the source array only has 3
        stream.write(source = source, kapacity = 5.byte)

        // Should safely clamp and write only the 3 available bytes without crashing
        assertContentEquals(byteArrayOf(1, 2, 3), stream.toByteArray())
    }

    @Test
    fun testOutputStreamWrite_withSourceOffset() {
        val stream = ByteArrayOutputStream()
        val source = byteArrayOf(9, 8, 7, 6, 5)

        // Skip the first 2 bytes. We ask for 10 bytes, but only 3 remain in the array.
        stream.write(
            source = source,
            sourceOffset = 2,
            kapacity = 10.byte
        )

        // Should clamp to the remaining bytes after the offset
        assertContentEquals(byteArrayOf(7, 6, 5), stream.toByteArray())
    }

    @Test
    fun testOutputStreamWrite_zeroKapacityEarlyExit() {
        val stream = ByteArrayOutputStream()
        val source = byteArrayOf(1, 2, 3)

        stream.write(source = source, kapacity = 0.byte)

        assertEquals(0, stream.toByteArray().size, "Stream should remain completely empty")
    }
}