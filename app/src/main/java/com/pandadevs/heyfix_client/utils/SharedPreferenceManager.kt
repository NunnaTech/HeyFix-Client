package com.pandadevs.heyfix_client.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.pandadevs.heyfix_client.data.model.UserGet

class SharedPreferenceManager(val context: Context) {
    private val PREFS_NAME = "SHARED_PREF"
    val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)

    fun saveUser(user: UserGet) {
        sharedPref.edit().putString("user", Gson().toJson(user)).apply()
    }

    fun saveProviderMail(provider:String){
        sharedPref.edit().putString("provider",provider).apply();
    }

    fun saveUID(id:String){
        sharedPref.edit().putString("uid",id).apply();
    }
    
     fun getProviderEmail():String?{
        return sharedPref.getString("provider","")
    }
    
    fun getUID():String?{
        return sharedPref.getString("uid","")
    }

    fun saveSession(){
        sharedPref.edit().putBoolean("active",true).apply()
    }

    fun getSession():Boolean{
        return sharedPref.getBoolean("active", false)
    }

    fun getUser(): UserGet? {
        val data = sharedPref.getString("user", null) ?: return null
        return Gson().fromJson(data, UserGet::class.java)
    }

    fun cleanShared(){
        sharedPref.edit().clear().apply()
    }
}
