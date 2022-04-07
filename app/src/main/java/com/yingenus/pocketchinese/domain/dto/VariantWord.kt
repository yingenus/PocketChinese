package com.yingenus.pocketchinese.domain.dto

data class VariantWord(val id : Int, val index : Int, val weight: Int ) {
    override fun hashCode(): Int {
        return (id shl 10) + index
    }
}