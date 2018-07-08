package com.lksh.dev.lkshassistant

import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

private const val ARG_LATITUDE = "lat"
private const val ARG_LONGITUDE = "long"
const val minLat = 57.855300
const val maxLat = 57.858790
const val minLong = 41.708843
const val maxLong = 41.717549

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MapFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MapFragment : Fragment() {
    //private var listener: OnFragmentInteractionListener? = null
    private var mapView: MapView? = null
    private val TAG = "LKSH_MAP"

    private fun setupMap() {
        if (mapView == null)
            throw NullPointerException("mapView is empty")
        try {
            Log.d(TAG, "map fragment setup started")
            AndroidGraphicFactory.createInstance(activity!!.application)

            mapView!!.isClickable = true
            mapView!!.mapScaleBar.isVisible = true
            mapView!!.setBuiltInZoomControls(true)

            mapView!!.mapZoomControls.isShowMapZoomControls = true

            val mapDataStore = MapFile(prepareMapData())
            val tileCache = AndroidUtil.createTileCache(context, "mapcache",
                    mapView!!.model.displayModel.tileSize, 1f,
                    mapView!!.model.frameBufferModel.overdrawFactor)
            val tileRendererLayer = TileRendererLayer(tileCache, mapDataStore,
                    mapView!!.model.mapViewPosition, AndroidGraphicFactory.INSTANCE)
            tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT)
            mapView!!.layerManager.layers.add(tileRendererLayer)

            mapView!!.setCenter(LatLong(Bundle().getDouble(ARG_LATITUDE), Bundle().getDouble(ARG_LONGITUDE)))
            mapView!!.mapZoomControls.zoomLevelMax = 12
            mapView!!.mapZoomControls.zoomLevelMin = 1
            mapView!!.setZoomLevel(6.toByte())
            /*val boundingBox = BoundingBox(minLat, minLong, maxLat, maxLong)
            val dimension = mapView!!.model.mapViewDimension.dimension

            mapView!!.model.mapViewPosition.mapPosition = MapPosition(boundingBox.centerPoint,
                LatLongUtils.zoomForBounds(dimension, boundingBox, mapView!!.model.displayModel.tileSize))
            */
            Log.d(TAG, "Map fragment setup successfully")
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }
    }

    private fun prepareMapData(): File {
        val mapFolder = File(Environment.getExternalStorageDirectory(), "lksh")
        if (!mapFolder.exists())
            mapFolder.mkdir()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO: request permissions: write files and gps
        super.onCreate(savedInstanceState)
        Log.i(TAG, "I'm started (onCreate)")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated(...)")
        //mapView = view.findViewById(R.id.mapViewFr) as MapView
        mapView = view as MapView
        //activity!!.setContentView(mapView)
        setupMap()
    }

    override fun onDestroy() {
        mapView!!.destroyAll()
        AndroidGraphicFactory.clearResourceMemoryCache()
        super.onDestroy()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView(...)")
        return inflater.inflate(R.layout.fragment_map, container, false).findViewById(R.id.mapViewFr)
    }

    override fun onDestroyView() {
        if (mapView != null)
            mapView!!.destroyAll()
        AndroidGraphicFactory.clearResourceMemoryCache()
        super.onDestroyView()
    }

    fun setNightTheme() {
        mapView!!.model.displayModel.filter = Filter.INVERT
    }
    fun setLigthTheme() {
        mapView!!.model.displayModel.filter = Filter.NONE
    }
}
//57.85722%2C41.71562#map=18/57.85760/41.70948
//geoURI: geo://57.85760,41.70948?z=18
