package com.avangard.stock.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.avangard.stock.data.model.Product
import com.avangard.stock.data.model.TransactionType
import com.avangard.stock.ui.theme.*
import com.avangard.stock.ui.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(viewModel: TransactionViewModel = viewModel()) {
    val products by viewModel.products.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var selectedType by remember { mutableStateOf(TransactionType.INCOMING) }
    var quantity by remember { mutableStateOf("") }
    var pricePerUnit by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            selectedProduct = null
            quantity = ""
            pricePerUnit = ""
            note = ""
            viewModel.resetUiState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Yangi amal",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Skladga kirdi/chiqdi/qaytdi qo'shish",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        // Amal turi
        Text("Amal turi", fontWeight = FontWeight.Medium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TransactionTypeChip("Kirdi", selectedType == TransactionType.INCOMING, IncomeGreen) {
                selectedType = TransactionType.INCOMING
            }
            TransactionTypeChip("Sotildi", selectedType == TransactionType.SOLD, SoldBlue) {
                selectedType = TransactionType.SOLD
            }
            TransactionTypeChip("Qaytdi", selectedType == TransactionType.RETURNED, ReturnOrange) {
                selectedType = TransactionType.RETURNED
            }
        }

        // Mahsulot tanlash
        Text("Mahsulotni tanlang", fontWeight = FontWeight.Medium)
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selectedProduct?.name ?: "",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                placeholder = { Text("Mahsulotni tanlang") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                products.forEach { product ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(product.name, fontWeight = FontWeight.Medium)
                                Text("Omborda: ${product.stockQuantity} dona", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                        },
                        onClick = {
                            selectedProduct = product
                            pricePerUnit = when (selectedType) {
                                TransactionType.INCOMING -> product.purchasePrice.toLong().toString()
                                TransactionType.SOLD -> product.sellingPrice.toLong().toString()
                                TransactionType.RETURNED -> product.sellingPrice.toLong().toString()
                            }
                            expanded = false
                        }
                    )
                }
            }
        }

        // Soni
        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it.filter { c -> c.isDigit() } },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Soni (dona)") },
            leadingIcon = { Icon(Icons.Filled.Numbers, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Narxi
        OutlinedTextField(
            value = pricePerUnit,
            onValueChange = { pricePerUnit = it.filter { c -> c.isDigit() || c == '.' } },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Narxi (dona uchun, so'm)") },
            leadingIcon = { Icon(Icons.Filled.AttachMoney, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Izoh
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Izoh (ixtiyoriy)") },
            leadingIcon = { Icon(Icons.Filled.Note, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            maxLines = 3
        )

        // Jami summa
        val qty = quantity.toIntOrNull() ?: 0
        val price = pricePerUnit.toDoubleOrNull() ?: 0.0
        val total = qty * price
        if (total > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Jami summa:", fontWeight = FontWeight.Medium)
                    Text("${String.format("%,.0f", total)} so'm", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // Saqlash
        Button(
            onClick = {
                selectedProduct?.let { product ->
                    val q = quantity.toIntOrNull() ?: 0
                    val p = pricePerUnit.toDoubleOrNull() ?: 0.0
                    if (q > 0 && p > 0) {
                        when (selectedType) {
                            TransactionType.INCOMING -> viewModel.addIncomingTransaction(product.id, q, p, note)
                            TransactionType.SOLD -> viewModel.addSoldTransaction(product.id, q, p, note)
                            TransactionType.RETURNED -> viewModel.addReturnedTransaction(product.id, q, p, note)
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = selectedProduct != null && quantity.isNotEmpty() && pricePerUnit.isNotEmpty(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = when (selectedType) {
                    TransactionType.INCOMING -> IncomeGreen
                    TransactionType.SOLD -> SoldBlue
                    TransactionType.RETURNED -> ReturnOrange
                }
            )
        ) {
            Icon(Icons.Filled.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                when (selectedType) {
                    TransactionType.INCOMING -> "Skladga qo'shish"
                    TransactionType.SOLD -> "Sotuvga chiqarish"
                    TransactionType.RETURNED -> "Qaytarishni ro'yxatga olish"
                },
                fontWeight = FontWeight.Bold
            )
        }

        uiState.errorMessage?.let { error ->
            Card(colors = CardDefaults.cardColors(containerColor = AlertRed.copy(alpha = 0.1f))) {
                Text(text = error, modifier = Modifier.padding(12.dp), color = AlertRed)
            }
        }

        if (uiState.isSuccess) {
            Card(colors = CardDefaults.cardColors(containerColor = IncomeGreen.copy(alpha = 0.1f))) {
                Text(text = "Muvaffaqiyatli saqlandi!", modifier = Modifier.padding(12.dp), color = IncomeGreen, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionTypeChip(label: String, selected: Boolean, color: Color, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = color.copy(alpha = 0.2f),
            selectedLabelColor = color
        )
    )
}
