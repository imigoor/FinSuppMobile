package br.edu.ifpb.finsupp.network.model

data class CreateAccountRequest(
    val description: String,
    val accountType: String,
    val bank: Int,
    val balance: Double,
    val closingDay: Int,
    val paymentDueDay: Int
)

data class AccountListResponse(
    val message: String,
    val dataList: List<AccountApiData>
)

data class AccountApiData(
    val id: Int,
    val description: String,
    val bank: Int,
    val accountType: String,
    val closingDay: Int,
    val paymentDueDay: Int,
    val balance: Double
)
