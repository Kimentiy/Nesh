package com.nesh

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class SongsRepository(private val app: NeshApp) {

    private val fileName = "RapGod.mp3"

    suspend fun getRapGodSong() = withContext(Dispatchers.IO) {
        val service = app.retrofit.create(SongsService::class.java)

        val call = service.getRapGodSong()

        val response = call.execute()

        android.util.Log.d("MyTag", response.code().toString())

        if (response.isSuccessful) {
            val songFile = File(app.cacheDir, fileName)

            songFile.writeBytes(response.body()!!.bytes())

            songFile
        } else {
            null
        }
    }
}
