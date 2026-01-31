package br.edu.ifpb.finsupp.di

import androidx.room.Room
import br.edu.ifpb.finsupp.data.local.AppDatabase
import br.edu.ifpb.finsupp.network.service.AccountApi
import br.edu.ifpb.finsupp.network.service.BankApi
import br.edu.ifpb.finsupp.network.service.TransactionApi
import br.edu.ifpb.finsupp.ui.viewmodel.AccountsViewModel
import br.edu.ifpb.finsupp.ui.viewmodel.TransactionsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import br.edu.ifpb.finsupp.network.TokenManager
import br.edu.ifpb.finsupp.network.service.*
import br.edu.ifpb.finsupp.repository.AccountRepository
import br.edu.ifpb.finsupp.ui.viewmodel.*
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext

val appModule = module {

    // configura o Cliente HTTP com o interceptor do Token
    single {
        OkHttpClient.Builder().addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()

            // pega o token do Singleton TokenManager
            TokenManager.token?.let {
                requestBuilder.header("Authorization", "Bearer $it")
            }

            chain.proceed(requestBuilder.build())
        }.build()
    }

    // configurar o Retrofit usando o cliente acima
    single {
        Retrofit.Builder()
            .baseUrl("https://finsupp-api-472774405ab6.herokuapp.com/")
            .client(get()) // Injeta o OkHttpClient configurado acima
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // cria as instâncias das APIs
    single { get<Retrofit>().create(AuthApi::class.java) }
    single { get<Retrofit>().create(AccountApi::class.java) }
    single { get<Retrofit>().create(TransactionApi::class.java) }
    single { get<Retrofit>().create(BankApi::class.java) }
    single { get<Retrofit>().create(CategoryApi::class.java) }

    // --- ROOM DATABASE (NOVO) ---
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "finsupp-db"
        ).fallbackToDestructiveMigration() // Limpa o banco se mudar a versão
            .build()
    }

    // Dao
    single { get<AppDatabase>().accountDao() }

    // --- REPOSITORY (NOVO) ---
    // O Repositório recebe a API e o DAO
    single { AccountRepository(get(), get()) }

    // injeta os ViewModels
    viewModel { LoginViewModel(get()) } // Recebe AuthApi
    viewModel { AccountsViewModel(get(), get()) } // Recebe AccountApi e BankApi
    viewModel { TransactionsViewModel(get()) } // Recebe TransactionApi
    viewModel { AddAccountViewModel(get(), get()) } // Recebe AccountRepository e BankApi
    viewModel { EditAccountViewModel(get(), get()) } // Recebe AccountApi e BankApi
    viewModel { AddTransactionViewModel(get(), get(), get()) } // Recebe TransactionApi, AccountApi, CategoryApi
}