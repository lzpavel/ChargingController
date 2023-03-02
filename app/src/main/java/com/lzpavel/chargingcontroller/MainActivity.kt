package com.lzpavel.chargingcontroller

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
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
            mainView.switchControl.value = chargingService.isStarted
            mainView.switchCharge.value = chargingService.chargingControl.commands.getSwitch()

            subscribeSwitches()

            Log.d(LOG_TAG, "onServiceConnected")
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d(LOG_TAG, "onServiceDisconnected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            initLevelLimit(mainView.levelLimit.value)
            initCurrentLimit(mainView.currentLimit.value)
            startService(Intent(this, ChargingService::class.java))
            //chargingService.chargingControl.startControl(mainView.levelLimit.value, mainView.currentLimit.value)
            //startService(Intent(this, ChargingService::class.java))
        }
        mainView.onClickButtonStop {
            chargingService.stopChargingService()
        }
        mainView.onClickButtonTest {
            //Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show()
            mainView.switchControl.value = !mainView.switchControl.value
        }

        mainView.onCheckedChangeSwitchControl {
            Log.d(LOG_TAG, "onCheckedChangeSwitchControl:$it")
            if (it) {
                //chargingService.chargingControl.startControl(mainView.levelLimit.value, mainView.currentLimit.value)
                initLevelLimit(mainView.levelLimit.value)
                initCurrentLimit(mainView.currentLimit.value)
                //chargingService.chargingControl.startControl()
                startService(Intent(this, ChargingService::class.java))
            } else {
                chargingService.stopChargingService()
            }
        }
        mainView.onCheckedChangeSwitchCharge {
            chargingService.chargingControl.commands.setSwitch(it)
        }
        mainView.onCheckedChangeCheckBoxAutoResetBatteryStats = {
            chargingService.chargingControl.isAutoResetBatteryStats = it
        }
        mainView.onCheckedChangeCheckBoxAutoSwitchOn = {
            chargingService.chargingControl.isAutoSwitchOn = it
        }
        mainView.onCheckedChangeCheckBoxAutoStop = {
            chargingService.chargingControl.isAutoStop = it
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
        Intent(this, ChargingService::class.java).also { intent ->
            bindService(intent, chargingServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unsubscribeSwitches()
        unbindService(chargingServiceConnection)
    }

    fun subscribeSwitches() {
        chargingService.chargingControl.onChangeChargingSwitch = {
            mainView.switchCharge.value = it
        }
        chargingService.onServiceStateChanged = {
            mainView.switchControl.value = it
        }
    }

    fun unsubscribeSwitches() {
        chargingService.chargingControl.onChangeChargingSwitch = {}
        chargingService.onServiceStateChanged = {}
    }

    fun initLevelLimit(value: String) {
        val lim = value.toIntOrNull()
        if ((lim != null) && (lim >= 0) && (lim <= 100)) {
            chargingService.chargingControl.levelLimit = lim
        } else {
            chargingService.chargingControl.levelLimit = 80
            mainView.levelLimit.value = "80"
        }
    }
    fun initCurrentLimit(value: String) {
        val lim = value.toIntOrNull()
        if ((lim != null) && (lim >= 0)) {
            chargingService.chargingControl.currentLimit = lim
        } else {
            chargingService.chargingControl.currentLimit = 1000000
            mainView.currentLimit.value = "1000000"
        }
    }

}

