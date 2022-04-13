package com.yingenus.pocketchinese.domain.entities.namestandards

class NamingStandardsImpl : NamingStandards {
    companion object{
        private const val PINREGEX="[A-Za-zāáǎàīíǐìōóǒòēèěéūùǔúǚ]"
        private const val CHNREGEX="[\\p{script=Han}]"
        private const val TRNREGEX="[A-Za-zА-Яа-я]"
    }
    override fun isCorrectName(name: String): Boolean =
        Regex("""^[${PINREGEX + CHNREGEX + TRNREGEX}\s\d-]*?$""")
        .matches(name)
}