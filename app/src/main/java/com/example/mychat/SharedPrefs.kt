package com.example.mychat

import android.content.Context
import android.content.SharedPreferences


class SharedPrefs(context: Context) {
    private val sharedPrefs: SharedPreferences= context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)

    fun setValue(key: String, value: String){
        sharedPrefs.edit().putString(key, value).apply()

    }

    fun getValue(key: String) : String?{
        return sharedPrefs.getString(key,null)

    }

    fun setIntValue(key: String,value: Int){
        sharedPrefs.edit().putInt(key, value).apply()

    }

    fun getIntValue(key: String, i: Int) : Int?{
        return sharedPrefs.getInt(key,i.toInt())

    }

}