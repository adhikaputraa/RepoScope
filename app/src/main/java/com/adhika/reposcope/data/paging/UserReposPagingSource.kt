package com.adhika.reposcope.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.adhika.reposcope.data.remote.GitHubApi
import com.adhika.reposcope.data.remote.toDomain
import com.adhika.reposcope.domain.model.GitHubRepo

class UserReposPagingSource(
    private val api: GitHubApi,
    private val username: String,
    private val language: String? = null
) : PagingSource<Int, GitHubRepo>() {

    override fun getRefreshKey(state: PagingState<Int, GitHubRepo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GitHubRepo> {
        val page = params.key ?: 1
        return try {
            val response = api.getUserRepos(username, page, params.loadSize)
            val repos = response
                .filter { !it.isForked }
                .filter { language.isNullOrEmpty() || it.language == language }
                .map { it.toDomain() }
            LoadResult.Page(
                data = repos,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (repos.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}