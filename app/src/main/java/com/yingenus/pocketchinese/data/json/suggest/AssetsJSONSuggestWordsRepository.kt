package com.yingenus.pocketchinese.data.json.suggest

import android.content.Context
import com.yingenus.pocketchinese.data.json.suggest.JSONHelper.loadWordList
import com.yingenus.pocketchinese.domain.dto.*
import com.yingenus.pocketchinese.domain.repository.SuggestWordsRepository
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.InputStream
import java.util.*
import javax.inject.Inject

class AssetsJSONSuggestWordsRepository @Inject constructor(
    private val context: Context
) : SuggestWordsRepository {

    override fun getAllSuggestLists(): Single<List<SuggestList>> {
        return Single.defer { Single.create<List<SuggestList>> {
                val ips = context.assets.open("suggest/ListsInfo.json")
                val lists = JSONHelper.loadDirInfo(ips).files
                it.onSuccess( lists.map { suggestList(it) })
            }
        }.subscribeOn(Schedulers.io())
    }

    override fun getSuggestListByName(name: String): Maybe<SuggestListDetailed> {
        return Maybe.defer { Maybe.create<SuggestListDetailed> {
            val ips = context.assets.open("suggest/ListsInfo.json")
            val fInfo = JSONHelper.loadDirInfo(ips).files.find { it.name == name }
            if (fInfo == null) it.onComplete()
            val ips1: InputStream = context.getAssets().open(
                "suggest/" + fInfo?.getFileName()?.lowercase(
                    Locale.getDefault()
                )
            )
            val suggestWords = loadWordList(ips1)
            it.onSuccess(suggestListDetailed(suggestWords,fInfo!!))
            it.onComplete()
        }
        }
            .subscribeOn(Schedulers.io())
    }

    override fun getSuggestWords(name : String): Single<List<SuggestWordGroup>> {
        return Single.defer { Single.create<List<SuggestWordGroup>> {
            val ips = context.assets.open("suggest/ListsInfo.json")
            val fInfo = JSONHelper.loadDirInfo(ips).files.find { it.name == name }
            val ips1: InputStream = context.getAssets().open(
                "suggest/" + fInfo?.getFileName()?.lowercase(
                    Locale.getDefault()
                )
            )
            val suggestWords = loadWordList(ips1)
            it.onSuccess(suggestWords.words.map { suggestWordGroup(it) })
        }
        }
            .subscribeOn(Schedulers.io())
    }

    private fun suggestWordGroup(wordsGroup : JSONObjects.WordsGroup) : SuggestWordGroup =
        SuggestWordGroup(
            name = wordsGroup.name,
            words = wordsGroup.words.map { suggestWord(it) }
        )

    private fun suggestWord(word: JSONObjects.Word): SuggestWord =
        SuggestWord(
            word = word.word,
            pinyin = word.pinyin,
            translation = word.translation,
            examples = word.examples.map { example(it) },
            null
        )

    private fun example(example : JSONObjects.Example): Example =
        Example(
            id = -1,
            chinese = example.chinese,
            pinyin = example.pinyin,
            translation = example.translation
        )

    private fun suggestListDetailed( wordList : JSONObjects.WordList, fileInfo: JSONObjects.FileInfo): SuggestListDetailed =
        SuggestListDetailed(
            name = wordList.name?: "null",
            version = wordList.version?:"null",
            words = wordList.items,
            image = wordList.image?:"null",
            tags = fileInfo.tags,
            description = wordList.description?:"null"
        )

    private fun suggestList( fileInfo: JSONObjects.FileInfo): SuggestList =
        SuggestList(
            name = fileInfo.name?: "null",
            version = fileInfo.version?:"null",
            words = fileInfo.words_count,
            image = fileInfo.image?:"null",
            tags = fileInfo.tags
        )
}