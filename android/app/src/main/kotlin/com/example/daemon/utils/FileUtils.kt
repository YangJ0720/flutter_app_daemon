package com.example.daemon.utils

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class FileUtils {

    companion object {

        fun getPath(context: Context): String {
            val fileDir = File(context.cacheDir, "media")
            if (!fileDir.exists()) {
                fileDir.mkdirs()
            }
            return File(fileDir, "media.ogg").absolutePath
        }

        fun writeFileToSDCard(context: Context, input: InputStream) {
            val fileDir = File(context.cacheDir, "media")
            if (!fileDir.exists()) {
                fileDir.mkdirs()
            }
            val child = "media.ogg"
            val file = File(fileDir, child)
            if (file.exists()) {
                return
            } else {
                file.createNewFile()
            }
            var outputStream: FileOutputStream? = null
            try {
                outputStream = FileOutputStream(file)
                var len: Int
                val buffer = ByteArray(2048)
                while (true) {
                    len = input.read(buffer)
                    println("len = $len")
                    if (len == -1) return
                    outputStream.write(buffer, 0, len)
                }
            } finally {
                input.close()
                outputStream?.close()
            }
        }
    }
}