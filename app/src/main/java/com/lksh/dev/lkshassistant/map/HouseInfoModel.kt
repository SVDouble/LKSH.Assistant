package com.lksh.dev.lkshassistant.map

import org.mapsforge.core.model.LatLong

data class HouseInfoModel(
        val latLong: LatLong,
        val name: String,
        val radius: Double,
        val buildingType: BuildingType
)