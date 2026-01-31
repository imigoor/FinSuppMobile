package br.edu.ifpb.finsupp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.edu.ifpb.finsupp.network.model.AccountApiData

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: Int,
    val description: String,
    val bank: Int,
    val accountType: String,
    val closingDay: Int,
    val paymentDueDay: Int,
    val balance: Double
)

// Função auxiliar para converter o dado da API para o Banco
fun AccountApiData.toEntity(): AccountEntity {
    return AccountEntity(
        id = this.id,
        description = this.description,
        bank = this.bank,
        accountType = this.accountType,
        closingDay = this.closingDay,
        paymentDueDay = this.paymentDueDay,
        balance = this.balance
    )
}

// Função auxiliar para converter do Banco para a UI
fun AccountEntity.toApiData(): AccountApiData {
    return AccountApiData(
        id = this.id,
        description = this.description,
        bank = this.bank,
        accountType = this.accountType,
        closingDay = this.closingDay,
        paymentDueDay = this.paymentDueDay,
        balance = this.balance
    )
}