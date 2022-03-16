package com.yingenus.pocketchinese.controller.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.PocketApplication
import com.yingenus.pocketchinese.controller.fragment.RepeatableUserListsFragment

class RepeatableUserListsActivity : AppCompatActivity() {

    companion object{
        fun getIntent(context: Context): Intent {
            return Intent(context, RepeatableUserListsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PocketApplication.postStartActivity(false)
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