package com.yingenus.pocketchinese.model.database.pocketDB


class PocketDbSchema {
    object StudyListTable{
        const val NAME="study_list"

        object Cols{
            const val UUID="uuid"
            const val NAME="name"
            const val UPDATE_DATE="update_date"
            const val ITEMS="items"
            const val LAST_REPEAT="last"
            const val SUCCESS="success"
            const val WORST="worst"
            const val NOTIFY = "notify"
        }
    }

    object StudyWordLinkTable{
        const val NAME="sw_linc"

        object Cols{
            const val LIST_UUID="list"
            const val WORDS_UUID="word"
            const val BLOCK="block"
        }
    }

    object WordsTable{
        const val NAME="words"

        object Cols{
            //индивидуальный номер слова
            const val UUID="uuid"
            // значения слова в различных смыслах
            const val WORD="wordchn"
            const val PINYIN="pinyin"
            const val TRANSLATE="translate"
            //общее число повторений
            const val COMMON_REPEAT_W="repeatw"
            const val COMMON_REPEAT_P="repeatp"
            const val COMMON_REPEAT_T="repeatt"
            //уровень владения слова
            const val LEVEL_W="levelw"
            const val LEVEL_P="levelp"
            const val LEVEL_T="levelt"
            //даты созданя слова
            const val LAST_REPEAT_W="lastw"
            const val LAST_REPEAT_P="lastp"
            const val LAST_REPEAT_T="lastt"
            const val CREATE_DATE="create_date"
            //стадия повтарения
            const val REPEAT_STATE_W="statew"
            const val REPEAT_STATE_P="statep"
            const val REPEAT_STATE_T="statet"
        }
    }

}