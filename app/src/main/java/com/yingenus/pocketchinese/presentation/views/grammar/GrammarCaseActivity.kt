package com.yingenus.pocketchinese.presentation.views.grammar

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.*
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.webkit.WebViewAssetLoader
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.data.assets.GrammarAssetsRepository
import com.yingenus.pocketchinese.data.assets.ImageAssetsRepository
import com.yingenus.pocketchinese.domain.dto.GrammarCase
import java.net.URI

class GrammarCaseActivity() : AppCompatActivity(), GrammarCaseInterface {

    companion object{
        private const val GRAMMAR_CASE_NAME = "com.yingenus.pocketchinese.presentation.views.grammar_grammar_case"


        fun getIntent(context : Context, grammarCase: GrammarCase): Intent{
            val intent = Intent(context, GrammarCaseActivity::class.java)
            intent.putExtra(GRAMMAR_CASE_NAME, grammarCase.name)
            return intent
        }

        fun getIntent(context : Context, grammarCaseName: String): Intent{
            val intent = Intent(context, GrammarCaseActivity::class.java)
            intent.putExtra(GRAMMAR_CASE_NAME, grammarCaseName)
            return intent
        }
    }

    private var toolbar : Toolbar? = null
    private var webView : WebView? = null

    private var presenter : GrammarPresenter? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.grammar_case_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_favorite ->{
                presenter?.likePressed()
                true
            }
            else ->{
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val caseName =intent.getStringExtra(GRAMMAR_CASE_NAME)

        if (caseName == null) declareError("")

        setContentView(R.layout.grammar_case_layout)

        toolbar = findViewById(R.id.toolbar)
        webView = findViewById(R.id.web_view)

        configureWebView()

        presenter = GrammarPresenter(this, caseName!!, GrammarAssetsRepository(baseContext), ImageAssetsRepository(baseContext))
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
        toolbar = null
        webView = null
        presenter = null
    }

    override fun setTitle(title: String) {
        toolbar?.title = title
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun setLiked(isLiked: Boolean) {
        val icon = if (isLiked)
            baseContext.getDrawable(R.drawable.ic_favorite)
        else
            baseContext.getDrawable(R.drawable.ic_favorite_borded)

        toolbar?.menu?.findItem(R.id.menu_favorite)?.icon = icon
    }

    override fun setTitleIconURI(iconLink: URI) {
        // not supported
    }

    override fun setHtmlURI(htmlLink: URI) {
        webView?.loadUrl(htmlLink.toString())
    }

    override fun declareError(msg: String) {
        MaterialAlertDialogBuilder(baseContext)
            .setMessage(getString(R.string.error_msg_def))
            .setPositiveButton(android.R.string.cancel) { _, _-> this.finish()}
            .show()
    }

    private fun configureWebView(){

        val assetsPathHandler = WebViewAssetLoader.AssetsPathHandler(baseContext)

        val isNight = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        val assetLoader = WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", ThemeDependsAssetsPathHandler(assetsPathHandler,isNight))
                .addPathHandler("/images/", ImageAssetsPathHandler(assetsPathHandler))
                .addPathHandler("/fonts/", FontAssetsPathHandler(assetsPathHandler))
                .addPathHandler("/", HttpAssetsPathHandler(assetsPathHandler))
                .build()

        webView?.webViewClient = object : WebViewClient(){

            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                val response = assetLoader.shouldInterceptRequest(request!!.url)
                return response
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                //declareError("web error")
            }
        }
    }

    private class ImageAssetsPathHandler(val assetsPathHandler: WebViewAssetLoader.AssetsPathHandler) : WebViewAssetLoader.PathHandler{

        private companion object{
            const val imageDir = "image"
        }


        override fun handle(path: String): WebResourceResponse? {
            val lastSplash = path.lastIndexOf("/")
            val name = if (lastSplash == -1) path else path.substring(0,lastSplash + 1)

            val newPath = "$imageDir/$name"
            val result =  assetsPathHandler.handle(newPath)
            return result
        }
    }

    private class FontAssetsPathHandler(val assetsPathHandler: WebViewAssetLoader.AssetsPathHandler) : WebViewAssetLoader.PathHandler{

        private companion object{
            const val fontDir = "font"
        }


        override fun handle(path: String): WebResourceResponse? {
            val lastSplash = path.lastIndexOf("/")
            val name = if (lastSplash == -1) path else path.substring(0,lastSplash + 1)

            val newPath = "$fontDir/$name"
            val result =  assetsPathHandler.handle(newPath)
            return result
        }
    }

    private class HttpAssetsPathHandler(val assetsPathHandler: WebViewAssetLoader.AssetsPathHandler) : WebViewAssetLoader.PathHandler{

        private companion object{
            const val httpDir = "www"
        }

        override fun handle(path: String): WebResourceResponse? {
            val lastSplash = path.lastIndexOf("/")
            val name = if (lastSplash == -1) path else path.substring(0,lastSplash + 1)

            val newPath = "$httpDir/$name"
            val result =  assetsPathHandler.handle(newPath)
            return result
        }
    }

    private class ThemeDependsAssetsPathHandler(val assetsPathHandler: WebViewAssetLoader.AssetsPathHandler,val isDarkTheme : Boolean) : WebViewAssetLoader.PathHandler{

        private companion object{
            const val darkAssets = "assets_dark"
            const val defaultAssets = "assets_def"
        }


        override fun handle(path: String): WebResourceResponse? {
            val lastSplash = path.lastIndexOf("/")
            val name = if (lastSplash == -1) path else path.substring(0,lastSplash + 1)

            val assetsPath = if (isDarkTheme) darkAssets else defaultAssets

            val newPath = "$assetsPath/$name"
            val result =  assetsPathHandler.handle(newPath)
            return result
        }
    }
}