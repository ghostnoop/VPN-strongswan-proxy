package com.fast.fastvpnsecured.retrofit


object Common {
    private val BASE_URL = "https://fastpingregistration.com/"
    val retrofitService: RetrofitServices
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitServices::class.java)
}