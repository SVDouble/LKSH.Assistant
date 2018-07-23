package com.lksh.dev.lkshassistant.data

import android.content.Context
import android.util.Log
import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
                fetchVersions(ctx)
                if (localVersions != null && serverVersions != null)
                    if ((localVersions!!.tables[fileName]?.version ?: -1) < serverVersions!!.tables[fileName]!!.version)
                        if (updateFile(ctx, fileName)) {
                            localVersions!!.tables[fileName]!!.version = serverVersions!!.tables[fileName]!!.version
                            writeToFS(ctx, FC_CONFIG_FILENAME, Klaxon().toJsonString(localVersions!!))
                            //TODO: not so many times override file
                        }
                val response = readFromFS(ctx, fileName)
                ctx.runOnUiThread {
                    listener.receiveFile(response)
                }
            }
        }


        /* Inner logic */

        /* Key: filename; Value: version */
        private var localVersions: VersionsInfo? = null
        private var serverVersions: VersionsInfo? = null

        @JvmStatic
        private fun updateFile(ctx: Context, fileName: String): Boolean {
            NetworkHelper.getTextFile(ctx,
                    object : NetworkHelper.OnDispatchResponse {
                        override fun dispatchResult(result: String?) {
                            if (result != null)
                                writeToFS(ctx, fileName, result)
                        }
                    }, fileName)
            return true
        }

        @JvmStatic
        private fun fetchVersions(ctx: Context) {
            Log.d(TAG, "FileController: fetch versions from server")

            /* Local config */
            val localConfig = readFromFS(ctx, FC_CONFIG_FILENAME)
            Log.d(TAG, "FileController: get local versions:\n$localConfig")
            if (localConfig != null)
                localVersions = Klaxon().parse<VersionsInfo>(localConfig)

            /* Server config */
            NetworkHelper.getTextFile(ctx,
                    object : NetworkHelper.OnDispatchResponse {
                        override fun dispatchResult(result: String?) {
                            Log.d(TAG, "FileController: get server versions:\n$result")
                            if (result != null)
                                serverVersions = Klaxon().parse<VersionsInfo>(result)
                            Log.d(TAG, serverVersions.toString())
                        }
                    }, FC_CONFIG_FILENAME)
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

    private inline fun <reified T> Gson.fromJson(json: String) =
            this.fromJson<T>(json, object: TypeToken<T>() {}.type)
}