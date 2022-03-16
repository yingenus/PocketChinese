package com.yingenus.pocketchinese

import android.content.Context
import com.yingenus.pocketchinese.model.RepeatType

object Settings{

    val DEBUG: Boolean = BuildConfig.DEBUG

    private const val SEPARATOR = "__,__"
    private const val APP_PREFERENCE="com.yingenus.pocketchinese.preference"
    private const val USE_APP_KEYBOARD="com.yingenus.pocketchinese.useAppKeyboard"
    private const val REPEAT_TYPE_CHN= "com.yingenus.pocketchinese.repeatType.chn"
    private const val REPEAT_TYPE_PIN= "com.yingenus.pocketchinese.repeatType.pin"
    private const val REPEAT_TYPE_TRN= "com.yingenus.pocketchinese.repeatType.trn"
    private const val SEARCH_HISTORY="com.yingenus.pocketchinese.searchHistory"
    private const val VIEWED_LISTS = "com.yingenus.pocketchinese.viewedLists"
    private const val SHOW_NOTIFICATION = "com.yingenus.pocketchinese.showNotifications"
    private const val NIGHT_THEME = "com.yingenus.pocketchinese.nightTheme"


    fun useAppKeyboard(context: Context):Boolean{
        return context.getSharedPreferences(APP_PREFERENCE,0).getBoolean(USE_APP_KEYBOARD,true)
    }

    fun setUseAppKeyboard(context: Context,use: Boolean){
        context.getSharedPreferences(APP_PREFERENCE,0).edit().putBoolean(USE_APP_KEYBOARD,use).apply()
    }

    fun getRepeatType(context: Context): RepeatType{
        val preference=context.getSharedPreferences(APP_PREFERENCE,0)

        val ignoreCHN=preference.getBoolean(REPEAT_TYPE_CHN,false)
        val ignorePIN=preference.getBoolean(REPEAT_TYPE_PIN,false)
        val ignoreTRN=preference.getBoolean(REPEAT_TYPE_TRN,false)

        return RepeatType(ignoreCHN=ignoreCHN,ignoreTRN = ignoreTRN,ignorePIN = ignorePIN)
    }
    fun setRepeatType(context: Context,repeatType: RepeatType){
        val editPreferences=context.getSharedPreferences(APP_PREFERENCE,0).edit()

        editPreferences.putBoolean(REPEAT_TYPE_CHN,repeatType.ignoreCHN)
        editPreferences.putBoolean(REPEAT_TYPE_PIN,repeatType.ignorePIN)
        editPreferences.putBoolean(REPEAT_TYPE_TRN,repeatType.ignoreTRN)
        editPreferences.apply()
    }

    fun getSearchHistory(context: Context): Array<Int>{
        val preference = context.getSharedPreferences(APP_PREFERENCE,0);

        val iDs = Utils.getIDs(preference.getString(SEARCH_HISTORY, "") ?: "")

        return iDs
    }

    fun addSearchItem(context: Context, iD : Int){
        val preferences = context.getSharedPreferences(APP_PREFERENCE,0)
        val history = preferences.getString(SEARCH_HISTORY,"")

        val newHistory = Utils.setIDs(history ?: "", iD)

        preferences.edit().putString(SEARCH_HISTORY,newHistory).apply()
    }

    fun getViewedItems(context: Context): List<Pair<Int, String>>{
        val preferences = context.getSharedPreferences(APP_PREFERENCE, 0 )
        val viewed = preferences.getStringSet(VIEWED_LISTS, setOf<String>())

        val result = mutableListOf<Pair<Int, String>>()

        viewed!!.forEach {

            val line = it.split(SEPARATOR)

            val version = line[0].toInt()
            val name = line[1]

            result.add(Pair(version , name))
        }
        return result
    }

    fun setViewItem(context: Context, version : Int , name : String){
        val preferences = context.getSharedPreferences(APP_PREFERENCE, 0 )
        val viewed = preferences.getStringSet(VIEWED_LISTS, setOf<String>())!!.toMutableList()

        val editPreferences = preferences.edit()

        viewed!!.add(version.toString()+ SEPARATOR +name)

        editPreferences.putStringSet(VIEWED_LISTS, viewed.toSet())
        editPreferences.apply()
    }

    fun isNightModeOn(context : Context):Boolean{
        val preferences = context.getSharedPreferences(APP_PREFERENCE,0)
        return preferences.getBoolean(NIGHT_THEME,false)
    }

    fun nightMode(on : Boolean,context : Context){
        context.getSharedPreferences(APP_PREFERENCE,0)
                .edit()
                .putBoolean(NIGHT_THEME,on)
                .apply()
    }

    fun showNotifications(show : Boolean, context: Context){
        context.getSharedPreferences(APP_PREFERENCE,0)
                .edit()
                .putBoolean(SHOW_NOTIFICATION,show)
                .apply()
    }

    fun shouldShowNotifications(context: Context): Boolean{
        val preferences = context.getSharedPreferences(APP_PREFERENCE,0)
        return preferences.getBoolean(SHOW_NOTIFICATION,true)
    }

    private object Utils{
        const val maxItems = 30;
        const val separator = "__,__"

        fun getIDs(pref : String): Array<Int>{
            if (pref.isNotEmpty()){
                val itms = pref.split(separator)

                return itms.map { it.toInt() }.toTypedArray()
            }
            return emptyArray()
        }

        fun setIDs(pref: String, iD: Int): String{
            val itms = getIDs(pref);

            val newSet = mutableSetOf<Int>()
            newSet.add(iD)
            newSet.addAll(itms)

            val lastItem = if (newSet.size > maxItems) maxItems else newSet.size

            val subArg = newSet.toTypedArray().slice(0 until lastItem)

            return set2Str(subArg)
        }

        private fun set2Str( set : List<Int>): String{
            return  set.map { it.toString() }.joinToString(separator = separator)
        }


    }
}


