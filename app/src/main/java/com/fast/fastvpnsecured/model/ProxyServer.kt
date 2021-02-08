package com.fast.fastvpnsecured.model

import android.os.Parcelable
import com.fast.fastvpnsecured.R
import com.fast.fastvpnsecured.app.VpnUtils
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProxyServer(
    val name: String,
    val ipAddress: String,
    val port: Int = 80,
    val countryPhoto: String,
    var caCertificate: String = "",
    var caIndex:Int,
    val login: String = "FVSuserA",
    val password: String = "FVSpasswordA",
    var current: Boolean = false
) : Parcelable {
    companion object {
        fun getDefault(): ArrayList<ProxyServer> {
            return arrayListOf(
                ProxyServer("US", "123.256.83.56", 80, "", "ca_cert_us", R.raw.ca_cert_us
            )
        }


    }
}
