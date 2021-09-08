package com.yingenus.pocketchinese.controller.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.controller.PocketApplication
import com.yingenus.pocketchinese.controller.Settings
import com.yingenus.pocketchinese.controller.activity.AboutActivity
import com.yingenus.pocketchinese.controller.activity.ActivateInputActivity
import com.yingenus.pocketchinese.model.RepeatType
import java.lang.IllegalArgumentException

class SettingsFragment: Fragment(){

    companion object{
        private const val enableDark = true
    }


    private lateinit var mThemeSwitch: Switch
    private lateinit var mUseKeyboardSwitch: Switch
    private lateinit var mIgnoreCHNSwitch: Switch
    private lateinit var mIgnorePINSwitch: Switch
    private lateinit var mIgnoreTRNSwitch: Switch
    private lateinit var mShowNotifySwitch : Switch
    private lateinit var mSetDefaultKeyboardButton:Button
    private lateinit var toolbar: Toolbar
    private lateinit var mScrollView: ScrollView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val colorPrimary = TypedValue()
        if(!context!!.theme.resolveAttribute(R.attr.colorPrimary,colorPrimary,true)){
            throw IllegalArgumentException("cant found attribute colorPrimary")
        }
        val colorOnPrimary = TypedValue()
        if(!context!!.theme.resolveAttribute(R.attr.colorOnPrimary,colorOnPrimary,true)){
            throw IllegalArgumentException("cant found attribute colorOnPrimary")
        }

        val layout= LinearLayout(context)
        layout.orientation=LinearLayout.VERTICAL
        layout.setBackgroundColor(colorPrimary.data)


        toolbar = Toolbar(context!!)
        toolbar.elevation = 0f
        toolbar.title = getString(R.string.menu_item_settings)
        toolbar.setBackgroundColor(colorPrimary.data)
        toolbar.setTitleTextColor(colorOnPrimary.data)

        toolbar.measure(View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED),View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED))
        val toolHeight = toolbar.measuredHeight

        val topBarParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                toolHeight)

        layout.addView(toolbar,topBarParams)

        val scrollViewParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT)

        mScrollView=inflater.inflate(R.layout.settings_layout,null) as ScrollView

        layout.addView(mScrollView,scrollViewParams)

        mThemeSwitch=mScrollView.findViewById(R.id.switch_theme)
        mUseKeyboardSwitch=mScrollView.findViewById(R.id.switch_use_keyboard)
        mIgnoreCHNSwitch=mScrollView.findViewById(R.id.switch_ignore_chn)
        mIgnorePINSwitch=mScrollView.findViewById(R.id.switch_ignore_pin)
        mIgnoreTRNSwitch=mScrollView.findViewById(R.id.switch_ignore_trn)
        mSetDefaultKeyboardButton=mScrollView.findViewById(R.id.set_default_keyboard_button)
        mShowNotifySwitch = mScrollView.findViewById(R.id.switch_notification)

        val aboutView = mScrollView.findViewById<View>(R.id.about)
        aboutView.setOnClickListener(this::onAboutClicked)

        mIgnoreCHNSwitch.setOnClickListener(this::onIgnoreSwitchClicked)
        mIgnorePINSwitch.setOnClickListener(this::onIgnoreSwitchClicked)
        mIgnoreTRNSwitch.setOnClickListener(this::onIgnoreSwitchClicked)
        mThemeSwitch.setOnClickListener(this::onDarkThemeSwitchClicked)
        mUseKeyboardSwitch.setOnClickListener(this::onUseAppKeyboardClicked)
        mSetDefaultKeyboardButton.setOnClickListener(this::onSetDefaultClicked)
        mShowNotifySwitch.setOnClickListener(this::onShowNotifyClicked)

        initValues()

        return layout
    }

    private fun initValues(){
        //Theme switch
        mThemeSwitch.isChecked=Settings.isNightModeOn(context!!)
        //Use App Keyboard
        mUseKeyboardSwitch.isChecked=Settings.useAppKeyboard(context!!)
        // Ignore Switches
        val repeatType=Settings.getRepeatType(context!!)
        mIgnoreCHNSwitch.isChecked=!repeatType.ignoreCHN
        mIgnorePINSwitch.isChecked=!repeatType.ignorePIN
        mIgnoreTRNSwitch.isChecked=!repeatType.ignoreTRN
        // notify Switch
        mShowNotifySwitch.isChecked = Settings.shouldShowNotifications(context!!)
    }

    private fun setupInputButton(){
        val imm=context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val defaultInput = imm.enabledInputMethodList.find {
            it.id == android.provider.Settings.Secure.getString(context!!.contentResolver,android.provider.Settings.Secure.DEFAULT_INPUT_METHOD) }
        if (defaultInput != null && defaultInput.serviceName.contains("PinyinPocketInputMethodService", true)){
            mSetDefaultKeyboardButton.setText(R.string.off_app_keyboard_as_default)
        }
    }



    override fun onResume() {
        super.onResume()
        setupInputButton()
    }

    //TODO: переделать
    private fun onSetDefaultClicked(v:View){
        val imm=context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val enableList = imm.enabledInputMethodList
        if (enableList.any { it.serviceName.contains("PinyinPocketInputMethodService", true) }){
            imm.showInputMethodPicker()
        }else {
            val intent = ActivateInputActivity.getIntent(context!!)
            startActivity(intent)
        }
    }

    private fun onIgnoreSwitchClicked(v:View){
        if (!mIgnoreCHNSwitch.isChecked&&!mIgnorePINSwitch.isChecked&&!mIgnoreTRNSwitch.isChecked){
            when(v){
                mIgnoreCHNSwitch->{
                    mIgnorePINSwitch.isChecked=true
                    mIgnoreTRNSwitch.isChecked=true
                }
                mIgnorePINSwitch->{
                    mIgnoreCHNSwitch.isChecked=true
                    mIgnoreTRNSwitch.isChecked=true
                }
                mIgnoreTRNSwitch->{
                    mIgnoreCHNSwitch.isChecked=true
                    mIgnorePINSwitch.isChecked=true
                }
            }
        }

        val repeatType=RepeatType(
                ignoreCHN = !mIgnoreCHNSwitch.isChecked,
                ignorePIN = !mIgnorePINSwitch.isChecked,
                ignoreTRN = !mIgnoreTRNSwitch.isChecked
        )

        Settings.setRepeatType(activity!!,repeatType)

    }

    private fun onUseAppKeyboardClicked(v:View){
        Settings.setUseAppKeyboard(activity!!,mUseKeyboardSwitch.isChecked)
    }

    private fun onDarkThemeSwitchClicked(v:View){
        if (enableDark){
            val isChecked = (v as Switch).isChecked
            if (isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Settings.nightMode(true,context!!)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Settings.nightMode(false,context!!)
            }
        }else {
            mThemeSwitch.isChecked = false
        }
    }

    private fun onShowNotifyClicked(v : View){
        val isChecked = (v as Switch).isChecked
        Settings.showNotifications(isChecked,context!!)
        PocketApplication.updateNotificationStatus()
    }

    private fun onAboutClicked( v : View){
        val intent = Intent(context!!, AboutActivity::class.java)
        startActivity(intent)
    }
}