package com.lksh.dev.lkshassistant

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import kotlinx.android.synthetic.main.activity_map.*
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.InternalRenderTheme
import java.io.File

private const val lat = 57.85760 //coordinates of dormitory
private const val long = 41.71000

class MapActivity : AppCompatActivity() {
    private val TAG = "LKSH_MAP_A"

    private fun setupMap() {
        if (mapView == null)
            throw NullPointerException("mapView is empty")
        try {
            Log.d(TAG, "map fragment setup started")
            AndroidGraphicFactory.createInstance(application)

            mapView.isClickable = true
            mapView.mapScaleBar.isVisible = true
            //mapView.setBuiltInZoomControls(true)

            mapView.mapZoomControls.isShowMapZoomControls = true

            val mapDataStore = MapFile(prepareMapData())
            val tileCache = AndroidUtil.createTileCache(applicationContext, "mapcache",
                    mapView.model.displayModel.tileSize, 1f,
                    mapView.model.frameBufferModel.overdrawFactor)
            val tileRendererLayer = TileRendererLayer(tileCache, mapDataStore,
                    mapView.model.mapViewPosition, AndroidGraphicFactory.INSTANCE)
            tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT)
            mapView.layerManager.layers.add(tileRendererLayer)

            mapView.setCenter(LatLong(lat, long))
            mapView.mapZoomControls.zoomLevelMax = 21
            mapView.mapZoomControls.zoomLevelMin = 16
            mapView.setZoomLevel(19.toByte())
            /*val boundingBox = BoundingBox(minLat, minLong, maxLat, maxLong)
            val dimension = mapView.model.mapViewDimension.dimension

            mapView.model.mapViewPosition.mapPosition = MapPosition(boundingBox.centerPoint,
                LatLongUtils.zoomForBounds(dimension, boundingBox, mapView.model.displayModel.tileSize))
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
        setupMap()
    }

    override fun onDestroy() {
        if (mapView != null)
            mapView.destroyAll()
        AndroidGraphicFactory.clearResourceMemoryCache()
        super.onDestroy()
    }
}
