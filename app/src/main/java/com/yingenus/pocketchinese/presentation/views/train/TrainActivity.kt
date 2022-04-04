package com.yingenus.pocketchinese.presentation.views.train

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.yingenus.pocketchinese.domain.entitiys.LanguageCase
import com.yingenus.pocketchinese.view.activity.SingleFragmentActivityWithKeyboard
import java.lang.RuntimeException
import java.util.*

class TrainActivity:
    SingleFragmentActivityWithKeyboard() {

    private class TrainActivityFragmentFactory(val lang : LanguageCase, val uuid: UUID, val block : Int) : FragmentFactory(){
        override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
            if(className == TrainingFragment::class.java.name)
                return TrainingFragment(
                    lang,
                    uuid,
                    block
                )
            return super.instantiate(classLoader, className)
        }
    }

    companion object{
        const val INNER_TRAIN_ACTIVITY_LANG = "com.pocketchinese.com.trainactivity.language"
        const val INNER_TRAIN_ACTIVITY_UUID = "com.pocketchinese.com.trainactivity.uuid"
        const val INNER_TRAIN_ACTIVITY_BLOCK = "com.pocketchinese.com.trainactivity.block"

        fun getIntent(context: Context, language: LanguageCase, studyListUUID: UUID, block: Int): Intent {
            val intent=  Intent(context, TrainActivity::class.java)

            val langCode=when(language){
                LanguageCase.Chin->0
                LanguageCase.Pin->1
                LanguageCase.Trn->2
            }
            intent.putExtra(INNER_TRAIN_ACTIVITY_LANG,langCode)
            intent.putExtra(INNER_TRAIN_ACTIVITY_UUID,studyListUUID.toString())
            intent.putExtra(INNER_TRAIN_ACTIVITY_BLOCK,block)
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
            outState.putInt(INNER_TRAIN_ACTIVITY_BLOCK, ff.block)
            outState.putInt(INNER_TRAIN_ACTIVITY_LANG, getLangCode(ff.lang))
            outState.getString(INNER_TRAIN_ACTIVITY_UUID, ff.uuid.toString())
        }
    }

    private fun createFragmentFactory(savedInstanceState: Bundle?): FragmentFactory{
        val block : Int
        val lang : Int
        val uuid : String?

        if (savedInstanceState != null
                && savedInstanceState.containsKey(INNER_TRAIN_ACTIVITY_BLOCK)
                && savedInstanceState.containsKey(INNER_TRAIN_ACTIVITY_LANG)
                && savedInstanceState.containsKey(INNER_TRAIN_ACTIVITY_UUID)
        ){
            block = savedInstanceState.getInt(INNER_TRAIN_ACTIVITY_BLOCK)
            lang = savedInstanceState.getInt(INNER_TRAIN_ACTIVITY_LANG)
            uuid = savedInstanceState.getString(INNER_TRAIN_ACTIVITY_UUID)
        }
        else{
            block = intent.getIntExtra(INNER_TRAIN_ACTIVITY_BLOCK,-1)
            lang = intent.getIntExtra(INNER_TRAIN_ACTIVITY_LANG,-1)
            uuid = intent.getStringExtra(INNER_TRAIN_ACTIVITY_UUID)
        }

        if (block == -1 || lang == -1 || uuid == null) throw RuntimeException("cant extract com.yingenus.pocketchinese.data")

        return TrainActivityFragmentFactory(getLangFromInt(lang), UUID.fromString(uuid), block)
    }

    private fun getFragment():Fragment{
        return supportFragmentManager.fragmentFactory.instantiate(classLoader,
            TrainingFragment::class.java.name)
    }
}