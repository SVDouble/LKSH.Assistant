package com.lksh.dev.lkshassistant.domain

import android.graphics.drawable.Drawable
import com.lksh.dev.lkshassistant.domain.listeners.OnMapInteractionListener
import com.lksh.dev.lkshassistant.domain.model.HouseInfo
import org.mapsforge.core.model.LatLong
import org.mapsforge.core.model.Point
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.layer.overlay.Marker
import kotlin.math.sqrt

class TappableMarker(icon: Drawable, private val houseInfo: HouseInfo,
                     val listener: OnMapInteractionListener) :
        Marker(houseInfo.latLong, AndroidGraphicFactory.convertToBitmap(icon),
                0,
                0) {
    override fun onTap(tapLatLong: LatLong?, layerXY: Point?, tapXY: Point?): Boolean {
        if (tapLatLong == null || getDistance(tapLatLong, latLong) > houseInfo.radius ||
                houseInfo.buildingType == BuildingType.NONE)
            return false
        listener.dispatchClickBuilding(houseInfo)
        return true
    }

    private fun sqr(x: Double) = x * x
    private fun getDistance(latLong: LatLong, latLong2: LatLong) =
            sqrt(sqr(latLong.latitude - latLong2.latitude) +
                    sqr(latLong.longitude - latLong2.longitude))
}