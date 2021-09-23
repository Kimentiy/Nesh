package com.nesh

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

sealed class PlayerState(protected val player: Player) {

    fun release(): Released {
        player.release()

        return Released(player).apply(player::setNewState)
    }

    fun stop(): Idle = stopImpl()

    protected abstract fun stopImpl(): Idle

    class Idle(player: Player) : PlayerState(player) {
        suspend fun setSong(song: File) = withContext(Dispatchers.IO) {
            player.setNewSong(song)

            Paused(player, Uri.EMPTY).apply(player::setNewState)
        }

        suspend fun setSong(context: Context, uri: Uri) = withContext(Dispatchers.IO) {
            player.setNewSong(context, uri)

            Paused(player, uri).apply(player::setNewState)
        }

        override fun stopImpl(): Idle = this
    }

    class Paused(
        player: Player,
        val pausedSong: Uri
    ) : PlayerState(player) {
        fun play(): Playing {
            player.play()

            return Playing(player, pausedSong).apply(player::setNewState)
        }

        override fun stopImpl(): Idle {
            player.stop()

            return Idle(player).apply(player::setNewState)
        }
    }

    class Playing(
        player: Player,
        val playingSong: Uri
    ) : PlayerState(player) {
        fun pause(): Paused {
            player.pause()

            return Paused(player, playingSong).apply(player::setNewState)
        }

        override fun stopImpl(): Idle {
            player.stop()

            return Idle(player).apply(player::setNewState)
        }
    }

    class Released(player: Player) : PlayerState(player) {

        override fun stopImpl(): Idle {
            throw Exception("Player is in released state, you can't use it anymore")
        }
    }
}
