package com.adhika.reposcope.presentation.userdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.adhika.reposcope.domain.model.GitHubRepo
import com.adhika.reposcope.domain.model.GitHubUserDetail
import com.adhika.reposcope.domain.usecase.GetUserDetailUseCase
import com.adhika.reposcope.domain.usecase.GetUserReposUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val getUserDetailUseCase: GetUserDetailUseCase,
    private val getUserReposUseCase: GetUserReposUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserDetailUiState>(UserDetailUiState.Loading)
    val uiState: StateFlow<UserDetailUiState> = _uiState

    private val _username = MutableStateFlow<String?>(null)
    private val _selectedLanguage = MutableStateFlow<String?>(null)
    val selectedLanguage: StateFlow<String?> = _selectedLanguage

    private val _languages = MutableStateFlow<List<String>>(emptyList())
    val languages: StateFlow<List<String>> = _languages

    @OptIn(ExperimentalCoroutinesApi::class)
    val reposFlow = _username
        .filterNotNull()
        .flatMapLatest { username ->
            _selectedLanguage.flatMapLatest { language ->
                getUserReposUseCase.paging(username, language).cachedIn(viewModelScope)
            }
        }

    fun loadUserDetail(username: String, avatarUrl: String? = null) {
        viewModelScope.launch {
            _uiState.value = UserDetailUiState.Loading
            try {
                val user = getUserDetailUseCase.invoke(username).getOrThrow()
                _uiState.value = UserDetailUiState.Success(user)
                _username.value = username
                // Optionally, update languages list here if needed
            } catch (e: Exception) {
                _uiState.value = UserDetailUiState.Error(e.message ?: "Unknown error", username, avatarUrl)
            }
        }
    }

    fun onLanguageSelected(language: String?) {
        _selectedLanguage.value = language
    }
}

sealed class UserDetailUiState {
    object Loading : UserDetailUiState()
    data class Success(val user: GitHubUserDetail) : UserDetailUiState()
    data class Error(val message: String, val username: String, val avatarUrl: String?) : UserDetailUiState()
}

fun UserDetailUiState.errorWithUsername(message: String, username: String, avatarUrl: String?): UserDetailUiState.Error {
    return UserDetailUiState.Error(message, username, avatarUrl)
}
