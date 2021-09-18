package com.nesh

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.OpenDocumentTree
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private val adapter =
        HeterogeneousAdapter(itemGroups = listOf(createSongItemGroup(onPlayPauseClicked = ::onPlayPauseClicked)))

    private lateinit var prefsHelper: PrefsHelper

    private val getWorkDirectory = registerForActivityResult(OpenDocumentTree()) {
        prefsHelper.workDirectory = it
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefsHelper = PrefsHelper(requireContext())

        val recycler = view.findViewById<RecyclerView>(R.id.rv_downloaded_songs)

        val layoutManager = LinearLayoutManager(context)

        recycler.layoutManager = layoutManager
        recycler.addItemDecoration(createDividerDecoration(requireContext(), layoutManager))
        recycler.adapter = adapter

        val searchFab = view.findViewById<FloatingActionButton>(R.id.fab_search)

        val button = view.findViewById<ImageButton>(R.id.button_play_pause)

        player.stateLiveData.observe(viewLifecycleOwner, { currentState ->
            when (currentState) {
                is PlayerState.Playing -> button.setImageResource(R.drawable.ic_pause_white)
                else -> button.setImageResource(R.drawable.ic_play_white)
            }
            button.setOnClickListener {
                when (currentState) {
                    is PlayerState.Paused -> currentState.play()
                    is PlayerState.Playing -> currentState.pause()
                    else -> Unit
                }
            }
        })

        val progress = view.findViewById<ProgressBar>(R.id.pb_playback_position)

        player.positionLiveData.observe(viewLifecycleOwner, { currentPosition ->
            progress.progress = currentPosition
        })

        player.durationLiveData.observe(viewLifecycleOwner, { newDuration ->
            progress.max = newDuration
        })

        searchFab.setOnClickListener {
            childFragmentManager.beginTransaction()
                .add(SearchFragment(), "SearchFragmentTag")
                .commit()
        }

        if (prefsHelper.workDirectory == null) {
            viewLifecycleOwner.lifecycleScope.launch {
                val result = showAlertDialog(requireContext(), "Please select a working directory")

                when (result) {
                    DialogResult.Accepted -> getWorkDirectory.launch(Uri.EMPTY)
                    DialogResult.Dismissed -> Unit
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (prefsHelper.workDirectory != null) {
            val fileStorage = FileStorageImpl(requireContext().applicationContext)

            lifecycleScope.launch {
                when (val result = fileStorage.getSavedSongs()) {
                    is Result.Error -> Toast.makeText(context, result.e.message, Toast.LENGTH_LONG)
                        .show()
                    is Result.Successful -> adapter.data = result.value
                }
            }
        }
    }

    private fun onPlayPauseClicked(song: SavedSong) {
        lifecycleScope.launch {
            (player.stateLiveData.value as? PlayerState.Idle)?.let {
                it.setSong(requireContext(), song.storageUri).play()
            }
        }
    }
}
