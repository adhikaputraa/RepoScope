package com.adhika.reposcope.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.adhika.reposcope.data.paging.UserReposPagingSource
import com.adhika.reposcope.data.paging.UserSearchPagingSource
import com.adhika.reposcope.data.remote.GitHubApi
import com.adhika.reposcope.data.remote.toDomain
import com.adhika.reposcope.domain.GitHubRepo
import com.adhika.reposcope.domain.GitHubRepository
import com.adhika.reposcope.domain.GitHubUser
import com.adhika.reposcope.domain.GitHubUserDetail
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GitHubRepositoryImpl @Inject constructor(
    private val api: GitHubApi
) : GitHubRepository {

    override suspend fun searchUsers(query: String): Result<List<GitHubUser>> {
        return runCatching {
            api.searchUsers(query).users.map { it.toDomain() }
        }
    }

    override fun searchUsersPaging(query: String): Flow<PagingData<GitHubUser>> {
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UserSearchPagingSource(api, query) }
        ).flow
    }

    override suspend fun getUserDetail(username: String): Result<GitHubUserDetail> {
        return runCatching {
            api.getUserDetail(username).toDomain()
        }
    }

    override suspend fun getUserRepos(username: String): Result<List<GitHubRepo>> {
        return runCatching {
            api.getUserRepos(username)
                .filter { !it.isForked }
                .map { it.toDomain() }
        }
    }

    override fun getUserReposPaging(username: String): Flow<PagingData<GitHubRepo>> {
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UserReposPagingSource(api, username) }
        ).flow
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 30
    }
}