package com.nesh

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SongsRepository(private val app: NeshApp) {

    private val fileName = "RapGod"

    suspend fun getRapGodSong(dirUri: Uri) = withContext(Dispatchers.IO) {
        val service = app.retrofit.create(SongsService::class.java)

        val call = service.getRapGodSong()

        val response = call.execute()

        android.util.Log.d("MyTag", response.code().toString())

        if (response.isSuccessful) {
            val directory = DocumentFile.fromTreeUri(app, dirUri)!!

            val newFile = directory.createFile("audio/mpeg", fileName)

            if (newFile != null) {
                val outputStream = app.contentResolver.openOutputStream(newFile.uri)

                outputStream?.write(response.body()!!.bytes())

                outputStream?.close()

                newFile.uri
            } else {
                null
            }
        } else {
            null
        }
    }
}
