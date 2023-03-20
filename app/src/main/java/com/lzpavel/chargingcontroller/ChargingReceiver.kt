package com.lzpavel.chargingcontroller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import com.lzpavel.chargingcontroller.charging.ChargingControl

class ChargingReceiver(chargingControl: ChargingControl) : BroadcastReceiver() {

    private val chargingControl: ChargingControl

    init {
        this.chargingControl = chargingControl
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {

            val isPlugged = 0 != intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)

            val mLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val mScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val percent: Float = mLevel * 100 / mScale.toFloat()

            chargingControl.onBatteryChanged(isPlugged, percent.toInt())
        }

    }

}