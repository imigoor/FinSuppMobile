package br.edu.ifpb.finsupp.network.model

data class Bank(
    val id: Int,
    val name: String
)

data class BankListResponse(
    val message: String,
    val dataList: List<Bank>
)