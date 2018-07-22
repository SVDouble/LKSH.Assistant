package com.lksh.dev.lkshassistant.data

import android.content.Context
import com.beust.klaxon.Klaxon

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
            val newValue = "" //NetworkHelper. ...
            writeToFS(ctx, fileName, newValue)
        }

        @JvmStatic
        private fun fetchVersions(ctx: Context) {
            /* From server */

            /* From FS */
            localVersions.clear()
            Klaxon().parseArray<FileInfo>(readFromFS(ctx, FC_CONFIG_FILENAME)!!)!!
                    .map { it.fileName to it.fileVersion }
                    .toMap(localVersions)
        }
    }

    private data class FileInfo(
            val fileName: String,
            val fileVersion: Int
    )
}