package com.example.lab8_9

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.lab8_9.lab8.*
import com.example.lab8_9.lab9.*
import com.example.lab8_9.ui.theme.Lab8_9Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab8_9Theme {
                MainAppScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen() {
    var selectedLab by remember { mutableIntStateOf(0) } // 0: Lab 8, 1: Lab 9
    var selectedTaskIndex by remember { mutableIntStateOf(0) } // 0..5

    val labTitles = listOf("Lab 8: Ruxsatlar", "Lab 9: Side-Effects")
    val taskTitles = listOf("1-Task", "2-Task", "3-Task", "4-Task", "5-Task", "6-Task")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lab 8 & Lab 9 Birlashtirilgan") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column {
                TabRow(selectedTabIndex = selectedLab) {
                    labTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedLab == index,
                            onClick = {
                                selectedLab = index
                                selectedTaskIndex = 0
                            },
                            text = { Text(title, fontWeight = FontWeight.Bold) }
                        )
                    }
                }

                SecondaryScrollableTabRow(selectedTabIndex = selectedTaskIndex) {
                    taskTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTaskIndex == index,
                            onClick = { selectedTaskIndex = index },
                            text = { Text(title) }
                        )
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    if (selectedLab == 0) {
                        when (selectedTaskIndex) {
                            0 -> Lab8Task1Screen()
                            1 -> Lab8Task2Screen()
                            2 -> Lab8Task3CameraScreen()
                            3 -> Lab8Task4OverlayScreen()
                            4 -> Lab8Task5LocationScreen()
                            5 -> Lab8Task6NotificationScreen()
                        }
                    } else {
                        when (selectedTaskIndex) {
                            0 -> Lab9Task1Screen()
                            1 -> Lab9Task2Screen()
                            2 -> Lab9Task3Screen()
                            3 -> Lab9Task4Screen()
                            4 -> Lab9Task5Screen()
                            5 -> Lab9Task6Screen()
                        }
                    }
                }
            }
        }
    }
}
