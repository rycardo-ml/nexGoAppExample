package com.example.nexgoapp.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.nexgoapp.R
import com.example.nexgoapp.main.model.LedVO
import com.example.nexgoapp.main.model.MainViewModel
import com.example.nexgoapp.util.SmartPosServiceConnection
import com.example.nexgoapp.util.bindSmartPosService
import com.example.nexgoapp.util.receivers.LedReceiver
import com.example.nexgoapp.util.unbindSmartPosService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.concurrent.atomic.AtomicInteger


class MainFragment : Fragment() {

    private val TAG = "MainFragment"

    companion object {
        fun newInstance() = MainFragment()
    }

    private val smartPosConnection = SmartPosServiceConnection()
    private val ledReceiver = LedReceiver({color, active -> viewModel.toggleLed(color, active)}, { message -> showError(message) })

    private lateinit var viewModel: MainViewModel

    private lateinit var tvLedRed: TextView
    private lateinit var tvLedGreen: TextView
    private lateinit var tvLedBlue: TextView
    private lateinit var tvLedYellow: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        setModelListeners()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvLedRed = view.findViewById(R.id.frg_main_tv_red)
        tvLedGreen = view.findViewById(R.id.frg_main_tv_green)
        tvLedBlue = view.findViewById(R.id.frg_main_tv_blue)
        tvLedYellow = view.findViewById(R.id.frg_main_tv_yellow)

        setLedListeners()
    }

    override fun onResume() {
        super.onResume()

        activity?.run {
            bindSmartPosService(this,  smartPosConnection)
        }
    }

    override fun onPause() {
        super.onPause()

        activity?.run {
            unbindSmartPosService(this,  smartPosConnection)
        }
    }

    private fun setModelListeners() {
        viewModel.getLedRed().removeObservers(this)
        viewModel.getLedRed().observe(this, Observer {
            tvLedRed.setTextColor( ContextCompat.getColor(context!!, if (it.active) R.color.red else android.R.color.tab_indicator_text) )
        })

        viewModel.getLedGreen().removeObservers(this)
        viewModel.getLedGreen().observe(this, Observer {
            tvLedGreen.setTextColor( ContextCompat.getColor(context!!, if (it.active) R.color.green else android.R.color.tab_indicator_text) )
        })

        viewModel.getLedBlue().removeObservers(this)
        viewModel.getLedBlue().observe(this, Observer {
            tvLedBlue.setTextColor( ContextCompat.getColor(context!!, if (it.active) R.color.blue else android.R.color.tab_indicator_text) )
        })

        viewModel.getLedYellow().removeObservers(this)
        viewModel.getLedYellow().observe(this, Observer {
            tvLedYellow.setTextColor( ContextCompat.getColor(context!!, if (it.active) R.color.yellow else android.R.color.tab_indicator_text) )
        })
    }

    private fun setLedListeners() {
        tvLedRed.setOnClickListener {
            sendLedEvent(viewModel.getLedRed().value!!)
        }

        tvLedGreen.setOnClickListener {
            sendLedEvent(viewModel.getLedGreen().value!!)
        }

        tvLedBlue.setOnClickListener {
            sendLedEvent(viewModel.getLedBlue().value!!)
        }

        tvLedYellow.setOnClickListener {
            sendLedEvent(viewModel.getLedYellow().value!!)
        }
    }

    private fun sendLedEvent(ledVO: LedVO) {

        ledReceiver.registerReceiver(context!!)

//        val intent = Intent().apply {
//            action = "POS_LED_REQUEST"
//
//            putExtra("color", ledVO.color.description)
//            putExtra("active", !ledVO.active)
//
//            putExtra("responseType", "BROADCAST_RECEIVER")
//            putExtra("responseAction", "POS_LED_RESPONSE")
//
//            addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
//        }
//        context!!.sendBroadcast(intent)

        GlobalScope.launch(Dispatchers.Default) {
            smartPosConnection.service.toggleLed(ledVO.color.description, !ledVO.active, "POS_LED_RESPONSE")
        }
    }

    private fun showError(message: String) {
        Toast.makeText(context!!, message, Toast.LENGTH_LONG).show()
    }
}
