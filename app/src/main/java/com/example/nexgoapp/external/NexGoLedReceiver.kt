package com.example.nexgoapp.external

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.nexgoapp.external.vo.LedData
import com.example.nexgoapp.external.vo.ResponseAction
import com.nexgo.oaf.apiv3.APIProxy

class NexGoLedReceiver: BroadcastReceiver() {

    private val TAG  = "NexGoLedReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive")

        if (context == null) return
        if (intent == null) return

        val responseAction = getResponse(intent)

        try {
            val ledData = getParams(intent)
            changeColor(ledData)

            Log.d(TAG, "send event back to app")
            sendSuccess(context, responseAction, ledData)
        } catch (e: Exception) {
            Log.e(TAG, "fail to change led color", e)
            sendError(context, responseAction, e)
        }
    }

    @Throws(Exception::class)
    private fun changeColor(data: LedData) {
        Log.d(TAG, "changeColor ${data.color} - ${data.active}")

        val color = data.getNexGoColor()

        val deviceEngine = APIProxy.getDeviceEngine()
        val ledDriver = deviceEngine.ledDriver

        ledDriver.setLed(color, data.active)
    }

    private fun getParams(intent: Intent): LedData {
        Log.d(TAG, "getParams")

        val color = getColor(intent)
        val active = intent.getBooleanExtra("active", false)

        return LedData(color, active)
    }

    @Throws(Exception::class)
    private fun getColor(intent: Intent): String {
        val color = intent.getStringExtra("color")

        color ?: throw  java.lang.Exception("Informe a cor do led que deseja alterar")

        return color
    }

    private fun getResponse(intent: Intent): ResponseAction {
        val type = intent.getStringExtra("responseType")
        val action = intent.getStringExtra("responseAction")
        return ResponseAction(type ?: "", action ?: "")
    }

    private fun sendError(context: Context, response: ResponseAction, exception: Exception) {
        if (response.action.isBlank() || response.type.isBlank()) {
            Log.d(TAG, "response action not found")
            return
        }

        val intent = Intent().apply {
            action = response.action

            putExtra("success", false)
            putExtra("messageError", exception.message)

            addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
        }
        context.sendBroadcast(intent)
    }

    private fun sendSuccess(context: Context, response: ResponseAction, ledData: LedData) {
        if (response.action.isBlank() || response.type.isBlank()) {
            Log.d(TAG, "response action not found")
            return
        }

        val intent = Intent().apply {
            action = response.action

            putExtra("success", true)
            putExtra("color", ledData.color)
            putExtra("active", ledData.active)

            addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
        }
        context.sendBroadcast(intent)
    }
}
