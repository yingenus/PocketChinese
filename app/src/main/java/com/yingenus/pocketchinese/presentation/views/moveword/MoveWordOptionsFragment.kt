package com.yingenus.pocketchinese.presentation.views.moveword

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.controller.BoundsDecoratorBottom
import com.yingenus.pocketchinese.controller.CardBoundTopBottom
import com.yingenus.pocketchinese.presentation.views.addword.OptionsAdapter
import com.yingenus.pocketchinese.presentation.views.addword.OptionsDetailsLookup
import com.yingenus.pocketchinese.presentation.views.addword.OptionsKeyProvider

class MoveWordOptionsFragment : Fragment(R.layout.move_options_layout) {

    private var statistics : RecyclerView? = null
    private var statisticsSelectionTracker : SelectionTracker<Long>? = null

    private var _saveStatistics : Boolean = true
    val saveStatistics : Boolean
        get() = _saveStatistics


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!

        statistics = view.findViewById(R.id.recyclerview_statistic)
        statistics!!.layoutManager = LinearLayoutManager(requireContext())
        statistics!!.adapter = StatisticsAdapter()
        statistics!!.addItemDecoration(CardBoundTopBottom(requireContext(),4))
        statistics!!.addItemDecoration(BoundsDecoratorBottom(requireContext()))

        val statisticsSelection : SelectionTracker.SelectionObserver<Long> = object : SelectionTracker.SelectionObserver<Long>(){
            override fun onItemStateChanged(key: Long, selected: Boolean) {
                if (selected){
                    when(key){
                        0L -> _saveStatistics = false
                        1L -> _saveStatistics = true
                    }
                }
            }
        }

        val statisticsTracker = SelectionTracker.Builder<Long>(
            "pin_selection",
            statistics!!,
            OptionsKeyProvider(),
            OptionsDetailsLookup(statistics!!),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectSingleAnything())
            .build()

        statisticsSelectionTracker = statisticsTracker

        statisticsTracker.addObserver(statisticsSelection)

        (statistics!!.adapter as OptionsAdapter).selectionTracker = statisticsTracker

        statisticsTracker.select(0)

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        statisticsSelectionTracker = null
    }

    class StatisticsAdapter() : OptionsAdapter(){
        override fun getTitle(position: Int, context: Context): String {
            return when(position){
                0 -> context.getString(R.string.save_statistic)
                1 -> context.getString(R.string.clear_statistic_on_move)
                else -> throw RuntimeException()
            }
        }

        override fun getItemCount(): Int {
            return 2
        }
    }
}