package com.yingenus.pocketchinese

internal fun Throwable.logErrorMes() = cause.toString()+"\n"+suppressed+"\n"+message+"\n"+stackTrace.joinToString(separator = "\n")