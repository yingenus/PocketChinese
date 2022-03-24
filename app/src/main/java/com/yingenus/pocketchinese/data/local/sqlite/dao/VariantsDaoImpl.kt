package com.yingenus.pocketchinese.data.local.sqlite.dao

import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.support.ConnectionSource
import com.yingenus.pocketchinese.data.local.sqlite.entity.PinVariants


class VariantsDaoImpl(connectionSource: ConnectionSource) : BaseDaoImpl<PinVariants, String>(connectionSource, PinVariants::class.java),
    VariantsDao {
    //@Query("SELECT * FROM py_variants")
    override fun getAll(): List<PinVariants>{
        return queryForAll()
    }
}