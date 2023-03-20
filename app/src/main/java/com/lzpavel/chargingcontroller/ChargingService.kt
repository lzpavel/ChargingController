package com.lzpavel.chargingcontroller

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.lzpavel.chargingcontroller.charging.ChargingControl
import com.lzpavel.chargingcontroller.charging.SuperUserCommands


class ChargingService : Service() {

    val LOG_TAG = "ChargingService"

    private val chargingServiceBinder = ChargingServiceBinder()
    var superUser: SuperUser? = null
    var chargingControl = ChargingControl()
    private val chargingReceiver = ChargingReceiver(chargingControl)

    override fun onCreate() {
        super.onCreate()
        try {
            superUser = SuperUser()
            chargingControl.commands = SuperUserCommands(superUser!!)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "SU permission denied")
        }
        registerBroadcastReceiver()
        chargingControl.chargingService = this
        Log.d(LOG_TAG, "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(LOG_TAG, "onStartCommand")
        startForeground(1, ChargingNotification.build(this, "level limit: ${Settings.levelLimit}"))
        chargingControl.startControl()
        Settings.isControl = true

        //return super.onStartCommand(intent, flags, startId)
        //return START_REDELIVER_INTENT
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(LOG_TAG, "onBind")
        return chargingServiceBinder
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.d(LOG_TAG, "onRebind")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(LOG_TAG, "onUnbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterBroadcastReceiver()
        superUser?.close()
        Log.d(LOG_TAG, "onDestroy")
    }

    fun stopChargingService() {
        chargingControl.stopControl()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        Settings.isControl = false
    }

    fun registerBroadcastReceiver() {
        registerReceiver(chargingReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    fun unregisterBroadcastReceiver() {
        unregisterReceiver(chargingReceiver)
    }

    inner class ChargingServiceBinder : Binder() {
        fun getService(): ChargingService = this@ChargingService
    }

}