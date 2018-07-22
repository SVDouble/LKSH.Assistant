package com.lksh.dev.lkshassistant.data

import android.content.Context
import com.beust.klaxon.Klaxon
import com.lksh.dev.lkshassistant.web.NetworkHelper
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

const val FC_CONFIG_FILENAME = "files_config.json"

class FileController private constructor() {
    companion object {
        /* Public API */

        @JvmStatic
        fun requestFile(ctx: Context, listener: GetFileListener, fileName: String) {
            doAsync {
                fetchVersions(ctx)
                if ((localVersions[fileName] ?: -1) < serverVersions[fileName]!!)
                    if (updateFile(ctx, fileName)) {
                        localVersions[fileName] = serverVersions[fileName]!!
                        writeToFS(ctx, FC_CONFIG_FILENAME, Klaxon().toJsonString(localVersions))
                    }
                val response = readFromFS(ctx, fileName)
                ctx.runOnUiThread {
                    listener.receiveFile(response)
                }
            }
        }


        /* Inner logic */

        /* Key: filename; Value: version */
        private var localVersions: MutableMap<String, Int> = mutableMapOf()
        private var serverVersions: MutableMap<String, Int> = mutableMapOf()

        @JvmStatic
        private fun updateFile(ctx: Context, fileName: String): Boolean {
            val newValue = NetworkHelper.getTextFile(ctx, fileName) ?: return false
            writeToFS(ctx, fileName, newValue)
            return true
        }

        @JvmStatic
        private fun fetchVersions(ctx: Context) {
            /* From server */
            serverVersions.clear()
            NetworkHelper.getTextFile(ctx, FC_CONFIG_FILENAME)
            /* Parse ... and add to map */

            /* From FS */
            localVersions.clear()
            val localVersionsRaw = readFromFS(ctx, FC_CONFIG_FILENAME)
            if (localVersionsRaw != null)
                localVersions = Klaxon().parse<MutableMap<String, Int>>(localVersionsRaw)!!
        }
    }

    interface GetFileListener {
        fun receiveFile(file: String?)
    }
}