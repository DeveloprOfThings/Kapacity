package io.github.developrofthings.kapacity

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle

private val _iosKapacityDecFormater = NSNumberFormatter().apply {
    setMinimumFractionDigits(minimumFractionDigits = 0U)
    setMaximumFractionDigits(maximumFractionDigits = 3U)
    setNumberStyle(numberStyle = NSNumberFormatterDecimalStyle)
    usesGroupingSeparator = true
}

internal actual fun formatByteCount(byteCount: Long): String =
    _iosKapacityDecFormater.stringFromNumber(number = NSNumber(long = byteCount))
        ?: throw IllegalArgumentException("$byteCount cannot be represented as a string!")


internal actual fun formatSize(size: Double): String =
    _iosKapacityDecFormater.stringFromNumber(number = NSNumber(double = size))
        ?: throw IllegalArgumentException("$size cannot be represented as a string!")