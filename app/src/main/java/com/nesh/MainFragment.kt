package com.nesh

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.rv_downloaded_songs)

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
        recycler.adapter = SongsAdapter(List(40) { Song("Eminem - rap god $it") })

        val searchFab = view.findViewById<FloatingActionButton>(R.id.fab_search)

        searchFab.setOnClickListener {
            childFragmentManager.beginTransaction()
                .add(SearchFragment(), "SearchFragmentTag")
                .commit()
        }
    }
}
