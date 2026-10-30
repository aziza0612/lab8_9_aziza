package com.example.lab8_9.lab9

import android.util.Log
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

object Lab9Analytics {
    var userType: String = "Guest"
}

@Composable
fun Lab9Task4Screen(modifier: Modifier = Modifier) {
    var userType by remember { mutableStateOf("Guest") }
    var counter by remember { mutableIntStateOf(0) }
    var logs by remember { mutableStateOf(listOf<String>()) }

    fun addLog(text: String) {
        Log.d("Lab9Task4", text)
        logs = logs + text
    }

    SideEffect {
        counter++
        Lab9Analytics.userType = userType
        addLog("SideEffect ishladi (#$counter): userType = $userType")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Lab 9 - 4-topshiriq: SideEffect va analytics",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("userType: $userType", fontWeight = FontWeight.Bold)
                Text("SideEffect counter: $counter")
            }
        }

        Button(
            onClick = { userType = if (userType == "Guest") "Admin" else "Guest" }
        ) {
            Text("UserType'ni o'zgartirish")
        }

        Text("Loglar:", fontWeight = FontWeight.Bold)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            color = Color(0xFF1E1E1E)
        ) {
            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                logs.forEach { log ->
                    Text(log, fontFamily = FontFamily.Monospace, color = Color(0xFF80D8FF), fontSize = 12.sp)
                }
            }
        }
    }
}
