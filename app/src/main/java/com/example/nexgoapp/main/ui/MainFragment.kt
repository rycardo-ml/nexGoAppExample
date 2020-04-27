package com.example.nexgoapp.main.ui

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
import java.lang.Exception


class MainFragment : Fragment() {

    private val TAG = "MainFragment"

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    private lateinit var tvLedRed: TextView
    private lateinit var tvLedGreen: TextView
    private lateinit var tvLedBlue: TextView
    private lateinit var tvLedYellow: TextView

    private val ledReceiver = object: BroadcastReceiver() {
        override fun onReceive(ctx: Context?, responseIntent: Intent?) {
            Log.d(TAG, "ledReceiver")
            responseIntent ?: return

            handleLedResponse(responseIntent)
        }
    }

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
        val intent = Intent().apply {
            action = "POS_LED_REQUEST"

            putExtra("color", ledVO.color.description)
            putExtra("active", !ledVO.active)

            putExtra("responseType", "BROADCAST_RECEIVER")
            putExtra("responseAction", "POS_LED_RESPONSE")

            addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
        }

        val filter = IntentFilter().apply {
            addAction("POS_LED_RESPONSE")
        }

        context!!.registerReceiver(ledReceiver, filter)
        context!!.sendBroadcast(intent)
    }

    private fun handleLedResponse(responseIntent: Intent) {
        context!!.unregisterReceiver(ledReceiver)

        Log.d(TAG, "handling led response")

        if (!responseIntent.getBooleanExtra("success", false)) {
            showError(getErrorMessage(responseIntent))
            return
        }

        val color = responseIntent.getStringExtra("color") ?: throw Exception("color not found")
        val active = responseIntent.getBooleanExtra("active", false)

        viewModel.toggleLed(color, active)
    }

    private fun showError(message: String) {
        Toast.makeText(context!!, message, Toast.LENGTH_LONG).show()
    }

    private fun getErrorMessage(responseIntent: Intent): String {
        val value = responseIntent.getStringExtra("messageError")
        return value ?: "Nao foi possivel identificar o problema ocorrido"
    }
}
