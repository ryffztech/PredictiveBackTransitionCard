package com.ryffztech.predictiveback.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val textColor = MaterialTheme.colorScheme.onBackground

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp)
        ) {

            Text(
                text = "Settings",
                color = textColor,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "FiloOrganizer Preferences",
                color = textColor,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(16.dp))

            Button(onClick = onBack) {
                Text("Back")
            }
        }
    }
}
