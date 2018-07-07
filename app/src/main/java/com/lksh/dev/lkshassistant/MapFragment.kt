package com.lksh.dev.lkshassistant

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.mapsforge.core.graphics.Filter
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.InternalRenderTheme
import java.io.File
import java.io.FileNotFoundException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_LATITUDE = "lat"
private const val ARG_LONGITUDE = "long"

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidGraphicFactory.createInstance(activity!!.application)
        try {
            mapView = MapView(context)
            mapView!!.setClickable(true)
            mapView!!.mapScaleBar.setVisible(true)
            mapView!!.setBuiltInZoomControls(true)

            val tileCache = AndroidUtil.createTileCache(context, "mapcache",
                    mapView!!.model.displayModel.tileSize, 1f,
                    mapView!!.model.frameBufferModel.overdrawFactor)
            val mapFile = File(Environment.getExternalStorageDirectory(), "map.map")
            if (!mapFile.exists()) {
                mapFile.createNewFile()
                throw (FileNotFoundException(Environment.getExternalStorageDirectory().absolutePath + " map.map"))
            }
            val mapDataStore = MapFile(mapFile)
            val tileRendererLayer = TileRendererLayer(tileCache, mapDataStore,
                    mapView!!.model.mapViewPosition, AndroidGraphicFactory.INSTANCE)
            tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT)

            mapView!!.layerManager.layers.add(tileRendererLayer);

            mapView!!.setCenter(LatLong(57.51457, 41.42603))
            mapView!!.setZoomLevel(12.toByte())

        } catch (e: Exception) {
            Log.e("LKSH-MAP", e.message, e)
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

    // TODO: Rename method, update argument and hook method into UI event
    /*fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }*/

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
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
         * @param posLat latitude of center.
         * @param posLong longitude of center.
         * @return A new instance of fragment MapFragment.
         */
        @JvmStatic
        fun newInstance(posLat: Double = 57.51074, posLong: Double = 41.35674) =
                MapFragment().apply {
                    arguments = Bundle().apply {
                        putDouble(ARG_LATITUDE, posLat)
                        putDouble(ARG_LONGITUDE, posLong)
                    }
                }
    }

    /*

    public static MapaForgeFragment newInstance() {
        MapaForgeFragment fragment = new MapaForgeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
     */

}
