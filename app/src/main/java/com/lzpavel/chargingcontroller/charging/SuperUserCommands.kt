package com.lzpavel.chargingcontroller.charging

import com.lzpavel.chargingcontroller.SuperUser

class SuperUserCommands(superUser: SuperUser) : Commands {

    private val superUser: SuperUser = superUser

    /*init {
        this.superUser = msuperUser
    }*/

    val switchFile1 = "/sys/class/power_supply/battery/mmi_charging_enable"
    val switchFile2 =  "/sys/class/power_supply/battery/input_suspend"

    val currentFile = "/sys/class/power_supply/main/current_max"

    override fun setSwitch(state: Boolean) {
        if (state) {
            superUser.execute("echo 0 > $switchFile2")
            superUser.execute("echo 1 > $switchFile1")
        } else {
            superUser.execute("echo 0 > $switchFile1")
            superUser.execute("echo 1 > $switchFile2")
        }
    }

    override fun getSwitch(): Boolean {
        val rx = superUser.execute("cat $switchFile1").first()
        return rx == "1"
    }

    override fun setCurrent(value: String) {
        val regex = Regex("-rw.*")
        val lst = superUser.execute("ls -l $currentFile")
        if (!regex.matches(lst[0]) && lst.size == 1) {
            superUser.execute("chmod u+w $currentFile")
        }
        superUser.execute("echo $value > $currentFile")
    }

    override fun getCurrent(): String {
        return superUser.execute("cat $currentFile").first()
    }

    override fun resetBatteryStats() {
        superUser.execute("dumpsys batterystats --reset")
    }
}