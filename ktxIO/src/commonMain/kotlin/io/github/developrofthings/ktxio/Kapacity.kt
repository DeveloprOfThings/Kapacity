package io.github.developrofthings.ktxio

import io.github.developrofthings.kapacity.Kapacity
import io.github.developrofthings.kapacity.byte
import kotlinx.io.Buffer
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
