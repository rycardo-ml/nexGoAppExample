package com.example.nexgoapp.util

import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.nexgoservice.SmartPOS


fun bindSmartPosService(context: Context, connection: SmartPosServiceConnection) {

    Intent("com.example.nexgoservice.SMART_POS").apply {
        `package` = "com.example.nexgoservice"

        context.bindService(this, connection.connection, BIND_AUTO_CREATE)
    }
}

fun unbindSmartPosService(context: Context, connection: SmartPosServiceConnection) {
    if (!connection.isBound()) return

    context.unbindService(connection.connection)
    connection.unbound()
}

class SmartPosServiceConnection {

    lateinit var service: SmartPOS
    private var mBound: Boolean = false

    val connection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            this@SmartPosServiceConnection.service = SmartPOS.Stub.asInterface(service)
            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            unbound()
        }
    }

    fun unbound() {
        mBound = false
    }


    fun isBound(): Boolean {
        return mBound
    }
}
