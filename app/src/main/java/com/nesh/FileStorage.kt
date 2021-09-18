package com.nesh

import android.net.Uri

interface FileStorage {

    suspend fun getSavedSongs(): Result<List<SavedSong>>

    suspend fun saveSong(title: String, bytes: ByteArray): Result<SavedSong>
}

class SavedSong(val title: String, val storageUri: Uri)
