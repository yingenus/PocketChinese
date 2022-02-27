package com.yingenus.pocketchinese.common

sealed class Result<T> {

    internal class Success<T>( internal val value : T): Result<T>() {
    }

    internal class Empty<T>( ): Result<T>() {
    }

    internal class Failure<T>( val msg : String): Result<T>() {
    }



}