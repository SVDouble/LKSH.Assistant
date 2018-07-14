package com.lksh.dev.lkshassistant.domain

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log

class LocationTrackingService : Service() {
    private var locationManager: LocationManager? = null

    private fun requestLocationUpdates(locationManager: LocationManager?, provider: String) {
        try {
            val locationListener = LTRLocationListener(provider)
            locationManager?.requestLocationUpdates(provider, internal, distance, locationListener)
            locationListeners.add(locationListener to provider)
        } catch (e: SecurityException) {
            Log.e(tag, "Fail to request location update", e)
        } catch (e: IllegalArgumentException) {
            Log.e(tag, "$provider provider does not exist")
        }
    }

    override fun onBind(intent: Intent?) = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onCreate() {
        if (locationManager == null)
            locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = arrayOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER)
        for (provider in providers)
            try {
                requestLocationUpdates(locationManager, provider)
            } catch (e: Exception) {
                Log.e(tag, e.message, e)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (locationManager != null)
            for (locationListener in locationListeners) { // <- fix
                try {
                    locationManager?.removeUpdates(locationListener.first)
                } catch (e: Exception) {
                    Log.w(tag, "Failed to remove location listeners")
                }
            }
    }

    companion object {
        const val tag = "LocationTrackingService"
        const val internal = 1000.toLong() // In milliseconds
        const val distance = 0f // In meters
        val locationListeners = arrayListOf<Pair<LTRLocationListener, String>>()

        class LTRLocationListener(provider: String) : android.location.LocationListener {
            val lastLocation = Location(provider)

            override fun onLocationChanged(location: Location?) {
                lastLocation.set(location)
                if (location != null)
                    Log.v("LKSH_LOCATION-SERVICE", "update location to ${location.latitude}, " +
                            "${location.longitude} (${location.accuracy})")
                else
                    Log.v("LKSH_LOCATION-SERVICE", "null location")
            }

            override fun onProviderDisabled(provider: String?) {}
            override fun onProviderEnabled(provider: String?) {}
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }
    }
}