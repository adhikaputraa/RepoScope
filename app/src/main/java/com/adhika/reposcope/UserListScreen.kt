package com.adhika.reposcope

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.adhika.reposcope.ui.common.ErrorItem
import com.adhika.reposcope.ui.common.LoadingItem

@Composable
fun UserListScreen(
    viewModel: UserListViewModel = hiltViewModel(),
    onUserClick: (String) -> Unit
) {
    val query by viewModel.searchQuery.collectAsState()
    val pagingItems = viewModel.usersFlow.collectAsLazyPagingItems()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            label = { Text("Search GitHub Users") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (query.isBlank()) {
            Text("Type something to search.")
        } else {
            LazyColumn {
                items(
                    count = pagingItems.itemCount,
                    key = { index -> pagingItems[index]?.username ?: index }
                ) { index ->
                    val user = pagingItems[index] ?: return@items
                    ListItem(
                        headlineContent = { Text(user.username) },
                        leadingContent = {
                            AsyncImage(
                                model = user.avatarUrl,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp).clip(CircleShape)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onUserClick(user.username) }
                    )
                    Divider()
                }

                // Add loading and error states
                when (pagingItems.loadState.refresh) {
                    is LoadState.Loading -> {
                        item { LoadingItem() }
                    }
                    is LoadState.Error -> {
                        val error = pagingItems.loadState.refresh as LoadState.Error
                        item {
                            ErrorItem(
                                message = error.error.localizedMessage ?: "Error loading users",
                                onRetry = { pagingItems.refresh() }
                            )
                        }
                    }
                    else -> {}
                }

                // Handle pagination loading/error states
                when (pagingItems.loadState.append) {
                    is LoadState.Loading -> {
                        item { LoadingItem() }
                    }
                    is LoadState.Error -> {
                        val error = pagingItems.loadState.append as LoadState.Error
                        item {
                            ErrorItem(
                                message = error.error.localizedMessage ?: "Error loading more users",
                                onRetry = { pagingItems.retry() }
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}