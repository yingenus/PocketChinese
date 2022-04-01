package com.yingenus.pocketchinese.domain.usecase

import io.reactivex.rxjava3.core.Observable


interface SearchersInitUseCase {
    fun initSearchers(): Observable<String>
}