package com.lksh.dev.lkshassistant.model

import com.lksh.dev.lkshassistant.BuildingType
import org.mapsforge.core.model.LatLong

data class HouseInfo(
        val latLong: LatLong,
        val name: String,
        val radius: Double,
        val buildingType: BuildingType
)