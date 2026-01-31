package br.edu.ifpb.finsupp.network.model

// Modelo para ler transações da API
data class TransactionApiData(
    val id: Int,
    val description: String,
    val amount: Double,
    val type: String, // "WITHDRAW", "DEPOSIT", "TRANSFER"
    val transactionDate: String,
    val account: Int, // ID da conta origem
    val recipientAccountId: Int?, // ID da conta destino (para transferencias)
    val category: Int?
)

data class TransactionListResponse(
    val message: String,
    val dataList: List<TransactionApiData>
)

// Modelo para enviar nova transação (POST)
data class CreateTransactionRequest(
    val description: String,
    val amount: Double,
    val transactionDate: String, // Formato YYYY-MM-DD
    val type: String,
    val accountId: Int,
    val recipientAccountId: Int? = null, // Só preenche se for TRANSFER
    val category: Int? = null,
    val addToBill: Boolean = false,
    val installments: Int = 1
)

// Modelo simples para Categoria (para o dropdown)
data class Category(
    val id: Int,
    val description: String
)

data class CategoryListResponse(
    val message: String,
    val dataList: List<Category>
)