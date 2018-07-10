package com.lksh.dev.lkshassistant.fragments

import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import kotlinx.android.synthetic.main.fragment_map.*
import org.mapsforge.core.graphics.Filter
import org.mapsforge.core.model.BoundingBox
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.InternalRenderTheme
import java.io.File
import kotlin.concurrent.thread

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

interface OnMapInteractionListener {
    fun dispatchClickBuilding(marker: HouseInfo)
}

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentMapBox.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentMapBox.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FragmentMapBox : Fragment(), OnMapInteractionListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private val TAG = "LKSH_MAP_F"
    private var myPos = LatLong(defaultLat, defaultLong)
    private var posMarker: TappableMarker? = null
    private var working = true
    private var trackMe = false
    private var locationManager: LocationManager? = null
    private var mapView: MapView? = null

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            updateMyLocation(location.latitude, location.longitude)
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    fun setNightTheme() {
        mapView!!.model.displayModel.filter = Filter.INVERT
    }

    fun setLigthTheme() {
        mapView!!.model.displayModel.filter = Filter.NONE
    }

    private fun initAll() {
        myPos = LatLong(Bundle().getDouble(LAT, defaultLat), Bundle().getDouble(LONG, defaultLong))
        setupMap()
        activity!!.startService(Intent(activity, LocationTrackingService::class.java))
        startGPSTrackingThread()
        setHouseMarkers()

        setMyPosButton.setOnClickListener {
            centerByMe()
        }

        Log.d(TAG, "tracking: $trackMe")
        posAutoSwitch.setOnCheckedChangeListener { _, checked ->
            trackMe = checked
            Log.d(TAG, "tracking: $trackMe")
        }
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
                        locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                0L, 0f, locationListener)
                        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                0L, 0f, locationListener)
                    } catch (e: SecurityException) {
                        Log.d(TAG, e.message, e)
                    }
                    if (trackMe) {
                        centerByMe(false)
                        //Log.d(TAG, "Updated pos")
                    } else {
                        //Log.d(TAG, "not updated pos")
                    }
                }
                //Log.d(TAG, "iteration completed")
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
    }

    private fun centerByMe(showAccuracy: Boolean = true) {
        val gpsLocation = LocationTrackingService.locationListeners[0].lastLocation
        val networkLocation = LocationTrackingService.locationListeners[1].lastLocation
        val endLocation: Location

        endLocation = if (!gpsLocation.hasAccuracy() && !networkLocation.hasAccuracy()) {
            Toast.makeText(activity!!.applicationContext, "Unable to get location",
                    Toast.LENGTH_SHORT).show()
            return
        } else if (!gpsLocation.hasAccuracy() || (networkLocation.hasAccuracy()
                        && networkLocation.accuracy < gpsLocation.accuracy)) networkLocation
        else gpsLocation
        updateMyLocation(endLocation.latitude, long = endLocation.longitude)
        setLocation(myPos, if (showAccuracy) endLocation.accuracy else 0.toFloat())
    }

    private fun drawPos() {
        val drawable = ResourcesCompat.getDrawable(resources, android.R.drawable.radiobutton_on_background, null)!!
        val marker = TappableMarker(drawable, HouseInfo(myPos, "Your position", 0.00025,
                BuildingType.USER), this)
        mapView!!.layerManager.layers.add(marker)
        posMarker = marker

    }

    private fun updateMyLocation(lat: Double, long: Double) {
        myPos = LatLong(lat, long)
    }

    private fun setLocation(pos: LatLong, accuracy: Float = 0.toFloat()) {
        if (posMarker != null)
            mapView!!.layerManager.layers.remove(posMarker)
        val drawable = ResourcesCompat.getDrawable(resources, android.R.drawable.radiobutton_on_background, null)!!
        val marker = TappableMarker(drawable, HouseInfo(myPos, "Your position", 0.00025, BuildingType.USER), this)
        mapView!!.layerManager.layers.add(marker)
        mapView!!.model.mapViewPosition.center = pos
        posMarker = marker
        Log.d("LKSH_MAP", "set center to ${pos.latitude} ${pos.longitude} ($accuracy)")
        if (accuracy != 0.toFloat())
            Toast.makeText(activity!!.applicationContext, "Accuracy is $accuracy m",
                    Toast.LENGTH_SHORT).show()
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

            val mapDataStore = MapFile(prepareMapData())
            val tileCache = AndroidUtil.createTileCache(activity!!.applicationContext, "mapcache",
                    mapView!!.model.displayModel.tileSize, 1f,
                    mapView!!.model.frameBufferModel.overdrawFactor)
            val tileRendererLayer = TileRendererLayer(tileCache, mapDataStore,
                    mapView!!.model.mapViewPosition, AndroidGraphicFactory.INSTANCE)
            tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT)
            mapView!!.layerManager.layers.add(tileRendererLayer)

            mapView!!.setCenter(myPos)
            mapView!!.setZoomLevel(19.toByte())
            mapView!!.setZoomLevelMax(22)
            mapView!!.setZoomLevelMin(16)
            mapView!!.model.mapViewPosition.mapLimit = BoundingBox(minLat, minLong, maxLat, maxLong)
            Log.d(TAG, "Map fragment setup successfully")
            drawPos()
            Log.d(TAG, "dining room's position is marked (but it isn't exactly)")

        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }
    }

    private fun prepareMapData(): File {
        val mapFolder = File(Environment.getExternalStorageDirectory(), "lksh")
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
            activity!!.supportFragmentManager.beginTransaction().add(R.id.activity_main, BuildingInfoFragment.newInstance(marker.name)).commit()
        else
            Toast.makeText(activity!!.applicationContext, "This is ${marker.name}", Toast.LENGTH_SHORT).show()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.mapViewFr)
        initAll()
    }

    override fun onDestroy() {
        if (mapView != null)
            mapView!!.destroyAll()
        AndroidGraphicFactory.clearResourceMemoryCache()
        super.onDestroy()
    }

    override fun onPause() {
        working = false
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        working = true
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentMapBox.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentMapBox().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
