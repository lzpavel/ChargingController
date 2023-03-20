package com.lzpavel.chargingcontroller

import android.content.Context

object Settings {

    var mainActivity: MainActivity? = null

    var levelLimit: Int = 80
        set(value) {
            field = value
            mainActivity?.mainView?.levelLimit?.value = value.toString()
        }
    var currentLimit: Int = 1000000
        set(value) {
            field = value
            mainActivity?.mainView?.currentLimit?.value = value.toString()
        }
    var isAutoResetBatteryStats: Boolean = false
        set(value) {
            field = value
            mainActivity?.mainView?.checkBoxAutoResetBatteryStats?.value = value
        }
    var isAutoSwitchOn: Boolean = false
        set(value) {
            field = value
            mainActivity?.mainView?.checkBoxAutoSwitchOn?.value = value
        }
    var isAutoStop: Boolean = false
        set(value) {
            field = value
            mainActivity?.mainView?.checkBoxAutoStop?.value = value
        }

    var isControl: Boolean = false
        set(value) {
            field = value
            mainActivity?.mainView?.switchControl?.value = value
        }
    var isChargingSwitch: Boolean = true
        set(value) {
            field = value
            mainActivity?.mainView?.switchCharge?.value = value
        }

    fun save() {
        val sharedPref = mainActivity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt("levelLimit", levelLimit)
            putInt("currentLimit", currentLimit)
            putBoolean("isAutoResetBatteryStats", isAutoResetBatteryStats)
            putBoolean("isAutoSwitchOn", isAutoSwitchOn)
            putBoolean("isAutoStop", isAutoStop)
            apply()
        }
    }

    fun load() {
        val sharedPref = mainActivity?.getPreferences(Context.MODE_PRIVATE) ?: return
        levelLimit = sharedPref.getInt("levelLimit", 80)
        currentLimit = sharedPref.getInt("currentLimit", 1000000)
        isAutoResetBatteryStats = sharedPref.getBoolean("isAutoResetBatteryStats", false)
        isAutoSwitchOn = sharedPref.getBoolean("isAutoSwitchOn", false)
        isAutoStop = sharedPref.getBoolean("isAutoStop", false)
    }
}