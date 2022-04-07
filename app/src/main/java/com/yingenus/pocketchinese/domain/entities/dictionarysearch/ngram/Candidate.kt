package com.yingenus.pocketchinese.domain.entities.dictionarysearch.ngram

class  Candidate<T> ( val value : T)  {
    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> {
                true
            }
            other is Candidate<*> -> {
                return other.value == this.value
            }
            else -> {
                false
            }
        }
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "Ngram Candidate of ${value.toString()}"
    }
}