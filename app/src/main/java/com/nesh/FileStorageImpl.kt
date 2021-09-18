package com.nesh

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException

class FileStorageImpl(private val context: Context) : FileStorage {

    private val prefsHelper = PrefsHelper(context)

    override suspend fun getSavedSongs(): Result<List<SavedSong>> = withContext(Dispatchers.IO) {
        val directoryUri = prefsHelper.workDirectory
            ?: return@withContext Result.Error(Exception("Don't have work directory"))

        val directory = DocumentFile.fromTreeUri(context, directoryUri)!!

        val songs = directory.listFiles().map {
            SavedSong(
                title = it.name!!,
                storageUri = it.uri
            )
        }

        Result.Successful(songs)
    }

    override suspend fun saveSong(title: String, bytes: ByteArray): Result<SavedSong> =
        withContext(Dispatchers.IO) {
            val fileName = "$title.mp3"

            val directoryUri =
                prefsHelper.workDirectory
                    ?: return@withContext Result.Error(Exception("Don't have work directory"))

            val directory = DocumentFile.fromTreeUri(context, directoryUri)!!

            val songFile = directory.createFile(FILES_MIME_TYPE, fileName)
                ?: return@withContext Result.Error(Exception("Can't create song file"))

            try {
                context.contentResolver.openOutputStream(songFile.uri)?.use {
                    it.write(bytes)
                }

                Result.Successful(SavedSong(title, songFile.uri))
            } catch (e: FileNotFoundException) {
                Result.Error(Exception("Can't open song file", e))
            } catch (e: IOException) {
                Result.Error(Exception("Error on writing in file", e))
            }
        }

    companion object {
        private const val FILES_MIME_TYPE = "audio/mpeg"
    }
}
