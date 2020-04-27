package com.example.nexgoapp.external.vo

import com.nexgo.oaf.apiv3.device.led.LightModeEnum

class LedData(val color: String, val active: Boolean) {

    @Throws(Exception::class)
    fun getNexGoColor(): LightModeEnum {
        return when (color) {
            "RED" -> LightModeEnum.RED
            "GREEN" -> LightModeEnum.GREEN
            "BLUE" -> LightModeEnum.BLUE
            "YELLOW" -> LightModeEnum.YELLOW
            else -> throw java.lang.Exception("color not found ${color}")
        }
    }
}