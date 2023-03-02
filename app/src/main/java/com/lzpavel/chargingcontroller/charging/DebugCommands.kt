package com.lzpavel.chargingcontroller.charging

import android.util.Log

class DebugCommands : Commands {

    val LOG_TAG = "DebugCommands"

    var switchVal = true
    var currentVal = "1000000"

    override fun setSwitch(state: Boolean) {
        switchVal = state
        Log.d(LOG_TAG, "setSwitch value $switchVal")
    }

    override fun getSwitch(): Boolean {
        Log.d(LOG_TAG, "getSwitch value $switchVal")
        return switchVal
    }

    override fun setCurrent(value: String) {
        currentVal = value
        Log.d(LOG_TAG, "setCurrent value $currentVal")
    }

    override fun getCurrent(): String {
        Log.d(LOG_TAG, "getCurrent value $currentVal")
        return currentVal
    }

    override fun resetBatteryStats() {
        Log.d(LOG_TAG, "resetBatteryStats")
    }
}