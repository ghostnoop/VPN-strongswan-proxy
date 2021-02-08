package com.fast.fastvpnsecured.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fast.fastvpnsecured.R
import com.fast.fastvpnsecured.app.getUserIdDevice
import com.fast.fastvpnsecured.app.hardCodeUrl
import com.fast.fastvpnsecured.model.Post
import com.fast.fastvpnsecured.retrofit.Common
import com.fast.fastvpnsecured.storage.SharedPrefManager
import com.google.gson.JsonObject
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.Branch
import io.branch.referral.BranchError
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    lateinit var userId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Branch.getAutoInstance(this);
        animate()

        if (!SharedPrefManager.getInstance(this).agree) {
            generateUserId()
            initBranch()
        } else
            nextPage()
    }

    private fun animate() {
        GlobalScope.launch {
            var i = 0f
            while (true) {
                runOnUiThread {
                    earth.rotation = i++
                    if (i == 360f) {
                        i = 0f
                    }
                }
                delay(20L)
            }
        }

    }

    override fun onStart() {
        super.onStart()
        Branch.sessionBuilder(this).withCallback(branchListener).withData(this.intent?.data).init()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent
        Branch.sessionBuilder(this).withCallback(branchListener).reInit()
    }

    object branchListener : Branch.BranchReferralInitListener {
        override fun onInitFinished(referringParams: JSONObject?, error: BranchError?) {
            if (error == null) {
                Log.i("BRANCH SDK", referringParams.toString())
                // Retrieve deeplink keys from 'referringParams' and evaluate the values to determine where to route the user
                // Check '+clicked_branch_link' before deciding whether to use your Branch routing logic
            } else {
                Log.e("BRANCH SDK", error.message)
            }
        }
    }

    private fun generateUserId() {
        userId = getUserIdDevice()
        SharedPrefManager.getInstance(this).saveInfo(userId)
        Toast.makeText(this, userId, Toast.LENGTH_SHORT).show()
    }

    private fun initBranch() {
        val branch = Branch.getInstance(
            applicationContext
        )
        branch.setIdentity(userId);


        Branch.sessionBuilder(this).withCallback(object : Branch.BranchReferralInitListener {
            override fun onInitFinished(referringParams: JSONObject?, error: BranchError?) {
                if (error == null) {
                    Log.i("BRANCH SDK", referringParams.toString())
                } else {
                    Log.e("BRANCH SDK", error.message)
                }
            }
        }).withData(this.intent.data).init()


        val buo = BranchUniversalObject()
            .setCanonicalIdentifier(userId)
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)

        val sessionParams: JSONObject = branch.firstReferringParamsSync


        val post = Post(
            branchParams = buo.convertToJson(),
            branchParams1st = sessionParams,
            userid = userId
        )
        pushToServer(post)
    }

    private fun pushToServer(post: Post) {
        val mService = Common.retrofitService

        mService.pushSetup(post.appcode, post.branchParams, post.branchParams1st, post.userid)
                .enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        try {
                            val status = response.body()?.get("status")?.asInt

                            if (status == 202) {
                                val message = response.body()?.get("message")!!.asString
                                accessServer(message)
                            }
                            else
                                failServer()

                        } catch (e: Exception) {
                            Log.e("N@@exp", e.message.toString())
                            failServer()
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Log.e("N@@error", t.message.toString())
                        failServer()
                    }
                })
    }


    private fun failServer() {
        val intent = Intent(this, WebActivity::class.java)
        intent.putExtra("status", 1)
        intent.putExtra("message", hardCodeUrl)
        startActivity(intent)
        finish()

    }

    private fun accessServer(message: String) {
        val intent = Intent(this, WebActivity::class.java)
        intent.putExtra("status", 0)
        intent.putExtra("message", message)
        startActivity(intent)
        finish()

    }

    private fun nextPage() {
        startActivity(Intent(this, VPNEActivity::class.java))
        finish()
    }
}