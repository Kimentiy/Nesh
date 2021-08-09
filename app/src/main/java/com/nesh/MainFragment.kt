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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFragment : Fragment() {

    private val player = Player(GlobalScope)
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
            val repository = SongsRepository(activity?.application as NeshApp)

            GlobalScope.launch {
                prefsHelper.workDirectory?.let { dirUri ->
                    val uri = repository.getRapGodSong(dirUri)

                    val currentPlayerState = player.stateLiveData.requireValue

                    if (uri != null) {
                        currentPlayerState.stop().setSong(requireContext(), uri).play()

                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Song was downloaded", Toast.LENGTH_SHORT).show()
                    }
                }
            }
//            childFragmentManager.beginTransaction()
//                .add(SearchFragment(), "SearchFragmentTag")
//                .commit()
        }

        if (prefsHelper.workDirectory == null) {
            getWorkDirectory.launch(Uri.EMPTY)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        player.stateLiveData.requireValue.release()
    }
}
