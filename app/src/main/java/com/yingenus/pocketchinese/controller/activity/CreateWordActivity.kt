package com.yingenus.pocketchinese.controller.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.yingenus.pocketchinese.controller.fragment.CreateNewWordFragment
import com.yingenus.pocketchinese.di.ServiceLocator
import com.yingenus.pocketchinese.domain.repository.ChinCharRepository
import com.yingenus.pocketchinese.model.database.DictionaryDBOpenManger
import java.lang.IllegalArgumentException
import java.lang.RuntimeException
import java.sql.SQLException
import java.util.*

class CreateWordActivity :SingleFragmentActivityWithKeyboard() {

    class CreateWordFragmentFactory(val chinChar : com.yingenus.pocketchinese.domain.dto.ChinChar?, val uuid: UUID?,val  chinCharRepository: ChinCharRepository): FragmentFactory(){
        init {
            if (chinChar == null && uuid == null) throw IllegalArgumentException("two params cant be null")
        }

        override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
            if (className == CreateNewWordFragment::class.java.name)
                return if (chinChar != null) CreateNewWordFragment(chinChar,chinCharRepository) else CreateNewWordFragment(uuid!!,chinCharRepository)
            return super.instantiate(classLoader, className)
        }
    }

    companion object{
        const val INNER_WORD_CREATE_STUDY_LIST="com.pocketchinese.com.studylist.studyworduuid"
        const val INNER_WORD_CREATE_CHIN_CHAR_ID="com.pocketchinese.com.studylist.chinchar"

        fun getIntent(context: Context, studyListUUID: UUID): Intent {
            val intent= Intent(context,CreateWordActivity::class.java)
            intent.putExtra(INNER_WORD_CREATE_STUDY_LIST,studyListUUID.toString())
            return intent
        }

        fun getIntent(context: Context, chinChar: com.yingenus.pocketchinese.domain.dto.ChinChar): Intent {
            val intent= Intent(context,CreateWordActivity::class.java)
            intent.putExtra(INNER_WORD_CREATE_CHIN_CHAR_ID,chinChar.id)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = createFragmentFactory(savedInstanceState)
        super.onCreate(savedInstanceState)

        supportActionBar?.elevation = 0f

    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        val ff = supportFragmentManager.fragmentFactory
        if(ff is CreateWordFragmentFactory){
            if (ff.chinChar != null)
                outState.putInt(INNER_WORD_CREATE_CHIN_CHAR_ID, ff.chinChar.id)
            if (ff.uuid != null)
                outState.putString(INNER_WORD_CREATE_STUDY_LIST, ff.uuid.toString())
        }
    }

    override fun createFragment():Fragment{
        return supportFragmentManager.fragmentFactory.instantiate(classLoader,CreateNewWordFragment::class.java.name)
    }

    private fun createFragmentFactory(savedInstanceState: Bundle?): FragmentFactory{
        val chinCharId : Int
        val uuid : String?

        if (savedInstanceState != null && (savedInstanceState.containsKey(INNER_WORD_CREATE_STUDY_LIST) || savedInstanceState.containsKey(INNER_WORD_CREATE_CHIN_CHAR_ID))){
            chinCharId = savedInstanceState.getInt(INNER_WORD_CREATE_CHIN_CHAR_ID, -1)
            uuid = savedInstanceState.getString(INNER_WORD_CREATE_STUDY_LIST)
        }else{
            chinCharId = intent.getIntExtra(INNER_WORD_CREATE_CHIN_CHAR_ID,-1)
            uuid = intent.getStringExtra(INNER_WORD_CREATE_STUDY_LIST)
        }

        if (chinCharId == -1 && uuid == null) throw RuntimeException("cant extract params")

        var chinChar : com.yingenus.pocketchinese.domain.dto.ChinChar?

        if (chinCharId != -1){
            try {
                //val connection = DictionaryDBOpenManger.getHelper(applicationContext, DictionaryDBHelper::class.java ).connectionSource
                val dao : ChinCharRepository = ServiceLocator.get(applicationContext, ChinCharRepository::class.java.name)
                chinChar = dao.findById(chinCharId)
                        //dao.findChinCharInColumn(chinCharId.toString(),ChinChar.ID_FIELD_NAME).first()
            }catch (e : SQLException){
                chinChar = null
            }finally {
                //DictionaryDBOpenManger.releaseHelper()
            }
        }else{
            chinChar = null
        }

        return CreateWordFragmentFactory(
                chinChar = chinChar,
                uuid = if (uuid != null) UUID.fromString(uuid) else null,
                chinCharRepository = ServiceLocator.get(applicationContext, ChinCharRepository::class.java.name)
        )

    }
}