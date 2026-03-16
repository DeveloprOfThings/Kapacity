package io.github.developrofthings.kapacity.io

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class FileExtensionTest {

    @Test
    fun testFileKapacityExtension() {
        // Create a temp file and write exactly 15 bytes to it
        val tempFile = File.createTempFile("kapacity_test", ".txt")
        tempFile.writeBytes(ByteArray(15) { 0 })
        tempFile.deleteOnExit()

        assertEquals(15L, tempFile.kapacity.rawBytes)
    }

    @Test
    fun testNonExistentFileReturnsZero() {
        val fakeFile = File("does_not_exist_12345.txt")
        assertEquals(0L, fakeFile.kapacity.rawBytes)
    }

    @Test
    fun testPathKapacityExtension() {
        // Create a temp path and write exactly 1024 bytes (1 KB) to it
        val tempPath = Files.createTempFile("kapacity_test_path", ".txt")
        Files.write(tempPath, ByteArray(1024) { 0 })
        tempPath.toFile().deleteOnExit()

        assertEquals(1024L, tempPath.kapacity.rawBytes)
    }
}