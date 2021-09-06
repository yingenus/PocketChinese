package com.yingenus.pocketchinese.presenters

 fun isRussian( str : String) = Regex("""[А-Яа-я]""").containsMatchIn(str)