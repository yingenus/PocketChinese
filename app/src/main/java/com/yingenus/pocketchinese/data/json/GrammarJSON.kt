package com.yingenus.pocketchinese.data.json

import com.google.gson.annotations.SerializedName
import com.yingenus.pocketchinese.domain.dto.GrammarCase

class GrammarJSON(
        val cases : Array<GrammarCaseJSON>
) {


    class GrammarCaseJSON(
            val name : String,
            val title : String,
            val short : String,
            val tags : Array<String>,
            val version : Int,
            @SerializedName("image_link" ) val image : String,
            @SerializedName("html_link") val link : String
    ){

        public fun toGrammarCase(): GrammarCase =
                GrammarCase(
                        name = name,
                        link = link,
                        title = title,
                        short = short,
                        tags = tags,
                        version = version,
                        image = image
                )

    }

}