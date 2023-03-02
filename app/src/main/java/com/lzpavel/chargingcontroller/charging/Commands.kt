package com.lzpavel.chargingcontroller.charging

import com.lzpavel.chargingcontroller.SuperUser

interface Commands {
    fun setSwitch(state: Boolean)

    fun getSwitch(): Boolean

    fun setCurrent(value: String)

    fun getCurrent(): String

    fun resetBatteryStats()
}