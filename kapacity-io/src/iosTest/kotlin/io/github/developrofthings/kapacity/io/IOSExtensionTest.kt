@file:OptIn(BetaInteropApi::class)

package io.github.developrofthings.kapacity.io

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.create
import platform.Foundation.writeToFile
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalForeignApi::class)
class IOSExtensionTest {

    @Test
    fun testNSDataKapacityExtension() {
        // Create a byte array of exactly 42 bytes and wrap it in NSData
        val byteArray = ByteArray(42) { 0 }
        val nsData = byteArray.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = byteArray.size.toULong())
        }

        assertEquals(42L, nsData.kapacity.rawBytes)
    }

    @Test
    fun testNSURLKapacityExtension() {
        // 1. Create exactly 100 bytes of data
        val byteArray = ByteArray(100) { 0 }
        val nsData = byteArray.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = byteArray.size.toULong())
        }

        // 2. Write it to the iOS temporary directory
        val tempDir = NSTemporaryDirectory()
        val filePath = "$tempDir/kapacity_test_file.dat"
        nsData.writeToFile(filePath, atomically = true)

        // 3. Create an NSURL and test the extension
        val url = NSURL.fileURLWithPath(filePath)
        assertEquals(100L, url.kapacity.rawBytes)

        // Clean up
        NSFileManager.defaultManager.removeItemAtPath(filePath, null)
    }

    @Test
    fun testNonExistentNSURLReturnsZero() {
        val fakeUrl = NSURL.fileURLWithPath("/tmp/does_not_exist_123.dat")
        assertEquals(0L, fakeUrl.kapacity.rawBytes)
    }
}