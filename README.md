# Kapacity

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-blue.svg)](https://kotlinlang.org/docs/multiplatform.html)

Stop guessing if "MB" means 1,000 bytes or 1,024 bytes in your codebase. **Kapacity** is a lightweight, zero-allocation Kotlin Multiplatform library designed to make handling digital data sizes safe, strictly typed, and intuitive.

It provides fluent extensions for both Metric (base-10 / SI) and Binary (base-2 / IEC) standards, complete with safe math operators, byte array allocations, and easy string formatting.

## Features

* **Zero Allocation:** Built on `@JvmInline value class`, meaning instances are represented as raw `Long` primitives at runtime. Zero garbage collection overhead.
* **Metric vs. Binary Strictness:** Explicit support for both Metric (`5.megabyte` = 5,000,000 bytes) and Binary (`5.binaryMegabyte` = 5,242,880 bytes) standards.
* **Safe Dimensional Math:** Prevents "negative byte" bugs with built-in zero-coercion, and enforces correct dimensional analysis (e.g., dividing two capacities returns a scalar ratio, not a capacity).
* **Array & Buffer Allocation:** Instantly allocate `ByteArray` or `Array<Byte>` buffers directly from a capacity, with safe coercion to JVM limits.
* **Multiplatform Ready:** Designed for KMP, with `expect`/`actual` hooks for native string formatting across JVM, iOS, JS, and more.
* **`kotlinx-io` Integration:** Optional extension module for seamless interop with `Buffer` and `ByteString`.

## Installation

Add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    // Core library
    implementation("io.github.developrofthings:kapacity:0.9.9-beta02")
    
    // Optional: kotlinx-io extensions
    implementation("io.github.developrofthings:kapacity-io:0.9.9-beta01")
}
```
*(Note: Update the group ID and version as necessary once published)*

## Quick Start

### Creating Capacities
Kapacity provides fluent extension properties on `Int` and `Long` primitives.

``` kotlin
import io.github.developrofthings.kapacity.*

// Metric (Base-10 / Powers of 1,000)
val fileSize = 5.megabyte
val hardDrive = 2.terabyte

// Binary (Base-2 / Powers of 1,024)
val ramChunk = 512.binaryMegabyte 
val maxMemory = 16.binaryGigabyte
```

### Safe Math Operators
Perform calculations without worrying about negative bounds or invalid unit states.

```kotlin
val baseSize = 10.megabyte

// Add / Subtract capacities safely (coerced to 0 minimum)
val total = baseSize + 5.megabyte
val remaining = baseSize - 15.megabyte // Returns 0 Bytes

// Multiply / Divide by scalar numbers
val tripled = baseSize * 3
val half = baseSize / 2

// Divide a capacity by a capacity to get a scalar ratio (Long)
val chunksFit = 10.megabyte / 2.megabyte // Returns 5L
```

### Formatting for UI
Easily convert capacities into human-readable strings. The library dynamically resolves the most appropriate unit size based on your metric/binary preference.

```kotlin
val size = 1_500_000.byte
println(size.toString(useMetric = true))  // "1.5 MB"
println(size.toString(useMetric = false)) // "1.43 MiB"

// Or force a specific unit
println(size.toString(unit = KapacityUnit.Kilobyte)) // "1500 KB"
```

### Allocating Arrays
You can easily allocate arrays or primitive byte arrays directly from a capacity.

*Note: Because Kotlin arrays are strictly indexed by `Int`, the maximum allowed size is `Int.MAX_VALUE` (≈ 2.14 GB). If your capacity exceeds that limit, the resulting array size will be silently truncated to prevent runtime crashes.*

```kotlin
// Fast native allocation filled with zeros
val buffer: ByteArray = 512.kilobyte.toByteArray()

// Allocation with custom initialization
val customBuffer: ByteArray = 10.megabyte.toByteArray { index -> 
    (index % 256).toByte() 
}
```

### Measuring Existing Data
Quickly get the `Kapacity` of standard library arrays and collections, or import the `kapacity-io` module for `kotlinx-io` support.

``` kotlin
// Standard Library
val rawData = ByteArray(1024)
val payloadSize: Kapacity = rawData.kapacity

// kotlinx-io module
import io.github.developrofthings.kapacity.io.*

val ioBuffer = Buffer().apply { writeString("Hello World") }
val bufferSize: Kapacity = ioBuffer.kapacity
```

## License

This project is licensed under the MIT License.