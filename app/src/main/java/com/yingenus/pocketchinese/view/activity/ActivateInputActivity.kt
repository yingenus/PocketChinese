package com.yingenus.pocketchinese.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.yingenus.pocketchinese.R

class ActivateInputActivity : AppCompatActivity(){

    companion object{
        fun getIntent(context: Context): Intent{
            return Intent(context, ActivateInputActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.active_input_promit)

        val closeButton : Button = findViewById(R.id.close_button)
        closeButton.setOnClickListener { _ : View ->
            finish()
        }

        val acceptButton : Button = findViewById(R.id.accept_button)
        acceptButton.setOnClickListener { _ : View ->
            val intent = Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.enabledInputMethodList.any{ it.serviceName.contains("PinyinPocketInputMethodService",true)}){
            finish()
        }
    }
}