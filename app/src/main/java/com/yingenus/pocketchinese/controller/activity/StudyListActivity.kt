package com.yingenus.pocketchinese.controller.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.yingenus.pocketchinese.controller.fragment.StudyListFragment
import java.lang.RuntimeException
import java.util.*

class StudyListActivity:SingleFragmentActivityWithActionBar() {

    companion object{
        const val INNER_STUDY_LIST="com.pocketchinese.com.studylist.studyworduuid"

        fun getIntent(context: Context, studyListUUID: UUID):Intent{
            val intent=Intent(context,StudyListActivity::class.java)
            intent.putExtra(INNER_STUDY_LIST,studyListUUID.toString())
            return intent
        }
    }

    class StudyListFragmentFactory(val studyListUUID: UUID) : FragmentFactory(){
        override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
            if(className == StudyListFragment::class.java.name)
                return StudyListFragment(studyListUUID)
            return super.instantiate(classLoader, className)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = createFragmentFactory(savedInstanceState)
        super.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        val ff = supportFragmentManager.fragmentFactory
        if (ff is StudyListFragmentFactory){
            outState.putString(INNER_STUDY_LIST, ff.studyListUUID.toString())
        }
    }

    override fun createFragment(): Fragment {
        return supportFragmentManager.fragmentFactory.instantiate(classLoader,StudyListFragment::class.java.name)
    }

    private fun createFragmentFactory(savedInstanceState: Bundle?): FragmentFactory{
        val uuid = (if (savedInstanceState != null && savedInstanceState.containsKey(INNER_STUDY_LIST))
            savedInstanceState.getString(INNER_STUDY_LIST)
        else
            intent.getStringExtra(INNER_STUDY_LIST))
                ?: throw RuntimeException("cant extract studyList uuid")

        return StudyListFragmentFactory(UUID.fromString(uuid))
    }
}