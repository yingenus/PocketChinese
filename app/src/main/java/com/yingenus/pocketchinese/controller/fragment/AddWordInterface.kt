package com.yingenus.pocketchinese.controller.fragment

interface AddWordInterface {

    enum class CreateError{
        NOT_AVAIL_CHARS, NAME_BUSY, EMPTY_NAME
    }

    enum class PinType{
        STANDARD, SIMPLIFIED
    }

    data class SelectedParams(
            val existBlock : Boolean,
            val pinType : PinType,
            val startedBlock : Int,
            val splitToBlocks : Boolean,
            val wordsInBlock : Int,
            val mixWords : Boolean
    )

    fun getNewListName(): String
    fun getSelectedList(): String
    fun getSelectedParams(): SelectedParams


    fun setBlocksCount(blocks : Int)
    fun setUserLists(names : List<String>)

    fun showNameError(error : CreateError, show : Boolean)
    fun listNotSelected(show: Boolean)
    fun startInsert()
    fun finishInsert(isSuccessful : Boolean)

}