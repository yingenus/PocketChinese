package com.yingenus.pocketchinese.domain.entitiys.words.statistic

class FibRepeatHelper: RepeatHelper() {

    //    0      1     2   3 4 5 6  7  8  9  10
    //  0.045   0.13   1   2 3 5 8  13 21 34 55
    //  0.045   0.09  0.5  1 1 2 2  3  5  7  10
    override fun repeatWindow(lvl: Int) = when(lvl){
        0 ->0.045f
        1->0.09f
        2->0.5f
        3->1f
        4->1f
        5,6->2f
        7->3f
        8->5f
        9->7f
        10->10f
        else->10f
    }
    override fun nextDay(lvl: Int) = when(lvl){
        0 ->0.045f
        1->0.13f
        2->1f
        3->2f
        4->3f
        5->5f
        6->8f
        7->13f
        8->21f
        9->34f
        10->55f
        else->14f
    }
}
