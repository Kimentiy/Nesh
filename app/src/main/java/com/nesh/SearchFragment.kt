package com.nesh

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchFragment : DialogFragment() {

    private val adapter =
        HeterogeneousAdapter(itemGroups = listOf(createSearchSongItemGroup { onDownloadClicked() }))

    private lateinit var prefsHelper: PrefsHelper

    override fun onAttach(context: Context) {
        super.onAttach(context)

        prefsHelper = PrefsHelper(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setContentView(R.layout.fragment_search)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            val recycler = this.findViewById<RecyclerView>(R.id.rv_search_result)
            val layoutManager = LinearLayoutManager(context)

            recycler.layoutManager = layoutManager
            recycler.addItemDecoration(createDividerDecoration(requireContext(), layoutManager))
            recycler.adapter = adapter

            this.findViewById<ImageView>(R.id.image_close).setOnClickListener {
                parentFragmentManager.beginTransaction().remove(this@SearchFragment).commit()
            }

            adapter.data = listOf(Song("Eminem - Rap god", Uri.EMPTY))
        }
    }

    private fun onDownloadClicked() {
        requireContext().blockUiAndDo(lifecycleScope) {
            val repository = SongsRepository(activity?.application as NeshApp)

            prefsHelper.workDirectory?.let { dirUri ->
                val uri = repository.getRapGodSong(dirUri)

                val textToShow = if (uri != null) {
                    "Song was downloaded"
                } else {
                    "Got error"
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
