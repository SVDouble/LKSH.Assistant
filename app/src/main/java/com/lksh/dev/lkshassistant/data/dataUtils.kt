package com.lksh.dev.lkshassistant.data

import android.content.Context
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

fun parseCsv(ctx: Context, resourceId: Int, delimiter: String = ","): List<List<String>> {
    val inputStream = ctx.resources.openRawResource(resourceId)
    return BufferedReader(InputStreamReader(inputStream)).readLines().map {
        it.split(delimiter).map {
            it.trim(' ').replace('^', ',')
        }
    }
}

fun writeToFS(ctx: Context,
              fileName: String,
              text: String,
              shouldRewrite: Boolean = true) {
    val file = File(ctx.filesDir, fileName)
    if (!file.exists())
        file.createNewFile()
    if (shouldRewrite)
        file.writeText(text)
    else
        file.appendText(text)
}

fun readFromFS(ctx: Context,
               fileName: String): String? {
    val file = File(ctx.filesDir, fileName)
    if (!file.exists()) {
//        throw FileNotFoundException("File $fileName doesn't exist")
        return null
    }
    return file.readText()
}
