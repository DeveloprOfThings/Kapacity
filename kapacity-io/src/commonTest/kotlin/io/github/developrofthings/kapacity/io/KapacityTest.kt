package io.github.developrofthings.kapacity.io

import io.github.developrofthings.kapacity.byte
import kotlinx.io.Buffer
import kotlinx.io.bytestring.ByteString
import kotlinx.io.bytestring.ByteStringBuilder
import kotlinx.io.readByteArray
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class KapacityTest {

    @Test
    fun testBufferKapacity() {
        val buffer = Buffer()
        assertEquals(0.byte, buffer.kapacity)

        val data = byteArrayOf(1, 2, 3, 4, 5)
        buffer.write(data)
        assertEquals(5.byte, buffer.kapacity)
    }

    @Test
    fun testByteStringKapacity() {
        val data = byteArrayOf(1, 2, 3)
        val byteString = ByteString(data)
        assertEquals(3.byte, byteString.kapacity)

        val emptyByteString = ByteString()
        assertEquals(0.byte, emptyByteString.kapacity)
    }

    @Test
    fun testByteStringBuilderKapacity() {
        val builder = ByteStringBuilder()
        assertEquals(0.byte, builder.kapacity)

        builder.append(byteArrayOf(1, 2, 3, 4))
        assertEquals(4.byte, builder.kapacity)
    }

    //region Buffer.skip() Tests
    @Test
    fun testBufferSkip() {
        val buffer = Buffer().apply { write(byteArrayOf(1, 2, 3, 4, 5)) }

        buffer.skip(kapacity = 2.byte)

        assertEquals(3L, buffer.size)
        assertContentEquals(byteArrayOf(3, 4, 5), buffer.readByteArray())
    }
    //endregion

    //region Buffer.readAtMostTo(Buffer) Tests
    @Test
    fun testBufferReadAtMostTo_bufferSink() {
        val source = Buffer().apply { write(byteArrayOf(1, 2, 3, 4, 5)) }
        val sink = Buffer()

        val bytesRead = source.readAtMostTo(sink = sink, kapacity = 3.byte)

        assertEquals(3L, bytesRead.rawBytes)
        assertEquals(2L, source.size) // 2 bytes left in source
        assertEquals(3L, sink.size)   // 3 bytes transferred to sink
        assertContentEquals(byteArrayOf(1, 2, 3), sink.readByteArray())
    }

    @Test
    fun testBufferReadAtMostTo_bufferSink_exceedsAvailable() {
        val source = Buffer().apply { write(byteArrayOf(1, 2)) }
        val sink = Buffer()

        // Ask for 5 bytes, but only 2 are available
        val bytesRead = source.readAtMostTo(sink = sink, kapacity = 5.byte)

        assertEquals(2L, bytesRead.rawBytes) // Should only read 2
        assertEquals(0L, source.size)
        assertContentEquals(byteArrayOf(1, 2), sink.readByteArray())
    }
    //endregion

    //region Buffer.readAtMostTo(ByteArray) Tests
    @Test
    fun testBufferReadAtMostTo_byteArray_exactFit() {
        val source = Buffer().apply { write(byteArrayOf(1, 2, 3, 4, 5)) }
        val sink = ByteArray(size = 5)

        val bytesRead = source.readAtMostTo(sink = sink, kapacity = 5.byte)

        assertEquals(5L, bytesRead.rawBytes)
        assertContentEquals(byteArrayOf(1, 2, 3, 4, 5), sink)
    }

    @Test
    fun testBufferReadAtMostTo_byteArray_withOffsetAndClamping() {
        val source = Buffer().apply { write(byteArrayOf(9, 8, 7, 6)) }
        val sink = ByteArray(size = 5)

        // Start writing at index 2.
        // We ask for 10 bytes, but the array only has 3 slots left (indices 2, 3, 4).
        // The safe clamping should prevent an IndexOutOfBoundsException!
        val bytesRead = source.readAtMostTo(
            sink = sink,
            startIndex = 2,
            kapacity = 10.byte
        )

        assertEquals(3L, bytesRead.rawBytes) // Only 3 slots were safely available
        assertContentEquals(byteArrayOf(0, 0, 9, 8, 7), sink)
    }
    //endregion

    //region Buffer.write(ByteArray) Tests
    @Test
    fun testBufferWrite_byteArray_exactFit() {
        val source = byteArrayOf(1, 2, 3, 4, 5)
        val buffer = Buffer()

        buffer.write(source = source, kapacity = 3.byte)

        assertEquals(3L, buffer.size)
        assertContentEquals(byteArrayOf(1, 2, 3), buffer.readByteArray())
    }

    @Test
    fun testBufferWrite_byteArray_withOffsetAndClamping() {
        val source = byteArrayOf(0, 0, 9, 8, 7)
        val buffer = Buffer()

        // Start reading from index 2.
        // Ask to write 10 bytes, but the source array only has 3 bytes left after index 2.
        buffer.write(
            source = source,
            startIndex = 2,
            kapacity = 10.byte
        )

        assertEquals(3L, buffer.size) // Only safely wrote 3 bytes
        assertContentEquals(byteArrayOf(9, 8, 7), buffer.readByteArray())
    }
    //endregion

    //region Buffer.write(RawSource) Tests
    @Test
    fun testBufferWrite_rawSource() {
        val source = Buffer().apply { write(byteArrayOf(1, 2, 3, 4, 5)) }
        val sink = Buffer()

        sink.write(source = source, kapacity = 3.byte)

        assertEquals(3L, sink.size)
        assertEquals(2L, source.size)
        assertContentEquals(byteArrayOf(1, 2, 3), sink.readByteArray())
    }
    //endregion

    //region ByteString.substring() Tests
    @Test
    fun testByteStringSubstring_exact() {
        val byteString = ByteString(1, 2, 3, 4, 5)

        val chunk = byteString.substring(startIndex = 1, kapacity = 3.byte)

        assertEquals(3, chunk.size)
        assertContentEquals(byteArrayOf(2, 3, 4), chunk.toByteArray())
    }

    @Test
    fun testByteStringSubstring_clamping() {
        val byteString = ByteString(1, 2, 3)

        // Start at index 1 (value 2). Ask for 50 bytes.
        // Should safely clamp to the end of the ByteString without crashing.
        val chunk = byteString.substring(startIndex = 1, kapacity = 50.byte)

        assertEquals(2, chunk.size)
        assertContentEquals(byteArrayOf(2, 3), chunk.toByteArray())
    }
    //endregion
}
