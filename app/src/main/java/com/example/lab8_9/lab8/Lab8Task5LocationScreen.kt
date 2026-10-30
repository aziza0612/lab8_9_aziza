package com.example.lab8_9.lab8

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Lab8Task5LocationScreen(modifier: Modifier = Modifier) {
    var locationGranted by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Lab 8 - 5-topshiriq: Joylashuv (Location) ruxsati",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Location statusi: ${if (locationGranted) "Ruxsat berilgan" else "Ruxsat berilmagan"}")
                Button(onClick = { locationGranted = !locationGranted }) {
                    Text(if (locationGranted) "O'chirish" else "Joylashuv ruxsatini so'rash")
                }
            }
        }
    }
}
