@file:OptIn(InternalKapacityApi::class)

package io.github.developrofthings.kapacity.io

import io.github.developrofthings.kapacity.InternalKapacityApi
import io.github.developrofthings.kapacity.Kapacity
import io.github.developrofthings.kapacity.gigabyte
import io.github.developrofthings.kapacity.megabyte
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KapacityByteBufferAllocationTest {

    // --- 1. TEST THE MATH (Zero Allocation) ---
    @Test
    fun testCoercion_safelyClampsMassiveCapacities() {
        val massiveKapacity = 3.gigabyte

        // Test the internal property directly to prove the math works
        // without ever touching the JVM's memory allocator.
        assertEquals((Int.MAX_VALUE - 8), massiveKapacity.rawBytesCoercedToIntRange)
    }

    @Test
    fun testCoercion_handlesNegativeOverflowGracefully() {
        // If someone bypasses builders and forces a negative value
        val negativeKapacity = Kapacity.fromBytes(-5000L)

        // Prove it clamps to 0 to prevent NegativeArraySizeException
        assertEquals(0, negativeKapacity.rawBytesCoercedToIntRange)
    }

    // --- 2. TEST THE ALLOCATOR (Safe Memory Bounds) ---
    @Test
    fun testAllocateByteBuffer_wiresCorrectly() {
        // Use a safe, reasonable size (e.g., 10 Megabytes) that will
        // comfortably allocate on any CI runner or Android emulator.
        val safeKapacity = 10.megabyte

        val buffer = safeKapacity.allocateByteBuffer()

        assertEquals(safeKapacity.rawBytes, buffer.capacity().toLong())
        assertTrue(buffer.hasArray())
    }

    @Test
    fun testDirectAllocateByteBuffer_wiresCorrectly() {
        val safeKapacity = 10.megabyte

        val buffer = safeKapacity.directAllocateByteBuffer()

        assertEquals(safeKapacity.rawBytes, buffer.capacity().toLong())
        assertTrue(buffer.isDirect)
    }
}