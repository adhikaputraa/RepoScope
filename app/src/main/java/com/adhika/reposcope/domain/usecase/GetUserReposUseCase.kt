package com.adhika.reposcope.domain.usecase

import com.adhika.reposcope.domain.model.GitHubRepo
import com.adhika.reposcope.domain.repository.GitHubRepository
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

class GetUserReposUseCase(private val repository: GitHubRepository) {
    suspend operator fun invoke(username: String) = repository.getUserRepos(username)
    fun paging(username: String, language: String? = null): Flow<PagingData<GitHubRepo>> = repository.getUserReposPaging(username, language)
}
