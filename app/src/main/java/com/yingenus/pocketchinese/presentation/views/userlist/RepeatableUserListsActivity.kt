package com.yingenus.pocketchinese.presentation.views.userlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.PocketApplication


class RepeatableUserListsActivity : AppCompatActivity() {

    companion object{
        fun getIntent(context: Context): Intent {
            return Intent(context, RepeatableUserListsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()
        PocketApplication.setupApplication()

        super.onCreate(savedInstanceState)
        PocketApplication.postStartActivity(this,false)
        setContentView(R.layout.repeatable_list_layout)
        val toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        val fm = supportFragmentManager
        val fragment = fm.findFragmentById(R.id.counter)

        if (fragment == null)
            fm.beginTransaction().add(R.id.container,getFragment()).commit()

    }

    private fun getFragment(): Fragment{
        return RepeatableUserListsFragment()
    }
}