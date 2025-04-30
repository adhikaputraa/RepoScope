package com.adhika.reposcope

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.adhika.reposcope.presentation.common.EmptyState
import com.adhika.reposcope.presentation.common.ErrorState
import com.adhika.reposcope.presentation.common.InlineErrorWithRetry
import com.adhika.reposcope.presentation.userlist.UserListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    viewModel: UserListViewModel = hiltViewModel(),
    onUserClick: (username: String, avatarUrl: String?) -> Unit
) {
    val pendingQuery by viewModel.pendingQuery.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val pagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GitHub Users", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1976D2))
            )
        },
        containerColor = Color(0xFFF7F7F8)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF7F7F8))
        ) {
            OutlinedTextField(
                value = pendingQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                placeholder = { Text("Search users", color = Color(0xFFB0B0B0)) },
                trailingIcon = {
                    if (pendingQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Close,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .background(Color.White, RoundedCornerShape(32.dp)),
                colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFF1976D2)
                )
            )

            if (pendingQuery.isBlank()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    EmptyState(title = "Start searching", subtitle = "Type a username to begin")
                }
            } else if (errorMessage != null && pagingItems.itemCount == 0) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    InlineErrorWithRetry(
                        message = errorMessage ?: "Unable to load data",
                        onRetry = { viewModel.retry() }
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp)
                ) {
                    items(
                        count = pagingItems.itemCount,
                        key = { index -> pagingItems[index]?.username ?: index }
                    ) { index ->
                        val user = pagingItems[index] ?: return@items
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { onUserClick(user.username, user.avatarUrl) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                            ) {
                                AsyncImage(
                                    model = user.avatarUrl,
                                    contentDescription = null,
                                    modifier = Modifier.size(54.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                    placeholder = painterResource(id = R.drawable.ic_avatar_placeholder),
                                    error = painterResource(id = R.drawable.ic_avatar_placeholder)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    user.username,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = Color(0xFF222222)
                                )
                            }
                        }
                    }
                    when (pagingItems.loadState.refresh) {
                        is LoadState.Loading -> {
                            item { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(Modifier.padding(24.dp)) } }
                        }
                        is LoadState.Error -> {
                            val error = pagingItems.loadState.refresh as LoadState.Error
                            item {
                                ErrorState(title = "An error occured", subtitle = error.error.localizedMessage ?: "Unable to load data")
                            }
                        }
                        else -> {}
                    }
                    when (pagingItems.loadState.append) {
                        is LoadState.Loading -> {
                            item { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(Modifier.padding(24.dp)) } }
                        }
                        is LoadState.Error -> {
                            val error = pagingItems.loadState.append as LoadState.Error
                            item {
                                InlineErrorWithRetry(
                                    message = error.error.localizedMessage ?: "Error loading more users",
                                    onRetry = { pagingItems.retry() }
                                )
                            }
                        }
                        else -> {}
                    }
                    if (pagingItems.itemCount == 0 && pagingItems.loadState.refresh is LoadState.NotLoading) {
                        item { EmptyState() }
                    }
                }
            }
        }
    }

    val context = LocalContext.current
    val prevError = remember { mutableStateOf<String?>(null) }
    val prevQuery = remember { mutableStateOf("") }

    // Show toast on new error after a search (not initial load)
    LaunchedEffect(errorMessage) {
        if (errorMessage != null && pagingItems.itemCount > 0) {
            Toast.makeText(context, errorMessage ?: "Error loading users", Toast.LENGTH_SHORT).show()
        }
    }
}