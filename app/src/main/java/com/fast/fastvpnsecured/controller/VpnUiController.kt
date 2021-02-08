package com.fast.fastvpnsecured.controller

import android.app.Activity
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import android.widget.TextView
import com.fast.fastvpnsecured.model.ProxyServer
import com.fast.fastvpnsecured.view.VPNEActivity

class VpnUiController(
    private val status_tv: TextView,
    private val turn_tv: TextView,
    private val turn_btn: ImageView
) {
    private val statuses = listOf("Not connected", "Connected")
    var toggle: Boolean = false

    init {
        status_tv.text = setConnectionStatusText(toggle)

    }


    fun changeStatuses() {
        toggle = !toggle
        status_tv.text = setConnectionStatusText(toggle)

    }
    fun setStatus(status: Boolean){
        status_tv.text = setConnectionStatusText(status)
    }

     fun setConnectionStatusText(status: Boolean): SpannableString {
        var text = "Status: "
        val color: ForegroundColorSpan

        if (status) {
            text += statuses[1]
            color = ForegroundColorSpan(Color.parseColor("#3ee823"))

        } else {
            text += statuses[0]
            color = ForegroundColorSpan(Color.parseColor("#ca2727"))
        }

        val mSpannableString = SpannableString(text)
        mSpannableString.setSpan(color, 7, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return mSpannableString
    }



}