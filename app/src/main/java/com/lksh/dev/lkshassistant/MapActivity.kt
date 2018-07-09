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
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.layer.overlay.Marker
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.InternalRenderTheme
import java.io.File
import kotlin.concurrent.thread


private const val defaultLat = 57.85760 //coordinates of dormitory
private const val defaultLong = 41.71000
val LAT = "LAT"
val LONG = "LONG"

class MapActivity : AppCompatActivity() {
    private val TAG = "LKSH_MAP_A"
    private var myPos = LatLong(defaultLat, defaultLong)
    private var posMarker: TappableMarker? = null
    private var working = true
    private var trackMe = true
    private var locationManager: LocationManager? = null
    private fun setupMap() {
        if (mapView == null)
            throw NullPointerException("mapView is empty")
        try {
            Log.d(TAG, "map fragment setup started")
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
            mapView.mapZoomControls.zoomLevelMax = 21
            mapView.mapZoomControls.zoomLevelMin = 16
            mapView.setZoomLevel(19.toByte())
            mapView.model.mapViewPosition.mapLimit = BoundingBox(minLat, minLong, maxLat, maxLong)
            //mapView.model.
            /*val boundingBox = BoundingBox(minLat, minLong, maxLat, maxLong)
            val dimension = mapView.model.mapViewDimension.dimension

            mapView.model.mapViewPosition.mapPosition = MapPosition(boundingBox.centerPoint,
                LatLongUtils.zoomForBounds(dimension, boundingBox, mapView.model.displayModel.tileSize))
            */
            Log.d(TAG, "Map fragment setup successfully")
            drawPos()
            Log.d(TAG, "dining room's position is marked (but it isn't exactly)")
//            mapView.setGestureDetector(GestureDetector())

            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
            thread(name = "PosThread", isDaemon = true) {
                Looper.prepare()
                while (true) {
                    if (working) {
                        try {
                            // Request location updates
                            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
                            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)

                        } catch (e: SecurityException) {
                            Log.d("LKSH_GPS_THR", e.message, e)
                        }
                        if (trackMe)
                            setLocation(myPos)
                    }
                    Thread.sleep(500) // 2 updates/s
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            //Looper.prepare()
            //setLocation(location.latitude, location.longitude)
            updateMyLocation(location.latitude, location.longitude)
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private fun drawPos() {
        val drawable = resources.getDrawable(android.R.drawable.radiobutton_on_background)
        val marker = TappableMarker(drawable, myPos)
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
        val marker = TappableMarker(drawable, myPos)
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

        setMyPosButton.setOnClickListener {
            val gpsLocation = LocationTrackingService.locationListeners[0].lastLocation
            val networkLocation = LocationTrackingService.locationListeners[1].lastLocation

            val endLocation: Location
            endLocation = if (!gpsLocation.hasAccuracy() && !networkLocation.hasAccuracy()) {
                Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (!gpsLocation.hasAccuracy() || (networkLocation.hasAccuracy()
                            && networkLocation.accuracy < gpsLocation.accuracy)) networkLocation
            else gpsLocation
            updateMyLocation(endLocation.latitude, long = endLocation.longitude)
            setLocation(myPos, endLocation.accuracy)
        }

        posAutoSwitch.setOnCheckedChangeListener { _, checked ->
            trackMe = checked
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
private class TappableMarker(icon: Drawable, localLatLong: LatLong) :
        Marker(localLatLong, AndroidGraphicFactory.convertToBitmap(icon),
                AndroidGraphicFactory.convertToBitmap(icon).width / 2,
                -1 * AndroidGraphicFactory.convertToBitmap(icon).height / 2)

class LocationTrackingService : Service() {

    var locationManager: LocationManager? = null

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onCreate() {
        if (locationManager == null)
            locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, INTERVAL, DISTANCE, locationListeners[1])
        } catch (e: SecurityException) {
            Log.e(TAG, "Fail to request location update", e)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Network provider does not exist", e)
        }

        try {
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL, DISTANCE, locationListeners[0])
        } catch (e: SecurityException) {
            Log.e(TAG, "Fail to request location update", e)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "GPS provider does not exist", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (locationManager != null)
            for (locationListener in locationListeners) { // <- fix
                try {
                    locationManager?.removeUpdates(locationListener)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to remove location listeners")
                }
            }
    }


    companion object {
        val TAG = "LocationTrackingService"

        val INTERVAL = 1000.toLong() // In milliseconds
        val DISTANCE = 0.toFloat() // In meters

        val locationListeners = arrayOf(
                LTRLocationListener(LocationManager.GPS_PROVIDER),
                LTRLocationListener(LocationManager.NETWORK_PROVIDER)
        )

        class LTRLocationListener(provider: String) : android.location.LocationListener {
            val lastLocation = Location(provider)
            override fun onLocationChanged(location: Location?) {
                lastLocation.set(location)
                if (location != null) {
                    val mes = "update location to ${location.latitude}, ${location.longitude} " +
                            "(${location.accuracy})"
                    Log.d("LKSH_LOCATION-SERVICE", mes)
                } else
                    Log.d("LKSH_LOCATION-SERVICE", "null location")
                // TODO: Do something here
            }

            override fun onProviderDisabled(provider: String?) {
            }

            override fun onProviderEnabled(provider: String?) {
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }
        }
    }

}