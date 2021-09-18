package com.nesh

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SongsRepository(private val app: NeshApp) {

    suspend fun getSong(song: SearchSong): Result<ByteArray> = withContext(Dispatchers.IO) {
        val service = app.retrofit.create(SongsService::class.java)

        val call = service.getSong(song.downloadUrl)

        val response = call.execute()

        if (response.isSuccessful) {
            val body = response.body()
                ?: return@withContext Result.Error(Exception("Downloaded song does not have body"))

            Result.Successful(body.bytes())
        } else {
            Result.Error(Exception("Error while downloading song, code: ${response.code()}"))
        }
    }
}
