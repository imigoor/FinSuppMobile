package br.edu.ifpb.finsupp.network.service

import br.edu.ifpb.finsupp.network.model.CategoryListResponse
import retrofit2.Response
import retrofit2.http.GET

interface CategoryApi {
    @GET("categories/")
    suspend fun getCategories(): Response<CategoryListResponse>
}