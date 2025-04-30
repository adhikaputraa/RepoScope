package com.adhika.reposcope.presentation.userlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.adhika.reposcope.domain.model.GitHubUser
import com.adhika.reposcope.domain.usecase.SearchUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val searchUsersUseCase: SearchUsersUseCase
) : ViewModel() {
    private val _pendingQuery = MutableStateFlow("")
    val pendingQuery: StateFlow<String> = _pendingQuery

    private val _activeQuery = MutableStateFlow("")
    val activeQuery: StateFlow<String> = _activeQuery

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingDataFlow: Flow<PagingData<GitHubUser>> = _activeQuery
        .filter { it.isNotBlank() }
        .flatMapLatest { query ->
            searchUsersUseCase.paging(query)
        }
        .cachedIn(viewModelScope)

    fun onSearchQueryChanged(query: String) {
        _pendingQuery.value = query
        if (query.isBlank()) {
            _activeQuery.value = ""
            _errorMessage.value = null
        } else {
            trySearch(query)
        }
    }

    private fun trySearch(query: String) {
        viewModelScope.launch {
            val result = searchUsersUseCase.invoke(query)
            if (result.isSuccess) {
                _activeQuery.value = query
                _errorMessage.value = null
            } else {
                _errorMessage.value = result.exceptionOrNull()?.localizedMessage ?: "Unknown error"
            }
        }
    }

    fun retry() {
        val query = _pendingQuery.value
        if (query.isNotBlank()) {
            trySearch(query)
        }
    }
}
