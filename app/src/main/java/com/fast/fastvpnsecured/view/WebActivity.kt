package com.fast.fastvpnsecured.view

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.fast.fastvpnsecured.R
import com.fast.fastvpnsecured.storage.SharedPrefManager
import kotlinx.android.synthetic.main.activity_web.*

class WebActivity : AppCompatActivity() {
    var message: ValueCallback<Uri>? = null
    var messageArray: ValueCallback<Array<Uri>>? = null
    val FILE = 100
    val Result = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        val status: Int = intent.extras!!.getInt("status")
        val url: String = intent.extras!!.getString("message")!!
        loadUrlByStatus(status, url)


        setSettingsToWebView()
    }

    private fun loadUrlByStatus(status: Int, url: String) {
//        status 0 normal else hardcode
        if (status == 0) {
            webView.loadUrl(url)
            waitThread()
        } else {
            webView.loadUrl(url)
            agreeControl()
        }

    }

    fun waitThread() {
        Runnable {
            Thread.sleep(1000)
            val intent = Intent(this, VPNEActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }
    }

    fun agreeControl() {
        agree_btn.visibility = View.VISIBLE
        agree_btn.setOnClickListener {
            SharedPrefManager.getInstance(this).saveAgree()

            agree_btn.visibility = View.GONE

            val intent = Intent(this, VPNEActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }
    }

    fun setSettingsToWebView() {
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                view: WebView,
                filePath: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                if (message != null) {
                    message!!.onReceiveValue(null)
                    message = null
                }
                messageArray = filePath
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val intent = fileChooserParams.createIntent()
                    intent.type = "image/*"
                    try {
                        startActivityForResult(intent, FILE)
                    } catch (e: Exception) {
                        message = null
                        return false
                    }
                    return true
                } else {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.type = "image/*"
                    startActivityForResult(
                        Intent.createChooser(intent, "Chose"),
                        Result
                    )
                    return true
                }
            }


            fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String = "") {
                message = uploadMsg
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"
                startActivityForResult(
                    Intent.createChooser(intent, "Chose"),
                    Result
                )
            }
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack())
            webView.goBack()


    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        webView.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        webView.restoreState(savedInstanceState)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == FILE) {
                if (messageArray == null)
                    return
                messageArray!!.onReceiveValue(
                    WebChromeClient.FileChooserParams.parseResult(
                        resultCode,
                        data
                    )
                )
                messageArray = null
            }
        } else if (requestCode == Result) {
            if (null == message)
                return
            val result =
                if (data == null || resultCode != RESULT_OK) null else data.data
            message!!.onReceiveValue(result)
            message = null
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}