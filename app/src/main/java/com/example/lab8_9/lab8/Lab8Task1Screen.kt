package com.example.lab8_9.lab8

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Lab8Task1Screen(modifier: Modifier = Modifier) {
    var model by remember { mutableStateOf(PermissionModel()) }
    var logs by remember { mutableStateOf(listOf("Boshlang'ich holat: $model")) }

    fun addLog(log: String) {
        logs = logs + log
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Lab 8 - 1-topshiriq: Kotlin Permission Modeli",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "\"Denied twice\" modelini va holatlar o'tishini simulyatsiya qilish.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Joriy Holat:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(text = "• granted = ${model.granted}")
                Text(text = "• rationale = ${model.rationale}")
                Text(text = "• dialogShows = ${model.dialogShows}")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    val stepNum = logs.size
                    model.request(userAllows = false)
                    addLog("$stepNum-qadam (Rad etish): $model")
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Rad etish")
            }

            Button(
                onClick = {
                    val stepNum = logs.size
                    model.request(userAllows = true)
                    addLog("$stepNum-qadam (Ruxsat berildi): $model")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Ruxsat berish")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val newModel = PermissionModel()
                    newModel.request(false) // 1
                    newModel.request(false) // 2
                    val logsList = mutableListOf(
                        "=== Avto 4-qadam simulyatsiyasi ===",
                        "Boshlang'ich: granted=false, rationale=false, dialogShows=true",
                        "1-qadam (Rad): $newModel",
                        "2-qadam (Rad): $newModel"
                    )
                    newModel.request(false)
                    logsList.add("3-qadam: $newModel")
                    newModel.request(true)
                    logsList.add("4-qadam: $newModel")

                    model = newModel
                    logs = logsList
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Demo 4 qadam", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = {
                    model = PermissionModel()
                    logs = listOf("Boshlang'ich holat: $model")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Qayta tushirish")
            }
        }

        Text(
            text = "Simulyatsiya jurnali:",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            color = Color(0xFF1E1E1E)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                logs.forEach { log ->
                    Text(
                        text = log,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF80D8FF),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
