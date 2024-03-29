package com.yingenus.pocketchinese.presentation.dialogs

import android.os.Bundle
import android.os.FileObserver
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.yingenus.pocketchinese.R
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.DisposableContainer

class LoadingDialog : DialogFragment() {

    private lateinit var textView : TextView
    private lateinit var progressIndicator: CircularProgressIndicator
    private val disposables = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.load_dialog,container)

        textView = view.findViewById(R.id.textView)
        progressIndicator = view.findViewById(R.id.progressBar)
        progressIndicator.show()

        isCancelable = false

        return view
    }

    fun registerObserver( observer: Observable<Pair<Int,String>>){
        disposables.add(observer
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {next ->
                    textView.text = next.second
                    progressIndicator.progress = next.first
                },{error ->
                    Log.d("LoadingDialog","error occurred : ${error.localizedMessage}")
                    if (!parentFragmentManager.isStateSaved)
                        this.dismiss()
                },{
                    if (!parentFragmentManager.isStateSaved)
                        this.dismiss()
                }
            )
        )
    }

    override fun onDestroyView() {
        progressIndicator.hide()
        super.onDestroyView()
        disposables.dispose()
    }
}