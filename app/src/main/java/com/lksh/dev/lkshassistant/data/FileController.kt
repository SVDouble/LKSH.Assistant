package com.lksh.dev.lkshassistant.data

import android.content.Context
import com.beust.klaxon.Klaxon
import com.lksh.dev.lkshassistant.web.NetworkHelper

const val FC_CONFIG_FILENAME = "files_config.json"

class FileController private constructor() {
    companion object {
        /* Public API */

        @JvmStatic
        fun getFile(ctx: Context, fileName: String): String {
            fetchVersions(ctx)
            if ((localVersions[fileName] ?: -1) < serverVersions[fileName]!!)
                updateFile(ctx, fileName)
            return readFromFS(ctx, fileName)!!
        }


        /* Inner logic */

        /* Key: filename; Value: version */
        private var localVersions: MutableMap<String, Int> = mutableMapOf()
        private var serverVersions: MutableMap<String, Int> = mutableMapOf()

        @JvmStatic
        private fun updateFile(ctx: Context, fileName: String) {
            val newValue = NetworkHelper.getTextFile(ctx, fileName)
            writeToFS(ctx, fileName, newValue)
        }

        @JvmStatic
        private fun fetchVersions(ctx: Context) {
            /* From server */
            NetworkHelper.getTextFile(ctx, FC_CONFIG_FILENAME)
            /* From FS */
            localVersions.clear()
            Klaxon().parseArray<Pair<String, Int>>(readFromFS(ctx, FC_CONFIG_FILENAME)!!)!!
                    .map { it.first to it.second }
                    .toMap(localVersions)
        }
    }
}