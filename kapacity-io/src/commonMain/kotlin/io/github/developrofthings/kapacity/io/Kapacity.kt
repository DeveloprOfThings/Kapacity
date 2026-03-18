@file:Suppress("unused")

package io.github.developrofthings.kapacity.io

import io.github.developrofthings.kapacity.InternalKapacityApi
import io.github.developrofthings.kapacity.Kapacity
import io.github.developrofthings.kapacity.byte
import kotlinx.io.Buffer
import kotlinx.io.RawSink
import kotlinx.io.RawSource
import kotlinx.io.bytestring.ByteString
import kotlinx.io.bytestring.ByteStringBuilder

/**
 * Returns the [Kapacity] of this [Buffer], calculated directly from its size.
 *
 * Each byte currently held in the buffer represents exactly 1 byte of capacity.
 */
val Buffer.kapacity: Kapacity get() = this.size.byte

/**
 * Returns the [Kapacity] of this immutable [ByteString], calculated directly from its size.
 *
 * Each byte in the string represents exactly 1 byte of capacity.
 */
val ByteString.kapacity: Kapacity get() = this.size.byte

/**
 * Returns the [Kapacity] of the data currently written to this [ByteStringBuilder],
 * calculated directly from its current size.
 */
val ByteStringBuilder.kapacity: Kapacity get() = this.size.byte

/**
 * Removes and discards the exact number of bytes represented by the given [kapacity] from this buffer.
 *
 * @param kapacity The amount of data to skip.
 * @throws kotlinx.io.EOFException if the buffer is exhausted before the requested capacity is skipped.
 */
fun Buffer.skip(kapacity: Kapacity) = skip(byteCount = kapacity.rawBytes)

/**
 * Removes up to the specified [kapacity] of bytes from this buffer and appends them to the [sink].
 *
 * @param sink The destination buffer to write the bytes into.
 * @param kapacity The maximum amount of data to read.
 * @return A [Kapacity] representing the exact number of bytes that were successfully read and transferred.
 */
fun Buffer.readAtMostTo(sink: Buffer, kapacity: Kapacity): Kapacity = this.readAtMostTo(
    sink = sink,
    byteCount = kapacity.rawBytes,
).byte

/**
 * Removes up to the specified [kapacity] of bytes from this buffer and writes them into the provided [sink] array.
 *
 * **Warning on Bounds:** Because primitive arrays are strictly indexed by [Int], reading operations
 * are safely bounded to [Int.MAX_VALUE]` - 8` (approximately 2.14 GB). The target end index is safely clamped
 * to prevent `IndexOutOfBoundsException` if the calculated boundary exceeds the array's physical size.
 *
 * @param sink The destination primitive array.
 * @param startIndex The index in the [sink] array to begin writing the data. Defaults to 0.
 * @param kapacity The maximum amount of data to read from this buffer.
 * @return A [Kapacity] representing the exact number of bytes that were successfully read into the array.
 */
@OptIn(InternalKapacityApi::class)
fun Buffer.readAtMostTo(
    sink: ByteArray,
    startIndex: Int = 0,
    kapacity: Kapacity,
): Kapacity {
    val targetEndIndex = startIndex + kapacity.rawBytesCoercedToIntRange
    val safeEndIndex = minOf(a = targetEndIndex, b = sink.size)

    return this.readAtMostTo(
        sink = sink,
        startIndex = startIndex,
        endIndex = safeEndIndex,
    ).byte
}

/**
 * Removes exactly the specified [kapacity] of bytes from this buffer and appends them to the [sink].
 *
 * @param sink The destination sink to write the bytes to.
 * @param kapacity The exact amount of data to read.
 * @throws kotlinx.io.EOFException if the buffer is exhausted before the requested capacity is read.
 */
fun Buffer.readTo(sink: RawSink, kapacity: Kapacity) = readTo(
    sink = sink,
    byteCount = kapacity.rawBytes,
)

/**
 * Appends the specified [kapacity] of bytes from the [source] array into this buffer.
 *
 * **Warning on Bounds:** Because primitive arrays are strictly indexed by [Int], writing operations
 * are safely bounded to [Int.MAX_VALUE]` - 8` (approximately 2.14 GB). The target end index is safely clamped
 * to prevent `IndexOutOfBoundsException` if the calculated boundary exceeds the array's physical size.
 *
 * @param source The primitive array containing the data to write.
 * @param startIndex The index in the [source] array to begin reading from. Defaults to 0.
 * @param kapacity The amount of data to write into this buffer.
 */
@OptIn(InternalKapacityApi::class)
fun Buffer.write(
    source: ByteArray,
    startIndex: Int = 0,
    kapacity: Kapacity,
) {
    val targetEndIndex = startIndex + kapacity.rawBytesCoercedToIntRange
    val safeEndIndex = minOf(a = targetEndIndex, b = source.size)

    write(
        source = source,
        startIndex = startIndex,
        endIndex = safeEndIndex,
    )
}

/**
 * Appends exactly the specified [kapacity] of bytes from the [source] into this buffer.
 *
 * @param source The source to read the bytes from.
 * @param kapacity The exact amount of data to transfer into this buffer.
 * @throws kotlinx.io.EOFException if the source is exhausted before the requested capacity is written.
 */
fun Buffer.write(
    source: RawSource,
    kapacity: Kapacity,
) = write(
    source = source,
    byteCount = kapacity.rawBytes,
)

/**
 * Returns a new [ByteString] containing a subset of bytes from this instance, starting at the
 * [startIndex] and capturing up to the specified [kapacity].
 *
 * **Warning on Bounds:** Because [ByteString] relies on `Int` indexing, extraction is safely
 * bounded to [Int.MAX_VALUE]` - 8` (approximately 2.14 GB). The target end index is clamped to prevent
 * `IndexOutOfBoundsException` if the capacity exceeds the available data.
 *
 * @param startIndex The index to begin the extraction. Defaults to 0.
 * @param kapacity The total amount of data to capture in the new [ByteString].
 * @return A new [ByteString] containing the requested chunk of data.
 */
@OptIn(InternalKapacityApi::class)
fun ByteString.substring(
    startIndex: Int = 0,
    kapacity: Kapacity,
): ByteString {
    val targetEndIndex = startIndex + kapacity.rawBytesCoercedToIntRange
    val safeEndIndex = minOf(a = targetEndIndex, b = this.size)

    return this.substring(
        startIndex = startIndex,
        endIndex = safeEndIndex,
    )
}