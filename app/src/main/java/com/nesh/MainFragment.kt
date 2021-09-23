package com.nesh

import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.content.pm.PackageManager
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
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private val adapter =
        HeterogeneousAdapter(itemGroups = listOf(createSongItemGroup(onPlayPauseClicked = ::onPlayPauseClicked)))

    private lateinit var prefsHelper: PrefsHelper

    private val savedSongs = MutableLiveData<List<SavedSong>>(emptyList())

    private val getWorkDirectory = registerForActivityResult(OpenDocumentTree()) {
        requireContext().contentResolver.takePersistableUriPermission(
            it,
            FLAG_GRANT_READ_URI_PERMISSION and FLAG_GRANT_WRITE_URI_PERMISSION
        )

        prefsHelper.workDirectory = it
    }

    private lateinit var songQueue: SimpleSongQueue

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
        songQueue = SimpleSongQueue(requireContext(), player, lifecycleScope)

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

        savedSongs.observe(viewLifecycleOwner, { songs ->
            songQueue.setSongs(songs)
        })

        MediatorLiveData<Pair<List<SavedSong>, PlayerState>>().apply {
            addSource(savedSongs) {
                postValue(it to player.stateLiveData.requireValue)
            }

            addSource(player.stateLiveData) {
                postValue(savedSongs.requireValue to it)
            }
        }.observe(viewLifecycleOwner, { (songs, playerState) ->
            updateItems(songs, playerState)
        })

        if (prefsHelper.workDirectory == null) {
            launchWorkDirectorySelection()
        } else if (!checkPermissions(prefsHelper.workDirectory!!)) {
            Toast.makeText(
                requireContext(),
                "Have work directory uri, but not permissions",
                Toast.LENGTH_LONG
            ).show()

            launchWorkDirectorySelection()
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
                    is Result.Successful -> {
                        savedSongs.postValue(result.value)
                    }
                }
            }
        }
    }

    private fun updateItems(songs: List<SavedSong>, playerState: PlayerState) {
        adapter.data = songs.map {
            SavedSongItem(
                it,
                isPlaying = (playerState as? PlayerState.Playing)?.playingSong == it.storageUri
            )
        }
    }

    private fun launchWorkDirectorySelection() {
        viewLifecycleOwner.lifecycleScope.launch {
            val result = showAlertDialog(requireContext(), "Please select a working directory")

            when (result) {
                DialogResult.Accepted -> getWorkDirectory.launch(Uri.EMPTY)
                DialogResult.Dismissed -> Unit
            }
        }
    }

    private fun checkPermissions(uri: Uri): Boolean {
        return requireContext().checkCallingOrSelfUriPermission(
            uri,
            FLAG_GRANT_READ_URI_PERMISSION and FLAG_GRANT_WRITE_URI_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun onPlayPauseClicked(item: SavedSongItem) {
        val currentPlayerState = player.stateLiveData.requireValue
        val songUri = item.song.storageUri

        lifecycleScope.launch {
            when (currentPlayerState) {
                is PlayerState.Idle -> currentPlayerState.setSong(requireContext(), songUri).play()
                is PlayerState.Paused -> {
                    if (currentPlayerState.pausedSong == songUri) {
                        currentPlayerState.play()
                    } else {
                        currentPlayerState.stop().setSong(requireContext(), songUri).play()
                    }
                }
                is PlayerState.Playing -> {
                    if (currentPlayerState.playingSong == songUri) {
                        currentPlayerState.pause()
                    } else {
                        currentPlayerState.stop().setSong(requireContext(), songUri).play()
                    }
                }
                is PlayerState.Released -> Unit
            }
        }
    }
}
