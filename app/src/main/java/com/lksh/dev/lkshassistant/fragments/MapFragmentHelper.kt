package com.lksh.dev.lkshassistant.fragments

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import org.mapsforge.core.model.LatLong
import org.mapsforge.core.model.Point
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.layer.overlay.Marker
import kotlin.math.sqrt

//icon = resources.getDrawable(android.R.drawable.radiobutton_on_background)
class TappableMarker(icon: Drawable, private val houseInfo: HouseInfo,
                     val listener: OnMapInteractionListener) :
        Marker(houseInfo.latLong, AndroidGraphicFactory.convertToBitmap(icon),
                /*AndroidGraphicFactory.convertToBitmap(icon).width / 2*/ 0,
                /*-1 * AndroidGraphicFactory.convertToBitmap(icon).height / 2*/ 0) {
    override fun onTap(tapLatLong: LatLong?, layerXY: Point?, tapXY: Point?): Boolean {
        if (tapLatLong == null || getDistance(tapLatLong, latLong) > houseInfo.radius ||
                houseInfo.buildingType == BuildingType.NONE)
            return false
        listener.dispatchClickBuilding(houseInfo)
        //Log.d("LKSH_MARKER", "$name is tapped (${tapLatLong.latitude}:${tapLatLong.longitude}/${latLong.latitude}:${latLong.longitude})")
        return true
    }

    private fun sqr(x: Double) = x * x
    private fun getDistance(latLong: LatLong, latLong2: LatLong) =
            sqrt(sqr(latLong.latitude - latLong2.latitude) +
                    sqr(latLong.longitude - latLong2.longitude))
}

class LocationTrackingService : Service() {
    private var locationManager: LocationManager? = null

    private fun requestLocationUpdates(locationManager: LocationManager?, provider: String) {
        try {
            val locationListener = LTRLocationListener(provider)
            locationManager?.requestLocationUpdates(provider, internal, distance, locationListener)
            locationListeners.add(locationListener to provider)
        } catch (e: SecurityException) {
            Log.e(tag, "Fail to request location update", e)
        } catch (e: IllegalArgumentException) {
            Log.e(tag, "$provider provider does not exist")
        }
    }

    override fun onBind(intent: Intent?) = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onCreate() {
        if (locationManager == null)
            locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = arrayOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER)
        for (provider in providers)
            try {
                requestLocationUpdates(locationManager, provider)
            } catch (e: Exception) {
                Log.e(tag, e.message, e)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (locationManager != null)
            for (locationListener in locationListeners) { // <- fix
                try {
                    locationManager?.removeUpdates(locationListener.first)
                } catch (e: Exception) {
                    Log.w(tag, "Failed to remove location listeners")
                }
            }
    }

    companion object {
        const val tag = "LocationTrackingService"
        const val internal = 1000.toLong() // In milliseconds
        const val distance = 0f // In meters
        val locationListeners = arrayListOf<Pair<LTRLocationListener, String>>(

                /*LTRLocationListener(LocationManager.GPS_PROVIDER),
                LTRLocationListener(LocationManager.NETWORK_PROVIDER)*/
        )

        class LTRLocationListener(provider: String) : android.location.LocationListener {
            val lastLocation = Location(provider)

            override fun onLocationChanged(location: Location?) {
                lastLocation.set(location)
                if (location != null)
                    Log.v("LKSH_LOCATION-SERVICE", "update location to ${location.latitude}, " +
                            "${location.longitude} (${location.accuracy})")
                else
                    Log.v("LKSH_LOCATION-SERVICE", "null location")
            }

            override fun onProviderDisabled(provider: String?) {}
            override fun onProviderEnabled(provider: String?) {}
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }
    }
}

data class HouseInfo(val latLong: LatLong, val name: String, val radius: Double, val buildingType: BuildingType)

enum class BuildingType {
    HOUSE,
    OTHER,
    USER,
    NONE
}

val houseCoordinates = arrayOf(
        HouseInfo(LatLong(57.858785, 41.71165), "0", 0.00015,
                BuildingType.HOUSE),
        HouseInfo(LatLong(57.857963, 41.712258), "1", 0.00015,
                BuildingType.HOUSE),
        HouseInfo(LatLong(57.858197, 41.712056), "2", 0.00015,
                BuildingType.HOUSE),
        HouseInfo(LatLong(57.858433, 41.712241), "3", 0.00015,
                BuildingType.HOUSE),
        HouseInfo(LatLong(57.857929, 41.712768), "4", 0.00015,
                BuildingType.HOUSE),
        HouseInfo(LatLong(57.856296, 41.711431), "5", 0.00015,
                BuildingType.HOUSE),
        HouseInfo(LatLong(57.856514, 41.711359), "6", 0.00015,
                BuildingType.HOUSE),
        HouseInfo(LatLong(57.858274, 41.712611), "8", 0.00015,
                BuildingType.HOUSE),
        HouseInfo(LatLong(57.857621, 41.712989), "10", 0.00015,
                BuildingType.HOUSE),
        HouseInfo(LatLong(57.856478, 41.713326), "17", 0.00015,
                BuildingType.HOUSE),
        HouseInfo(LatLong(57.855682, 41.713292), "32", 0.00015,
                BuildingType.HOUSE),
        HouseInfo(LatLong(57.85552, 41.713419), "33", 0.00015,
                BuildingType.HOUSE),
        HouseInfo(LatLong(57.855341, 41.71351), "34", 0.00015,
                BuildingType.HOUSE),
        HouseInfo(LatLong(57.855307, 41.712678), "35", 0.00015,
                BuildingType.HOUSE),
        HouseInfo(LatLong(57.857403, 41.711691), "ГК", 0.00025,
                BuildingType.HOUSE),
        HouseInfo(LatLong(57.858095, 41.711262), "Club", 0.00025,
                BuildingType.OTHER),
        HouseInfo(LatLong(57.857525, 41.712398), "Kompovnik", 0.00025,
                BuildingType.OTHER),
        HouseInfo(LatLong(57.856865, 41.712037), "Romantic", 0.00015,
                BuildingType.OTHER),
        HouseInfo(LatLong(57.857136, 41.711321), "Garazh", 0.00015,
                BuildingType.OTHER),
        HouseInfo(LatLong(57.857064, 41.711265), "Gnezdo", 0.00005,
                BuildingType.OTHER),
        HouseInfo(LatLong(57.857413, 41.710131), "Stolovaya", 0.0005,
                BuildingType.OTHER),
        HouseInfo(LatLong(57.856306, 41.712751), "Korabl", 0.00025,
                BuildingType.OTHER),
        HouseInfo(LatLong(57.85674, 41.717549), "Horosho", 0.00025,
                BuildingType.OTHER)
)

const val minLat = 57.855300
const val maxLat = 57.858790
const val minLong = 41.708843
const val maxLong = 41.717549

const val defaultLat = 57.85760 //coordinates of dormitory
const val defaultLong = 41.71000
const val LAT = "LAT"
const val LONG = "LONG"
