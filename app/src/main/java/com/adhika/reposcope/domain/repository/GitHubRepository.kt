// Repository interface for GitHub
package com.adhika.reposcope.domain.repository

import androidx.paging.PagingData
import com.adhika.reposcope.domain.model.GitHubUser
import com.adhika.reposcope.domain.model.GitHubUserDetail
import com.adhika.reposcope.domain.model.GitHubRepo
import kotlinx.coroutines.flow.Flow

interface GitHubRepository {
    suspend fun searchUsers(query: String): Result<List<GitHubUser>>
    fun searchUsersPaging(query: String): Flow<PagingData<GitHubUser>>
    suspend fun getUserDetail(username: String): Result<GitHubUserDetail>
    suspend fun getUserRepos(username: String): Result<List<GitHubRepo>>
    fun getUserReposPaging(username: String, language: String? = null): Flow<PagingData<GitHubRepo>>
}
