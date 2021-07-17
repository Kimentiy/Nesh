package com.nesh

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface SongsService {

    @GET("examples/mp3/SoundHelix-Song-1.mp3")
    fun getRapGodSong(): Call<ResponseBody>
}
