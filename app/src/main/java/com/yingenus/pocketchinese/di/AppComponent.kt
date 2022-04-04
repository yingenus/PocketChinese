package com.yingenus.pocketchinese.di

import android.content.Context
import com.yingenus.pocketchinese.presentation.dialogs.radicalsearch.RadicalSearchDialog
import com.yingenus.pocketchinese.presentation.views.character.CharacterSheetDialog
import com.yingenus.pocketchinese.presentation.views.creteeditword.CreateNewWordFragment
import com.yingenus.pocketchinese.presentation.views.creteeditword.CreateWordActivity
import com.yingenus.pocketchinese.presentation.views.creteeditword.EditWordFragment
import com.yingenus.pocketchinese.presentation.views.dictionary.DictionaryFragment
import com.yingenus.pocketchinese.presentation.views.grammar.GrammarCaseActivity
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

        fun build() : AppComponent

    }

    fun injectRadicalSearchDialog( radicalSearchDialog : RadicalSearchDialog)
    fun injectCharacterSheetDialog(characterSheetDialog: CharacterSheetDialog)
    fun injectDictionaryFragment( dictionaryFragment: DictionaryFragment)
    fun injectGrammarCaseActivity( grammarCaseActivity : GrammarCaseActivity)
    fun injectCreateNewWordFragment(createNewWordFragment : CreateNewWordFragment)
    fun injectCreateWordActivity(createWordActivity : CreateWordActivity)
    fun injectEditWordFragment(editWordFragment: EditWordFragment)
}