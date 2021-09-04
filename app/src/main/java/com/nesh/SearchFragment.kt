package com.nesh

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : DialogFragment() {

    private val adapter =
        HeterogeneousAdapter(itemGroups = listOf(createSearchSongItemGroup { onDownloadClicked(it) }))

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

            val searchView = this.findViewById<SearchView>(R.id.search_view)

            this.findViewById<Button>(R.id.search_button).setOnClickListener {
                val query = searchView.query

                GlobalScope.launch(Dispatchers.IO) {
                    val cursor = context.contentResolver.query(
                        Uri.parse("content://com.nesh"),
                        null,
                        null,
                        null,
                        null
                    )

                    cursor?.use {
                        val songs = mutableListOf<SearchSong>()

                        it.moveToFirst()

                        do {
                            songs.add(
                                SearchSong(
                                    title = cursor.getString(0),
                                    downloadUrl = cursor.getString(1)
                                )
                            )
                        } while (it.moveToNext())

                        withContext(Dispatchers.Main) {
                            adapter.data = songs
                        }
                    }
                }
            }
        }
    }

    private fun onDownloadClicked(song: SearchSong) {
        requireContext().blockUiAndDo(lifecycleScope) {
            val repository = SongsRepository(activity?.application as NeshApp)

            prefsHelper.workDirectory?.let { dirUri ->
                val uri = repository.getSong(song, dirUri)

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
