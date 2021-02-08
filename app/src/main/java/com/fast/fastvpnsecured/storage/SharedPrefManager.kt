package com.fast.fastvpnsecured.storage

import android.annotation.SuppressLint
import android.content.Context


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class SharedPrefManager private constructor(private val mCtx: Context) {

    val userId:String
        get() {
            val sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            with(sharedPreferences) {
                return getString("userId","").toString()
            }
        }
    val agree:Boolean
        get() {
            val sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            with(sharedPreferences) {
                return getBoolean("agree",false)
            }
        }

    fun saveInfo(userId:String){
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userId", userId)
        editor.commit()
        editor.apply()
    }
    fun saveAgree(){
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("agree", true)
        editor.commit()
        editor.apply()
    }




    fun clear() {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    //
    companion object {
        val SHARED_PREF_NAME = "my_shared_preff"
        private val SHARED_PREF_ADVER = "my_shared_adver"

        @SuppressLint("StaticFieldLeak")
        private var mInstance: SharedPrefManager? = null

        @Synchronized
        fun getInstance(mCtx: Context): SharedPrefManager {
            if (mInstance == null) {
                mInstance = SharedPrefManager(mCtx)
            }
            return mInstance as SharedPrefManager
        }
    }

}