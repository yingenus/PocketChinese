package com.yingenus.pocketchinese.di

import android.content.Context
import com.yingenus.pocketchinese.data.local.db.sqlite.SqliteDatabaseManager
import com.yingenus.pocketchinese.data.proxy.ProxyRepositoryProvider
import com.yingenus.pocketchinese.domain.entities.dictionarysearch.ProxySearcherProvider
import com.yingenus.pocketchinese.presentation.dialogs.StartTrainingSheetDialog
import com.yingenus.pocketchinese.presentation.dialogs.radicalsearch.RadicalSearchDialog
import com.yingenus.pocketchinese.presentation.views.addword.AddSuggestWordsActivity
import com.yingenus.pocketchinese.presentation.views.addword.AddWordFromDictionary
import com.yingenus.pocketchinese.presentation.views.addword.CreateWordForListFragment
import com.yingenus.pocketchinese.presentation.views.character.CharacterSheetDialog
import com.yingenus.pocketchinese.presentation.views.creteeditword.CreateNewWordFragment
import com.yingenus.pocketchinese.presentation.views.creteeditword.CreateWordActivity
import com.yingenus.pocketchinese.presentation.views.creteeditword.EditWordFragment
import com.yingenus.pocketchinese.presentation.views.dictionary.DictionaryFragment
import com.yingenus.pocketchinese.presentation.views.grammar.GrammarCaseActivity
import com.yingenus.pocketchinese.presentation.views.settings.SettingsFragment
import com.yingenus.pocketchinese.presentation.views.stydylist.NewStudyListActivity
import com.yingenus.pocketchinese.presentation.views.suggestist.SuggestWordsActivity
import com.yingenus.pocketchinese.presentation.views.train.TrainingFragment
import com.yingenus.pocketchinese.presentation.views.userlist.NewUserListsFragment
import dagger.Component
import dagger.BindsInstance
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {

    @Component.Builder
    interface Builder{

        @BindsInstance
        fun context(context: Context): Builder

        @BindsInstance
        fun proxyRepository( proxyRepositoryProvider: ProxyRepositoryProvider) : Builder

        @BindsInstance
        fun proxySearchers (proxySearcherProvider: ProxySearcherProvider) : Builder

        @BindsInstance
        fun sqliteDatabaseManager(databaseManager: SqliteDatabaseManager) : Builder

        fun build() : AppComponent

    }

    fun injectRadicalSearchDialog( radicalSearchDialog : RadicalSearchDialog)
    fun injectCharacterSheetDialog(characterSheetDialog: CharacterSheetDialog)
    fun injectDictionaryFragment( dictionaryFragment: DictionaryFragment)
    fun injectGrammarCaseActivity( grammarCaseActivity : GrammarCaseActivity)
    fun injectCreateNewWordFragment(createNewWordFragment : CreateNewWordFragment)
    fun injectCreateWordActivity(createWordActivity : CreateWordActivity)
    fun injectEditWordFragment(editWordFragment: EditWordFragment)
    fun injectSettingsFragment( settingsFragment: SettingsFragment)
    fun injectSuggestWordsActivity( suggestWordsActivity : SuggestWordsActivity)
    fun injectUserListFragment(userListsFragment: NewUserListsFragment)
    fun injectStudyListActivity(studyListActivity: NewStudyListActivity)
    fun injectStartTrainingDialog(startTrainingSheetDialog: StartTrainingSheetDialog)
    fun injectTrainingFragment( trainingFragment: TrainingFragment)
    fun injectAddSuggestWordsActivity( addSuggestWordsActivity : AddSuggestWordsActivity)
    fun injectAddWordFromDictionary(addWordFromDictionary : AddWordFromDictionary)
    fun injectCreateWordForListFragment( createWordForListFragment : CreateWordForListFragment)
    fun injectEditWordFragment(editWordFragment: com.yingenus.pocketchinese.presentation.views.addword.EditWordFragment)
}