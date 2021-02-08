package com.fast.fastvpnsecured.model

import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class Post(

    @SerializedName("appcode")
    @Expose
    val appcode: String = "4",

    @SerializedName("branchParams")
    @Expose
    val branchParams: JSONObject,

    @SerializedName("branchParams1st")
    @Expose
    val branchParams1st: JSONObject,

    @SerializedName("userid")
    @Expose
    val userid: String
)