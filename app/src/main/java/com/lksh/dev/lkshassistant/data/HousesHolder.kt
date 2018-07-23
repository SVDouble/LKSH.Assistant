package com.lksh.dev.lkshassistant.data

import android.content.Context
import com.lksh.dev.lkshassistant.map.HouseInfoModel
import org.jetbrains.anko.doAsync

object HousesHolder : FileController.GetFileListener {
    private var forceInitLock = false
    private var allHouses: List<HouseInfoModel> = listOf()

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
//            houseCoordinates = Klaxon().parseArray<JsonHouseInfoModel>(data)!!
//                    .map { HouseInfoModel(LatLong(it.latitude, it.longitude), it.name, it.radius, it.buildingType) }
            // allHouses = ...
        }
        forceInitLock = false
    }
}