package com.lksh.dev.lkshassistant.data

import android.content.Context
import com.beust.klaxon.Klaxon
import com.lksh.dev.lkshassistant.map.HouseInfoModel
import com.lksh.dev.lkshassistant.map.JsonHouseInfoModel
import org.jetbrains.anko.doAsync
import org.mapsforge.core.model.LatLong

object HousesHolder : FileController.GetFileListener {
    private var forceInitLock = false
    private var allHouses: List<HouseInfoModel> = listOf()

    data class HousesFromServer(
            val error: String,
            val result: Array<JsonHouseInfoModel>
    )

    fun initHouses(ctx: Context) {
        doAsync {
            while (allHouses.isEmpty()) {
                if (!forceInitLock)
                    FileController.requestFile(ctx, this@HousesHolder, HOUSES_DB_FILENAME)
                forceInitLock = true
            }
        }
    }

    fun getHouses(): List<HouseInfoModel> =
            allHouses

    override fun receiveFile(file: String?) {
        if (file != null) {
            allHouses = Klaxon().parse<HousesFromServer>(file)!!.result
                    .map { HouseInfoModel(LatLong(it.latitude, it.longitude), it.name, it.radius, it.buildingType) }
            // allHouses = ...
        }
        forceInitLock = false
    }

}