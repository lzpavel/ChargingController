package com.lzpavel.chargingcontroller.appview

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lzpavel.chargingcontroller.ui.theme.ChargingControllerTheme

class MainView {

    var onClickButtonTest: () -> Unit = {}
    var onClickButtonSetCurrent: () -> Unit = {}
    var onClickButtonSwitchOn: () -> Unit = {}
    var onClickButtonSwitchOff: () -> Unit = {}
    var onClickButtonResetBatteryStats: () -> Unit = {}
    var onClickButtonStart: () -> Unit = {}
    var onClickButtonStop: () -> Unit = {}

    var onCheckedChangeSwitchControl: (Boolean) -> Unit = {}
    var onCheckedChangeSwitchCharge: (Boolean) -> Unit = {}
    var switchControl = mutableStateOf(false)
    var switchCharge = mutableStateOf(true)

    var onDoneLevelLimit: (String) -> Unit = {}
    var onDoneCurrentLimit: (String) -> Unit = {}
    var levelLimit = mutableStateOf("80")
    var currentLimit = mutableStateOf("1000000")

    var onCheckedChangeCheckBoxAutoResetBatteryStats: (Boolean) -> Unit = {}
    var onCheckedChangeCheckBoxAutoSwitchOn: (Boolean) -> Unit = {}
    var onCheckedChangeCheckBoxAutoStop: (Boolean) -> Unit = {}
    var checkBoxAutoResetBatteryStats = mutableStateOf(false)
    var checkBoxAutoSwitchOn = mutableStateOf(false)
    var checkBoxAutoStop = mutableStateOf(false)



    @Composable
    fun setView() {
        MainViewPreview()
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Preview(showBackground = true)
    @Composable
    fun MainViewPreview() {

        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current

        ChargingControllerTheme {
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Control")
                        Switch(
                            checked = switchControl.value,
                            onCheckedChange = {
                                switchControl.value = it
                                onCheckedChangeSwitchControl.invoke(it)
                            })
                    }
                    OutlinedTextField(
                        value = levelLimit.value,
                        onValueChange = { levelLimit.value = it },
                        label = { Text(text = "% limit") },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Decimal

                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                onDoneLevelLimit.invoke(levelLimit.value)
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                    OutlinedTextField(
                        value = currentLimit.value,
                        onValueChange = { currentLimit.value = it },
                        label = { Text(text = "Current limit") },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Decimal

                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                onDoneCurrentLimit.invoke(currentLimit.value)
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                    Button(
                        onClick = onClickButtonSetCurrent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(text = "Set current")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Charging switch")
                        Switch(
                            checked = switchCharge.value,
                            onCheckedChange = {
                                switchCharge.value = it
                                onCheckedChangeSwitchCharge.invoke(it)
                            })
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(
                            onClick = onClickButtonSwitchOn,
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .padding(8.dp)
                            ) {
                            Text(text = "Switch ON")
                        }
                        Button(
                            onClick = onClickButtonSwitchOff,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                            ) {
                            Text(text = "Switch OFF")
                        }
                    }
                    Button(
                        onClick = onClickButtonResetBatteryStats,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(text = "Reset battery stats")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(
                            onClick = onClickButtonStart,
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .padding(8.dp)
                        ) {
                            Text(text = "Start")
                        }
                        Button(
                            onClick = onClickButtonStop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(text = "Stop")
                        }
                    }
                    Button(
                        onClick = onClickButtonTest,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                        ) {
                        Text(text = "Test")
                    }
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Auto")
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Reset battery stats when unplug")
                            Checkbox(
                                checked = checkBoxAutoResetBatteryStats.value,
                                onCheckedChange = {
                                    checkBoxAutoResetBatteryStats.value = it
                                    onCheckedChangeCheckBoxAutoResetBatteryStats.invoke(it)
                                }
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Switch on when unplug")
                            Checkbox(
                                checked = checkBoxAutoSwitchOn.value,
                                onCheckedChange = {
                                    checkBoxAutoSwitchOn.value = it
                                    onCheckedChangeCheckBoxAutoSwitchOn.invoke(it)
                                }
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Stop when unplug")
                            Checkbox(
                                checked = checkBoxAutoStop.value,
                                onCheckedChange = {
                                    checkBoxAutoStop.value = it
                                    onCheckedChangeCheckBoxAutoStop.invoke(it)
                                }
                            )
                        }

                    }
                }

            }
        }
    }

    fun onClickButtonTest(click: () -> Unit) {
        onClickButtonTest = click
    }
    fun onClickButtonSetCurrent(click: () -> Unit) {
        onClickButtonSetCurrent = click
    }
    fun onClickButtonSwitchOn(click: () -> Unit) {
        onClickButtonSwitchOn = click
    }
    fun onClickButtonSwitchOff(click: () -> Unit) {
        onClickButtonSwitchOff = click
    }
    fun onClickButtonResetBatteryStats(click: () -> Unit) {
        onClickButtonResetBatteryStats = click
    }
    fun onClickButtonStart(click: () -> Unit) {
        onClickButtonStart = click
    }
    fun onClickButtonStop(click: () -> Unit) {
        onClickButtonStop = click
    }

    fun onCheckedChangeSwitchControl(change: (Boolean) -> Unit) {
        onCheckedChangeSwitchControl = change
    }
    fun onCheckedChangeSwitchCharge(change: (Boolean) -> Unit) {
        onCheckedChangeSwitchCharge = change
    }

}