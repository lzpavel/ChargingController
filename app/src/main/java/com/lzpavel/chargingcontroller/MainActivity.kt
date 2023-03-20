package com.lzpavel.chargingcontroller

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.lzpavel.chargingcontroller.appview.MainView


class MainActivity : ComponentActivity() {

    val LOG_TAG = "MainActivity"

    val mainView = MainView()

    private lateinit var chargingService: ChargingService

    private val chargingServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as ChargingService.ChargingServiceBinder
            chargingService = binder.getService()
            Settings.isChargingSwitch = chargingService.chargingControl.commands.getSwitch()

            Log.d(LOG_TAG, "onServiceConnected")
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d(LOG_TAG, "onServiceDisconnected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "onCreate")
        setContent {
            mainView.setView()
        }

        ChargingNotification.createChannel(getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)

        mainView.onClickButtonSetCurrent {
            chargingService.chargingControl.commands.setCurrent(mainView.currentLimit.value)
        }
        mainView.onClickButtonSwitchOn {
            chargingService.chargingControl.commands.setSwitch(true)
        }
        mainView.onClickButtonSwitchOff {
            chargingService.chargingControl.commands.setSwitch(false)
        }
        mainView.onClickButtonResetBatteryStats {
            chargingService.chargingControl.commands.resetBatteryStats()
        }
        mainView.onClickButtonStart {
            startControl()
        }
        mainView.onClickButtonStop {
            chargingService.stopChargingService()
        }
        mainView.onClickButtonTest {

        }

        mainView.onCheckedChangeSwitchControl {
            Log.d(LOG_TAG, "onCheckedChangeSwitchControl:$it")
            if (it) {
                startControl()
            } else {
                chargingService.stopChargingService()
            }
        }
        mainView.onCheckedChangeSwitchCharge {
            chargingService.chargingControl.commands.setSwitch(it)
        }
        mainView.onCheckedChangeCheckBoxAutoResetBatteryStats = {
            Settings.isAutoResetBatteryStats = it
        }
        mainView.onCheckedChangeCheckBoxAutoSwitchOn = {
            Settings.isAutoSwitchOn = it
        }
        mainView.onCheckedChangeCheckBoxAutoStop = {
            Settings.isAutoStop = it
        }
        mainView.onDoneLevelLimit = {
            initLevelLimit(it)
        }
        mainView.onDoneCurrentLimit = {
            initCurrentLimit(it)
        }



    }

    override fun onStart() {
        super.onStart()
        Log.d(LOG_TAG, "onStart")
        Settings.mainActivity = this
        Settings.load()
        Intent(this, ChargingService::class.java).also { intent ->
            bindService(intent, chargingServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(LOG_TAG, "onStop")
        if (mainView.isChangedSettings) {
            Settings.save()
            mainView.isChangedSettings = false
        }
        Settings.mainActivity = null
        unbindService(chargingServiceConnection)
    }

    fun startControl() {
        initLevelLimit(mainView.levelLimit.value)
        initCurrentLimit(mainView.currentLimit.value)
        startService(Intent(this, ChargingService::class.java))
    }

    fun initLevelLimit(value: String) {
        val lim = value.toIntOrNull()
        if ((lim != null) && (lim >= 0) && (lim <= 100)) {
            Settings.levelLimit = lim
        } else {
            Settings.levelLimit = 80
            mainView.levelLimit.value = "80"
        }
    }
    fun initCurrentLimit(value: String) {
        val lim = value.toIntOrNull()
        if ((lim != null) && (lim >= 0)) {
            Settings.currentLimit = lim
        } else {
            Settings.currentLimit = 1000000
            mainView.currentLimit.value = "1000000"
        }
    }

}

