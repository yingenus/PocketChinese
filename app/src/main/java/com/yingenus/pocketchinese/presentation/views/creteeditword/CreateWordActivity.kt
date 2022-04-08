package com.yingenus.pocketchinese.presentation.views.creteeditword

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.yingenus.pocketchinese.PocketApplication
import com.yingenus.pocketchinese.domain.dto.DictionaryItem
import com.yingenus.pocketchinese.view.activity.SingleFragmentActivityWithKeyboard
import com.yingenus.pocketchinese.domain.repository.DictionaryItemRepository
import java.lang.IllegalArgumentException
import java.lang.RuntimeException
import java.sql.SQLException
import java.util.*
import javax.inject.Inject

class CreateWordActivity : SingleFragmentActivityWithKeyboard() {

    @Inject
    lateinit var dictionaryItemRepository: DictionaryItemRepository


    class CreateWordFragmentFactory(val dictionaryItem : DictionaryItem?, val uuid: UUID?): FragmentFactory(){
        init {
            if (dictionaryItem == null && uuid == null) throw IllegalArgumentException("two params cant be null")
        }

        override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
            if (className == CreateNewWordFragment::class.java.name)
                return if (dictionaryItem != null) CreateNewWordFragment(dictionaryItem) else CreateNewWordFragment(uuid!!)
            return super.instantiate(classLoader, className)
        }
    }

    companion object{
        const val INNER_WORD_CREATE_STUDY_LIST="com.pocketchinese.com.studylist.studyworduuid"
        const val INNER_WORD_CREATE_CHIN_CHAR_ID="com.pocketchinese.com.studylist.chinchar"

        fun getIntent(context: Context, studyListUUID: UUID): Intent {
            val intent= Intent(context, CreateWordActivity::class.java)
            intent.putExtra(INNER_WORD_CREATE_STUDY_LIST,studyListUUID.toString())
            return intent
        }

        fun getIntent(context: Context, dictionaryItem: com.yingenus.pocketchinese.domain.dto.DictionaryItem): Intent {
            val intent= Intent(context,CreateWordActivity::class.java)
            intent.putExtra(INNER_WORD_CREATE_CHIN_CHAR_ID,dictionaryItem.id)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        PocketApplication.getAppComponent().injectCreateWordActivity(this)
        supportFragmentManager.fragmentFactory = createFragmentFactory(savedInstanceState)
        super.onCreate(savedInstanceState)

        supportActionBar?.elevation = 0f

    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        val ff = supportFragmentManager.fragmentFactory
        if(ff is CreateWordFragmentFactory){
            if (ff.dictionaryItem != null)
                outState.putInt(INNER_WORD_CREATE_CHIN_CHAR_ID, ff.dictionaryItem.id)
            if (ff.uuid != null)
                outState.putString(INNER_WORD_CREATE_STUDY_LIST, ff.uuid.toString())
        }
    }

    override fun createFragment():Fragment{
        return supportFragmentManager.fragmentFactory.instantiate(classLoader,
            CreateNewWordFragment::class.java.name)
    }

    private fun createFragmentFactory(savedInstanceState: Bundle?): FragmentFactory{
        val chinCharId : Int
        val uuid : String?

        if (savedInstanceState != null && (savedInstanceState.containsKey(
                INNER_WORD_CREATE_STUDY_LIST
            ) || savedInstanceState.containsKey(INNER_WORD_CREATE_CHIN_CHAR_ID))){
            chinCharId = savedInstanceState.getInt(INNER_WORD_CREATE_CHIN_CHAR_ID, -1)
            uuid = savedInstanceState.getString(INNER_WORD_CREATE_STUDY_LIST)
        }else{
            chinCharId = intent.getIntExtra(INNER_WORD_CREATE_CHIN_CHAR_ID,-1)
            uuid = intent.getStringExtra(INNER_WORD_CREATE_STUDY_LIST)
        }

        if (chinCharId == -1 && uuid == null) throw RuntimeException("cant extract params")

        var dictionaryItem : com.yingenus.pocketchinese.domain.dto.DictionaryItem?

        if (chinCharId != -1){
            try {
                dictionaryItem = dictionaryItemRepository.findById(chinCharId)
            }catch (e : SQLException){
                dictionaryItem = null
            }finally {
                //DictionaryDBOpenManger.releaseHelper()
            }
        }else{
            dictionaryItem = null
        }

        return CreateWordFragmentFactory(
                dictionaryItem = dictionaryItem,
                uuid = if (uuid != null) UUID.fromString(uuid) else null)

    }
}