package com.lksh.dev.lkshassistant

import android.content.Intent
import android.app.Service
import android.content.Context
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_map.*
import org.mapsforge.core.model.BoundingBox
import org.mapsforge.core.model.LatLong
import org.mapsforge.core.model.Point
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.layer.overlay.Marker
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.InternalRenderTheme
import java.io.File
import kotlin.concurrent.thread
import kotlin.math.sqrt


private const val defaultLat = 57.85760 //coordinates of dormitory
private const val defaultLong = 41.71000
val LAT = "LAT"
val LONG = "LONG"

data class HouseInfo(val latLong: LatLong, val name: String, val radius: Double)
val houseCoordinates = arrayOf(
        HouseInfo(LatLong(57.858785, 41.71165), "0", 0.00025),
        HouseInfo(LatLong(57.857963, 41.712258), "1", 0.00025),
        HouseInfo(LatLong(57.858197, 41.712056), "2", 0.00025),
        HouseInfo(LatLong(57.858433, 41.712241), "3", 0.00025),
        HouseInfo(LatLong(57.857929, 41.712768), "4", 0.00025),
        HouseInfo(LatLong(57.856296, 41.711431), "5", 0.00025),
        HouseInfo(LatLong(57.856296, 41.711431), "6", 0.00025),
        HouseInfo(LatLong(57.858274, 41.712611), "8", 0.00025),
        HouseInfo(LatLong(57.857621, 41.712989), "10", 0.00025),
        HouseInfo(LatLong(57.856478, 41.713326), "17", 0.00025),
        HouseInfo(LatLong(57.855682, 41.713292), "32", 0.00025),
        HouseInfo(LatLong(57.85552, 41.713419), "33", 0.00025),
        HouseInfo(LatLong(57.855341, 41.71351), "34", 0.00025),
        HouseInfo(LatLong(57.855307, 41.712678), "35", 0.00025),
        HouseInfo(LatLong(57.857403, 41.711691), "Main House", 0.0004),
        HouseInfo(LatLong(57.858095, 41.711262), "Club", 0.0003),
        HouseInfo(LatLong(57.857525, 41.712398), "Kompovnik", 0.0005),
        HouseInfo(LatLong(57.856865, 41.712037), "Romantic", 0.00025),
        HouseInfo(LatLong(57.857136, 41.711321), "Garazh", 0.00015),
        HouseInfo(LatLong(57.857064, 41.711265), "Gnezdo", 0.00015),
        HouseInfo(LatLong(57.857413, 41.710131), "Stolovaya", 0.0007),
        HouseInfo(LatLong(57.856306, 41.712751), "Korabl", 0.00025)
)

class MapActivity : AppCompatActivity() {
    private val tag = "LKSH_MAP_A"
    private var myPos = LatLong(defaultLat, defaultLong)
    private var posMarker: TappableMarker? = null
    private var working = true
    private var trackMe = true
    private var locationManager: LocationManager? = null

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            updateMyLocation(location.latitude, location.longitude)
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private fun setupMap() {
        val tag = tag + "_INIT"
        if (mapView == null)
            throw NullPointerException("mapView is empty")
        try {
            Log.d(tag, "map fragment setup started")
            AndroidGraphicFactory.createInstance(application)
            mapView.isClickable = true
            mapView.mapScaleBar.isVisible = true
            mapView.setBuiltInZoomControls(false)
            mapView.mapZoomControls.isShowMapZoomControls = false

            val mapDataStore = MapFile(prepareMapData())
            val tileCache = AndroidUtil.createTileCache(applicationContext, "mapcache",
                    mapView.model.displayModel.tileSize, 1f,
                    mapView.model.frameBufferModel.overdrawFactor)
            val tileRendererLayer = TileRendererLayer(tileCache, mapDataStore,
                    mapView.model.mapViewPosition, AndroidGraphicFactory.INSTANCE)
            tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT)
            mapView.layerManager.layers.add(tileRendererLayer)

            mapView.setCenter(myPos)
            mapView.setZoomLevel(19.toByte())
            mapView.setZoomLevelMax(22)
            mapView.setZoomLevelMin(18)
            mapView.model.mapViewPosition.mapLimit = BoundingBox(minLat, minLong, maxLat, maxLong)
            Log.d(tag, "Map fragment setup successfully")
            drawPos()
            Log.d(tag, "dining room's position is marked (but it isn't exactly)")

        } catch (e: Exception) {
            Log.e(tag, e.message, e)
        }
    }
    private fun startGPSTrackingThread() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        thread(name = "PosThread", isDaemon = true) {
            Looper.prepare()
            val tag = "LKSH_GPS_THR"
            while (true) {
                if (working) {
                    try {
                        // Request location updates
                        locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                0L, 0f, locationListener)
                        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                0L, 0f, locationListener)
                    } catch (e: SecurityException) {
                        Log.d(tag, e.message, e)
                    }
                    if (trackMe) {
                        centerByMe(false)
                        //Log.d(tag, "Updated pos")
                    } else {
                        //Log.d(tag, "not updated pos")
                    }
                }
                //Log.d(tag, "iteration completed")
                Thread.sleep(500) // 2 updates/s
            }
        }
        Log.d(tag, "GPS started")
    }
    private fun setHouseMarkers() {
        for (house in houseCoordinates) {
            val marker = TappableMarker(resources.getDrawable(android.R.drawable.btn_radio),
                    house.latLong, house.name, house.radius)
            mapView.layerManager.layers.add(marker)
        }
    }

    private fun centerByMe(showAccuracy: Boolean = true) {
        val gpsLocation = LocationTrackingService.locationListeners[0].lastLocation
        val networkLocation = LocationTrackingService.locationListeners[1].lastLocation
        val endLocation: Location

        endLocation = if (!gpsLocation.hasAccuracy() && !networkLocation.hasAccuracy()) {
            Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
            return
        } else if (!gpsLocation.hasAccuracy() || (networkLocation.hasAccuracy()
                        && networkLocation.accuracy < gpsLocation.accuracy)) networkLocation
        else gpsLocation
        updateMyLocation(endLocation.latitude, long = endLocation.longitude)
        setLocation(myPos, if (showAccuracy) endLocation.accuracy else 0.toFloat())
    }

    private fun drawPos() {
        val drawable = resources.getDrawable(android.R.drawable.radiobutton_on_background)
        val marker = TappableMarker(drawable, myPos, "Your position", 0.00025)
        mapView.layerManager.layers.add(marker)
        posMarker = marker

    }

    private fun updateMyLocation(lat: Double, long: Double) {
        myPos = LatLong(lat, long)
    }

    private fun setLocation(pos: LatLong, accuracy: Float = 0.toFloat()) {
        if (posMarker != null)
            mapView.layerManager.layers.remove(posMarker)
        val drawable = resources.getDrawable(android.R.drawable.radiobutton_on_background)
        val marker = TappableMarker(drawable, myPos, "Your position", 0.00025)
        mapView.layerManager.layers.add(marker)
        mapView.model.mapViewPosition.center = pos
        posMarker = marker
        Log.d("LKSH_MAP", "set center to ${pos.latitude} ${pos.longitude} ($accuracy)")
        if (accuracy != 0.toFloat())
            Toast.makeText(this, "Accuracy is $accuracy m", Toast.LENGTH_SHORT).show()
    }

    private fun prepareMapData(): File {
        val mapFolder = File(Environment.getExternalStorageDirectory(), "lksh")
        if (!mapFolder.exists())
            mapFolder.mkdir()
        val mapFile = File(mapFolder, "map.map")
        if (!mapFile.exists()) {
            mapFile.createNewFile()
            assets.open("map.map").copyTo(mapFile.outputStream())
            Log.d("MAP", "copy from assets to ${mapFile.absolutePath}")
        } else {
            Log.d("MAP", "${mapFile.absolutePath} already exists")
        }
        return mapFile
    }

    override fun onPause() {
        working = false
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        working = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        myPos = LatLong(Bundle().getDouble(LAT, defaultLat), Bundle().getDouble(LONG, defaultLong))
        setupMap()
        startService(Intent(this, LocationTrackingService::class.java))
        startGPSTrackingThread()
        setHouseMarkers()

        setMyPosButton.setOnClickListener {
            centerByMe()
        }

        Log.d(tag, "tracking: $trackMe")
        posAutoSwitch.setOnCheckedChangeListener { _, checked ->
            trackMe = checked
            Log.d(tag, "tracking: $trackMe")
        }


    }
    override fun onDestroy() {
        if (mapView != null)
            mapView.destroyAll()
        AndroidGraphicFactory.clearResourceMemoryCache()
        super.onDestroy()
    }
}

//icon = resources.getDrawable(android.R.drawable.radiobutton_on_background)
private class TappableMarker(icon: Drawable, localLatLong: LatLong, val name: String, val radius: Double):
        Marker(localLatLong, AndroidGraphicFactory.convertToBitmap(icon),
                AndroidGraphicFactory.convertToBitmap(icon).width / 2,
                -1 * AndroidGraphicFactory.convertToBitmap(icon).height / 2) {
    override fun onTap(tapLatLong: LatLong?, layerXY: Point?, tapXY: Point?): Boolean {
        if (tapLatLong == null || getDistance(tapLatLong, latLong) > radius)
            return false
        Log.d("LKSH_MARKER", "$name is tapped (${tapLatLong.latitude}:${tapLatLong.longitude}/${latLong.latitude}:${latLong.longitude})")
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
            locationManager?.requestLocationUpdates(provider, internal, distance, locationListeners[1])
        } catch (e: SecurityException) {
            Log.e(tag, "Fail to request location update", e)
        } catch (e: IllegalArgumentException) {
            Log.e(tag, "$provider provider does not exist", e)
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
        requestLocationUpdates(locationManager, LocationManager.NETWORK_PROVIDER)
        requestLocationUpdates(locationManager, LocationManager.GPS_PROVIDER)
    }
    override fun onDestroy() {
        super.onDestroy()
        if (locationManager != null)
            for (locationListener in locationListeners) { // <- fix
                try {
                    locationManager?.removeUpdates(locationListener)
                } catch (e: Exception) {
                    Log.w(tag, "Failed to remove location listeners")
                }
            }
    }
    companion object {
        const val tag = "LocationTrackingService"
        const val internal = 1000.toLong() // In milliseconds
        const val distance = 0f // In meters
        val locationListeners = arrayOf(
                LTRLocationListener(LocationManager.GPS_PROVIDER),
                LTRLocationListener(LocationManager.NETWORK_PROVIDER)
        )

        class LTRLocationListener(provider: String) : android.location.LocationListener {
            val lastLocation = Location(provider)

            override fun onLocationChanged(location: Location?) {
                lastLocation.set(location)
                if (location != null)
                    Log.d("LKSH_LOCATION-SERVICE", "update location to ${location.latitude}, " +
                            "${location.longitude} (${location.accuracy})")
                else
                    Log.d("LKSH_LOCATION-SERVICE", "null location")
            }
            override fun onProviderDisabled(provider: String?) {}
            override fun onProviderEnabled(provider: String?) {}
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }
    }
}