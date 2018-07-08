package com.lksh.dev.lkshassistant

import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import kotlinx.android.synthetic.main.activity_map.*
import org.mapsforge.core.graphics.Bitmap
import org.mapsforge.core.model.BoundingBox
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.layer.overlay.Marker
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.InternalRenderTheme
import java.io.File


private const val defaultLat = 57.85760 //coordinates of dormitory
private const val defaultLong = 41.71000
val LAT = "LAT"
val LONG = "LONG"

class MapActivity : AppCompatActivity() {
    private val TAG = "LKSH_MAP_A"
    private var myPos = LatLong(defaultLat, defaultLong)

    private fun setupMap() {
        if (mapView == null)
            throw NullPointerException("mapView is empty")
        try {
            Log.d(TAG, "map fragment setup started")
            AndroidGraphicFactory.createInstance(application)

            mapView.isClickable = true
            mapView.mapScaleBar.isVisible = true
            mapView.setBuiltInZoomControls(false)

            mapView.mapZoomControls.isShowMapZoomControls = true

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
            /*val boundingBox = BoundingBox(minLat, minLong, maxLat, maxLong)
            val dimension = mapView.model.mapViewDimension.dimension

            mapView.model.mapViewPosition.mapPosition = MapPosition(boundingBox.centerPoint,
                LatLongUtils.zoomForBounds(dimension, boundingBox, mapView.model.displayModel.tileSize))
            */
            Log.d(TAG, "Map fragment setup successfully")
            setPos()
            Log.d(TAG, "dining room's position is marked (but it isn't exactly)")
//            mapView.setGestureDetector(GestureDetector())
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }
    }

    private fun setPos() {
        val drawable = resources.getDrawable(android.R.drawable.radiobutton_on_background)
        val marker = TappableMarker(drawable, myPos)
        mapView.layerManager.layers.add(marker)
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
