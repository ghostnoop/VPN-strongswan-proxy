package com.fast.fastvpnsecured.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.fast.fastvpnsecured.R
import com.fast.fastvpnsecured.adapter.ServerAdapter
import com.fast.fastvpnsecured.model.ProxyServer
import kotlinx.android.synthetic.main.activity_server_chooser.*


class ServerChooserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_chooser)
        val proxyServerLast = intent.getParcelableExtra<ProxyServer>("proxyServer")

        val servers = ProxyServer.getDefault()

        for (i in 0 until servers.size) {
            if (servers[i].ipAddress.equals(proxyServerLast?.ipAddress)) {
                servers[i].current = true
                break
            }
        }


        recyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )
        recyclerView.adapter = ServerAdapter(servers) {
            getChooseServer(servers[it])
        }
        back_btn.setOnClickListener {
            finish()
        }

    }

    fun getChooseServer(proxyServer: ProxyServer) {
        val data = Intent()
        data.putExtra("proxy", proxyServer)
        setResult(RESULT_OK, data)
        finish()

    }
}