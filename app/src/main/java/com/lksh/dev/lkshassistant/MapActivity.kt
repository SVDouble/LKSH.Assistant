package com.lksh.dev.lkshassistant

import android.content.Intent
import android.os.IBinder
import android.app.Service
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.util.Log
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
            thread(name = "PosThread", isDaemon = true) {
                Looper.prepare()
                val gps = GpsTracker(applicationContext)
                while (true) {
                    //check if the GPS is active
                    if (working && gps.canGetLocation())
                        setLocation(gps.getLatitude(), gps.getLongitude())
                    Thread.sleep(1000 * 2)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }
    }

    private fun drawPos() {
        val drawable = resources.getDrawable(android.R.drawable.radiobutton_on_background)
        val marker = TappableMarker(drawable, myPos)
        mapView.layerManager.layers.add(marker)
        posMarker = marker

    }

    private fun setLocation(lat: Double, long: Double) {
        if (posMarker != null)
            mapView.layerManager.layers.remove(posMarker)
        myPos = LatLong(lat, long)
        val drawable = resources.getDrawable(android.R.drawable.radiobutton_on_background)
        val marker = TappableMarker(drawable, myPos)
        mapView.layerManager.layers.add(marker)
        mapView.model.mapViewPosition.center = myPos
        posMarker = marker
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
        //button_center.setOnClickListener {mapView.setCenter(LatLong(defaultLat, defaultLong))}
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


/**
 * Ahmet Ertugrul OZCAN
 * Cihazin konum bilgisini goruntuler
 */
class GpsTracker(private val context: Context) : Service(), LocationListener {
    internal var isGPSEnabled = false
    internal var isNetworkEnabled = false
    internal var canGetLocation = false
    internal var location: Location? = null
    internal var latitude = 0.0
    internal var longitude = 0.0
    protected var locationManager: LocationManager? = null

    init {
        getLocation()
    }

    private fun requestLocation(provider: String) {
        if (location == null && ActivityCompat.checkSelfPermission(context,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager!!.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
            if (locationManager != null) {
                location = locationManager!!.getLastKnownLocation(provider)
                if (location != null) {
                    latitude = location!!.latitude
                    longitude = location!!.longitude
                }
            }
        }
    }
    fun getLocation(): Location? {
        try {
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled) { } else {
                this.canGetLocation = true
                if (isNetworkEnabled)
                    requestLocation(LocationManager.NETWORK_PROVIDER)
                if (isGPSEnabled)
                    requestLocation(LocationManager.GPS_PROVIDER)
            }
        } catch (e: Exception) {
            Log.e("LKSH_GPS", e.message, e)
        }
        return location
    }
    fun getLatitude(): Double {
        if (location != null)
            latitude = location!!.latitude
        return latitude
    }
    fun getLongitude(): Double {
        if (location != null)
            longitude = location!!.longitude
        return longitude
    }
    override fun onLocationChanged(location: Location) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onBind(arg0: Intent): IBinder? {
        return null
    }
    fun canGetLocation(): Boolean {
        return this.canGetLocation
    }
    fun stopUsingGPS() {
        if (locationManager != null)
            locationManager!!.removeUpdates(this)
    }
    companion object {
        private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10
        private val MIN_TIME_BW_UPDATES = 1000L * 60 * 1
    }
}