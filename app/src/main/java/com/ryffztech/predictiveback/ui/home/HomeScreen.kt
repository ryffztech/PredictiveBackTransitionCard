package com.ryffztech.predictiveback.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    onNavigate: (String) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp)
        ) {

            Text("FiloOrganizer")

            Spacer(Modifier.height(20.dp))


            Text("Storage access granted!")

            Button(
                onClick = {
                    onNavigate("gallery")
                }
            ) {
                Text("Open Gallery")
            }


                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        onNavigate("settings")
                    }
                ) {
                    Text("Settings")
                }
            }
        }
    }
