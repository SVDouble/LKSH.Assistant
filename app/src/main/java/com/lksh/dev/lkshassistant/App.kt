package com.lksh.dev.lkshassistant

import android.app.Application
import com.beust.klaxon.Klaxon
import com.lksh.dev.lkshassistant.data.Prefs
import com.lksh.dev.lkshassistant.map.HouseInfoModel
import com.lksh.dev.lkshassistant.map.JsonHouseInfoModel
import org.mapsforge.core.model.LatLong

lateinit var houseCoordinates: List<HouseInfoModel>

class App : Application() {
    override fun onCreate() {
        /* Instantiate prefs */
        Prefs.getInstance(applicationContext)
        super.onCreate()

        /* Load house coordinates from json */
        val data = resources.openRawResource(R.raw.houses)
        houseCoordinates = Klaxon().parseArray<JsonHouseInfoModel>(data)!!
                .map { HouseInfoModel(LatLong(it.latitude, it.longitude), it.name, it.radius, it.buildingType) }
    }
}