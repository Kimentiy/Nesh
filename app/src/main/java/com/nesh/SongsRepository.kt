package com.nesh

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SongsRepository(private val app: NeshApp) {

    suspend fun getRapGodSong() = withContext(Dispatchers.IO) {
        val service = app.retrofit.create(SongsService::class.java)

        val call = service.getRapGodSong()

        val response = call.execute().body()

        val asdf = 3
    }
}
