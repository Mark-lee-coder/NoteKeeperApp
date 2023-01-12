package com.example.notekeeperapp

import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.example.notekeeperapp.files.NoteInfo
import com.example.notekeeperapp.pseudomanager.PseudoLocationManager
import com.example.notekeeperapp.pseudomanager.PseudoMessagingConnection
import com.example.notekeeperapp.pseudomanager.PseudoMessagingManager

class NoteGetTogetherHelper(val context: Context, private val lifecycle: Lifecycle) : LifecycleObserver {
    init {
        lifecycle.addObserver(this)
    }

    private val tag = this::class.simpleName
    var currentLat = 0.0
    var currentLong = 0.0
    private val msgManager = PseudoMessagingManager(context)
    var msgConnection : PseudoMessagingConnection? = null
    private val locManager = PseudoLocationManager(context) { lat, long ->
        currentLat = lat
        currentLong = long
        Log.d(tag, "Location Callback Lat: $currentLat Long: $currentLong")
    }

    fun sendMessage(note: NoteInfo) {
        val getTogetherMessage = "$currentLat | $currentLong | ${note.title} | ${note.course?.title}"
        msgConnection?.send(getTogetherMessage)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun startHandler() {
        Log.d(tag, "startHandler")
        locManager.start()
        msgManager.connect { connection ->
            Log.d(tag, "Connection callback - Lifecycle state: ${lifecycle.currentState}")
            if(lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                msgConnection = connection
            }
            else {
                msgConnection?.disconnect()
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopHandler() {
        Log.d(tag, "stopHandler")
        locManager.stop()
        msgConnection?.disconnect()
    }
}