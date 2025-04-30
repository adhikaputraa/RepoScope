package com.adhika.reposcope

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.adhika.reposcope.presentation.common.InlineErrorWithRetry
import com.adhika.reposcope.presentation.common.LoadingItem
import com.adhika.reposcope.presentation.userdetail.UserDetailUiState
import com.adhika.reposcope.presentation.userdetail.UserDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    username: String,
    avatarUrl: String? = null,
    viewModel: UserDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    LaunchedEffect(username) {
        viewModel.loadUserDetail(username, avatarUrl)
    }

    val uiState by viewModel.uiState.collectAsState()
    val reposPager = viewModel.reposFlow.collectAsLazyPagingItems()
    val context = LocalContext.current
    val languages by viewModel.languages.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(username, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1976D2))
            )
        },
        containerColor = Color(0xFFF7F7F8)
    ) { padding ->
        when (uiState) {
            is UserDetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UserDetailUiState.Error -> {
                val errorState = uiState as UserDetailUiState.Error
                val isPagingError = reposPager.loadState.refresh is LoadState.Error
                // If error is not from paging, show retry for user info
                if (!isPagingError) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        InlineErrorWithRetry(
                            message = errorState.message,
                            onRetry = { viewModel.loadUserDetail(errorState.username, errorState.avatarUrl) }
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .background(Color(0xFFF7F7F8))
                    ) {
                        item {
                            // Header (same as success, but with error data)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                                    .padding(top = 32.dp, bottom = 28.dp)
                            ) {
                                if (errorState.avatarUrl != null) {
                                    AsyncImage(
                                        model = errorState.avatarUrl,
                                        contentDescription = null,
                                        modifier = Modifier.size(100.dp).clip(CircleShape)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                                Text(errorState.username, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color(0xFF222222))
                                Spacer(modifier = Modifier.height(14.dp))
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Followers", color = Color(0xFF888888), fontSize = 13.sp)
                                        Text("-", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1976D2))
                                    }
                                    Spacer(modifier = Modifier.width(36.dp))
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Following", color = Color(0xFF888888), fontSize = 13.sp)
                                        Text("-", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1976D2))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(18.dp))
                            Text(
                                "Repositories",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = Color(0xFF222222),
                                modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
                            )
                        }
                        item {
                            val error = reposPager.loadState.refresh as LoadState.Error
                            InlineErrorWithRetry(
                                message = error.error.localizedMessage ?: errorState.message,
                                onRetry = { reposPager.refresh() }
                            )
                        }
                    }
                }
            }
            is UserDetailUiState.Success -> {
                val user = (uiState as UserDetailUiState.Success).user
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color(0xFFF7F7F8))
                ) {
                    item {
                        // Header
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                                .padding(top = 32.dp, bottom = 28.dp)
                        ) {
                            AsyncImage(
                                model = user.avatarUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(user.username, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color(0xFF222222))
                            user.fullName?.let {
                                Text(it, color = Color(0xFF888888), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Followers", color = Color(0xFF888888), fontSize = 13.sp)
                                    Text("${user.followers}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1976D2))
                                }
                                Spacer(modifier = Modifier.width(36.dp))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Following", color = Color(0xFF888888), fontSize = 13.sp)
                                    Text("${user.following}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1976D2))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            "Repositories",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = Color(0xFF222222),
                            modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
                        )
                        // Language filter dropdown
                        if (languages.isNotEmpty()) {
                            var expanded by remember { mutableStateOf(false) }
                            Box(Modifier.padding(start = 20.dp, end = 20.dp, bottom = 8.dp)) {
                                // Use standard Material3 DropdownMenu instead of ExposedDropdownMenu
                                androidx.compose.material3.TextField(
                                    value = selectedLanguage ?: "All languages",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Filter by language") },
                                    trailingIcon = {
                                        IconButton(onClick = { expanded = !expanded }) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowBack, // Use a suitable dropdown icon
                                                contentDescription = null
                                            )
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("All languages") },
                                        onClick = {
                                            viewModel.onLanguageSelected(null)
                                            expanded = false
                                        }
                                    )
                                    languages.forEach { lang ->
                                        DropdownMenuItem(
                                            text = { Text(lang) },
                                            onClick = {
                                                viewModel.onLanguageSelected(lang)
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    // Repositories
                    items(
                        count = reposPager.itemCount,
                        key = { index ->
                            val repo = reposPager[index]
                            if (repo != null) repo.htmlUrl else index
                        }
                    ) { index ->
                        val repo = reposPager[index] ?: return@items
                        androidx.compose.material3.Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.White),
                            elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repo.htmlUrl))
                                    context.startActivity(intent)
                                }
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        repo.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = Color(0xFF222222)
                                    )
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFFC107),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${repo.stars}", color = Color(0xFF888888), fontSize = 14.sp)
                                }
                                repo.language?.let {
                                    Text(it, color = Color(0xFF1976D2), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                }
                                repo.description?.let {
                                    Text(it, color = Color(0xFF888888), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                    // Loading/Error states (unchanged)
                    when (reposPager.loadState.refresh) {
                        is LoadState.Loading -> {
                            item { LoadingItem() }
                        }
                        is LoadState.Error -> {
                            val error = reposPager.loadState.refresh as LoadState.Error
                            item {
                                InlineErrorWithRetry(
                                    message = error.error.localizedMessage ?: "Error loading repositories",
                                    onRetry = { reposPager.refresh() }
                                )
                            }
                        }
                        else -> {}
                    }
                    when (reposPager.loadState.append) {
                        is LoadState.Loading -> {
                            item { LoadingItem() }
                        }
                        is LoadState.Error -> {
                            val error = reposPager.loadState.append as LoadState.Error
                            item {
                                InlineErrorWithRetry(
                                    message = error.error.localizedMessage ?: "Error loading more repositories",
                                    onRetry = { reposPager.retry() }
                                )
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}