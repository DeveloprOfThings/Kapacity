package io.github.developrofthings.kapacity

import java.text.DecimalFormat

// Using a custom ThreadLocal object is perfectly safe across all Android API levels
private val _androidKapacityDecFormatter = object : ThreadLocal<DecimalFormat>() {
    override fun initialValue(): DecimalFormat = DecimalFormat("#,###.###")
}

internal actual fun formatByteCount(byteCount: Long): String =
    _androidKapacityDecFormatter.get()!!.format(/*number = */ byteCount)

internal actual fun formatSize(size: Double): String =
    _androidKapacityDecFormatter.get()!!.format(/*number = */ size)