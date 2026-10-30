package com.example.lab8_9.lab9

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
fun Lab9Task5Screen(modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()

    val showButtonDerived by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }

    var derivedRecomposeCount by remember { mutableIntStateOf(0) }
    var logs by remember { mutableStateOf(listOf<String>()) }

    fun addLog(text: String) {
        Log.d("Lab9Task5", text)
        logs = logs + text
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { index -> index > 0 }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                addLog("Analytics: Yuqoridan pastga o'tildi")
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Lab 9 - 5-topshiriq: derivedStateOf va snapshotFlow",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                SideEffect { derivedRecomposeCount++ }
                Text("derivedStateOf button recompose count: $derivedRecomposeCount")
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
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text("Element #$index", modifier = Modifier.padding(16.dp))
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
                    Text("Tepaga")
                }
            }
        }

        Text("Loglar:", fontWeight = FontWeight.Bold)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
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
