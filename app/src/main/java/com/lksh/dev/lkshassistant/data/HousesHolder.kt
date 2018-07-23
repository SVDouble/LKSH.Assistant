package com.lksh.dev.lkshassistant.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lksh.dev.lkshassistant.map.HouseInfoModel
import com.lksh.dev.lkshassistant.map.JsonHouseInfoModel
import com.lksh.dev.lkshassistant.ui.activities.TAG
import com.lksh.dev.lkshassistant.ui.fragments.MapBoxFragment
import org.jetbrains.anko.doAsync
import org.mapsforge.core.model.LatLong

object HousesHolder : FileController.GetFileListener {
    private var forceInitLock = false
    private var allHouses: List<HouseInfoModel> = listOf()

    data class HousesFromServer(
            val error: String,
            val result: Array<JsonHouseInfoModel>
    )

    fun initHouses(ctx: Context, useLocalVersion: Boolean = true) {
        doAsync {
            if (!useLocalVersion) {
                do {
                    if (!forceInitLock)
                        FileController.requestFile(ctx, this@HousesHolder, HOUSES_DB_FILENAME)
                    forceInitLock = true
                } while (allHouses.isEmpty())
            } else {
                FileController.requestFile(ctx, this@HousesHolder, HOUSES_DB_FILENAME, true)
            }
        }
    }

    fun getHouses(): List<HouseInfoModel> =
            allHouses

    override fun receiveFile(ctx: Context, file: String?) {
        if (file != null) {
            val frServ = Gson().fromJson<HousesHolder.HousesFromServer>(file, TypeToken.get(HousesHolder.HousesFromServer::class.java).type)
            allHouses = frServ.result.map { HouseInfoModel(it.id, LatLong(it.lat, it.long), it.name, it.radius, it.buildingType) }
            MapBoxFragment.onUpdateHouses()
            Log.d(TAG, "Update houses ${allHouses.size}!\n$allHouses")
        } else {
            initHouses(ctx, false)
        }
        forceInitLock = false
    }

}