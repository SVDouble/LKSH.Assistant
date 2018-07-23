package com.lksh.dev.lkshassistant.map

import org.mapsforge.core.model.LatLong

data class HouseInfoModel(
        var id: Int,
        var latLong: LatLong,
        var name: String,
        var radius: Double,
        var buildingType: BuildingType
)

data class JsonHouseInfoModel(
        var lat: Double,
        var long: Double,
        var name: String,
        var id: Int,
        var radius: Double,
        var buildingType: BuildingType
)
