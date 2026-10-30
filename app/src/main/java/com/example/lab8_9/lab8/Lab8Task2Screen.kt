package com.example.lab8_9.lab8

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun Lab8Task2Screen(modifier: Modifier = Modifier) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Lab 8 - 2-topshiriq: Scaffold & SnackbarHost",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Scaffold ichida SnackbarHostState va coroutine scope yordamida Snackbar chiqarish.",
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Lab 8 Snackbar xabari!")
                    }
                }
            ) {
                Text("Snackbar chiqarish")
            }
        }
    }
}
