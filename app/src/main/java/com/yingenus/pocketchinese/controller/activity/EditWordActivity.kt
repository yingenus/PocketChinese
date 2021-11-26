package com.yingenus.pocketchinese.controller.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.yingenus.pocketchinese.controller.fragment.EditWordFragment
import com.yingenus.pocketchinese.di.ServiceLocator
import com.yingenus.pocketchinese.domain.repository.ChinCharRepository
import com.yingenus.pocketchinese.model.database.pocketDB.StudyWord
import java.lang.RuntimeException
import java.util.*

class EditWordActivity : SingleFragmentActivityWithKeyboard(){

    class EditWordFragmentFactory(val studyListUUID: UUID, val studyWordUUID: UUID, val chinCharRepository: ChinCharRepository): FragmentFactory(){
        override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
            if(className == EditWordFragment::class.java.name)
                return EditWordFragment(studyWordUUID,studyListUUID,chinCharRepository)
            return super.instantiate(classLoader, className)
        }
    }

    companion object{
        const val INNER_EDIT_WORD_WORD_UUID= "com.pocketchinese.com.studyword_uuid"
        const val INNER_EDIT_WORD_LIST_UUID= "com.pocketchinese.com.studylist_uuid"

        fun getIntent(context: Context, studyWord: StudyWord, studyListUUID: UUID): Intent{
            val intent = Intent(context, EditWordActivity::class.java)
            intent.putExtra(INNER_EDIT_WORD_WORD_UUID,studyWord.uuid.toString())
            intent.putExtra(INNER_EDIT_WORD_LIST_UUID,studyListUUID.toString())
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = createFragmentFactory(savedInstanceState)
        super.onCreate(savedInstanceState)
    }

    override fun createFragment(): Fragment {
        val studyList = UUID.fromString(intent.getStringExtra(INNER_EDIT_WORD_LIST_UUID))
        val studyWord = UUID.fromString(intent.getStringExtra(INNER_EDIT_WORD_WORD_UUID))

        return EditWordFragment(studyWord,studyList,ServiceLocator.get(baseContext,ChinCharRepository::class.java.name))
    }

    private fun createFragmentFactory(savedInstanceState: Bundle?): FragmentFactory{
        val studyList : String?
        val studyWord : String?

        if (savedInstanceState!= null && savedInstanceState.containsKey(INNER_EDIT_WORD_WORD_UUID) && savedInstanceState.containsKey(INNER_EDIT_WORD_LIST_UUID)){
            studyList = savedInstanceState.getString(INNER_EDIT_WORD_LIST_UUID)
            studyWord = savedInstanceState.getString(INNER_EDIT_WORD_WORD_UUID)
        }else{
            studyList = intent.getStringExtra(INNER_EDIT_WORD_LIST_UUID)
            studyWord = intent.getStringExtra(INNER_EDIT_WORD_WORD_UUID)
        }

        if (studyList == null || studyWord == null) throw RuntimeException("cant extract com.yingenus.pocketchinese.data")

        return EditWordFragmentFactory(UUID.fromString(studyList), UUID.fromString(studyWord), ServiceLocator.get(baseContext,ChinCharRepository::class.java.name))
    }
}