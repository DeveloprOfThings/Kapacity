package io.github.developrofthings.kapacity

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertSame

class KapacityArrayCopyTest {

    //region ByteArray Tests
    @Test
    fun testByteArrayCopyInto_exactFit() {
        val source = byteArrayOf(1, 2, 3, 4, 5)
        val dest = ByteArray(5)

        val result = source.copyInto(destination = dest, kapacity = 5.byte)

        // PROVE ZERO ALLOCATION: The returned array must be the exact same instance in memory
        assertSame(dest, result)
        assertContentEquals(byteArrayOf(1, 2, 3, 4, 5), dest)
    }

    @Test
    fun testByteArrayCopyInto_limitedByKapacity() {
        val source = byteArrayOf(1, 2, 3, 4, 5)
        val dest = ByteArray(5)

        // We have 5, dest holds 5, but we only want to copy 3
        source.copyInto(destination = dest, kapacity = 3.byte)

        assertContentEquals(byteArrayOf(1, 2, 3, 0, 0), dest)
    }

    @Test
    fun testByteArrayCopyInto_limitedBySourceSpace() {
        val source = byteArrayOf(1, 2, 3)
        val dest = ByteArray(5)

        // Try to copy 5, but source only has 3 available
        source.copyInto(destination = dest, kapacity = 5.byte)

        assertContentEquals(byteArrayOf(1, 2, 3, 0, 0), dest)
    }

    @Test
    fun testByteArrayCopyInto_limitedByDestinationSpace() {
        val source = byteArrayOf(1, 2, 3, 4, 5)
        val dest = ByteArray(3)

        // Try to copy 5, but destination only has 3 slots
        source.copyInto(destination = dest, kapacity = 5.byte)

        assertContentEquals(byteArrayOf(1, 2, 3), dest)
    }

    @Test
    fun testByteArrayCopyInto_withOffsetsAndClamping() {
        val source = byteArrayOf(9, 9, 1, 2, 3) // We want to skip the two 9s
        val dest = ByteArray(5)

        // Start reading at index 2, start writing at index 1
        source.copyInto(
            destination = dest,
            destinationOffset = 1,
            startIndex = 2,
            kapacity = 10.byte // Ask for way too much to test clamping
        )

        // Should safely clamp to the 3 available source bytes
        assertContentEquals(byteArrayOf(0, 1, 2, 3, 0), dest)
    }

    @Test
    fun testByteArrayCopyInto_zeroKapacity() {
        val source = byteArrayOf(1, 2, 3)
        val dest = ByteArray(3)

        source.copyInto(destination = dest, kapacity = 0.byte)

        // Destination should remain completely untouched
        assertContentEquals(byteArrayOf(0, 0, 0), dest)
    }
    //endregion

    //region UByteArray Tests
    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    fun testUByteArrayCopyInto() {
        val source = ubyteArrayOf(255u, 254u, 253u)
        val dest = UByteArray(3)

        val result = source.copyInto(destination = dest, kapacity = 2.byte)

        assertContentEquals(dest, result)
        assertContentEquals(ubyteArrayOf(255u, 254u, 0u), dest)
    }
    //endregion

    //region Object Array<Byte> Tests
    @Test
    fun testObjectByteArrayCopyInto() {
        val source: Array<Byte> = arrayOf(1, 2, 3)
        val dest: Array<Byte> = Array(3) { 0 }

        val result = source.copyInto(destination = dest, kapacity = 2.byte)

        assertSame(dest, result)
        assertContentEquals(arrayOf<Byte>(1, 2, 0), dest)
    }
    //endregion

    //region Object Array<UByte> Tests
    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    fun testObjectUByteArrayCopyInto() {
        val source: Array<UByte> = arrayOf(10u, 20u, 30u)
        val dest: Array<UByte> = Array(3) { 0u }

        val result = source.copyInto(destination = dest, kapacity = 2.byte)

        assertSame(dest, result)
        assertContentEquals(arrayOf<UByte>(10u, 20u, 0u), dest)
    }
    //endregion
}