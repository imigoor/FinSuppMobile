package br.edu.ifpb.finsupp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.ifpb.finsupp.ui.theme.DarkBackground
import br.edu.ifpb.finsupp.ui.theme.PrimaryBlue
import br.edu.ifpb.finsupp.ui.viewmodel.AddTransactionViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: AddTransactionViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // PEGA O ESTADO ÚNICO
    val state = viewModel.uiState

    LaunchedEffect(Unit) { viewModel.loadDependencies() }

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            onBack()
            viewModel.onNavigatedAway()
        }
    }

    LaunchedEffect(state.toastMessage) {
        state.toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearToastMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Transaction", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        // ADICIONADO O SCROLL AQUI PARA NÃO CORTAR O BOTÃO
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // ABAS (Withdraw | Deposit | Transfer)
            TabRow(
                selectedTabIndex = state.selectedTab,
                containerColor = DarkBackground,
                contentColor = PrimaryBlue
            ) {
                listOf("Withdraw", "Deposit", "Transfer").forEachIndexed { index, title ->
                    Tab(
                        selected = state.selectedTab == index,
                        onClick = { viewModel.updateTab(index) },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Campos Comuns (LENDO DO STATE)
            DarkInput("Description", state.description, { viewModel.updateDescription(it) })
            DarkInput("Amount", state.amount, { viewModel.updateAmount(it) }, isNumber = true)
            DarkInput("Date (YYYY-MM-DD)", state.date, { viewModel.updateDate(it) })

            // Dropdown Conta Origem (Sempre visível)
            DropdownSelector(
                label = if (state.selectedTab == 2) "From Account" else "Account",
                selectedValue = state.selectedAccount?.description ?: "Select Account",
                items = state.accountsList,
                onItemSelected = { viewModel.updateSelectedAccount(it) },
                itemLabel = { it.description }
            )

            // Campos específicos
            if (state.selectedTab == 2) { // TRANSFER
                Spacer(modifier = Modifier.height(16.dp))
                DropdownSelector(
                    label = "To Account",
                    selectedValue = state.selectedToAccount?.description ?: "Select Recipient",
                    items = state.accountsList.filter { it.id != state.selectedAccount?.id }, // Não transferir para si mesmo
                    onItemSelected = { viewModel.updateSelectedToAccount(it) },
                    itemLabel = { it.description }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            DropdownSelector(
                label = "Category",
                selectedValue = state.selectedCategory?.description ?: "Select Category",
                items = state.categoriesList,
                onItemSelected = { viewModel.updateSelectedCategory(it) },
                itemLabel = { it.description }
            )

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { viewModel.createTransaction() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) CircularProgressIndicator(color = Color.White) else Text("Create")
            }

            // Espaço extra no final para garantir scroll confortável
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Componente auxiliar para dropdown (mantido igual)
@Composable
fun <T> DropdownSelector(label: String, selectedValue: String, items: List<T>, onItemSelected: (T) -> Unit, itemLabel: (T) -> String) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(label, color = Color.White, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Box {
            OutlinedTextField(
                value = selectedValue, onValueChange = {}, readOnly = true,
                modifier = Modifier.fillMaxWidth().clickable { expanded = true },
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = Color.White) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF1E293B), unfocusedContainerColor = Color(0xFF1E293B)
                ),
                enabled = false
            )
            Box(Modifier.matchParentSize().clickable { expanded = true })
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(Color(0xFF1E293B))) {
                items.forEach { item ->
                    DropdownMenuItem(text = { Text(itemLabel(item), color = Color.White) }, onClick = { onItemSelected(item); expanded = false })
                }
            }
        }
    }
}