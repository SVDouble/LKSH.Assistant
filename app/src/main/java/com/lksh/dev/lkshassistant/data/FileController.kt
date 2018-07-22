package com.lksh.dev.lkshassistant.data

import android.content.Context

class FileController private constructor() {
    companion object {
        /* Public API */

        @JvmStatic
        fun getFile(ctx: Context, fileName: String): String {
            return ""
        }


        /* Inner logic */

        /* Key: filename; Value: version */
        val versions: Map<String, Int> = mapOf()

        @JvmStatic
        private fun updateFile(ctx: Context, fileName: String) {

        }

        @JvmStatic
        private fun fetchVersions(ctx: Context, fileName: String) {

        }
    }
}