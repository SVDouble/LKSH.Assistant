package com.lksh.dev.lkshassistant.map

import org.mapsforge.core.model.LatLong

data class HouseInfoModel(
        var latLong: LatLong,
        var name: String,
        var radius: Double,
        var buildingType: BuildingType
)

data class JsonHouseInfoModel(
        var latitude: Double,
        var longitude: Double,
        var name: String,
        var radius: Double,
        var buildingType: BuildingType
)
