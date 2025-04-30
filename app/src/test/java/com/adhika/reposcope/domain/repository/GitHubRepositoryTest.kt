package com.adhika.reposcope.domain.repository

import com.adhika.reposcope.domain.model.GitHubUser
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GitHubRepositoryTest {
    private val repository = mock<GitHubRepository>()

    @Test
    fun `searchUsers returns expected result`() = runBlocking {
        val users = listOf(GitHubUser("user1", "avatar1"))
        whenever(repository.searchUsers("test")).thenReturn(Result.success(users))
        val result = repository.searchUsers("test")
        assertEquals(Result.success(users), result)
    }
}
