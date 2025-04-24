package com.adhika.reposcope.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.adhika.reposcope.data.remote.GitHubApi
import com.adhika.reposcope.data.remote.toDomain
import com.adhika.reposcope.domain.GitHubUser

class UserSearchPagingSource(
    private val api: GitHubApi,
    private val query: String
) : PagingSource<Int, GitHubUser>() {

    override fun getRefreshKey(state: PagingState<Int, GitHubUser>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GitHubUser> {
        val page = params.key ?: 1
        return try {
            val response = api.searchUsers(query, page, params.loadSize)
            val users = response.users.map { it.toDomain() }
            
            LoadResult.Page(
                data = users,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (users.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}