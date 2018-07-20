package com.lksh.dev.lkshassistant.data

import com.lksh.dev.lkshassistant.map.BuildingType
import com.lksh.dev.lkshassistant.map.HouseInfoModel
import org.mapsforge.core.model.LatLong

const val minLat = 57.855300
const val maxLat = 57.858790
const val minLong = 41.708843
const val maxLong = 41.717549

const val defaultLat = 57.85760 //coordinates of dormitory
const val defaultLong = 41.71000
const val LAT = "LAT"
const val LONG = "LONG"

val houseCoordinates = arrayOf(
        HouseInfoModel(LatLong(57.858785, 41.71165), "0", 0.00015,
                BuildingType.HOUSE),
        HouseInfoModel(LatLong(57.857963, 41.712258), "1", 0.00015,
                BuildingType.HOUSE),
        HouseInfoModel(LatLong(57.858197, 41.712056), "2", 0.00015,
                BuildingType.HOUSE),
        HouseInfoModel(LatLong(57.858433, 41.712241), "3", 0.00015,
                BuildingType.HOUSE),
        HouseInfoModel(LatLong(57.857929, 41.712768), "4", 0.00015,
                BuildingType.HOUSE),
        HouseInfoModel(LatLong(57.856296, 41.711431), "5", 0.00015,
                BuildingType.HOUSE),
        HouseInfoModel(LatLong(57.856514, 41.711359), "6", 0.00015,
                BuildingType.HOUSE),
        HouseInfoModel(LatLong(57.858274, 41.712611), "8", 0.00015,
                BuildingType.HOUSE),
        HouseInfoModel(LatLong(57.857621, 41.712989), "10", 0.00015,
                BuildingType.HOUSE),
        HouseInfoModel(LatLong(57.856478, 41.713326), "17", 0.00015,
                BuildingType.HOUSE),
        HouseInfoModel(LatLong(57.855682, 41.713292), "32", 0.00015,
                BuildingType.HOUSE),
        HouseInfoModel(LatLong(57.85552, 41.713419), "33", 0.00015,
                BuildingType.HOUSE),
        HouseInfoModel(LatLong(57.855341, 41.71351), "34", 0.00015,
                BuildingType.HOUSE),
        HouseInfoModel(LatLong(57.855307, 41.712678), "35", 0.00015,
                BuildingType.HOUSE),
        HouseInfoModel(LatLong(57.857403, 41.711691), "ГК", 0.00025,
                BuildingType.HOUSE),
        HouseInfoModel(LatLong(57.858095, 41.711262), "Club", 0.00025,
                BuildingType.OTHER),
        HouseInfoModel(LatLong(57.857525, 41.712398), "Kompovnik", 0.00025,
                BuildingType.OTHER),
        HouseInfoModel(LatLong(57.856865, 41.712037), "Romantic", 0.00015,
                BuildingType.OTHER),
        HouseInfoModel(LatLong(57.857136, 41.711321), "Garazh", 0.00015,
                BuildingType.OTHER),
        HouseInfoModel(LatLong(57.857064, 41.711265), "Gnezdo", 0.00005,
                BuildingType.OTHER),
        HouseInfoModel(LatLong(57.857413, 41.710131), "Stolovaya", 0.0005,
                BuildingType.OTHER),
        HouseInfoModel(LatLong(57.856306, 41.712751), "Korabl", 0.00025,
                BuildingType.OTHER),
        HouseInfoModel(LatLong(57.85674, 41.717549), "Horosho", 0.00025,
                BuildingType.OTHER)
)