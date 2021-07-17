package com.nesh

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface SongsService {

    @GET("youtubeXbGs_qK2PQA128.mp3?fn=Eminem%20-%20Rap%20God%20(Explicit).mp3")
    fun getRapGodSong(): Call<ResponseBody>
}
