package com.ryffztech.predictiveback.ui.gallery

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GalleryScreen(
    onBack: () -> Unit
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

            Text("Gallery")

            Text("Your photos will appear here.")

            Spacer(Modifier.height(16.dp))

            Button(onClick = onBack) {
                Text("Back")
            }
        }
    }
}
