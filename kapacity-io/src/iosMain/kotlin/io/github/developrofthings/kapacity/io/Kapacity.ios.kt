package io.github.developrofthings.kapacity.io

import io.github.developrofthings.kapacity.Kapacity
import io.github.developrofthings.kapacity.byte
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSFileSize
import platform.Foundation.NSNumber
import platform.Foundation.NSURL

/**
 * Returns the [Kapacity] of this [NSData] buffer, calculated directly from its length in memory.
 *
 * Each byte currently held in the buffer represents exactly 1 byte of capacity.
 */
val NSData.kapacity: Kapacity get() = this.length.toLong().byte

/**
 * Returns the [Kapacity] of this [NSURL], calculated directly from its file size on the disk.
 *
 * If the file does not exist, or if the URL does not represent a local file path,
 * this will return a [Kapacity] of 0 bytes.
 */
@OptIn(ExperimentalForeignApi::class)
val NSURL.kapacity: Kapacity
    get() {
        // We need the raw string path to pass to NSFileManager
        val path = this.path ?: return 0L.byte

        // Fetch the dictionary of file attributes
        val attributes = NSFileManager.defaultManager.attributesOfItemAtPath(path, null)

        // Extract the file size and safely cast it to an NSNumber
        val fileSize = attributes?.get(NSFileSize) as? NSNumber

        // Convert to a Long and wrap it in your capacity extension
        return (fileSize?.longValue ?: 0L).byte
    }