package com.lzpavel.chargingcontroller

import android.util.Log
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class SuperUser() {

    val LOG_TAG = "SuperUserSession"

    lateinit var process: Process

    lateinit var reader: BufferedReader
    lateinit var readerError: BufferedReader
    lateinit var writer: BufferedWriter

    var isBusy = false

    init {
        open()
    }

    fun open() {
        process = Runtime.getRuntime().exec("su")
        reader = BufferedReader(InputStreamReader(process.inputStream))
        readerError = BufferedReader(InputStreamReader(process.errorStream))
        writer = BufferedWriter(OutputStreamWriter(process.outputStream))
    }

    fun execute(cmd: String): List<String> {
        val end = """ ; echo "\n$?\nrxend""""
        while (isBusy) {
            Thread.sleep(100)
        }
        isBusy = true
        checkBuffer()
        writer.write("$cmd$end\n")
        writer.flush()
        return receive()
    }

    fun receive(): List<String> {
        val rxList = mutableListOf<String>()
        var rxStr: String? = reader.readLine()
        while (true) {
            if (rxStr == "rxend") {
                val status: Int? = rxList.removeLastOrNull()?.toIntOrNull()
                if (status == 0) {
                    if (rxList.last() == "") {
                        rxList.removeLastOrNull()
                    }
                    isBusy = false
                    return rxList
                } else {
                    throw Exception("SU receive status not 0")
                }

            }
            if (rxStr == null) {
                throw Exception("SU received null")
            }
            rxList.add(rxStr)
            rxStr = reader.readLine()
        }

    }

    fun checkBuffer() {
        if (reader.ready()) {
            val lst = receive()
            Log.d(LOG_TAG, "Buffer trash: $lst")
        }
    }

    fun close() {
        writer.write("exit\n")
        writer.flush()
        process.waitFor();
    }
}