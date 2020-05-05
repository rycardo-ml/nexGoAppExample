package com.example.nexgoapp.util.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import java.lang.Exception
import java.util.concurrent.atomic.AtomicInteger

class LedReceiver (val callback: (color: String, active: Boolean) -> Unit, val callbackError: (message: String) -> Unit): BroadcastReceiver() {

    private val TAG = "LedReceiver"

    private val counter = AtomicInteger(0)

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "ledReceiver")

        intent ?: return
        context ?: return

        unregisterReceiver(context)
        handleLedResponse(intent)
    }

    private fun handleLedResponse(responseIntent: Intent) {
        Log.d(TAG, "handling led response")

        if (!responseIntent.getBooleanExtra("success", false)) {
            callbackError.invoke(getErrorMessage(responseIntent))
            return
        }

        val color = responseIntent.getStringExtra("color") ?: throw Exception("color not found")
        val active = responseIntent.getBooleanExtra("active", false)

        callback.invoke(color, active)
    }

    private fun getErrorMessage(responseIntent: Intent): String {
        val value = responseIntent.getStringExtra("messageError")
        return value ?: "Nao foi possivel identificar o problema ocorrido"
    }

    fun registerReceiver(context: Context) {
        if (counter.get() > 0) {
            counter.incrementAndGet()
            return
        }

        Log.d(TAG, "register")

        counter.incrementAndGet()

        val filter = IntentFilter().apply {
            addAction("POS_LED_RESPONSE")
        }
        context.registerReceiver(this, filter)
    }

    private fun unregisterReceiver(context: Context) {
        if (counter.decrementAndGet() > 0) return

        Log.d(TAG, "unregister")
        context.unregisterReceiver(this)
    }
}
