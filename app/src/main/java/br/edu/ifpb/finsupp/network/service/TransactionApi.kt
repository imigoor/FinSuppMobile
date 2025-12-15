package br.edu.ifpb.finsupp.network.service

import br.edu.ifpb.finsupp.network.model.CreateTransactionRequest
import br.edu.ifpb.finsupp.network.model.TransactionListResponse
import retrofit2.Response
import retrofit2.http.*

interface TransactionApi {
    @GET("transactions/")
    suspend fun getTransactions(@Query("page") page: Int = 0, @Query("size") size: Int = 50): Response<TransactionListResponse>

    @POST("transactions/")
    suspend fun createTransaction(@Body request: CreateTransactionRequest): Response<Void>

    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: Int): Response<Void>
}