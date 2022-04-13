package com.yingenus.pocketchinese.domain.repository

import android.net.Uri
import java.net.URI

interface ImageRep {
    fun getImageURI(name : String) : URI?
}