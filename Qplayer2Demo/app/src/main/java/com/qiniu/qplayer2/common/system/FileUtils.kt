package com.qiniu.qplayer2.common.system

import android.content.Context
import androidx.annotation.RawRes
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object FileUtils {
    fun copyFromResToFile(context: Context, @RawRes resId: Int, fileName: String): String? {

        // 获取 raw 资源中的 InputStream
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            // 打开 res/raw 中的文件
            inputStream = context.resources.openRawResource(resId)

            // 获取 files 目录
            val filesDir: File = context.filesDir
            val outFile = File(filesDir, fileName)

            // 创建输出流，准备写入文件
            outputStream = FileOutputStream(outFile)

            // 复制文件内容
            val buffer = ByteArray(1024)
            var length: Int
            while ((inputStream.read(buffer).also { length = it }) > 0) {
                outputStream.write(buffer, 0, length)
            }

            outputStream.flush() // 确保所有数据都写入文件
            return outFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            // 关闭流
            try {
                inputStream?.close()
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }
}