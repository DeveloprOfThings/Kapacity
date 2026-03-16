@file:Suppress("unused")
@file:OptIn(ExperimentalUnsignedTypes::class)

package io.github.developrofthings.kapacity

import kotlin.jvm.JvmInline
import kotlin.math.roundToLong

internal expect fun formatByteCount(byteCount: Long): String
internal expect fun formatSize(size: Double): String

@JvmInline
value class Kapacity private constructor(val rawBytes: Long) : Comparable<Kapacity> {

    private fun determineKapacityUnit(useMetric: Boolean): KapacityUnit = KapacityUnit
        .entries
        .reversed()
        .firstOrNull { unit ->
            if (useMetric) unit.metric <= this.rawBytes
            else unit.binary <= this.rawBytes
        } ?: KapacityUnit.Byte

    fun toString(
        unit: KapacityUnit? = null,
        useMetric: Boolean = true,
        useUnitSuffix: Boolean = true,
    ): String {
        val resolvedUnit = unit ?: determineKapacityUnit(useMetric = useMetric)
        if (resolvedUnit == KapacityUnit.Byte) {
            return formatByteCount(byteCount = this.rawBytes).let { byteCountStr ->
                if (useUnitSuffix) "$byteCountStr bytes" else byteCountStr
            }
        }
        // Double division is perfectly safe here, even for Exabytes.
        val divisor = if (useMetric) resolvedUnit.metric else resolvedUnit.binary
        val size = this.rawBytes.toDouble() / divisor.toDouble()
        val formattedSize = formatSize(size = size)

        return if (useUnitSuffix) {
            if (size != 1.0) "$formattedSize ${resolvedUnit}s" else "$formattedSize $resolvedUnit"
        } else formattedSize
    }

    override fun toString(): String = toString(unit = null, useMetric = true, useUnitSuffix = true)

    /**
     * Adds the specified number of bytes to this capacity.
     * Ensures the resulting capacity is never negative.
     */
    operator fun plus(bytes: Int): Kapacity = plus(bytes = bytes.toLong())

    /**
     * Subtracts the specified number of bytes from this capacity.
     * Ensures the resulting capacity is never negative.
     */
    operator fun minus(bytes: Int): Kapacity = minus(bytes = bytes.toLong())

    /**
     * Multiplies this capacity by a given scalar factor.
     * Ensures the resulting capacity is never negative.
     */
    operator fun times(bytes: Int): Kapacity = times(bytes = bytes.toLong())

    /**
     * Divides this capacity by a given scalar divisor.
     * Ensures the resulting capacity is never negative.
     */
    operator fun div(bytes: Int): Kapacity = div(bytes = bytes.toLong())

    /**
     * Adds the specified number of bytes to this capacity.
     * Ensures the resulting capacity is never negative.
     */
    operator fun plus(bytes: Long): Kapacity = fromBytes(
        bytes = (this.rawBytes + bytes).coerceAtLeast(minimumValue = 0L)
    )

    /**
     * Subtracts the specified number of bytes from this capacity.
     * Ensures the resulting capacity is never negative.
     */
    operator fun minus(bytes: Long): Kapacity = fromBytes(
        bytes = (this.rawBytes - bytes).coerceAtLeast(minimumValue = 0L)
    )

    /**
     * Multiplies this capacity by a given scalar factor.
     * Ensures the resulting capacity is never negative.
     */
    operator fun times(bytes: Long): Kapacity = fromBytes(
        bytes = (this.rawBytes * bytes).coerceAtLeast(minimumValue = 0L)
    )

    /**
     * Divides this capacity by a given scalar divisor.
     * Ensures the resulting capacity is never negative.
     */
    operator fun div(bytes: Long): Kapacity = fromBytes(
        bytes = (this.rawBytes / bytes).coerceAtLeast(minimumValue = 0L)
    )

    /**
     * Adds another [Kapacity] to this one.
     * Ensures the resulting capacity is never negative.
     */
    operator fun plus(other: Kapacity): Kapacity = plus(bytes = other.rawBytes)

    /**
     * Subtracts another [Kapacity] from this one.
     * Ensures the resulting capacity is never negative.
     */
    operator fun minus(other: Kapacity): Kapacity = minus(bytes = other.rawBytes)

    /**
     * Divides this [Kapacity] by another [Kapacity].
     * * Since dividing a unit by the same unit cancels it out, this returns a scalar
     * [Long] ratio representing how many times the [other] capacity fits into this one.
     */
    operator fun div(other: Kapacity): Long = this.rawBytes / other.rawBytes

    override fun compareTo(other: Kapacity): Int = this.rawBytes.compareTo(other.rawBytes)

    companion object {
        fun fromBytes(bytes: Long): Kapacity = Kapacity(rawBytes = bytes)

        fun fromBytes(bytes: ULong): Kapacity = fromBytes(bytes = bytes.toLong())
    }
}

/**
 * Converts this numerical value into a [Kapacity] instance based on the specified [unit] and standard.
 *
 * By default, this uses the Metric (SI) standard (powers of 1,000). For example, converting
 * `5L` with a unit of Megabytes will result in 5,000,000 bytes. If [useMetric] is set to `false`,
 * the Binary (IEC) standard (powers of 1,024) is used instead, resulting in 5,242,880 bytes.
 *
 * @param unit The unit of measurement this value represents (e.g., Kilobyte, Megabyte).
 * @param useMetric If `true`, calculates using the base-10 Metric standard (1 KB = 1,000 bytes).
 * If `false`, calculates using the base-2 Binary standard (1 KiB = 1,024 bytes). Defaults to `true`.
 * @return A strictly-typed [Kapacity] representing the exact total in bytes.
 */
fun Long.toKapacity(
    unit: KapacityUnit,
    useMetric: Boolean = true,
): Kapacity = Kapacity.fromBytes(
    bytes = (this * if (useMetric) unit.metric else unit.binary)
)

/**
 * Converts this double-precision fractional value into a [Kapacity] instance based on the specified [unit] and standard.
 *
 * The resulting byte count is rounded to the nearest whole byte.
 * By default, this uses the Metric (SI) standard (powers of 1,000). For example, converting
 * `1.5` with a unit of Megabytes will result in 1,500,000 bytes. If [useMetric] is set to `false`,
 * the Binary (IEC) standard (powers of 1,024) is used instead, resulting in 1,572,864 bytes.
 *
 * @param unit The unit of measurement this value represents (e.g., Kilobyte, Megabyte).
 * @param useMetric If `true`, calculates using the base-10 Metric standard (1 KB = 1,000 bytes).
 * If `false`, calculates using the base-2 Binary standard (1 KiB = 1,024 bytes). Defaults to `true`.
 * @return A strictly-typed [Kapacity] representing the exact total in bytes.
 */
fun Double.toKapacity(
    unit: KapacityUnit,
    useMetric: Boolean = true,
): Kapacity = Kapacity.fromBytes(
    bytes = (this * if (useMetric) unit.metric else unit.binary).roundToLong()
)

/**
 * Converts this single-precision fractional value into a [Kapacity] instance based on the specified [unit] and standard.
 *
 * The resulting byte count is rounded to the nearest whole byte.
 * By default, this uses the Metric (SI) standard (powers of 1,000). If [useMetric] is set to `false`,
 * the Binary (IEC) standard (powers of 1,024) is used instead.
 * * **Note on Precision:** Because [Float] relies on 24-bit precision, representing large capacities
 * (generally above 16 Megabytes) may result in a loss of exact byte-level precision. For large fractional
 * capacities, prefer using [Double].
 *
 * @param unit The unit of measurement this value represents (e.g., Kilobyte, Megabyte).
 * @param useMetric If `true`, calculates using the base-10 Metric standard (1 KB = 1,000 bytes).
 * If `false`, calculates using the base-2 Binary standard (1 KiB = 1,024 bytes). Defaults to `true`.
 * @return A strictly-typed [Kapacity] representing the calculated total in bytes.
 */
fun Float.toKapacity(
    unit: KapacityUnit,
    useMetric: Boolean = true,
): Kapacity = Kapacity.fromBytes(
    bytes = (this * if (useMetric) unit.metric else unit.binary).roundToLong()
)

/**
 * Converts this unsigned numerical value into a [Kapacity] instance based on the specified [unit] and standard.
 *
 * By default, this uses the Metric (SI) standard (powers of 1,000). For example, converting
 * `5uL` with a unit of Megabytes will result in 5,000,000 bytes. If [useMetric] is set to `false`,
 * the Binary (IEC) standard (powers of 1,024) is used instead, resulting in 5,242,880 bytes.
 *
 * @param unit The unit of measurement this value represents (e.g., Kilobyte, Megabyte).
 * @param useMetric If `true`, calculates using the base-10 Metric standard (1 KB = 1,000 bytes).
 * If `false`, calculates using the base-2 Binary standard (1 KiB = 1,024 bytes). Defaults to `true`.
 * @return A strictly-typed [Kapacity] representing the exact total in bytes.
 */
fun ULong.toKapacity(
    unit: KapacityUnit,
    useMetric: Boolean = true,
): Kapacity = Kapacity.fromBytes(
    bytes = (this * (if (useMetric) unit.metric else unit.binary).toULong())
)

/**
 * Converts this [Long] value into a [Kapacity] representing Bytes.
 */
val Long.byte: Kapacity get() = toKapacity(unit = KapacityUnit.Byte, useMetric = true)

/**
 * Converts this [Long] value into a [Kapacity] representing metric Kilobytes (KB).
 *
 * This uses the base-10 standard where 1 KB = 1,000 Bytes.
 */
val Long.kilobyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Kilobyte,
        useMetric = true
    )

/**
 * Converts this [Long] value into a [Kapacity] representing metric Megabytes (MB).
 *
 * This uses the base-10 standard where 1 MB = 1,000,000 Bytes.
 */
val Long.megabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Megabyte,
        useMetric = true
    )

/**
 * Converts this [Long] value into a [Kapacity] representing metric Gigabytes (GB).
 *
 * This uses the base-10 standard where 1 GB = 1,000,000,000 Bytes.
 */
val Long.gigabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Gigabyte,
        useMetric = true
    )

/**
 * Converts this [Long] value into a [Kapacity] representing metric Terabytes (TB).
 *
 * This uses the base-10 standard where 1 TB = 1,000,000,000,000 Bytes.
 */
val Long.terabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Terabyte,
        useMetric = true
    )

/**
 * Converts this [Long] value into a [Kapacity] representing metric Petabytes (PB).
 *
 * This uses the base-10 standard where 1 PB = 1,000,000,000,000,000 Bytes.
 */
val Long.petabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Petabyte,
        useMetric = true
    )

/**
 * Converts this [Long] value into a [Kapacity] representing metric Exabytes (EB).
 *
 * This uses the base-10 standard where 1 EB = 1,000,000,000,000,000,000 Bytes.
 */
val Long.exabyte: Kapacity get() = toKapacity(unit = KapacityUnit.Exabyte, useMetric = true)

/**
 * Converts this [Long] value into a [Kapacity] representing Bytes.
 *
 * Evaluates the same as [byte], but explicitly utilizes the binary standard path.
 */
val Long.binaryByte: Kapacity get() = toKapacity(unit = KapacityUnit.Byte, useMetric = false)

/**
 * Converts this [Long] value into a [Kapacity] representing binary Kibibytes (KiB).
 *
 * This uses the base-2 IEC standard where 1 KiB = 1,024 Bytes.
 */
val Long.binaryKilobyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Kilobyte,
        useMetric = false
    )

/**
 * Converts this [Long] value into a [Kapacity] representing binary Mebibytes (MiB).
 *
 * This uses the base-2 IEC standard where 1 MiB = 1,048,576 Bytes.
 */
val Long.binaryMegabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Megabyte,
        useMetric = false
    )

/**
 * Converts this [Long] value into a [Kapacity] representing binary Gibibytes (GiB).
 *
 * This uses the base-2 IEC standard where 1 GiB = 1,073,741,824 Bytes.
 */
val Long.binaryGigabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Gigabyte,
        useMetric = false
    )

/**
 * Converts this [Long] value into a [Kapacity] representing binary Tebibytes (TiB).
 *
 * This uses the base-2 IEC standard where 1 TiB = 1,099,511,627,776 Bytes.
 */
val Long.binaryTerabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Terabyte,
        useMetric = false
    )

/**
 * Converts this [Long] value into a [Kapacity] representing binary Pebibytes (PiB).
 *
 * This uses the base-2 IEC standard where 1 PiB = 1,125,899,906,842,624 Bytes.
 */
val Long.binaryPetabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Petabyte,
        useMetric = false
    )

/**
 * Converts this [Long] value into a [Kapacity] representing binary Exbibytes (EiB).
 *
 * This uses the base-2 IEC standard where 1 EiB = 1,152,921,504,606,846,976 Bytes.
 */
val Long.binaryExabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Exabyte,
        useMetric = false
    )

/**
 * Converts this [ULong] value into a [Kapacity] representing Bytes.
 */
val ULong.byte: Kapacity get() = toKapacity(unit = KapacityUnit.Byte, useMetric = true)

/**
 * Converts this [ULong] value into a [Kapacity] representing metric Kilobytes (KB).
 *
 * This uses the base-10 standard where 1 KB = 1,000 Bytes.
 */
val ULong.kilobyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Kilobyte,
        useMetric = true
    )

/**
 * Converts this [ULong] value into a [Kapacity] representing metric Megabytes (MB).
 *
 * This uses the base-10 standard where 1 MB = 1,000,000 Bytes.
 */
val ULong.megabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Megabyte,
        useMetric = true
    )

/**
 * Converts this [ULong] value into a [Kapacity] representing metric Gigabytes (GB).
 *
 * This uses the base-10 standard where 1 GB = 1,000,000,000 Bytes.
 */
val ULong.gigabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Gigabyte,
        useMetric = true
    )

/**
 * Converts this [ULong] value into a [Kapacity] representing metric Terabytes (TB).
 *
 * This uses the base-10 standard where 1 TB = 1,000,000,000,000 Bytes.
 */
val ULong.terabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Terabyte,
        useMetric = true
    )

/**
 * Converts this [ULong] value into a [Kapacity] representing metric Petabytes (PB).
 *
 * This uses the base-10 standard where 1 PB = 1,000,000,000,000,000 Bytes.
 */
val ULong.petabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Petabyte,
        useMetric = true
    )

/**
 * Converts this [ULong] value into a [Kapacity] representing metric Exabytes (EB).
 *
 * This uses the base-10 standard where 1 EB = 1,000,000,000,000,000,000 Bytes.
 */
val ULong.exabyte: Kapacity get() = toKapacity(unit = KapacityUnit.Exabyte, useMetric = true)

/**
 * Converts this [ULong] value into a [Kapacity] representing Bytes.
 *
 * Evaluates the same as [byte], but explicitly utilizes the binary standard path.
 */
val ULong.binaryByte: Kapacity get() = toKapacity(unit = KapacityUnit.Byte, useMetric = false)

/**
 * Converts this [ULong] value into a [Kapacity] representing binary Kibibytes (KiB).
 *
 * This uses the base-2 IEC standard where 1 KiB = 1,024 Bytes.
 */
val ULong.binaryKilobyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Kilobyte,
        useMetric = false
    )

/**
 * Converts this [ULong] value into a [Kapacity] representing binary Mebibytes (MiB).
 *
 * This uses the base-2 IEC standard where 1 MiB = 1,048,576 Bytes.
 */
val ULong.binaryMegabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Megabyte,
        useMetric = false
    )

/**
 * Converts this [ULong] value into a [Kapacity] representing binary Gibibytes (GiB).
 *
 * This uses the base-2 IEC standard where 1 GiB = 1,073,741,824 Bytes.
 */
val ULong.binaryGigabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Gigabyte,
        useMetric = false
    )

/**
 * Converts this [ULong] value into a [Kapacity] representing binary Tebibytes (TiB).
 *
 * This uses the base-2 IEC standard where 1 TiB = 1,099,511,627,776 Bytes.
 */
val ULong.binaryTerabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Terabyte,
        useMetric = false
    )

/**
 * Converts this [ULong] value into a [Kapacity] representing binary Pebibytes (PiB).
 *
 * This uses the base-2 IEC standard where 1 PiB = 1,125,899,906,842,624 Bytes.
 */
val ULong.binaryPetabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Petabyte,
        useMetric = false
    )

/**
 * Converts this [ULong] value into a [Kapacity] representing binary Exbibytes (EiB).
 *
 * This uses the base-2 IEC standard where 1 EiB = 1,152,921,504,606,846,976 Bytes.
 */
val ULong.binaryExabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Exabyte,
        useMetric = false
    )

/**
 * Converts this [Int] value into a [Kapacity] representing Bytes.
 */
val Int.byte: Kapacity
    get() = toLong().toKapacity(
        unit = KapacityUnit.Byte,
        useMetric = true
    )

/**
 * Converts this [Int] value into a [Kapacity] representing metric Kilobytes (KB).
 *
 * This uses the base-10 standard where 1 KB = 1,000 Bytes.
 */
val Int.kilobyte: Kapacity
    get() = toLong().toKapacity(
        unit = KapacityUnit.Kilobyte,
        useMetric = true
    )

/**
 * Converts this [Int] value into a [Kapacity] representing metric Megabytes (MB).
 *
 * This uses the base-10 standard where 1 MB = 1,000,000 Bytes.
 */
val Int.megabyte: Kapacity
    get() = toLong().toKapacity(
        unit = KapacityUnit.Megabyte,
        useMetric = true
    )

/**
 * Converts this [Int] value into a [Kapacity] representing metric Gigabytes (GB).
 *
 * This uses the base-10 standard where 1 GB = 1,000,000,000 Bytes.
 */
val Int.gigabyte: Kapacity
    get() = toLong().toKapacity(
        unit = KapacityUnit.Gigabyte,
        useMetric = true
    )

/**
 * Converts this [Int] value into a [Kapacity] representing metric Terabytes (TB).
 *
 * This uses the base-10 standard where 1 TB = 1,000,000,000,000 Bytes.
 */
val Int.terabyte: Kapacity
    get() = toLong().toKapacity(
        unit = KapacityUnit.Terabyte,
        useMetric = true
    )

/**
 * Converts this [Int] value into a [Kapacity] representing metric Petabytes (PB).
 *
 * This uses the base-10 standard where 1 PB = 1,000,000,000,000,000 Bytes.
 */
val Int.petabyte: Kapacity
    get() = toLong().toKapacity(
        unit = KapacityUnit.Petabyte,
        useMetric = true
    )

/**
 * Converts this [Int] value into a [Kapacity] representing metric Exabytes (EB).
 *
 * This uses the base-10 standard where 1 EB = 1,000,000,000,000,000,000 Bytes.
 * * **Warning:** Due to the 64-bit limits of [Long], converting values of 8 or higher
 * will cause an integer overflow, resulting in a negative byte count.
 */
val Int.exabyte: Kapacity
    get() = toLong().toKapacity(
        unit = KapacityUnit.Exabyte,
        useMetric = true
    )

/**
 * Converts this [Int] value into a [Kapacity] representing Bytes.
 *
 * Evaluates the same as [byte], but explicitly utilizes the binary standard path.
 */
val Int.binaryByte: Kapacity
    get() = toLong().toKapacity(
        unit = KapacityUnit.Byte,
        useMetric = false
    )

/**
 * Converts this [Int] value into a [Kapacity] representing binary Kibibytes (KiB).
 *
 * This uses the base-2 IEC standard where 1 KiB = 1,024 Bytes.
 */
val Int.binaryKilobyte: Kapacity
    get() = toLong().toKapacity(
        unit = KapacityUnit.Kilobyte,
        useMetric = false
    )

/**
 * Converts this [Int] value into a [Kapacity] representing binary Mebibytes (MiB).
 *
 * This uses the base-2 IEC standard where 1 MiB = 1,048,576 Bytes.
 */
val Int.binaryMegabyte: Kapacity
    get() = toLong().toKapacity(
        unit = KapacityUnit.Megabyte,
        useMetric = false
    )

/**
 * Converts this [Int] value into a [Kapacity] representing binary Gibibytes (GiB).
 *
 * This uses the base-2 IEC standard where 1 GiB = 1,073,741,824 Bytes.
 */
val Int.binaryGigabyte: Kapacity
    get() = toLong().toKapacity(
        unit = KapacityUnit.Gigabyte,
        useMetric = false
    )

/**
 * Converts this [Int] value into a [Kapacity] representing binary Tebibytes (TiB).
 *
 * This uses the base-2 IEC standard where 1 TiB = 1,099,511,627,776 Bytes.
 */
val Int.binaryTerabyte: Kapacity
    get() = toLong().toKapacity(
        unit = KapacityUnit.Terabyte,
        useMetric = false
    )

/**
 * Converts this [Int] value into a [Kapacity] representing binary Pebibytes (PiB).
 *
 * This uses the base-2 IEC standard where 1 PiB = 1,125,899,906,842,624 Bytes.
 */
val Int.binaryPetabyte: Kapacity
    get() = toLong().toKapacity(
        unit = KapacityUnit.Petabyte,
        useMetric = false
    )

/**
 * Converts this [Int] value into a [Kapacity] representing binary Exbibytes (EiB).
 *
 * This uses the base-2 IEC standard where 1 EiB = 1,152,921,504,606,846,976 Bytes.
 *
 * **Warning:** Due to the 64-bit limits of [Long], converting values of 8 or higher
 * will cause an integer overflow, resulting in a negative byte count.
 */
val Int.binaryExabyte: Kapacity
    get() = toLong().toKapacity(
        unit = KapacityUnit.Exabyte,
        useMetric = false
    )

/**
 * Converts this [Double] value into a [Kapacity] representing Bytes.
 */
val Double.byte: Kapacity get() = toKapacity(unit = KapacityUnit.Byte, useMetric = true)

/**
 * Converts this [Double] value into a [Kapacity] representing metric Kilobytes (KB).
 *
 * This uses the base-10 standard where 1 KB = 1,000 Bytes.
 */
val Double.kilobyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Kilobyte,
        useMetric = true
    )

/**
 * Converts this [Double] value into a [Kapacity] representing metric Megabytes (MB).
 *
 * This uses the base-10 standard where 1 MB = 1,000,000 Bytes.
 */
val Double.megabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Megabyte,
        useMetric = true
    )

/**
 * Converts this [Double] value into a [Kapacity] representing metric Gigabytes (GB).
 *
 * This uses the base-10 standard where 1 GB = 1,000,000,000 Bytes.
 */
val Double.gigabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Gigabyte,
        useMetric = true
    )

/**
 * Converts this [Double] value into a [Kapacity] representing metric Terabytes (TB).
 *
 * This uses the base-10 standard where 1 TB = 1,000,000,000,000 Bytes.
 */
val Double.terabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Terabyte,
        useMetric = true
    )

/**
 * Converts this [Double] value into a [Kapacity] representing metric Petabytes (PB).
 *
 * This uses the base-10 standard where 1 PB = 1,000,000,000,000,000 Bytes.
 */
val Double.petabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Petabyte,
        useMetric = true
    )

/**
 * Converts this [Double] value into a [Kapacity] representing metric Exabytes (EB).
 *
 * This uses the base-10 standard where 1 EB = 1,000,000,000,000,000,000 Bytes.
 */
val Double.exabyte: Kapacity get() = toKapacity(unit = KapacityUnit.Exabyte, useMetric = true)

/**
 * Converts this [Double] value into a [Kapacity] representing Bytes.
 *
 * Evaluates the same as [byte], but explicitly utilizes the binary standard path.
 */
val Double.binaryByte: Kapacity get() = toKapacity(unit = KapacityUnit.Byte, useMetric = false)

/**
 * Converts this [Double] value into a [Kapacity] representing binary Kibibytes (KiB).
 *
 * This uses the base-2 IEC standard where 1 KiB = 1,024 Bytes.
 */
val Double.binaryKilobyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Kilobyte,
        useMetric = false
    )

/**
 * Converts this [Double] value into a [Kapacity] representing binary Mebibytes (MiB).
 *
 * This uses the base-2 IEC standard where 1 MiB = 1,048,576 Bytes.
 */
val Double.binaryMegabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Megabyte,
        useMetric = false
    )

/**
 * Converts this [Double] value into a [Kapacity] representing binary Gibibytes (GiB).
 *
 * This uses the base-2 IEC standard where 1 GiB = 1,073,741,824 Bytes.
 */
val Double.binaryGigabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Gigabyte,
        useMetric = false
    )

/**
 * Converts this [Double] value into a [Kapacity] representing binary Tebibytes (TiB).
 *
 * This uses the base-2 IEC standard where 1 TiB = 1,099,511,627,776 Bytes.
 */
val Double.binaryTerabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Terabyte,
        useMetric = false
    )

/**
 * Converts this [Double] value into a [Kapacity] representing binary Pebibytes (PiB).
 *
 * This uses the base-2 IEC standard where 1 PiB = 1,125,899,906,842,624 Bytes.
 */
val Double.binaryPetabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Petabyte,
        useMetric = false
    )

/**
 * Converts this [Double] value into a [Kapacity] representing binary Exbibytes (EiB).
 *
 * This uses the base-2 IEC standard where 1 EiB = 1,152,921,504,606,846,976 Bytes.
 */
val Double.binaryExabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Exabyte,
        useMetric = false
    )

/**
 * Converts this [Float] value into a [Kapacity] representing Bytes.
 */
val Float.byte: Kapacity get() = toKapacity(unit = KapacityUnit.Byte, useMetric = true)

/**
 * Converts this [Float] value into a [Kapacity] representing metric Kilobytes (KB).
 *
 * This uses the base-10 standard where 1 KB = 1,000 Bytes.
 */
val Float.kilobyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Kilobyte,
        useMetric = true
    )

/**
 * Converts this [Float] value into a [Kapacity] representing metric Megabytes (MB).
 *
 * This uses the base-10 standard where 1 MB = 1,000,000 Bytes.
 */
val Float.megabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Megabyte,
        useMetric = true
    )

/**
 * Converts this [Float] value into a [Kapacity] representing metric Gigabytes (GB).
 *
 * This uses the base-10 standard where 1 GB = 1,000,000,000 Bytes.
 */
val Float.gigabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Gigabyte,
        useMetric = true
    )

/**
 * Converts this [Float] value into a [Kapacity] representing metric Terabytes (TB).
 *
 * This uses the base-10 standard where 1 TB = 1,000,000,000,000 Bytes.
 */
val Float.terabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Terabyte,
        useMetric = true
    )

/**
 * Converts this [Float] value into a [Kapacity] representing metric Petabytes (PB).
 *
 * This uses the base-10 standard where 1 PB = 1,000,000,000,000,000 Bytes.
 */
val Float.petabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Petabyte,
        useMetric = true
    )

/**
 * Converts this [Float] value into a [Kapacity] representing metric Exabytes (EB).
 *
 * This uses the base-10 standard where 1 EB = 1,000,000,000,000,000,000 Bytes.
 */
val Float.exabyte: Kapacity get() = toKapacity(unit = KapacityUnit.Exabyte, useMetric = true)

/**
 * Converts this [Float] value into a [Kapacity] representing Bytes.
 *
 * Evaluates the same as [byte], but explicitly utilizes the binary standard path.
 */
val Float.binaryByte: Kapacity get() = toKapacity(unit = KapacityUnit.Byte, useMetric = false)

/**
 * Converts this [Float] value into a [Kapacity] representing binary Kibibytes (KiB).
 *
 * This uses the base-2 IEC standard where 1 KiB = 1,024 Bytes.
 */
val Float.binaryKilobyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Kilobyte,
        useMetric = false
    )

/**
 * Converts this [Float] value into a [Kapacity] representing binary Mebibytes (MiB).
 *
 * This uses the base-2 IEC standard where 1 MiB = 1,048,576 Bytes.
 */
val Float.binaryMegabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Megabyte,
        useMetric = false
    )

/**
 * Converts this [Float] value into a [Kapacity] representing binary Gibibytes (GiB).
 *
 * This uses the base-2 IEC standard where 1 GiB = 1,073,741,824 Bytes.
 */
val Float.binaryGigabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Gigabyte,
        useMetric = false
    )

/**
 * Converts this [Float] value into a [Kapacity] representing binary Tebibytes (TiB).
 *
 * This uses the base-2 IEC standard where 1 TiB = 1,099,511,627,776 Bytes.
 */
val Float.binaryTerabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Terabyte,
        useMetric = false
    )

/**
 * Converts this [Float] value into a [Kapacity] representing binary Pebibytes (PiB).
 *
 * This uses the base-2 IEC standard where 1 PiB = 1,125,899,906,842,624 Bytes.
 */
val Float.binaryPetabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Petabyte,
        useMetric = false
    )

/**
 * Converts this [Float] value into a [Kapacity] representing binary Exbibytes (EiB).
 *
 * This uses the base-2 IEC standard where 1 EiB = 1,152,921,504,606,846,976 Bytes.
 */
val Float.binaryExabyte: Kapacity
    get() = toKapacity(
        unit = KapacityUnit.Exabyte,
        useMetric = false
    )

/**
 * Represents standard data capacity units, providing multiplier values for both
 * the metric (base-10 / SI) and binary (base-2 / IEC) measurement systems.
 *
 * @property metric The multiplier for the base-10 standard (powers of 1,000).
 * @property binary The multiplier for the base-2 standard (powers of 1,024).
 */
enum class KapacityUnit(internal val metric: Long, internal val binary: Long) {
    Byte(metric = 1, binary = 1), // 1000^0 or 1024^0
    Kilobyte(metric = 1_000, binary = 1_024), // 1000^1 or 1024^1
    Megabyte(metric = 1_000_000, binary = 1_048_576), // 1000^2 or 1024^2
    Gigabyte(metric = 1_000_000_000, binary = 1_073_741_824), // 1000^3 or 1024^3
    Terabyte(metric = 1_000_000_000_000, binary = 1_099_511_627_776), // 1000^4 or 1024^4
    Petabyte(metric = 1_000_000_000_000_000, binary = 1_125_899_906_842_624),  // 1000^5 or 1024^5
    Exabyte(
        metric = 1_000_000_000_000_000_000,
        binary = 1_152_921_504_606_846_976
    ),  // 1000^6 or 1024^6
}

private val Kapacity.rawBytesCoercedToIntRange: Int
    // Maximum size for an array is limited to the max value of `Int` (≈ 2.147 Gigabytes)
    get() = this.rawBytes.coerceIn(minimumValue = 0, maximumValue = Int.MAX_VALUE.toLong()).toInt()

/**
 * Allocates a new boxed [Array] of [Byte] with a size equal to this capacity.
 *
 * **Warning on Truncation:** Because Kotlin arrays are strictly indexed by [Int], the maximum
 * allowed size is [Int.MAX_VALUE] (approximately 2.14 GB). If this capacity exceeds
 * that limit, the resulting array size will be silently truncated to [Int.MAX_VALUE].
 *
 * @param init A function used to compute the initial value of each array element based on its index.
 * @return A new boxed [Array] of [Byte].
 */
fun Kapacity.toArray(init: (Int) -> Byte = { 0 }): Array<Byte> =
    Array(size = this.rawBytesCoercedToIntRange, init = init)

/**
 * Allocates a new boxed [Array] of [UByte] with a size equal to this capacity.
 *
 * **Warning on Truncation:** Because Kotlin arrays are strictly indexed by [Int], the maximum
 * allowed size is [Int.MAX_VALUE] (approximately 2.14 GB). If this capacity exceeds
 * that limit, the resulting array size will be silently truncated to [Int.MAX_VALUE].
 *
 * @param init A function used to compute the initial value of each array element based on its index.
 * @return A new boxed [Array] of [UByte].
 */
fun Kapacity.toArrayUnsigned(init: (Int) -> UByte = { 0U }): Array<UByte> =
    Array(size = this.rawBytesCoercedToIntRange, init = init)

/**
 * Allocates a new primitive [ByteArray] with a size equal to this capacity, using the
 * provided [init] function to populate the elements.
 *
 * **Warning on Truncation:** Because Kotlin arrays are strictly indexed by [Int], the maximum
 * allowed size is [Int.MAX_VALUE] (approximately 2.14 GB). If this capacity exceeds
 * that limit, the resulting array size will be silently truncated to [Int.MAX_VALUE].
 *
 * @param init A function used to compute the initial value of each array element based on its index.
 * @return A new primitive [ByteArray].
 */
fun Kapacity.toByteArray(
    init: (Int) -> Byte = { 0 },
): ByteArray = ByteArray(size = this.rawBytesCoercedToIntRange, init = init)

/**
 * Allocates a new primitive [ByteArray] with a size equal to this capacity.
 * * This is the most performant way to allocate a buffer, as it utilizes native memory
 * allocation where all elements are instantly initialized to `0`.
 *
 * **Warning on Truncation:** Because Kotlin arrays are strictly indexed by [Int], the maximum
 * allowed size is [Int.MAX_VALUE] (approximately 2.14 GB). If this capacity exceeds
 * that limit, the resulting array size will be silently truncated to [Int.MAX_VALUE].
 *
 * @return A new primitive [ByteArray] filled with zeros.
 */
fun Kapacity.toByteArray(): ByteArray = ByteArray(size = this.rawBytesCoercedToIntRange)

/**
 * Allocates a new primitive [UByteArray] with a size equal to this capacity.
 * * This is the most performant way to allocate a buffer, as it utilizes native memory
 * allocation where all elements are instantly initialized to `0`.
 *
 * **Warning on Truncation:** Because Kotlin arrays are strictly indexed by [Int], the maximum
 * allowed size is [Int.MAX_VALUE] (approximately 2.14 GB). If this capacity exceeds
 * that limit, the resulting array size will be silently truncated to [Int.MAX_VALUE].
 *
 * @return A new primitive [UByteArray] filled with zeros.
 */
fun Kapacity.toUByteArray(): UByteArray = UByteArray(size = this.rawBytesCoercedToIntRange)

/**
 * Returns the [Kapacity] of this boxed [Array], calculated directly from its size.
 *
 * Each [Byte] element in the array represents exactly 1 byte of capacity.
 */
val Array<Byte>.kapacity: Kapacity get() = this.size.byte

/**
 * Returns the [Kapacity] of this primitive [ByteArray], calculated directly from its size.
 *
 * Each byte in the array represents exactly 1 byte of capacity.
 */
val ByteArray.kapacity: Kapacity get() = this.size.byte

/**
 * Returns the [Kapacity] of this boxed [Array], calculated directly from its size.
 *
 * Each [UByte] element in the array represents exactly 1 byte of capacity.
 */
val Array<UByte>.kapacity: Kapacity get() = this.size.byte

/**
 * Returns the [Kapacity] of this unsigned [UByteArray], calculated directly from its size.
 *
 * Each unsigned byte in the array represents exactly 1 byte of capacity.
 */
@OptIn(ExperimentalUnsignedTypes::class)
val UByteArray.kapacity: Kapacity get() = this.size.byte

/**
 * Returns the [Kapacity] of this [Collection] of bytes, calculated directly from its size.
 *
 * Each [Byte] element in the collection represents exactly 1 byte of capacity.
 */
val Collection<Byte>.kapacity: Kapacity get() = this.size.byte
