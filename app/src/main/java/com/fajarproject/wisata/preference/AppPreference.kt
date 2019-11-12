package com.fajarproject.wisata.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

/**
 * Created by Fajar Adi Prasetyo on 27/10/19.
 */

object AppPreference {
        const val THEME_COLOR : String = "com.fajarproject.THEME_COLOR"

    fun deletePreference(context: Context){
        val sharedPref : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    }

    fun writePreference(context: Context,prefName : String, prefValue : String){
        val sharedPref : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val editor : SharedPreferences.Editor = sharedPref.edit()
        editor.putString(prefName,prefValue)
        editor.apply()
    }
    fun writePreference(context: Context,prefName : String, prefValue : Int){
        val sharedPref : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val editor : SharedPreferences.Editor = sharedPref.edit()
        editor.putInt(prefName,prefValue)
        editor.apply()
    }
    fun writePreference(context: Context,prefName : String, prefValue : Long){
        val sharedPref : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val editor : SharedPreferences.Editor = sharedPref.edit()
        editor.putLong(prefName,prefValue)
        editor.apply()
    }
    fun writePreference(context: Context,prefName : String, prefValue : Boolean){
        val sharedPref : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val editor : SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(prefName,prefValue)
        editor.apply()
    }
    fun writePreference(context: Context,prefName : String, prefValue : Float){
        val sharedPref : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val editor : SharedPreferences.Editor = sharedPref.edit()
        editor.putFloat(prefName,prefValue)
        editor.apply()
    }
    fun getStringPreferenceByName(context: Context,prefName: String) : String?{
        val sharedPref : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        return sharedPref.getString(prefName,"")
    }
    fun getIntPreferenceByName(context: Context,prefName: String) : Int? {
        val sharedPref : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        return sharedPref.getInt(prefName,0)
    }
    fun getLongPreferenceByName(context: Context,prefName: String) : Long? {
        val sharedPref : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        return sharedPref.getLong(prefName,0)
    }
    fun getBooleanPreferenceByName(context: Context,prefName: String) : Boolean? {
        val sharedPref : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        return sharedPref.getBoolean(prefName,false)
    }
    fun getFloatPreferenceByName(context: Context,prefName: String) : Float? {
        val sharedPref : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        return sharedPref.getFloat(prefName,0f)
    }
    fun updateUnreadNotification(context: Context,isUnread : Boolean){
        val sharedPref : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val editor : SharedPreferences.Editor   = sharedPref.edit()
        editor.putBoolean("isUnread",isUnread)
        editor.apply()
    }
    fun isUnreadNotification(context: Context): Boolean? {
        val sharedPref : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        return sharedPref.getBoolean("isUnread",false)
    }
    fun setTheme(context: Context,theme: String){
        val sharedPref : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val editor : SharedPreferences.Editor   = sharedPref.edit()
        editor.putString(THEME_COLOR,theme)
        editor.apply()
    }
    fun getTheme(context: Context) : String? {
        val sharedPref : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        return sharedPref.getString(THEME_COLOR,"")
    }
}