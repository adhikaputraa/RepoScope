package com.adhika.reposcope

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.adhika.reposcope.domain.GitHubRepo
import com.adhika.reposcope.domain.GitHubRepository
import com.adhika.reposcope.domain.GitHubUserDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val repository: GitHubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserDetailUiState>(UserDetailUiState.Loading)
    val uiState: StateFlow<UserDetailUiState> = _uiState.asStateFlow()

    private val _username = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val reposFlow = _username
        .filterNotNull()
        .flatMapLatest { username ->
            repository.getUserReposPaging(username)
        }
        .cachedIn(viewModelScope)

    fun loadUserDetail(username: String) {
        viewModelScope.launch {
            _uiState.value = UserDetailUiState.Loading
            try {
                val user = repository.getUserDetail(username).getOrThrow()
                _uiState.value = UserDetailUiState.Success(user)
                _username.value = username // Trigger repo loading
            } catch (e: Exception) {
                _uiState.value = UserDetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class UserDetailUiState {
    object Loading : UserDetailUiState()
    data class Success(val user: GitHubUserDetail) : UserDetailUiState()
    data class Error(val message: String) : UserDetailUiState()
}