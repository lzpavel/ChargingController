package com.lzpavel.chargingcontroller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import com.lzpavel.chargingcontroller.charging.ChargingControl

class ChargingReceiver(chargingControl: ChargingControl) : BroadcastReceiver() {

    private val chargingControl: ChargingControl

    //var onBatteryChanged: ((isPlugged: Boolean, level: Int) -> Unit)? = null

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


            //onBatteryChanged?.invoke(isPlugged, percent.toInt())


            //onBatteryChanged?.invoke()

            //var isPlugged: Boolean = false
            //val plugged: Int = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            //isPlugged = plugged == 0




            /*val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            var percentNow: Float = level * 100 / scale.toFloat()*/
            //intent.getIntExtra(BatteryManager.BATTERY_PROPERTY_CAPACITY, -1)
            //val batteryManager = BatteryManager()
            //batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            //intent.
        }


    }

}