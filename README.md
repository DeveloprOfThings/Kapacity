# Kapacity 📦

[![Maven Central](https://img.shields.io/maven-central/v/io.github.developrofthings/kapacity.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.developrofthings/kapacity)
[![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-Multiplatform-blue.svg)](https://kotlinlang.org/docs/multiplatform.html)
[![Targets](https://img.shields.io/badge/Targets-Android%20%7C%20iOS-lightgrey.svg)](#)

**Kapacity** is a lightweight, zero-allocation Kotlin Multiplatform library for Android and iOS designed to make handling digital data sizes safe, strictly typed, and intuitive.

Stop guessing if a variable named `fileSize` is in Bytes, Kilobytes, or Megabytes. Stop wondering if "MB" means 1,000 bytes or 1,024 bytes. Kapacity brings strict typing, accurate dimensional math, and localized UI formatting directly to your codebase.

## ✨ Key Features
* **Zero Allocation:** Built entirely on `@JvmInline value class` for zero garbage collection overhead at runtime.
* **Strict Standards:** Explicit support for both Metric/SI (powers of 1,000) and Binary/IEC (powers of 1,024) capacities.
* **Safe Dimensional Math:** Add, subtract, multiply, and divide capacities safely. Dividing two capacities correctly yields a scalar ratio. Zero-coercion prevents negative byte bounds.
* **Memory Safe:** Safely allocate `ByteArray` and `UByteArray` buffers with built-in protections against `Int.MAX_VALUE` overflows.
* **Platform Native:** Read sizes instantly from Android `File`/`Path` and iOS `NSURL`/`NSData`.
* **Thread-Safe UI Formatting:** Localized, multiplatform string formatting using thread-safe `DecimalFormat` (Android) and `NSNumberFormatter` (iOS).

## 🚀 Installation

Kapacity is published to Maven Central. Add the following to your `build.gradle.kts`:

```kotlin
dependencies {
    // Core library
    implementation("io.github.developrofthings:kapacity:0.9.9-beta06")
    
    // Optional: kotlinx-io extensions (Buffer and ByteString interoperability)
    implementation("io.github.developrofthings:kapacity-io:0.9.9-beta06")
}
```

## 📖 Usage

### 1. Creating Capacities
Kapacity provides fluent extension properties for `Long`, `Int`, `Double`, `Float`, and their unsigned counterparts.
By default, standard properties (`.kilobyte`, `.megabyte`) use the **Metric (base-10)** standard. If you need exact memory measurements, use the **Binary (base-2)** equivalents.

``` kotlin
import io.github.developrofthings.kapacity.*

// Metric (1 MB = 1,000,000 Bytes)
val downloadSize = 5.megabyte
val fractionalSize = 1.5.gigabyte

// Binary (1 MiB = 1,048,576 Bytes)
val ramCache = 512.mebibyte
val bufferSize = 16.kibibyte

// Unsigned support
val rawBytes = 1024uL.byte

// Measure arrays and collections instantly
val myBuffer = ByteArray(2048)
val bufferCap = myBuffer.kapacity // 2.048 KB
```

### 2. Math & Operations
Because Kapacity is strictly typed, you can perform math operations directly on the objects without worrying about underlying byte conversions.

```kotlin
val total = 1.gigabyte
val downloaded = 250.megabyte

// Standard math operators
val remaining = total - downloaded
val twiceAsLarge = total * 2

// Dimensional Division: Dividing a capacity by a capacity returns a Long ratio!
val chunkCount = 10.megabyte / 2.megabyte // Returns 5L

// Zero-coercion prevents negative data bounds natively
val overSubtracted = 10.megabyte - 50.megabyte // Returns 0 Bytes
```

### 3. Safe Memory Allocation
Need to allocate a buffer based on a capacity? Kapacity provides safe builders that automatically truncate allocations to `Int.MAX_VALUE` (≈ 2.14 GB) to prevent fatal `OutOfMemoryError` or `IllegalArgumentException` crashes on the JVM.

```kotlin
val targetSize = 16.kilobyte

// Allocates an exactly sized, zero-initialized ByteArray or UByteArray
val primitiveBuffer = targetSize.toByteArray()
val unsignedBuffer = targetSize.toUByteArray()

// Safely caps out at Int.MAX_VALUE under the hood
val massiveBuffer = 5.gigabyte.toByteArray() 
```

### 4. File System Integration (Android & iOS)
Kapacity includes platform-specific extensions to instantly measure file sizes directly from the disk or memory.

**On Android:**
```kotlin
val myFile = File("/path/to/video.mp4")
val capacity = myFile.kapacity

val myPath = Path.of("/path/to/document.pdf")
val pathCapacity = myPath.kapacity
```

**On iOS:**
``` kotlin
val myUrl = NSURL.fileURLWithPath("/path/to/video.mp4")
val capacity = myUrl.kapacity // Uses NSFileManager under the hood

val buffer = NSData.dataWithBytes(...)
val bufferCapacity = buffer.kapacity
```

### 5. Human-Readable UI Formatting
Kapacity handles localized formatting out of the box. The `toString()` function safely formats the underlying bytes into a readable string (e.g., "1.5 MB"), utilizing localized decimal separators.

``` kotlin
val size = 1500.kilobyte

// Automatically scales the unit
println(size.toString()) // "1.5 MB"

// Force the binary standard format
println(size.toString(useMetric = false)) // "1.43 MiB"

// Force a specific unit
println(size.toString(unit = KapacityUnit.Kilobyte)) // "1,500 KB"

// Hide the unit suffix for clean UI tables
println(size.toString(useUnitSuffix = false)) // "1.5"
```

## 🛠️ Contributing & Feedback
This library is currently in Beta. If you run into any target-specific quirks, precision issues, or have feature requests, please open an issue!

## ⚖️ License
[Insert your license here, e.g., MIT License]