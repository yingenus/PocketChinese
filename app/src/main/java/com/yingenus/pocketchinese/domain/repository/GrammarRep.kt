package com.yingenus.pocketchinese.domain.repository

import com.yingenus.pocketchinese.domain.dto.GrammarCase

interface GrammarRep {
  fun getCases(): List<GrammarCase>
  fun getCase( name : String) : GrammarCase?
}