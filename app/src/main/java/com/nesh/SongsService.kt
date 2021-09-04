package com.nesh

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface SongsService {

    @GET("examples/mp3/SoundHelix-Song-1.mp3")
    fun getRapGodSong(): Call<ResponseBody>

    @GET
    fun getSong(@Url fileUrl: String): Call<ResponseBody>
}
