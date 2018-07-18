package com.lksh.dev.lkshassistant.presentation.fragments

import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.lksh.dev.lkshassistant.R
import com.lksh.dev.lkshassistant.domain.*
import com.lksh.dev.lkshassistant.domain.listeners.OnMapInteractionListener
import com.lksh.dev.lkshassistant.domain.model.HouseInfo
import kotlinx.android.synthetic.main.fragment_map.*
import org.mapsforge.core.graphics.Filter
import org.mapsforge.core.model.BoundingBox
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.model.MapViewPosition
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.InternalRenderTheme
import java.io.File
import java.io.IOException
import kotlin.concurrent.thread

class MapBoxFragment : Fragment(), OnMapInteractionListener {
    private val TAG = "LKSH_MAP_F"

    private var isFirstStart = true
    private var myPos: LatLong? = null
    private var posMarker: TappableMarker? = null
    private var working = true
    private var trackMe = false
    private var locationManager: LocationManager? = null
    private var mapView: MapView? = null
    private var mapViewPos: MapViewPosition? = null
    private lateinit var mapDataStore: MapFile
    private var tileRendererLayer: TileRendererLayer? = null

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            updateMyLocation(location.latitude, location.longitude)
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "view created")
        if (isFirstStart)
            initOnce()
        mapView = view.findViewById(R.id.mapViewFr)
        initCycle()
        isFirstStart = false
        if (gotoPos != null) {
            val curPos = mapView!!.model.mapViewPosition.center
            mapView!!.model.mapViewPosition.moveCenterAndZoom(gotoPos!!.longitude
                    - curPos.longitude, gotoPos!!.latitude - curPos.latitude,
                    (19 - mapView!!.model.mapViewPosition.zoomLevel).toByte(), true)
        }
    }

    override fun onPause() {
        working = false
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        working = true
    }

    override fun onDestroyView() {
        mapViewPos = mapView!!.model.mapViewPosition
        // mapView!!.destroyAll()
        // mapView!!.destroy()
        // mapView = null
        // need to destroy mapView to avoid memory leak
        // but it follows from this is bug in next map init: map isn't visible
        super.onDestroyView()
    }

    override fun onDestroy() {
        if (mapView != null)
            mapView!!.destroyAll()
        AndroidGraphicFactory.clearResourceMemoryCache()
        super.onDestroy()
    }

    fun setNightTheme() {
        mapView!!.model.displayModel.filter = Filter.INVERT
    }

    fun setLigthTheme() {
        mapView!!.model.displayModel.filter = Filter.NONE
    }

    private fun initCycle() {
        Log.d(TAG + "_I_CYCLE", "start")
        setupMap()
        setHouseMarkers()
        setMyPosButton.setOnClickListener {
            showMyPos(center = true)
            //nextTheme()
        }
        Log.d(TAG, "tracking: $trackMe")
        posAutoSwitch.setOnCheckedChangeListener { _, checked ->
            trackMe = checked
            Log.d(TAG, "tracking: $trackMe")
        }
        posAutoSwitch.isChecked = trackMe
        Log.d(TAG + "_I_CYCLE", "end")
    }

    private fun nextTheme() {
        mapView!!.model.displayModel.filter = when (mapView!!.model.displayModel.filter) {
            Filter.NONE -> Filter.INVERT
            Filter.INVERT -> Filter.GRAYSCALE
            Filter.GRAYSCALE -> Filter.GRAYSCALE_INVERT
            Filter.GRAYSCALE_INVERT -> Filter.NONE
        }
    }

    private fun initOnce() {
        Log.d(TAG + "_I_ONCE", "start")
        mapDataStore = MapFile(prepareMapData())
        activity!!.startService(Intent(activity, LocationTrackingService::class.java))
        startGPSTrackingThread()
        myPos = LatLong(Bundle().getDouble(LAT, defaultLat), Bundle().getDouble(LONG, defaultLong))
        Log.d(TAG + "_I_ONCE", "end")
    }

    private fun startGPSTrackingThread() {
        locationManager = activity!!.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager?
        thread(name = "PosThread", isDaemon = true) {
            Looper.prepare()
            val TAG = "LKSH_MAP_GPS_THR"
            while (true) {
                if (working) {
                    try {
                        // Request location updates
                        for (locListener in LocationTrackingService.locationListeners)
                            locationManager?.requestLocationUpdates(locListener.second, 0L,
                                    0f, locationListener)
                        if (LocationTrackingService.locationListeners.size == 0)
                            Toast.makeText(activity!!.applicationContext,
                                    "I can't get location providers. Do you turn on GPS?",
                                    Toast.LENGTH_SHORT).show()
                    } catch (e: SecurityException) {
                        Log.d(TAG, e.message, e)
                    }
                    showMyPos(false, trackMe)
                }
                Thread.sleep(1000 * 2) // 0.5 updates/s
            }
        }
        Log.d(TAG, "GPS started")
    }

    private fun setHouseMarkers() {
        for (house in houseCoordinates) {
            val marker = TappableMarker(ResourcesCompat.getDrawable(resources,
                    R.drawable.invisible, null)!!,
                    house, this)
            mapView!!.layerManager.layers.add(marker)
        }
        Log.d(TAG, "Houses marked")
    }

    private fun showMyPos(showAccuracy: Boolean = true, center: Boolean) {
        var endLocationAccuracy = 55.0f
        var endLocation: Location? = null

        for (locationListener in LocationTrackingService.locationListeners) {
            if (locationListener.first.lastLocation.hasAccuracy() &&
                    locationListener.first.lastLocation.accuracy < endLocationAccuracy) {
                endLocation = locationListener.first.lastLocation
                endLocationAccuracy = endLocation.accuracy
            }
        }

        if (endLocation == null)
            Toast.makeText(activity!!.applicationContext, "Unable to get your position",
                    Toast.LENGTH_SHORT).show()
        else {
            updateMyLocation(endLocation.latitude, long = endLocation.longitude)
            setLocation(myPos!!, if (showAccuracy) endLocation.accuracy else 0.toFloat(), center)
        }
    }

    private fun updateMyLocation(lat: Double, long: Double) {
        myPos = LatLong(lat, long)
    }

    private fun setLocation(pos: LatLong, accuracy: Float = 0.toFloat(), center: Boolean) {
        if (posMarker != null)
            mapView!!.layerManager.layers.remove(posMarker)
        val drawable = ResourcesCompat.getDrawable(resources,
                android.R.drawable.radiobutton_on_background,
                null)!!
        val marker = TappableMarker(drawable, HouseInfo(pos,
                "Your position",
                0.0001,
                BuildingType.NONE), this)
        posMarker = marker
        mapView!!.layerManager.layers.add(marker)
        //set marker on my position.

        if (center)
            mapView!!.model.mapViewPosition.center = pos
        if (accuracy != 0.toFloat())
            Toast.makeText(activity!!.applicationContext, "Accuracy is $accuracy m",
                    Toast.LENGTH_SHORT).show()
    }

    private fun setMapPos() {
        if (mapViewPos != null) {
            mapView!!.model.mapViewPosition.mapPosition = mapViewPos!!.mapPosition
            mapView!!.model.mapViewPosition.mapLimit = mapViewPos!!.mapLimit
        } else {
            mapView!!.setCenter(myPos)
            mapView!!.setZoomLevel(19.toByte())
            mapView!!.model.mapViewPosition.mapLimit = BoundingBox(minLat, minLong, maxLat, maxLong)
        }
        mapView!!.setZoomLevelMax(22)
        mapView!!.setZoomLevelMin(16)
    }

    private fun setupMap() {
        val TAG = TAG + "_INIT"
        if (mapView == null)
            throw NullPointerException("mapView is empty")
        try {
            Log.d(TAG, "map fragment setup started")
            AndroidGraphicFactory.createInstance(activity!!.application)
            mapView!!.isClickable = true
            mapView!!.mapScaleBar.isVisible = true
            mapView!!.setBuiltInZoomControls(false)
            mapView!!.mapZoomControls.isShowMapZoomControls = false

            setMapPos()
            val tileCache = AndroidUtil.createTileCache(activity!!.applicationContext, "mapcache",
                    mapView!!.model.displayModel.tileSize, 1f, //256
                    mapView!!.model.frameBufferModel.overdrawFactor) //1.2
            tileRendererLayer = TileRendererLayer(tileCache, mapDataStore,
                    mapView!!.model.mapViewPosition, AndroidGraphicFactory.INSTANCE)
            tileRendererLayer!!.setXmlRenderTheme(InternalRenderTheme.DEFAULT)
            mapView!!.layerManager.layers.add(tileRendererLayer)
            Log.d(TAG, "tileSize: ${mapView!!.model.displayModel.tileSize}; " +
                    "overdrawFactor: ${mapView!!.model.frameBufferModel.overdrawFactor}")
            Log.d(TAG, "Map fragment setup successfully")
        } catch (e: IOException) {
            Log.i(TAG, "Map create failed. The app have all needed permissions?")
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }
    }

    private fun prepareMapData(): File {
        val mapPath = activity!!.applicationContext.filesDir
        val mapFolder = File(mapPath, "lksh")
        if (!mapFolder.exists())
            mapFolder.mkdir()
        Log.d("MAP", mapFolder.absolutePath)
        val mapFile = File(mapFolder, "map.map")
        if (!mapFile.exists()) {
            mapFile.createNewFile()
            activity!!.assets.open("map.map").copyTo(mapFile.outputStream())
            Log.d("MAP", "copy from assets to ${mapFile.absolutePath}")
        } else {
            Log.d("MAP", "${mapFile.absolutePath} already exists")
        }
        return mapFile
    }

    override fun dispatchClickBuilding(marker: HouseInfo) {
        if (marker.buildingType == BuildingType.HOUSE)
            activity!!.supportFragmentManager.beginTransaction().add(R.id.activity_main,
                    BuildingInfoFragment.newInstance(marker.name)).commit()
        else
            Toast.makeText(activity!!.applicationContext,
                    "This is ${marker.name}",
                    Toast.LENGTH_SHORT).show()
    }

    fun setPosByHouseName(name: String): Boolean {
        gotoPos = findHouseLatLong(name)
        mapView!!.model.mapViewPosition.center = gotoPos
        return gotoPos != null
    }

    companion object {
        private const val ARG_LAT = "lat"
        private const val ARG_LONG = "long"

        private var gotoPos: LatLong? = null

        fun newInstance(param1: String, param2: String) =
                MapBoxFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_LONG, param1)
                        putString(ARG_LAT, param2)
                    }
                }

        private fun findHouseLatLong(name: String): LatLong? {
            if (name.length == 3 && name.startsWith("ГК"))
                return findHouseLatLong("ГК")
            for (house in houseCoordinates)
                if (house.name == name)
                    return house.latLong
            return null
        }
    }
}
