package com.example.lab8_9.task5

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
fun Task5Screen(modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()

    // 1. derivedStateOf varianti (faqat birinchi element indeks > 0 bo'lganda o'zgaradi)
    val showButtonDerived by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }

    // 2. To'g'ridan-to'g'ri variant (har bir scroll paytida recomposition beradi)
    val showButtonDirect = listState.firstVisibleItemIndex > 0

    // Recomposition counterlar
    var derivedRecomposeCount by remember { mutableIntStateOf(0) }
    var directRecomposeCount by remember { mutableIntStateOf(0) }

    var logs by remember { mutableStateOf(listOf<String>()) }

    fun addLog(text: String) {
        Log.d("Task5Scroll", text)
        logs = logs + text
    }

    // snapshotFlow bilan 0-elementdan o'tganda faqat 1 marta analytics yuborish
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { index -> index > 0 }
            .distinctUntilChanged()
            .filter { isScrolledDown -> isScrolledDown }
            .collect {
                addLog("Analytics: Foydalanuvchi yuqoridan pastga o'tdi (index > 0)")
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "5-topshiriq: derivedStateOf va snapshotFlow",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "100 ta elementli LazyColumn. derivedStateOf recomposition'ni qanday kamaytirishini solishtiring.",
            style = MaterialTheme.typography.bodyMedium
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                SideEffect { derivedRecomposeCount++ }
                SideEffect { directRecomposeCount++ }
                Text("derivedStateOf button recompose count: $derivedRecomposeCount")
                Text("Direct button recompose count: $directRecomposeCount")
                Text("Tugma ko'rinishi (derived): $showButtonDerived")
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(100) { index ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text(
                            text = "Element #$index",
                            modifier = Modifier.padding(16.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (showButtonDerived) {
                Button(
                    onClick = {},
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Text("Tepaga qaytish")
                }
            }
        }

        Text("Loglar & SnapshotFlow:", fontWeight = FontWeight.Bold)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = MaterialTheme.shapes.small,
            color = Color(0xFF1E1E1E)
        ) {
            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (logs.isEmpty()) {
                    Text("LazyColumn'ni aylantirib ko'ring...", color = Color.Gray, fontSize = 12.sp)
                }
                logs.forEach { log ->
                    Text(log, fontFamily = FontFamily.Monospace, color = Color(0xFF80D8FF), fontSize = 12.sp)
                }
            }
        }
    }
}
