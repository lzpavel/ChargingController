package com.lzpavel.chargingcontroller

object Settings {
    private var isInit: Boolean = false
    var levelLimit: Int = 80
    var currentLimit: Int = 1000000
    var isControl: Boolean = false
    var isChargingSwitch: Boolean = true
    var isAutoResetBatteryStats: Boolean = false
    var isAutoSwitchOn: Boolean = false
    var isAutoStop: Boolean = false

    fun load() {
        isInit = true
    }
}