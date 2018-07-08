package com.lksh.dev.lkshassistant

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.mapsforge.core.graphics.Filter
import org.mapsforge.core.model.BoundingBox
import org.mapsforge.core.model.LatLong
import org.mapsforge.core.model.MapPosition
import org.mapsforge.core.util.LatLongUtils
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.InternalRenderTheme
import java.io.File
import java.io.FileNotFoundException


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_LATITUDE = "lat"
private const val ARG_LONGITUDE = "long"
private const val minLat = 57.855300
private const val maxLat = 57.858790
private const val minLong = 41.708843
private const val maxLong = 41.717549

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
    private var listener: OnFragmentInteractionListener? = null
    private var mapView: MapView? = null

    /*
    String fileList[] = am.list(path);

            for (String fileName : fileList) {

                // open file within the assets folder
                // if it is not already there copy it to the sdcard
                String pathToDataFile = DATA_PATH + path + "/" + fileName;
                if (!(new File(pathToDataFile)).exists()) {
                }
            }
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO: request permissions: write files and gps
        super.onCreate(savedInstanceState)
        Log.i("START", "I'm started")
        Log.i("MAP", "map fragment started")
        for (it in activity!!.assets.list("."))
            Log.i("ASSET_PATH", it)

        AndroidGraphicFactory.createInstance(activity!!.application)
        try {
            mapView = MapView(context)
            mapView!!.setClickable(true)
            mapView!!.mapScaleBar.setVisible(true)
            mapView!!.setBuiltInZoomControls(true)

            val tileCache = AndroidUtil.createTileCache(context, "mapcache",
                    mapView!!.model.displayModel.tileSize, 1f,
                    mapView!!.model.frameBufferModel.overdrawFactor)
            val mapFolder = File(Environment.getExternalStorageDirectory(), "lksh")
            if (!mapFolder.exists())
                mapFolder.mkdir()
            val mapFile = File(mapFolder, "map.map")
            if (!mapFile.exists()) {
                mapFile.createNewFile()
                activity!!.assets.open("map.map").copyTo(mapFile.outputStream())
                Log.i("MAP", "copy from assets to ${mapFile.absolutePath}")

                //throw (FileNotFoundException(Environment.getExternalStorageDirectory().absolutePath + "map.map"))
            } else {
                Log.i("MAP", "${mapFile.absolutePath} already exists")
            }
            val mapDataStore = MapFile(mapFile)
            val tileRendererLayer = TileRendererLayer(tileCache, mapDataStore,
                    mapView!!.model.mapViewPosition, AndroidGraphicFactory.INSTANCE)
            tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT)

            mapView!!.layerManager.layers.add(tileRendererLayer)

            mapView!!.mapZoomControls.zoomLevelMax = 3
            mapView!!.mapZoomControls.zoomLevelMin = 1
            mapView!!.setZoomLevel(12.toByte())
            mapView!!.mapZoomControls.isShowMapZoomControls = true
            mapView!!.setBuiltInZoomControls(true)

            mapView!!.setCenter(LatLong(Bundle().getDouble(ARG_LATITUDE), Bundle().getDouble(ARG_LATITUDE)))
            activity!!.findViewById<MapView>(R.id.mapView).mapZoomControls.zoomLevelMax = 1
            val boundingBox = BoundingBox(minLat, minLong, maxLat, maxLong)
            val dimension = mapView!!.model.mapViewDimension.dimension
            mapView!!.model.mapViewPosition.setMapPosition(MapPosition(boundingBox.centerPoint,
                    LatLongUtils.zoomForBounds(dimension, boundingBox,
                            mapView!!.model.displayModel.tileSize)))
            /*
            @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            BoundingBox bb = new BoundingBox(latLong2.latitude,
                    latLong3.longitude, latLong3.latitude, latLong2.longitude);
            Dimension dimension = this.mapView.getModel().mapViewDimension.getDimension();
            this.mapView.getModel().mapViewPosition.setMapPosition(new MapPosition(
                    bb.getCenterPoint(),
                    LatLongUtils.zoomForBounds(
                            dimension,
                            bb,
                            this.mapView.getModel().displayModel.getTileSize())));
        }
    }

    @Override
    protected void addOverlayLayers(Layers layers) {
        Polyline polyline = new Polyline(Utils.createPaint(
                AndroidGraphicFactory.INSTANCE.createColor(Color.BLUE), 8,
                Style.STROKE), AndroidGraphicFactory.INSTANCE);
        List<LatLong> latLongs = new ArrayList<>();
        latLongs.add(latLong2);
        latLongs.add(latLong3);
        polyline.setPoints(latLongs);
        layers.add(polyline);
    }

             */

        } catch (e: Exception) {
            Log.e("LKSH_MAP", e.message, e)
        }
    }

    override fun onDestroy() {
        mapView!!.destroyAll()
        AndroidGraphicFactory.clearResourceMemoryCache()
        super.onDestroy()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        /*if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }*/
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun setNightTheme() {
        mapView!!.model.displayModel.filter = Filter.INVERT
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
         * default params: enter point of dining room
         * @param posLat latitude of center.
         * @param posLong longitude of center.
         * @return A new instance of fragment MapFragment.
         */
        @JvmStatic
        fun newInstance(posLat: Double = 57.85760, posLong: Double = 41.70948) =
                MapFragment().apply {
                    arguments = Bundle().apply {
                        putDouble(ARG_LATITUDE, posLat)
                        putDouble(ARG_LONGITUDE, posLong)
                    }
                }
    }
}
//57.85722%2C41.71562#map=18/57.85760/41.70948
//geoURI: geo://57.85760,41.70948?z=18
