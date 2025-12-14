package br.edu.ifpb.finsupp.network.service

import br.edu.ifpb.finsupp.network.model.*
import retrofit2.Response
import retrofit2.http.*


interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponse>
}
