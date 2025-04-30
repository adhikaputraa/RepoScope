package com.adhika.reposcope.domain.usecase

import com.adhika.reposcope.domain.repository.GitHubRepository
import androidx.paging.PagingData
import com.adhika.reposcope.domain.model.GitHubUser
import kotlinx.coroutines.flow.Flow

class SearchUsersUseCase(private val repository: GitHubRepository) {
    suspend operator fun invoke(query: String) = repository.searchUsers(query)
    fun paging(query: String): Flow<PagingData<GitHubUser>> = repository.searchUsersPaging(query)
}
