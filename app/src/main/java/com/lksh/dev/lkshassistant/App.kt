package com.lksh.dev.lkshassistant

import android.app.Application
import com.lksh.dev.lkshassistant.data.Prefs

class App : Application() {
    override fun onCreate() {
        /* Instantiate prefs */
        Prefs.getInstance(applicationContext)
        super.onCreate()

//        /* Load house coordinates from json */
//        val data = resources.openRawResource(R.raw.houses)
//        houseCoordinates = Klaxon().parseArray<JsonHouseInfoModel>(data)!!
//                .map { HouseInfoModel(LatLong(it.lat, it.long), it.name, it.radius, it.buildingType) }
    }
}