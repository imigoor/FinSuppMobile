package br.edu.ifpb.finsupp.network.service

import br.edu.ifpb.finsupp.network.model.BankListResponse
import retrofit2.Response
import retrofit2.http.GET

interface BankApi {
    @GET("bank/")
    suspend fun getBanks(): Response<BankListResponse>
}