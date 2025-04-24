package com.adhika.reposcope.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface GitHubRepository {
    suspend fun searchUsers(query: String): Result<List<GitHubUser>>
    fun searchUsersPaging(query: String): Flow<PagingData<GitHubUser>>
    suspend fun getUserDetail(username: String): Result<GitHubUserDetail>
    suspend fun getUserRepos(username: String): Result<List<GitHubRepo>>
    fun getUserReposPaging(username: String): Flow<PagingData<GitHubRepo>>
}