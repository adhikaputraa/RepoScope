package com.adhika.reposcope.presentation.userdetail

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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.adhika.reposcope.presentation.common.InlineErrorWithRetry
import com.adhika.reposcope.presentation.common.LoadingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    username: String,
    avatarUrl: String? = null,
    viewModel: UserDetailViewModel = hiltViewModel(),
    repoFavoriteViewModel: RepoFavoriteViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    LaunchedEffect(username) {
        viewModel.loadUserDetail(username, avatarUrl)
    }

    val uiState by viewModel.uiState.collectAsState()
    val reposPager = viewModel.reposFlow.collectAsLazyPagingItems()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(username, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        item {
                            // Header (same as success, but with error data)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
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
                                Text(errorState.username, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(modifier = Modifier.height(14.dp))
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Followers", color = Color(0xFF888888), fontSize = 13.sp)
                                        Text("-", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Spacer(modifier = Modifier.width(36.dp))
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Following", color = Color(0xFF888888), fontSize = 13.sp)
                                        Text("-", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(18.dp))
                            Text(
                                "Repositories",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onBackground,
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
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    item {
                        // Header
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
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
                            Text(user.username, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurface)
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
                                    Text("${user.followers}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(modifier = Modifier.width(36.dp))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Following", color = Color(0xFF888888), fontSize = 13.sp)
                                    Text("${user.following}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            "Repositories",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
                        )
                    }
                    // Repositories
                    items(
                        count = reposPager.itemCount,
                        key = { index ->
                            val repo = reposPager[index]
                            repo?.htmlUrl ?: index
                        }
                    ) { index ->
                        val repo = reposPager[index] ?: return@items
                        val isFavorite = repoFavoriteViewModel.favoriteState.collectAsState().value[repo.htmlUrl] ?: false
                        androidx.compose.material3.Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    IconButton(onClick = {
                                        if (isFavorite) repoFavoriteViewModel.removeFavorite(repo.htmlUrl)
                                        else repoFavoriteViewModel.addFavorite(repo, user.username)
                                    }) {
                                        Icon(
                                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else Color.Gray
                                        )
                                    }
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
                                    Text(it, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                }
                                repo.description?.let {
                                    Text(it, color = Color(0xFF888888), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                        LaunchedEffect(repo.htmlUrl) {
                            repoFavoriteViewModel.checkFavorite(repo.htmlUrl)
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