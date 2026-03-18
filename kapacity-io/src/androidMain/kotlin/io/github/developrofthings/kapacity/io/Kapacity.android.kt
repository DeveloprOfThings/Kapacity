@file:OptIn(InternalKapacityApi::class)
@file:Suppress("unused")

package io.github.developrofthings.kapacity.io

import io.github.developrofthings.kapacity.InternalKapacityApi
import io.github.developrofthings.kapacity.Kapacity
import io.github.developrofthings.kapacity.byte
import java.io.File
import java.nio.ByteBuffer
import java.nio.file.Path
import kotlin.io.path.fileSize

/**
 * Returns the [Kapacity] of this [File], calculated directly from its length on the disk.
 *
 * If the file does not exist, or if it is a directory, this will return a [Kapacity] of 0 bytes,
 * matching the underlying behavior of [File.length].
 */
val File.kapacity: Kapacity get() = this.length().byte

/**
 * Returns the [Kapacity] of this [Path], calculated directly from its length on the disk.
 *
 * If the file does not exist, throws an exception matching the underlying behavior of [Path.fileSize].
 */
val Path.kapacity: Kapacity get() = this.fileSize().byte

/**
 * Returns the [Kapacity] of this [ByteBuffer], calculated directly from its length on the disk.
 */
val ByteBuffer.kapacity: Kapacity get() = this.capacity().byte

/**
 * Allocates a new standard (heap-backed) [ByteBuffer] with a capacity equal to this [Kapacity].
 *
 * This buffer is allocated on the JVM heap and will be managed by the standard garbage collector.
 * It is backed by a standard `ByteArray`, which can be accessed via `buffer.array()`.
 *
 * **Warning on Bounds:** Because [ByteBuffer] capacities are strictly indexed by [Int], the maximum
 * allowed size is [Int.MAX_VALUE]` - 8` (approximately 2.14 GB). If this capacity exceeds that limit,
 * the resulting buffer size will be safely truncated to [Int.MAX_VALUE] to prevent memory allocation crashes.
 *
 * @return A new, empty [ByteBuffer] ready for writing.
 */
fun Kapacity.allocateByteBuffer(): ByteBuffer =
    ByteBuffer.allocate(/*capacity = */this.rawBytesCoercedToIntRange)

/**
 * Allocates a new direct (off-heap) [ByteBuffer] with a capacity equal to this [Kapacity].
 *
 * Direct buffers allocate memory outside of the standard JVM heap, bypassing standard garbage collection.
 * They are highly optimized for native I/O operations (like socket or file channel transfers) and JNI
 * interactions, as they avoid copying data between the JVM heap and native memory.
 *
 * **Note:** Allocating and deallocating direct buffers is generally more expensive than standard buffers,
 * so they are best suited for large, long-lived buffers.
 *
 * **Warning on Bounds:** Like standard buffers, the maximum allowed size is safely truncated to
 * [Int.MAX_VALUE]` - 8` (approximately 2.14 GB) to prevent memory allocation crashes.
 *
 * @return A new, empty direct [ByteBuffer] ready for writing.
 */
fun Kapacity.directAllocateByteBuffer(): ByteBuffer =
    ByteBuffer.allocateDirect(/*capacity = */this.rawBytesCoercedToIntRange)

/**
 * Reads up to the specified [kapacity] of bytes from this buffer into the [destination] array.
 *
 * **Warning on Bounds:** This operation safely clamps the number of bytes read to prevent
 * `BufferUnderflowException` and `IndexOutOfBoundsException`. The actual number of bytes transferred
 * will be the minimum of: the requested [kapacity], the [ByteBuffer.remaining] bytes in this buffer, or the
 * available space in the [destination] array accounting for the [destinationOffset].
 *
 * @param destination The byte array to write the data into.
 * @param destinationOffset The index within the destination array to begin writing. Defaults to 0.
 * @param kapacity The maximum amount of data to read from this buffer.
 * @return This buffer, to allow for fluent method chaining.
 */
fun ByteBuffer.get(
    destination: ByteArray,
    destinationOffset: Int = 0,
    kapacity: Kapacity,
): ByteBuffer {
    val length = (destination.size - destinationOffset)
    val safeLength = minOf(
        a = kapacity.rawBytesCoercedToIntRange,
        b = minOf(a = remaining(), b = length)
    )

    return this.get(
        /* dst = */ destination,
        /* offset = */ destinationOffset,
        /* length = */ safeLength,
    )
}

/**
 * Writes up to the specified [kapacity] of bytes from the [source] array into this buffer.
 *
 * **Warning on Bounds:** This operation safely clamps the number of bytes written to prevent
 * `BufferOverflowException` and `IndexOutOfBoundsException`. The actual number of bytes transferred
 * will be the minimum of: the requested [kapacity], the [ByteBuffer.remaining] space in this buffer, or the
 * available data in the [source] array accounting for the [sourceOffset].
 *
 * @param source The byte array containing the data to write.
 * @param sourceOffset The index within the source array to begin reading from. Defaults to 0.
 * @param kapacity The maximum amount of data to write into this buffer.
 * @return This buffer, to allow for fluent method chaining.
 */
fun ByteBuffer.put(
    source: ByteArray,
    sourceOffset: Int = 0,
    kapacity: Kapacity,
): ByteBuffer {
    val length = (source.size - sourceOffset)
    val safeLength = minOf(
        a = kapacity.rawBytesCoercedToIntRange,
        b = minOf(a = remaining(), b = length)
    )
    return this.put(
        /* src = */ source,
        /* offset = */ sourceOffset,
        /* length = */ safeLength,
    )
}