package com.nesh

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SongsRepository(private val app: NeshApp) {

    suspend fun getSong(song: SearchSong) = withContext(Dispatchers.IO) {
        val service = app.retrofit.create(SongsService::class.java)

        val call = service.getSong(song.downloadUrl)

        val response = call.execute()

        if (response.isSuccessful) {
            response.body()?.bytes()
        } else {
            null
        }
    }
}
