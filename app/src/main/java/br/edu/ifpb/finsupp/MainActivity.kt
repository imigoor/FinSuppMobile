package br.edu.ifpb.finsupp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.edu.ifpb.finsupp.ui.screens.AccountsScreen
import br.edu.ifpb.finsupp.ui.screens.AddAccountScreen
import br.edu.ifpb.finsupp.ui.screens.AddTransactionScreen
import br.edu.ifpb.finsupp.ui.screens.EditAccountScreen
import br.edu.ifpb.finsupp.ui.screens.HomeScreen
import br.edu.ifpb.finsupp.ui.screens.LoginScreen
import br.edu.ifpb.finsupp.ui.screens.TransactionsScreen
import br.edu.ifpb.finsupp.ui.theme.FinSuppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinSuppTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {

                    // ROTA 1: LOGIN
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = { nomeDoUsuario ->
                                navController.navigate("home/$nomeDoUsuario") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    // ROTA 2: HOME
                    composable(
                        route = "home/{name}",
                        arguments = listOf(navArgument("name") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val nome = backStackEntry.arguments?.getString("name") ?: "Visitante"

                        HomeScreen(
                            userName = nome,
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("home/{name}") { inclusive = true }
                                }
                            },
                            onNavigateToAccounts = {
                                navController.navigate("accounts/$nome")
                            },
                            onNavigateToTransactions = {
                                navController.navigate("transactions/$nome")
                            }
                        )
                    }

                    // ROTA 3: CONTAS
                    composable(
                        route = "accounts/{name}",
                        arguments = listOf(navArgument("name") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val nome = backStackEntry.arguments?.getString("name") ?: "Visitante"
                        AccountsScreen(
                            userName = nome,
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("home/{name}") { inclusive = true }
                                }
                            },
                            onNavigateToHome = { navController.navigate("home/$nome") },
                            onNavigateToAddAccount = { navController.navigate("add_account") },
                            onNavigateToEditAccount = { account ->
                                val route = "edit_account/${account.id}/${account.description}/${account.bank}/${account.accountType}/${account.balance.toFloat()}/${account.closingDay}/${account.paymentDueDay}"
                                navController.navigate(route)
                            },
                            onNavigateToTransactions = {
                                navController.navigate("transactions/$nome")
                            }
                        )
                    }

                    composable("add_account") {
                        AddAccountScreen(
                            onBack = { navController.popBackStack() },
                            onSaveSuccess = {
                                navController.popBackStack()
                            }
                        )
                    }

                    // ROTA 4: EDIT ACCOUNT (Rota longa para passar os dados)
                    composable(
                        route = "edit_account/{id}/{desc}/{bank}/{type}/{bal}/{close}/{due}",
                        arguments = listOf(
                            navArgument("id") { type = NavType.IntType },
                            navArgument("desc") { type = NavType.StringType },
                            navArgument("bank") { type = NavType.IntType },
                            navArgument("type") { type = NavType.StringType },
                            navArgument("bal") { type = NavType.FloatType },
                            navArgument("close") { type = NavType.IntType },
                            navArgument("due") { type = NavType.IntType }
                        )
                    ) { entry ->
                        EditAccountScreen(
                            accountId = entry.arguments?.getInt("id") ?: 0,
                            initialDescription = entry.arguments?.getString("desc") ?: "",
                            initialBankId = entry.arguments?.getInt("bank") ?: 0,
                            initialType = entry.arguments?.getString("type") ?: "CHECKING",
                            initialBalance = entry.arguments?.getFloat("bal")?.toDouble() ?: 0.0,
                            initialClosing = entry.arguments?.getInt("close") ?: 1,
                            initialDue = entry.arguments?.getInt("due") ?: 1,
                            onBack = { navController.popBackStack() },
                            onUpdateSuccess = { navController.popBackStack() }
                        )
                    }

                    // ROTA 5: TRANSACOES
                    composable(
                        route = "transactions/{name}",
                        arguments = listOf(navArgument("name") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val nome = backStackEntry.arguments?.getString("name") ?: "Usuário"
                        TransactionsScreen(
                            userName = nome,
                            onNavigateToHome = { navController.navigate("home/$nome") },
                            onNavigateToAccounts = { navController.navigate("accounts/$nome") },
                            onNavigateToAdd = { navController.navigate("add_transaction") },
                            onLogout = {
                                navController.navigate("login") { popUpTo("home/{name}") { inclusive = true } }
                            },
                            onNavigateToTransactions = {
                                navController.navigate("transactions/$nome")
                            }
                        )
                    }

                    // ROTA 6: ADICIONAR TRANSACAO
                    composable("add_transaction") {
                        AddTransactionScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}