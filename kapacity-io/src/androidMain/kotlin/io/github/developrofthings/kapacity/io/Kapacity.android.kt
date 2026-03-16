package io.github.developrofthings.kapacity.io

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