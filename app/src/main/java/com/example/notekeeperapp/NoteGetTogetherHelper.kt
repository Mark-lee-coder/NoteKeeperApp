package com.example.notekeeperapp

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleObserver
import com.example.notekeeperapp.pseudomanager.PseudoLocationManager

class NoteGetTogetherHelper(val context: Context) : LifecycleObserver {
    private val tag = this::class.simpleName
    var currentLat = 0.0
    var currentLong = 0.0
    val locManager = PseudoLocationManager(context) { lat, long ->
        currentLat = lat
        currentLong = long
        Log.d(tag, "Location Callback Lat: $currentLat Long: $currentLong")
    }
}