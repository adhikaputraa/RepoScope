package com.adhika.reposcope

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.adhika.reposcope.ui.common.ErrorItem
import com.adhika.reposcope.ui.common.LoadingItem

@Composable
fun UserDetailScreen(
    username: String,
    viewModel: UserDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(username) {
        viewModel.loadUserDetail(username)
    }

    val uiState by viewModel.uiState.collectAsState()
    val reposPager = viewModel.reposFlow.collectAsLazyPagingItems()

    when (uiState) {
        is UserDetailUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is UserDetailUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${(uiState as UserDetailUiState.Error).message}")
            }
        }

        is UserDetailUiState.Success -> {
            val user = (uiState as UserDetailUiState.Success).user

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                item {
                    // User header with avatar and info
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = user.avatarUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(text = user.fullName ?: user.username, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(text = "@${user.username}", color = Color.Gray)
                            Text(text = "${user.followers} followers · ${user.following} following")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Repositories", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Repositories list with pagination
                items(
                    count = reposPager.itemCount,
                    key = { index -> reposPager[index]?.name ?: index }
                ) { index ->
                    val repo = reposPager[index] ?: return@items
                    val context = LocalContext.current
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repo.htmlUrl))
                                context.startActivity(intent)
                            }
                            .padding(vertical = 8.dp)
                    ) {
                        Text(repo.name, fontWeight = FontWeight.SemiBold)
                        repo.description?.let {
                            Text(it, fontSize = 14.sp, color = Color.DarkGray)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repo.language?.let {
                                Text(it, fontSize = 12.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text("★ ${repo.stars}", fontSize = 12.sp, color = Color.Gray)
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }

                // Handle repository loading states
                when (reposPager.loadState.refresh) {
                    is LoadState.Loading -> {
                        item { LoadingItem() }
                    }
                    is LoadState.Error -> {
                        val error = reposPager.loadState.refresh as LoadState.Error
                        item {
                            ErrorItem(
                                message = error.error.localizedMessage ?: "Error loading repositories",
                                onRetry = { reposPager.refresh() }
                            )
                        }
                    }
                    else -> {}
                }

                // Handle pagination loading states
                when (reposPager.loadState.append) {
                    is LoadState.Loading -> {
                        item { LoadingItem() }
                    }
                    is LoadState.Error -> {
                        val error = reposPager.loadState.append as LoadState.Error
                        item {
                            ErrorItem(
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