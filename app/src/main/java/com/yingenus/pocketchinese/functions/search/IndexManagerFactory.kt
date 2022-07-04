package com.yingenus.pocketchinese.functions.search

import com.yingenus.pocketchinese.functions.search.indexfile.IndexCheckerImpl
import com.yingenus.pocketchinese.functions.search.indexfile.IndexCreatorImpl
import com.yingenus.pocketchinese.functions.search.indexfile.checksum.SimpleChecksumFactory

object IndexManagerFactory {
    fun create(): IndexManager =
        IndexManagerImpl(
            IndexCreatorImpl(SimpleChecksumFactory()),
            IndexCheckerImpl(SimpleChecksumFactory())
        )
}