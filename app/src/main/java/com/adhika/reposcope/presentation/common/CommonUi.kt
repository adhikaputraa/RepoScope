package com.adhika.reposcope.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adhika.reposcope.ui.theme.BluePrimary

@Composable
fun EmptyState(
    title: String = "No results found",
    subtitle: String = "Try adjusting your search criteria"
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color(0xFFB0B0B0),
                modifier = Modifier.padding(bottom = 12.dp).then(Modifier.align(Alignment.CenterHorizontally)).then(Modifier.size(64.dp))
            )
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color(0xFF222222),
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                color = Color(0xFF888888),
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 4.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ErrorState(
    title: String = "An error occured",
    subtitle: String = "Unable to load data"
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFD32F2F),
                modifier = Modifier.padding(bottom = 12.dp).then(Modifier.align(Alignment.CenterHorizontally)).then(Modifier.size(64.dp))
            )
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color(0xFF222222),
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                color = Color(0xFF888888),
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 4.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun InlineErrorWithRetry(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = Color(0xFFD32F2F),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = message,
            color = Color(0xFFD32F2F),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary, contentColor = Color.White)
        ) {
            Text("Retry")
        }
    }
}

@Composable
fun LoadingItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}