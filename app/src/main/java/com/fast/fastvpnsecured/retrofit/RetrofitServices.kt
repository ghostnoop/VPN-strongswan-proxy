package com.fast.fastvpnsecured.retrofit

import com.fast.fastvpnsecured.app.createJsonRequestBody
import com.google.gson.JsonObject
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RetrofitServices {
    @POST("setup")
    @FormUrlEncoded
    fun pushSetup(
        @Field("appcode") appcode: String,
        @Field("branchParams") branchParams: JSONObject,
        @Field("branchParams1st") branchParams1st: JSONObject,
        @Field("userid") userid: String,

        ): Call<JsonObject>

    @POST("setup")
    fun pushJson(
        @Body params: RequestBody
    ): Call<JsonObject>

    fun creater(
        appcode: String,
        userid: String,
        branchParams: JSONObject,
        branchParams1st: JSONObject
    ) =
        pushJson(
            createJsonRequestBody(
                "appcode" to appcode,
                "userid" to userid,
                "branchParams" to branchParams.toString(),
                "branchParams1st" to branchParams1st.toString()
            )
        )


}