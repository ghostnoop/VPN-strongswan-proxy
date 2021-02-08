package com.fast.fastvpnsecured.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fast.fastvpnsecured.R
import kotlinx.android.synthetic.main.activity_side_menu_feature.*

class SideMenuFeatureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_side_menu_feature)

        val url: String = intent.extras!!.getString("message")!!
        val title: String = intent.extras!!.getString("title")!!

        webView.loadUrl(url)
        title_tv.text = title

        back_btn.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        finish()
    }
}