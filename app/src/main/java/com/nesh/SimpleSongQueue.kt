package com.nesh

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SimpleSongQueue(
    private val context: Context,
    private val player: Player,
    private val scope: CoroutineScope
) {

    private var songs: List<Uri> = emptyList()

    init {
        player.completionListener = ::handleSongCompletion
    }

    fun setSongs(songs: List<SavedSong>) {
        this.songs = songs.map { it.storageUri }
    }

    private fun handleSongCompletion(paused: PlayerState.Paused) {
        scope.launch {
            paused.stop().setSong(context, getNextSong(paused)).play()
        }
    }

    private fun getNextSong(paused: PlayerState.Paused): Uri {
        val songPosition = songs.indexOf(paused.pausedSong)

        return if (songPosition != -1 && (songPosition + 1) < songs.size) {
            songs[songPosition + 1]
        } else if (songs.isNotEmpty()) {
            songs.first()
        } else {
            paused.pausedSong
        }
    }
}
