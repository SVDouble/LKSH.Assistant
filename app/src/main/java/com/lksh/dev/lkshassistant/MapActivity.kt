package com.lksh.dev.lkshassistant

import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.lksh.dev.lkshassistant.Fragments.*
import kotlinx.android.synthetic.main.activity_map.*
import org.mapsforge.core.model.BoundingBox
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.InternalRenderTheme
import java.io.File
import kotlin.concurrent.thread

import org.mapsforge.core.graphics.Filter


private const val defaultLat = 57.85760 //coordinates of dormitory
private const val defaultLong = 41.71000
const val LAT = "LAT"
const val LONG = "LONG"

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

    fun setNightTheme() {
        mapView!!.model.displayModel.filter = Filter.INVERT
    }
    fun setLigthTheme() {
        mapView!!.model.displayModel.filter = Filter.NONE
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
