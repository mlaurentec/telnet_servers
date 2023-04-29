package com.example.servertelnetlegacyalignet

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Body
interface ApiService {
    @POST("production/test")
    suspend fun validateServer(@Body body:com.example.servertelnetlegacyalignet.Body):Response<ServerResponse>
}