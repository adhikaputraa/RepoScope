package com.adhika.reposcope.data.repository

import com.adhika.reposcope.data.remote.GitHubApi
import com.adhika.reposcope.data.remote.GitHubUserSearchResponse
import com.adhika.reposcope.data.remote.GitHubUserDto
import com.adhika.reposcope.domain.model.GitHubUser
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GitHubRepositoryImplTest {
    private lateinit var api: GitHubApi
    private lateinit var repository: GitHubRepositoryImpl

    @Before
    fun setup() {
        api = mock()
        repository = GitHubRepositoryImpl(api)
    }

    @Test
    fun `searchUsers returns mapped users from API`() = runBlocking {
        val apiResponse = GitHubUserSearchResponse(listOf(GitHubUserDto("user1", "avatar1")))
        whenever(api.searchUsers("test", 1, 30)).thenReturn(apiResponse)
        val result = repository.searchUsers("test")
        assertEquals(Result.success(listOf(GitHubUser("user1", "avatar1"))), result)
    }
}
