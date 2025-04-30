package com.adhika.reposcope.presentation.userdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.cachedIn
import androidx.recyclerview.widget.DiffUtil
import com.adhika.reposcope.domain.model.GitHubRepo
import com.adhika.reposcope.domain.model.GitHubUserDetail
import com.adhika.reposcope.domain.usecase.GetUserDetailUseCase
import com.adhika.reposcope.domain.usecase.GetUserReposPagingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val getUserDetailUseCase: GetUserDetailUseCase,
    private val getUserReposPagingUseCase: GetUserReposPagingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserDetailUiState>(UserDetailUiState.Loading)
    val uiState: StateFlow<UserDetailUiState> = _uiState

    private val _username = MutableStateFlow<String?>(null)

    private val differ = AsyncPagingDataDiffer(
        diffCallback = object : DiffUtil.ItemCallback<GitHubRepo>() {
            override fun areItemsTheSame(oldItem: GitHubRepo, newItem: GitHubRepo): Boolean = oldItem.htmlUrl == newItem.htmlUrl
            override fun areContentsTheSame(oldItem: GitHubRepo, newItem: GitHubRepo): Boolean = oldItem == newItem
        },
        updateCallback = object : androidx.recyclerview.widget.ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {}
            override fun onRemoved(position: Int, count: Int) {}
            override fun onMoved(fromPosition: Int, toPosition: Int) {}
            override fun onChanged(position: Int, count: Int, payload: Any?) {}
        },
        mainDispatcher = Dispatchers.Main,
        workerDispatcher = Dispatchers.Default
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val reposFlow = _username
        .filterNotNull()
        .flatMapLatest { username ->
            getUserReposPagingUseCase.invoke(username).cachedIn(viewModelScope)
        }

    init {
        viewModelScope.launch {
            reposFlow.collectLatest { pagingData ->
                differ.submitData(pagingData)
            }
        }
    }

    fun loadUserDetail(username: String, avatarUrl: String? = null) {
        viewModelScope.launch {
            _uiState.value = UserDetailUiState.Loading
            try {
                val user = getUserDetailUseCase.invoke(username).getOrThrow()
                _uiState.value = UserDetailUiState.Success(user)
                _username.value = username
            } catch (e: Exception) {
                _uiState.value = UserDetailUiState.Error(e.message ?: "Unknown error", username, avatarUrl)
            }
        }
    }
}

sealed class UserDetailUiState {
    object Loading : UserDetailUiState()
    data class Success(val user: GitHubUserDetail) : UserDetailUiState()
    data class Error(val message: String, val username: String, val avatarUrl: String?) : UserDetailUiState()
}