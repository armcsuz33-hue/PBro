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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.avangard.stock.data.model.Product
import com.avangard.stock.ui.theme.*
import com.avangard.stock.ui.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(viewModel: ProductViewModel = viewModel()) {
    val products by viewModel.allProducts.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showProductList by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Admin Panel",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Mahsulotlarni boshqarish",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AvangardTeal.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Umumiy mahsulotlar", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Text("${products.size} ta", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = AvangardTeal)
            }
        }

        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AvangardTeal)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Yangi mahsulot qo'shish", fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
            onClick = { showProductList = !showProductList },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Filled.List, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (showProductList) "Ro'yxatni yashirish" else "Mahsulotlar ro'yxati")
        }

        if (showProductList) {
            products.forEach { product ->
                AdminProductItem(product = product, onDeactivate = { viewModel.deactivateProduct(product.id) })
            }
        }
    }

    if (showAddDialog) {
        AddProductDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, desc, purchasePrice, sellingPrice, quantity, category ->
                viewModel.addProduct(name, desc, purchasePrice, sellingPrice, quantity, category)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AdminProductItem(product: Product, onDeactivate: () -> Unit) {
    var showConfirm by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Medium)
                Text("Omborda: ${product.stockQuantity} | ${product.category}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            if (!showConfirm) {
                IconButton(onClick = { showConfirm = true }) {
                    Icon(Icons.Filled.Delete, contentDescription = "O'chirish", tint = AlertRed)
                }
            } else {
                Row {
                    TextButton(onClick = { showConfirm = false }) { Text("Bekor", color = TextSecondary) }
                    TextButton(onClick = { onDeactivate(); showConfirm = false }) {
                        Text("O'chirish", color = AlertRed, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductDialog(onDismiss: () -> Unit, onAdd: (String, String, Double, Double, Int, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var purchasePrice by remember { mutableStateOf("") }
    var sellingPrice by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Muzlatgich") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yangi mahsulot", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nomi *") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(8.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Tavsif") }, modifier = Modifier.fillMaxWidth(), maxLines = 2, shape = RoundedCornerShape(8.dp))
                OutlinedTextField(value = purchasePrice, onValueChange = { purchasePrice = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Sotib olish narxi (so'm) *") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, shape = RoundedCornerShape(8.dp))
                OutlinedTextField(value = sellingPrice, onValueChange = { sellingPrice = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Sotish narxi (so'm) *") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, shape = RoundedCornerShape(8.dp))
                OutlinedTextField(value = quantity, onValueChange = { quantity = it.filter { c -> c.isDigit() } }, label = { Text("Boshlang'ich soni *") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, shape = RoundedCornerShape(8.dp))
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Kategoriya") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(8.dp))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val pPrice = purchasePrice.toDoubleOrNull() ?: 0.0
                    val sPrice = sellingPrice.toDoubleOrNull() ?: 0.0
                    val qty = quantity.toIntOrNull() ?: 0
                    if (name.isNotBlank() && pPrice > 0 && sPrice > 0) {
                        onAdd(name, description, pPrice, sPrice, qty, category)
                    }
                },
                enabled = name.isNotBlank() && purchasePrice.isNotEmpty() && sellingPrice.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = AvangardTeal)
            ) { Text("Qo'shish") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Bekor qilish") } }
    )
}
