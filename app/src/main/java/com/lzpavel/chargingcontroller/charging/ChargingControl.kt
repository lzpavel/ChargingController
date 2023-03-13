package com.lzpavel.chargingcontroller.charging

import android.util.Log
import com.lzpavel.chargingcontroller.ChargingService
import com.lzpavel.chargingcontroller.Settings
import kotlin.concurrent.thread

class ChargingControl() {

    val LOG_TAG = "ChargingControl"

    var chargingService: ChargingService? = null
    var commands: Commands = DebugCommands()
    var levelNow = -1
    var isCheckingCurrent = false
    var isPlugged = false

    var mode = Mode.IDLE

    fun onBatteryChanged(isPlugged: Boolean, level: Int) {
        Log.d(LOG_TAG, "onBatteryChanged: isPlugged:$isPlugged level:$level")
        this.isPlugged = isPlugged
        levelNow = level
        when (mode) {
            Mode.IDLE -> {}
            Mode.WAIT_PLUG -> {
                if (isPlugged) {
                    mode = Mode.CHARGING
                    startCharging()
                }
            }
            Mode.CHARGING -> {
                checkLevel()
            }
            Mode.WAIT_UNPLUG -> {
                if (!isPlugged) {
                    if (Settings.isAutoSwitchOn) {
                        commands.setSwitch(true)
                        Settings.isChargingSwitch = true
                        Settings.updateUi()
                    }
                    if (Settings.isAutoResetBatteryStats) {
                        commands.resetBatteryStats()
                    }
                    if (Settings.isAutoStop) {
                        chargingService?.stopChargingService()
                    }
                    mode = Mode.IDLE
                }

            }
        }
    }

    fun startControl() {
        val rxCurrent = commands.getCurrent().toInt()
        isPlugged = rxCurrent != 0
        if (isPlugged) {
            mode = Mode.CHARGING
            startCharging()
        } else {
            mode = Mode.WAIT_PLUG
        }
    }

    fun stopControl() {
        mode = Mode.IDLE
        isCheckingCurrent = false
    }

    fun startCharging() {
        isCheckingCurrent = true
        startCheckingCurrent()
    }

    fun stopCharging() {
        commands.setSwitch(false)
        Settings.isChargingSwitch = false
        Settings.updateUi()
        isCheckingCurrent = false
    }

    private fun startCheckingCurrent() {
        thread {
            while (isCheckingCurrent) {
                Thread.sleep(1000)
                Log.d(LOG_TAG, "Check current: tick")
                checkCurrent()
            }
        }
    }

    private fun checkCurrent() {
        val rxCurrent = commands.getCurrent().toInt()
        Log.d(LOG_TAG, "Check current: rxCurrent:$rxCurrent currentLimit:${Settings.currentLimit}")
        if (rxCurrent != Settings.currentLimit && isCheckingCurrent && rxCurrent != 0) {
            commands.setCurrent(Settings.currentLimit.toString())
            Log.d(LOG_TAG, "Check: write current:${Settings.currentLimit}")
        }
    }

    private fun checkLevel() {
        Log.d(LOG_TAG, "Check level: levelNow:$levelNow levelLimit:${Settings.levelLimit}")
        if (levelNow >= Settings.levelLimit) {
            Log.d(LOG_TAG, "Check level: switchOff")
            stopCharging()
            if (Settings.isAutoStop || Settings.isAutoSwitchOn || Settings.isAutoResetBatteryStats) {
                mode = Mode.WAIT_UNPLUG
            } else {
                mode = Mode.IDLE
            }
        }
    }

    enum class Mode {
        IDLE,
        WAIT_PLUG,
        CHARGING,
        WAIT_UNPLUG
    }

}