package com.yingenus.pocketchinese.controller.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yingenus.pocketchinese.R

abstract class ScrollingContainerActivity: AppCompatActivity(), SingleFragmentActivityInterface {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scrolling_container_layout)

        val fm = supportFragmentManager

        setSupportActionBar(findViewById(R.id.toolbar))

        var fragment = fm.findFragmentById(R.id.container)

        if (fragment == null){
            fragment = createFragment()
            fm.beginTransaction().add(R.id.container,fragment).commit()
        }

    }
}