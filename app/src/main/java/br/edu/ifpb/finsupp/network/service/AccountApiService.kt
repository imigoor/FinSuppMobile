package br.edu.ifpb.finsupp.network.service

import br.edu.ifpb.finsupp.network.model.*
import retrofit2.Response
import retrofit2.http.*

interface AccountApi {
    @POST("accounts/")
    suspend fun createAccount(@Body request: CreateAccountRequest): Response<Void>

    @GET("accounts/")
    suspend fun getAccounts(@Query("page") page: Int = 0, @Query("size") size: Int = 50): Response<AccountListResponse>

    @DELETE("accounts/{id}")
    suspend fun deleteAccount(@Path("id") id: Int): Response<Void>

    @PUT("accounts/{id}")
    suspend fun updateAccount(@Path("id") id: Int, @Body request: CreateAccountRequest): Response<Void>
}

