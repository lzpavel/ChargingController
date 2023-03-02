package com.lzpavel.chargingcontroller.charging

import android.util.Log
import com.lzpavel.chargingcontroller.Settings
import kotlin.concurrent.thread

class ChargingControl() {

    val LOG_TAG = "ChargingControl"

    var commands: Commands = DebugCommands()
    var levelNow = -1
    var levelLimit = -1
    var currentLimit = 1000000
    var isCheckingCurrent = false
    var isPlugged = false

    var isAutoResetBatteryStats = false
    var isAutoSwitchOn = false
    var isAutoStop = false

    var mode = Mode.IDLE

    var onStopControl: () -> Unit = {}
    var onChangeChargingSwitch: (Boolean) -> Unit = {}

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
                    if (isAutoSwitchOn) {
                        commands.setSwitch(true)
                        onChangeChargingSwitch.invoke(true)
                    }
                    if (isAutoResetBatteryStats) {
                        commands.resetBatteryStats()
                    }
                    if (isAutoStop) {
                        onStopControl.invoke()
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
        onChangeChargingSwitch.invoke(false)
        isCheckingCurrent = false
    }

    private fun startCheckingCurrent() {
        thread {
            while (isCheckingCurrent) {
                Thread.sleep(1000)
                Log.d(LOG_TAG, "Monitoring current: tick")
                checkCurrent()
            }
        }
    }

    private fun checkCurrent() {
        val rxCurrent = commands.getCurrent().toInt()
        Log.d(LOG_TAG, "Monitoring current: rxCurrent:$rxCurrent currentLimit:$currentLimit")
        if (rxCurrent != currentLimit && isCheckingCurrent && rxCurrent != 0) {
            commands.setCurrent(currentLimit.toString())
            Log.d(LOG_TAG, "Monitoring: write current:$currentLimit")
        }
    }

    private fun checkLevel() {
        Log.d(LOG_TAG, "Monitoring level: levelNow:$levelNow levelLimit:$levelLimit")
        if (levelNow >= levelLimit) {
            Log.d(LOG_TAG, "Monitoring level: switchOff")
            stopCharging()
            if (isAutoStop || isAutoSwitchOn || isAutoResetBatteryStats) {
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