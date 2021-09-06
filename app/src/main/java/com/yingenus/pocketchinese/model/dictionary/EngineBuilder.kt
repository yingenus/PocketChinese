package com.yingenus.pocketchinese.model.dictionary

import android.content.res.AssetManager
import com.yingenus.pocketchinese.model.database.dictionaryDB.ChinCharDaoImpl
import com.yingenus.pocketchinese.model.dictionary.search.*
import com.yingenus.pocketchinese.model.dictionary.search.fuzzySearch.ChnFuzzSearch
import com.yingenus.pocketchinese.model.dictionary.search.fuzzySearch.PinFuzzSearch
import com.yingenus.pocketchinese.model.dictionary.search.fuzzySearch.RusFuzzSearch
import com.yingenus.pocketchinese.model.dictionary.search.sort.LevenshteinSortedSearch
import com.yingenus.pocketchinese.model.dictionary.search.sort.N2GramSortedSearch
import java.lang.IllegalArgumentException

abstract class DbEngineBuilder {
    fun build(chinDao : ChinCharDaoImpl,assetsManager : AssetManager,files : List<String> ) =
            SearchEngine(getSearcher(assetsManager, files), getExtractor(chinDao))

    abstract fun getExtractor(chinDao: ChinCharDaoImpl): Extractor
    abstract fun getSearcher(assetsManager : AssetManager, files : List<String>): Searcher
}

object FuzzyRusBuilder: DbEngineBuilder() {

    override fun getExtractor(chinDao: ChinCharDaoImpl) = IdDbExtractor(chinDao)
    override fun getSearcher(assetsManager : AssetManager, files: List<String>): Searcher {
        if (files.isEmpty()) throw IllegalArgumentException(" require at least one files")
        return ResSearcher(LevenshteinSortedSearch(RusFuzzSearch()), assetsManager, files)
    }
}
object FuzzyPinBuilder: DbEngineBuilder(){

    override fun getExtractor(chinDao: ChinCharDaoImpl) = IdDbExtractor(chinDao)
    override fun getSearcher(assetsManager : AssetManager, files : List<String>): Searcher
    {
        if (files.isEmpty()) throw IllegalArgumentException(" require at least one files")
        return ResSearcher(LevenshteinSortedSearch(PinFuzzSearch()), assetsManager, files)
    }

}
object FuzzyChnBuilder: DbEngineBuilder() {

    override fun getExtractor(chinDao: ChinCharDaoImpl) = IdDbExtractor(chinDao)
    override fun getSearcher(assetsManager : AssetManager, files: List<String>): Searcher {
        if (files.size < 2)  throw IllegalArgumentException( " require at least two files")
        return ResSearcher(N2GramSortedSearch(ChnFuzzSearch()), assetsManager, files)
    }
}
object RusBuilder: DbEngineBuilder() {

    override fun getExtractor(chinDao: ChinCharDaoImpl) = IdDbExtractor(chinDao)
    override fun getSearcher(assetsManager : AssetManager, files: List<String>): Searcher {
        if (files.isEmpty()) throw IllegalArgumentException(" require at least one files")
        return ResSearcher(MatchSearch(), assetsManager, files)
    }
}
object PinBuilder: DbEngineBuilder() {

    override fun getExtractor(chinDao: ChinCharDaoImpl) = IdDbExtractor(chinDao)
    override fun getSearcher(assetsManager : AssetManager, files: List<String>): Searcher {
        if (files.isEmpty()) throw IllegalArgumentException(" require at least one files")
        return ResSearcher(MatchSearch(), assetsManager, files)
    }
}
object ChnBuilder: DbEngineBuilder() {

    override fun getExtractor(chinDao: ChinCharDaoImpl) = IdDbExtractor(chinDao)
    override fun getSearcher(assetsManager : AssetManager, files: List<String>): Searcher {
        if (files.isEmpty()) throw IllegalArgumentException(" require at least one files")
        return ResSearcher(MatchSearch(), assetsManager, files)
    }
}