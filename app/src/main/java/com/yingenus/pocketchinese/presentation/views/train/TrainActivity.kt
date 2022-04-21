package com.yingenus.pocketchinese.presentation.views.train

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.yingenus.pocketchinese.domain.dto.TrainingConf
import com.yingenus.pocketchinese.domain.entitiys.LanguageCase
import com.yingenus.pocketchinese.view.activity.SingleFragmentActivityWithKeyboard
import java.lang.RuntimeException
import java.util.*

class TrainActivity:
    SingleFragmentActivityWithKeyboard() {

    private class TrainActivityFragmentFactory(val trainingConf: TrainingConf) : FragmentFactory(){
        override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
            if(className == TrainingFragment::class.java.name)
                return TrainingFragment(
                    trainingConf
                )
            return super.instantiate(classLoader, className)
        }
    }

    companion object{
        private const val INNER_TRAIN_ACTIVITY_LANG = "com.pocketchinese.com.trainactivity.language"
        private const val INNER_TRAIN_ACTIVITY_UUID = "com.pocketchinese.com.trainactivity.uuid"
        private const val INNER_TRAIN_ACTIVITY_BLOCK = "com.pocketchinese.com.trainactivity.block"
        private const val INNER_TRAIN_ACTIVITY_CONF = "com.pocketchinese.com.trainactivity.config"

        fun getIntent(trainingConf: TrainingConf, context : Context): Intent {
            val intent=  Intent(context, TrainActivity::class.java)
            intent.putExtra(INNER_TRAIN_ACTIVITY_CONF, trainingConf)
            //val langCode=when(language){
            //    LanguageCase.Chin->0
            //    LanguageCase.Pin->1
            //    LanguageCase.Trn->2
            //}
            //intent.putExtra(INNER_TRAIN_ACTIVITY_LANG,langCode)
            //intent.putExtra(INNER_TRAIN_ACTIVITY_UUID,studyListUUID.toString())
            //intent.putExtra(INNER_TRAIN_ACTIVITY_BLOCK,block)
            return intent
        }

        private fun getLangCode(lang : LanguageCase) =when(lang){
            LanguageCase.Chin->0
            LanguageCase.Pin->1
            LanguageCase.Trn->2
        }
        private fun getLangFromInt(int : Int) = when(int){
            0->LanguageCase.Chin
            1->LanguageCase.Pin
            2->LanguageCase.Trn
            else->LanguageCase.Chin
        }

    }

    override fun createFragment()=getFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = createFragmentFactory(savedInstanceState)
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val ff = supportFragmentManager.fragmentFactory
        if (ff is TrainActivityFragmentFactory){
            outState.putSerializable(INNER_TRAIN_ACTIVITY_CONF, ff.trainingConf)
        }
    }

    private fun createFragmentFactory(savedInstanceState: Bundle?): FragmentFactory{
        val trainingConf : TrainingConf

        if (savedInstanceState != null
                && savedInstanceState.containsKey(INNER_TRAIN_ACTIVITY_CONF)
        ){
            trainingConf = savedInstanceState.getSerializable(INNER_TRAIN_ACTIVITY_CONF) as TrainingConf
        }
        else{
            trainingConf = intent.getSerializableExtra(INNER_TRAIN_ACTIVITY_CONF) as TrainingConf
        }

        return TrainActivityFragmentFactory(trainingConf)
    }

    private fun getFragment():Fragment{
        return supportFragmentManager.fragmentFactory.instantiate(classLoader,
            TrainingFragment::class.java.name)
    }
}