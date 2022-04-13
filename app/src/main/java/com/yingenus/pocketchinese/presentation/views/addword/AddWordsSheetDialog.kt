package com.yingenus.pocketchinese.presentation.views.addword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.yingenus.pocketchinese.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yingenus.pocketchinese.domain.dto.SuggestWord

class AddWordsSheetDialog : BottomSheetDialogFragment(){

    private lateinit var words: List<SuggestWord>

    var callback : AddWordFragment.AddWordsCallbacks? = null
        set(value) {
            field = value
            if (isAdded && value != null) {
                val fragment = childFragmentManager.findFragmentById(R.id.fragment_container)
                if ( fragment != null) {
                    (fragment as AddWordFragment).setCallBack(callback!!)
                }
            }
        }


    fun setWords(words : List<SuggestWord>){
      this.words = words
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        childFragmentManager.fragmentFactory = AddWordFragment.AddWordFragmentFactory(words, callback)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val layout = inflater.inflate(R.layout.add_word_dialog,container,false)

        val headerView = layout.findViewById<Toolbar>(R.id.toolbar)
        headerView.title = getString(R.string.add_words)

        val fragment = childFragmentManager.fragmentFactory.instantiate(ClassLoader.getSystemClassLoader(), AddWordFragment::class.java.name)
        if (callback!=null){
            (fragment as AddWordFragment).setCallBack(callback!!)
        }
        childFragmentManager.beginTransaction().add(R.id.fragment_container,fragment).commit()

        return layout
    }

    override fun onResume() {
        super.onResume()
        val fragment =childFragmentManager.findFragmentById(R.id.fragment_container)
        if (fragment != null) {
            (fragment as AddWordFragment).setCallBack(callback)
        }
    }

    override fun onPause() {
        super.onPause()
        val fragment =childFragmentManager.findFragmentById(R.id.fragment_container)
        if (fragment != null) {
            (fragment as AddWordFragment).setCallBack(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        callback = null
    }
}