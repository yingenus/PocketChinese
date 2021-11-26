package com.yingenus.pocketchinese.domain.repository

import java.net.URI

interface ImageRep {
    fun getImageUri(name : String) : URI?
}