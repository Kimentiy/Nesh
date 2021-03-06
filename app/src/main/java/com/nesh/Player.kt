package com.nesh

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import java.io.File

class Player(private val scope: CoroutineScope) {

    private val mediaPlayer = MediaPlayer()

    private val _stateLiveData = MutableLiveData<PlayerState>(PlayerState.Idle(this))
    val stateLiveData: LiveData<PlayerState> = _stateLiveData

    private val _positionLiveData = PositionLiveData(mediaPlayer, scope)
    val positionLiveData: LiveData<Int> = _positionLiveData

    private val _durationLiveData = MutableLiveData(0)
    val durationLiveData: LiveData<Int> = _durationLiveData

    var completionListener: ((PlayerState.Paused) -> Unit)? = null

    init {
        // TODO is it correct place to update duration?
        mediaPlayer.setOnPreparedListener {
            _durationLiveData.postValue(mediaPlayer.duration)
        }
        mediaPlayer.setOnCompletionListener {
            val currentState = _stateLiveData.requireValue as? PlayerState.Playing
                ?: return@setOnCompletionListener

            val newState = PlayerState.Paused(this, currentState.playingSong)

            _stateLiveData.postValue(newState)

            completionListener?.invoke(newState)
        }
    }

    fun setNewState(state: PlayerState) {
        _stateLiveData.postValue(state)
    }

    fun play() {
        mediaPlayer.start()
    }

    fun pause() {
        mediaPlayer.pause()
    }

    fun stop() {
        mediaPlayer.stop()
        mediaPlayer.reset()
    }


    suspend fun setNewSong(file: File) = withContext(Dispatchers.IO) {
        mediaPlayer.setDataSource(file.absolutePath)
        mediaPlayer.prepare()
    }

    suspend fun setNewSong(context: Context, uri: Uri) = withContext(Dispatchers.IO) {
        mediaPlayer.setDataSource(context, uri)
        mediaPlayer.prepare()
    }

    fun release() {
        mediaPlayer.release()
    }

    class PositionLiveData(
        private val mediaPlayer: MediaPlayer,
        private val scope: CoroutineScope
    ) : MutableLiveData<Int>(0) {

        private var listenJob: Job? = null

        override fun onActive() {
            super.onActive()

            listenJob = scope.launch {
                while (isActive) {
                    try {
                        delay(POSITION_UPDATE_FREQUENCY_MS)
                    } catch (e: CancellationException) {
                        break
                    }

                    postValue(mediaPlayer.currentPosition)
                }
            }
        }

        override fun onInactive() {
            super.onInactive()

            listenJob?.cancel()
            listenJob = null
        }

        companion object {
            private const val POSITION_UPDATE_FREQUENCY_MS = 250L
        }
    }
}
