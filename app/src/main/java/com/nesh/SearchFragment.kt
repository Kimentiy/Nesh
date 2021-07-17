package com.nesh

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setContentView(R.layout.fragment_search)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            val recycler = this.findViewById<RecyclerView>(R.id.rv_search_result)
            val layoutManager = LinearLayoutManager(context)
            val dividerDecoration = DividerItemDecoration(context, layoutManager.orientation)

            dividerDecoration.setDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.divider
                )!!
            )

            recycler.layoutManager = layoutManager
            recycler.addItemDecoration(dividerDecoration)

            val heterogeneousAdapter = HeterogeneousAdapter(
                itemGroups = listOf(createSongItemGroup()),
                initialData = List(50) { Song(it.toString()) }
            )

            recycler.adapter = heterogeneousAdapter
        }
    }
}
