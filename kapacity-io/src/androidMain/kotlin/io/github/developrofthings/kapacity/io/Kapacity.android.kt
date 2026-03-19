@file:OptIn(InternalKapacityApi::class)
@file:Suppress("unused")

package io.github.developrofthings.kapacity.io

import io.github.developrofthings.kapacity.ExperimentalKapacityApi
import io.github.developrofthings.kapacity.InternalKapacityApi
import io.github.developrofthings.kapacity.Kapacity
import io.github.developrofthings.kapacity.byte
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
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


/**
 * Returns an estimate of the number of bytes that can be read (or skipped over) from this
 * [InputStream] without blocking, safely wrapped as a [Kapacity] instance.
 *
 * **Important Note on Java IO:** This property directly delegates to [InputStream.available].
 * It represents the number of bytes currently buffered locally or immediately accessible.
 * It does **not** represent the total remaining size of the stream or file. You should never
 * use this property to allocate a buffer intended to hold the entire contents of a network stream.
 *
 * @return The estimated number of non-blocking bytes currently available, represented as [Kapacity].
 * @throws java.io.IOException If an I/O error occurs while checking the underlying stream.
 */
val InputStream.available: Kapacity get() = available().byte

/**
 * Reads up to the specified [kapacity] of bytes from this input stream into the given [destination] array.
 *
 * This function safely guards against buffer overflows. It automatically calculates the available
 * space in the [destination] array starting from the [destinationOffset] and ensures that the number
 * of bytes read does not exceed this available space, even if the requested [kapacity] is larger.
 *
 * @param destination The byte array to which data is written.
 * @param destinationOffset The starting offset in the [destination] array where the data will be written. Defaults to 0.
 * @param kapacity The maximum number of bytes to read from the stream.
 * @return The total number of bytes read into the buffer, or `-1` if there is no more data because the end of the stream has been reached.
 * @throws IllegalArgumentException If [destinationOffset] is outside the bounds of the [destination] array,
 * or if [kapacity] represents a negative value.
 */
fun InputStream.read(
    destination: ByteArray,
    destinationOffset: Int = 0,
    kapacity: Kapacity,
): Int {
    require(destinationOffset in 0..destination.size) {
        "destinationOffset ($destinationOffset) must be between 0 and ${destination.size}"
    }
    require(kapacity.rawBytes >= 0L) {
        "Cannot read a negative kapacity: $kapacity"
    }

    val readLength = kapacity.rawBytesCoercedToIntRange
    val availableSpace = (destination.size - destinationOffset)
    val safeLength = minOf(
        a = readLength,
        b = availableSpace,
    )
    return this.read(
        /* b = */ destination,
        /* off = */ destinationOffset,
        /* len = */ safeLength
    )
}

/**
 * Reads up to the specified [kapacity] of bytes from this input stream into the given [destination] array,
 * returning the result as a [Kapacity] instance.
 *
 * Like its primitive counterpart, this function safely limits the read length to the available space
 * in the [destination] array to prevent buffer overflows.
 *
 * @param destination The byte array to which data is written.
 * @param destinationOffset The starting offset in the [destination] array where the data will be written. Defaults to 0.
 * @param kapacity The maximum number of bytes to read from the stream.
 * @return The total number of bytes read wrapped in a [Kapacity] instance, or [Kapacity.INVALID] if
 * the end of the stream has been reached.
 * @throws IllegalArgumentException If [destinationOffset] is outside the bounds of the [destination] array,
 * or if [kapacity] represents a negative value.
 */
@ExperimentalKapacityApi
fun InputStream.readKapacity(
    destination: ByteArray,
    destinationOffset: Int = 0,
    kapacity: Kapacity,
): Kapacity = this.read(
    destination = destination,
    destinationOffset = destinationOffset,
    kapacity = kapacity
).takeIf { it >= 0 }?.byte ?: Kapacity.INVALID

/**
 * Writes up to the specified [kapacity] of bytes from the [source] array to this output stream.
 *
 * This function blocks until the bytes are written or an exception is thrown.
 * * **Safe Bounds:** The actual number of bytes written is safely clamped to prevent
 * `IndexOutOfBoundsException`. The length will be the minimum of: the requested [kapacity] or
 * the available data in the [source] array accounting for the [sourceOffset].
 *
 * @param source The data to write.
 * @param sourceOffset The start offset in the [source] array from which to begin reading. Defaults to 0.
 * @param kapacity The maximum number of bytes to write to the stream.
 * @throws java.io.IOException If an I/O error occurs.
 */
fun OutputStream.write(
    source: ByteArray,
    sourceOffset: Int = 0,
    kapacity: Kapacity,
) {
    val writeLength = kapacity.rawBytesCoercedToIntRange
    val availableSpace = (source.size - sourceOffset)
    val safeLength = minOf(
        a = writeLength,
        b = availableSpace,
    )

    // Exit early if capacity is 0 or offsets are out of bounds
    if (safeLength <= 0) return

    this.write(
        /* b = */ source,
        /* off = */ sourceOffset,
        /* len = */ safeLength
    )
}