package io.github.developrofthings.kapacity

import java.text.DecimalFormat

private val _androidKapacityDecFormater = DecimalFormat(/* pattern = */ "#,###.###")

internal actual fun formatByteCount(byteCount: Long): String =
    _androidKapacityDecFormater.format(/*number = */ byteCount)