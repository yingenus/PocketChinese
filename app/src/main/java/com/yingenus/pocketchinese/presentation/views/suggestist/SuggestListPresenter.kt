package com.yingenus.pocketchinese.presentation.views.suggestist

import android.content.Context
import com.yingenus.pocketchinese.Settings
import com.yingenus.pocketchinese.data.json.suggest.JSONHelper
import com.yingenus.pocketchinese.data.json.suggest.JSONObjects
import java.lang.IllegalArgumentException

class SuggestListPresenter(val view: SuggestListsInterface) {

    private object Const{
        const val maxTags = 20;
    }

    private lateinit var lists : List<JSONObjects.FileInfo>
    private lateinit var tags : List<String>
    private lateinit var items : List<SuggestListsInterface.Item>
    private lateinit var displayedItems :  List<SuggestListsInterface.Item>

    private var isNewFirs = true
    private var sortType = SuggestListsInterface.SortType.AZ

    private var isFirstRest = true


    fun onCreate(context : Context){
        val ips = context.assets.open("suggest/ListsInfo.json")
        lists = JSONHelper.loadDirInfo(ips).files

        collectTags()
        view.setTags(tags)
        checkItems(context)
    }

    fun onResume(context: Context){
        if (isFirstRest){
            isFirstRest = false
            return
        }
        checkItems(context)
    }

    fun onDestroy(){

    }

    fun onSortTypeUpdate(newSortType : SuggestListsInterface.SortType){
        sortType = newSortType
        sort()
        view.setItems(displayedItems)
    }

    fun onNewFirstChanged( newFirst : Boolean){
        isNewFirs = newFirst
        sort()
        view.setItems(displayedItems)
    }

    private fun sort(){
        displayedItems = if (sortType == SuggestListsInterface.SortType.AZ){
            displayedItems.sortedWith( compareBy<SuggestListsInterface.Item> { it.wordsList.name } )
        }else{
            displayedItems.sortedWith( compareByDescending<SuggestListsInterface.Item> { it.wordsList.name })
        }

        if (isNewFirs){
            displayedItems = displayedItems.sortedByDescending { it.isNew }
        }
    }

    private fun checkItems(context: Context){
        var isFirs = false
        if (!::items.isInitialized){
            items = lists.map { SuggestListsInterface.Item(it) }
            isFirs = true
            displayedItems = items.map { it }
        }

        val viewed = Settings.getViewedItems(context)
        val updatedItems = mutableListOf<SuggestListsInterface.Item>()

        for (viewedIt in viewed){
            val item = items!!.find { it.wordsList.name == viewedIt.second }

            try {
                if (item != null) {
                    val curVersion = item.wordsList.version.replace(".", "").toInt()
                    if (curVersion <= viewedIt.first) {
                        updatedItems.add(item)
                        item.isNew = false
                    }
                }
            }catch (e : IllegalArgumentException){

            }
        }
        sort()
        if (isFirs){
            showItems(displayedItems)
        }else{
            updateItems(updatedItems)
        }
    }

    fun onItemClicked( fInfo : JSONObjects.FileInfo){
        view.showWordsList(fInfo)
    }

    fun selectedTagsRangeChanged(selectedTags : List<String>){
        val outList = mutableListOf<SuggestListsInterface.Item>()

        for (item in items){
            if ( item.wordsList.tags.any { selectedTags.contains(it) }){
                outList.add(item)
            }
        }

        displayedItems = outList;
        sort()
        view.setItems(displayedItems)
    }

    private fun showItems(items : List<SuggestListsInterface.Item>){
        view.setItems(items.filter{ displayedItems.contains(it)})
    }

    private fun updateItems(items : List<SuggestListsInterface.Item>){
        view.updateItems(items.filter{ displayedItems.contains(it)})
    }

    private fun collectTags(){
        val tags = mutableListOf<String>()

        for ( info in lists){
            if (tags.size <= Const.maxTags){
                tags.addAll(info.tags)
            }
        }

        this.tags = tags.distinct()
    }

}