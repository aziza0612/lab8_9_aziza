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
import kotlinx.coroutines.delay

@Composable
fun LandingScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onTimeout()
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("LandingScreen (2s kutish)...", fontWeight = FontWeight.Medium)
    }
}

@Composable
fun LandingScreenUpdated(onTimeout: () -> Unit) {
    val currentOnTimeout by rememberUpdatedState(onTimeout)
    LaunchedEffect(Unit) {
        delay(2000)
        currentOnTimeout()
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("LandingScreen (rememberUpdatedState)...", fontWeight = FontWeight.Medium)
    }
}

@Composable
fun Lab9Task1Screen(modifier: Modifier = Modifier) {
    var version by remember { mutableIntStateOf(0) }
    var logs by remember { mutableStateOf(listOf<String>()) }
    var showDirectLanding by remember { mutableStateOf(false) }
    var showUpdatedLanding by remember { mutableStateOf(false) }

    fun addLog(text: String) {
        Log.d("Lab9Task1", text)
        logs = logs + text
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            version++
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Lab 9 - 1-topshiriq: Splash ekrani & rememberUpdatedState",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Ota composable'da version: $version (har 500ms oshadi).",
            style = MaterialTheme.typography.bodyMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    showDirectLanding = true
                    addLog("Direct Landing boshlandi (v=$version)")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Direct Landing", fontSize = 12.sp)
            }
            Button(
                onClick = {
                    showUpdatedLanding = true
                    addLog("Updated Landing boshlandi (v=$version)")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Updated Landing", fontSize = 12.sp)
            }
        }

        if (showDirectLanding) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("1. To'g'ridan-to'g'ri lambda:", fontWeight = FontWeight.Bold)
                    LandingScreen(onTimeout = {
                        addLog("Direct timeout: v$version")
                        showDirectLanding = false
                    })
                }
            }
        }

        if (showUpdatedLanding) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("2. rememberUpdatedState bilan:", fontWeight = FontWeight.Bold)
                    val currentVersion = version
                    LandingScreenUpdated(onTimeout = {
                        addLog("Updated timeout: v$currentVersion")
                        showUpdatedLanding = false
                    })
                }
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
