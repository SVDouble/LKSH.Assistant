package com.lksh.dev.lkshassistant.data

import android.content.Context
import android.util.Log
import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import com.lksh.dev.lkshassistant.ui.activities.TAG
import com.lksh.dev.lkshassistant.web.NetworkHelper
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

typealias JsonConvertType = MutableMap<String, Int>

const val FC_CONFIG_FILENAME = "files_config.json"

class FileController private constructor() {
    companion object {
        /* Public API */

        @JvmStatic
        fun requestFile(ctx: Context, listener: GetFileListener, fileName: String) {
            doAsync {
                val fileNameOnServer = fileName.substringBefore('.')
                fetchVersions(ctx)
                if ((localVersions[fileName] ?: -1) < serverVersions[fileNameOnServer]!!)
                    if (updateFile(ctx, fileName)) {
                        Log.d(TAG, "Update file $fileName and its version from ${localVersions[fileName]
                                ?: "no_version_detected"} to ${serverVersions[fileNameOnServer]!!}")
                        localVersions[fileName] = serverVersions[fileNameOnServer]!!
                        writeToFS(ctx, FC_CONFIG_FILENAME, Klaxon().toJsonString(localVersions))
                    } else {
                        Log.d(TAG, "Can't update file $fileName")
                    }
                else {
                    Log.d(TAG, "File $fileName is up-to-date")
                }

                val response = readFromFS(ctx, fileName)
                ctx.runOnUiThread {
                    listener.receiveFile(response)
                }
            }
        }


        /* Inner logic */

        /* Key: filename; Value: version */
        private var localVersions: JsonConvertType = mutableMapOf()
        private var serverVersions: JsonConvertType = mutableMapOf()

        @JvmStatic
        private fun updateFile(ctx: Context, fileName: String): Boolean {
            val fileNameOnServer = fileName.substringBefore('.')
            val result = NetworkHelper.getTextFile(ctx, fileNameOnServer)
            if (result != null) {
                Log.d(TAG, "File $fileName new value:\n$result")
                writeToFS(ctx, fileName, result)
                return true
            }
            return false
        }

        @JvmStatic
        private fun fetchVersions(ctx: Context) {
            Log.d(TAG, "FileController: fetch versions from server")

            /* Local config */
            val localConfig = readFromFS(ctx, FC_CONFIG_FILENAME)
            Log.d(TAG, "FileController: get local versions:\n$localConfig")
            if (localConfig != null)
                localVersions = Klaxon().parse<VersionsInfo>(localConfig)?.tables!!
                        .map { it.key to it.value.version }.toMap().toMutableMap()

            /* Server config */
            val result = NetworkHelper.getTextFile(ctx, FC_CONFIG_FILENAME)
            Log.d(TAG, "FileController: get server versions:\n$result")
            if (result != null)
                serverVersions = Klaxon().parse<VersionsInfo>(result)?.tables!!
                        .map { it.key to it.value.version }.toMap().toMutableMap()
            Log.d(TAG, serverVersions.toString())
        }
    }

    data class VersionsInfo(
            @Json(name = "tables")
            val tables: MutableMap<String, TableInfo>,
            @Json(name = "houses")
            val houses: Map<String, Int>
    ) {
        data class TableInfo(
                @Json(name = "url")
                val url: String,
                @Json(name = "version")
                var version: Int
        )
    }

    interface GetFileListener {
        fun receiveFile(file: String?)
    }
}