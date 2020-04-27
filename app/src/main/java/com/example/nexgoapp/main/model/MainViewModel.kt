package com.example.nexgoapp.main.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nexgoapp.util.LedColor

class MainViewModel : ViewModel() {

    private val ledRed = MutableLiveData<LedVO>()
    private val ledGreen = MutableLiveData<LedVO>()
    private val ledBlue = MutableLiveData<LedVO>()
    private val ledYellow = MutableLiveData<LedVO>()

    init {
        ledRed.postValue(LedVO(LedColor.RED))
        ledGreen.postValue(LedVO(LedColor.GREEN))
        ledBlue.postValue(LedVO(LedColor.BLUE))
        ledYellow.postValue(LedVO(LedColor.YELLOW))
    }

    fun toggleLed(color: String, active: Boolean) {
        val liveDataLed = getColor(color)

        liveDataLed ?: return

        val ledVO = liveDataLed.value ?: return
        ledVO.active = active

        liveDataLed.postValue(ledVO)
    }

    private fun getColor(color: String): MutableLiveData<LedVO>? {
        return when (color) {
            "RED" -> ledRed
            "GREEN" -> ledGreen
            "BLUE" -> ledBlue
            "YELLOW" -> ledYellow
            else -> null
        }
    }

    fun getLedRed(): LiveData<LedVO> {
        return ledRed
    }

    fun getLedGreen(): LiveData<LedVO> {
        return ledGreen
    }

    fun getLedBlue(): LiveData<LedVO> {
        return ledBlue
    }

    fun getLedYellow(): LiveData<LedVO> {
        return ledYellow
    }
}
