package br.edu.ifpb.finsupp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: AddTransactionViewModel = viewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { viewModel.loadDependencies() }

    LaunchedEffect(viewModel.saveSuccess) {
        if (viewModel.saveSuccess) {
            onBack()
            viewModel.onNavigatedAway()
        }
    }

    LaunchedEffect(viewModel.toastMessage) {
        viewModel.toastMessage?.let {
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
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {

            // ABAS (Withdraw | Deposit | Transfer)
            TabRow(
                selectedTabIndex = viewModel.selectedTab,
                containerColor = DarkBackground,
                contentColor = PrimaryBlue
            ) {
                listOf("Withdraw", "Deposit", "Transfer").forEachIndexed { index, title ->
                    Tab(
                        selected = viewModel.selectedTab == index,
                        onClick = { viewModel.selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Campos Comuns
            DarkInput("Description", viewModel.description, { viewModel.description = it })
            DarkInput("Amount", viewModel.amount, { viewModel.amount = it }, isNumber = true)
            DarkInput("Date (YYYY-MM-DD)", viewModel.date, { viewModel.date = it })

            // Dropdown Conta Origem (Sempre visível)
            DropdownSelector(
                label = if (viewModel.selectedTab == 2) "From Account" else "Account",
                selectedValue = viewModel.selectedAccount?.description ?: "Select Account",
                items = viewModel.accountsList,
                onItemSelected = { viewModel.selectedAccount = it },
                itemLabel = { it.description }
            )

            // Campos específicos
            if (viewModel.selectedTab == 2) { // TRANSFER
                Spacer(modifier = Modifier.height(16.dp))
                DropdownSelector(
                    label = "To Account",
                    selectedValue = viewModel.selectedToAccount?.description ?: "Select Recipient",
                    items = viewModel.accountsList.filter { it.id != viewModel.selectedAccount?.id }, // Não transferir para si mesmo
                    onItemSelected = { viewModel.selectedToAccount = it },
                    itemLabel = { it.description }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            DropdownSelector(
                label = "Category",
                selectedValue = viewModel.selectedCategory?.description ?: "Select Category",
                items = viewModel.categoriesList,
                onItemSelected = { viewModel.selectedCategory = it },
                itemLabel = { it.description }
            )

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { viewModel.createTransaction() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) CircularProgressIndicator(color = Color.White) else Text("Create")
            }
        }
    }
}

// Componente auxiliar para dropdown
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