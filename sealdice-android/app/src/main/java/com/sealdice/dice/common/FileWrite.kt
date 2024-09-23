package com.sealdice.dice.common

import android.content.Context
import android.content.res.AssetManager
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * 提供公共方法，向外置存储读写文件
 * Created by helloklf on 2016/8/27.
 */
object FileWrite {
    val SDCardDir: String = Environment.getExternalStorageDirectory().absolutePath
    var FileCount = 0
    fun copyFolder(sourceFolder: File, targetFolder: File) {
        // create target folder if it doesn't exist
        if (!targetFolder.exists()) {
            targetFolder.mkdirs()
        }

        // list all files and subdirectories in the source folder
        val files = sourceFolder.listFiles() ?: return
        for (file in files) {
            if (file.isDirectory) {
                // create the subdirectory in the target folder
                val subTargetFolder = File(targetFolder, file.name)
                subTargetFolder.mkdir()
                // recursively copy the subdirectory
                copyFolder(file, subTargetFolder)
            } else {
                // copy the file to the target folder
                val targetFile = File(targetFolder, file.name)
                FileCount++
                Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                Log.d("CopyFolder", "Copied file ${file.absolutePath} to ${targetFile.absolutePath}")
            }
        }
    }
    fun writeFile(context: Context, file: String, hasExtName: Boolean): String? {
        val baseUrl = "${SDCardDir}/Android/data/${context.packageName}/"

        try {
            val inputStream = if (file.startsWith("file:///android_asset/")) {
                context.assets.open(file.substring("file:///android_asset/".length))
            } else {
                context.assets.open(file)
            }

            val dir = File(baseUrl)
            if (!dir.exists())
                dir.mkdirs()
            val filePath = baseUrl + if (hasExtName)
                file
            else
                file.substring(0, if (file.lastIndexOf(".") > 0) file.lastIndexOf(".") else file.length)

            val fileOutputStream = FileOutputStream(filePath)

            val datas = ByteArray(20480)
            while (true) {
                val len = inputStream.read(datas)
                if (len > 0) {
                    fileOutputStream.write(datas, 0, len)
                } else {
                    break
                }
            }

            fileOutputStream.close()
            inputStream.close()
            val writedFile = File(filePath)
            writedFile.setWritable(true)
            writedFile.setExecutable(true)
            writedFile.setReadable(true)
            return filePath
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun getPrivateFileDir(context: Context): String {
        return context.filesDir.absolutePath + "/"
    }

    fun getPrivateFilePath(context: Context, outName: String): String {
        return getPrivateFileDir(context) + (if (outName.startsWith("/")) outName.substring(1, outName.length) else outName)
    }
    fun writePrivateFile(file: String, outName: String, context: Context): String? {
        return writePrivateFile(context.assets, file, outName, context)
    }

    fun writePrivateFile(assetManager: AssetManager, file: String, outName: String, context: Context): String? {
        try {
            val inputStream = if (file.startsWith("file:///android_asset/")) {
                assetManager.open(file.substring("file:///android_asset/".length))
            } else {
                assetManager.open(file)
            }

            val dir = File(getPrivateFileDir(context))
            if (!dir.exists())
                dir.mkdirs()
            val filePath = getPrivateFilePath(context, outName)
            val fileDir = File(filePath).parentFile
            if (fileDir != null) {
                if (!fileDir.exists())
                    fileDir.mkdirs()
            }

            val fileOutputStream = FileOutputStream(filePath)

            val datas = ByteArray(20480)
            while (true) {
                val len = inputStream.read(datas)
                if (len > 0) {
                    fileOutputStream.write(datas, 0, len)
                } else {
                    break
                }
            }

            fileOutputStream.close()
            inputStream.close()
            val writedFile = File(filePath)
            writedFile.setWritable(true)
            writedFile.setExecutable(true)
            writedFile.setReadable(true)
            return filePath
            //getApplicationContext().getClassLoader().getResourceAsStream("");
        } catch (e: IOException) {
            Log.e("writePrivateFile", "" + e.message)
            e.printStackTrace()
        }
        return null
    }

    private fun writePrivateFile(bytes: ByteArray, outName: String, context: Context): Boolean {
        try {
            val dir = File(getPrivateFileDir(context))
            if (!dir.exists())
                dir.mkdirs()
            val filePath = getPrivateFilePath(context, outName)
            val fileDir = File(filePath).parentFile
            if (fileDir != null) {
                if (!fileDir.exists())
                    fileDir.mkdirs()
            }

            val fileOutputStream = FileOutputStream(filePath)
            fileOutputStream.write(bytes, 0, bytes.size)
            fileOutputStream.close()
            File(filePath).setExecutable(true, false)
            //getApplicationContext().getClassLoader().getResourceAsStream("");
            val writedFile = File(filePath)
            writedFile.setWritable(true)
            writedFile.setExecutable(true)
            writedFile.setReadable(true)
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    fun writePrivateShellFile(file: String, outName: String, context: Context): String? {
        val data = parseText(context, file)
        if (data.isNotEmpty() && writePrivateFile(data, outName, context)) {
            return getPrivateFilePath(context, outName)
        }
        return null
    }

    //Dos转Unix，避免\r\n导致的脚本无法解析
    private fun parseText(context: Context, fileName: String): ByteArray {
        return try {
            val assetManager = context.assets
            val inputStream = assetManager.open(fileName)
            val datas = ByteArray(inputStream.available())
            //inputStream.available()
            var len = inputStream.read(datas)
            if (len < 0) {
                len = 0
            }
            val codes = String(datas, 0, len).replace(Regex("\r\n"), "\n").replace(Regex("\r\t"), "\t")
            codes.toByteArray(Charsets.UTF_8)
        } catch (ex: Exception) {
            Log.e("script-parse", "" + ex.message)
            "".toByteArray()
        }
    }
}
