package com.lksh.dev.lkshassistant.data

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

fun parseCsv(ctx: Context, resourceId: Int, delimiter: String = ","): List<List<String>> {
    val inputStream = ctx.resources.openRawResource(resourceId)
    return BufferedReader(InputStreamReader(inputStream)).readLines().map {
        it.split(delimiter).map {
            it.trim(' ').replace('^', ',')
        }
    }
}