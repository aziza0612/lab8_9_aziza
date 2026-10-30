package com.example.lab8_9.lab9

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun TrackedChild(onLog: (String) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START || event == Lifecycle.Event.ON_STOP) {
                onLog("Lifecycle event: $event")
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onLog("addObserver bajarildi")

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            onLog("onDispose: removeObserver bajarildi")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Child Composable Faol", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun Lab9Task3Screen(modifier: Modifier = Modifier) {
    var showChild by remember { mutableStateOf(true) }
    var logs by remember { mutableStateOf(listOf<String>()) }

    fun addLog(text: String) {
        Log.d("Lab9Task3", text)
        logs = logs + text
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Lab 9 - 3-topshiriq: Lifecycle logger & DisposableEffect",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Child Composable'ni ko'rsatish:", fontWeight = FontWeight.Medium)
            Switch(
                checked = showChild,
                onCheckedChange = { showChild = it }
            )
        }

        if (showChild) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                TrackedChild(onLog = { addLog(it) })
            }
        }

        Text("Loglar:", fontWeight = FontWeight.Bold)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            color = Color(0xFF1E1E1E)
        ) {
            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (logs.isEmpty()) {
                    Text("Hozircha loglar yo'q.", color = Color.Gray, fontSize = 12.sp)
                }
                logs.forEach { log ->
                    Text(log, fontFamily = FontFamily.Monospace, color = Color(0xFF80D8FF), fontSize = 12.sp)
                }
            }
        }
    }
}
