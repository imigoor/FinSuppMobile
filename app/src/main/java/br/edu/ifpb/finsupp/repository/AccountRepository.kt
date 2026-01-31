package br.edu.ifpb.finsupp.repository

import br.edu.ifpb.finsupp.data.local.AccountDao
import br.edu.ifpb.finsupp.data.local.toApiData
import br.edu.ifpb.finsupp.data.local.toEntity
import br.edu.ifpb.finsupp.network.model.AccountApiData
import br.edu.ifpb.finsupp.network.service.AccountApi

class AccountRepository(
    private val api: AccountApi,
    private val dao: AccountDao
) {
    // Busca contas: Tenta API -> Salva no Room -> Retorna. Se falhar API -> Retorna Room
    suspend fun getAccounts(): List<AccountApiData> {
        return try {
            val response = api.getAccounts()
            if (response.isSuccessful) {
                val apiList = response.body()?.dataList ?: emptyList()

                // atualiza o cache local
                dao.clearAll() // limpa o antigo
                dao.insertAll(apiList.map { it.toEntity() }) // salva o novo

                apiList
            } else {
                dao.getAllAccounts().map { it.toApiData() }
            }
        } catch (e: Exception) {
            dao.getAllAccounts().map { it.toApiData() }
        }
    }

    suspend fun deleteAccount(id: Int): Boolean {
        return try {
            val response = api.deleteAccount(id)
            if (response.isSuccessful) {
                dao.deleteById(id) // Remove do local também
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    //  limpar o banco ao fazer Logout
    suspend fun clearLocalData() {
        dao.clearAll()
    }
}