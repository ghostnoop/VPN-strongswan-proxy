package com.fast.fastvpnsecured.app

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.fast.fastvpnsecured.model.ProxyServer
import okhttp3.RequestBody
import org.json.JSONObject

fun inflate(
    context: Context,
    viewId: Int,
    parent: ViewGroup? = null,
    attachToRoot: Boolean = false
): View {
    return LayoutInflater.from(context).inflate(viewId, parent, attachToRoot)
}

fun getUserIdDevice(): String {
    return "static_user-" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.DEVICE.length % 10 +
            Build.DISPLAY.length % 10 + Build.HOST.length % 10 + Build.ID.length % 10 +
            Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 +
            Build.PRODUCT.length % 10 + Build.TAGS.length % 10 + Build.USER.length % 10
}

const val hardCodeUrl = "https://www.google.ru/"
const val hardPrivacy = "https://www.google.ru/"
const val hardTerms = "https://www.google.ru/"
const val hardContact = "https://www.google.ru/"

fun getUrlFlag(country: String): String {
//    return "https://www.countryflags.io/$country/flat/64.png"
    return "https://flagcdn.com/64x48/${country.toLowerCase()}.png"
}

fun setImage(proxyServer: ProxyServer, imageView: ImageView, context: Context) {
    if (proxyServer.countryPhoto.isNotEmpty()) {
        Glide.with(context).load(proxyServer.countryPhoto).circleCrop().into(imageView)
    } else
        Glide.with(context).load(getUrlFlag(proxyServer.name)).circleCrop().into(imageView)

}

fun setImage(proxyServer: ProxyServer, imageView: ImageView, context: View) {
    if (proxyServer.countryPhoto.isNotEmpty()) {
        Glide.with(context).load(proxyServer.countryPhoto).circleCrop().into(imageView)
    } else
        Glide.with(context).load(getUrlFlag(proxyServer.name)).circleCrop().into(imageView)

}


fun createJsonRequestBody(vararg params: Pair<String, String>) =
    RequestBody.create(
        okhttp3.MediaType.parse("application/json; charset=utf-8"),
        JSONObject(mapOf(*params)).toString()
    )