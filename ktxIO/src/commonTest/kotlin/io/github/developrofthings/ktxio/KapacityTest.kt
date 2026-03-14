package io.github.developrofthings.ktxio

import io.github.developrofthings.kapacity.byte
import kotlinx.io.Buffer
import kotlinx.io.bytestring.ByteString
import kotlinx.io.bytestring.ByteStringBuilder
import kotlin.test.Test
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
}
