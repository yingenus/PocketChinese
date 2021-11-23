package com.yingenus.pocketchinese.data.assets

import android.content.Context
import android.content.res.AssetManager
import com.google.gson.GsonBuilder
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.yingenus.pocketchinese.data.json.GrammarJSON
import com.yingenus.pocketchinese.domain.dto.GrammarCase
import com.yingenus.pocketchinese.domain.repository.GrammarRep
import java.io.IOException

class GrammarAssetsRepository(context: Context) : GrammarRep {

    companion object{
        private const val grammarDir = "grammar"
        private const val grammarInfoFile = "grammarinfo.json"
    }

    private val manager : AssetManager = context.assets

    @Throws(IOException::class, JsonSyntaxException::class, JsonIOException::class)
    override fun getCase(name: String): GrammarCase? {
        val ips = manager.open("$grammarDir/$grammarInfoFile")

        val buider = GsonBuilder()
        buider.serializeNulls()
        val gson = buider.create()

        val grammarJSON = gson.fromJson(ips.reader(),GrammarJSON::class.java)

        return grammarJSON.cases.find { it.name == name }?.toGrammarCase()
    }

    @Throws(IOException::class, JsonSyntaxException::class, JsonIOException::class)
    override fun getCases(): List<GrammarCase> {
        val ips = manager.open("$grammarDir/$grammarInfoFile")

        val buider = GsonBuilder()
        buider.serializeNulls()
        val gson = buider.create()

        val grammarJSON = gson.fromJson(ips.reader(),GrammarJSON::class.java)

        return grammarJSON.cases.toList().map { it.toGrammarCase()}
    }
}