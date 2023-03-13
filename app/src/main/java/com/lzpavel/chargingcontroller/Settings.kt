package com.lzpavel.chargingcontroller

object Settings {

    var mainActivity: MainActivity? = null

    var levelLimit: Int = 80
    var currentLimit: Int = 1000000
    var isAutoResetBatteryStats: Boolean = false
    var isAutoSwitchOn: Boolean = false
    var isAutoStop: Boolean = false

    var isControl: Boolean = false
    var isChargingSwitch: Boolean = true

    fun updateUi() {
        mainActivity?.updateUi()
    }
}