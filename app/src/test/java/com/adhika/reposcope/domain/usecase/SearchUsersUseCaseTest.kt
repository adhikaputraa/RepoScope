package com.adhika.reposcope.domain.usecase

import androidx.paging.PagingData
import com.adhika.reposcope.domain.model.GitHubUser
import com.adhika.reposcope.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class SearchUsersUseCaseTest {
    private lateinit var repository: GitHubRepository
    private lateinit var useCase: SearchUsersUseCase

    @Before
    fun setup() {
        repository = mock()
        useCase = SearchUsersUseCase(repository)
    }

    @Test
    fun `invoke returns success from repository`() = runTest {
        val users = listOf(GitHubUser("user1", "avatar1"))
        whenever(repository.searchUsers("test")).thenReturn(Result.success(users))
        val result = useCase.invoke("test")
        assertEquals(Result.success(users), result)
    }

    @Test
    fun `paging returns flow from repository`() = runTest {
        val flow = flowOf<PagingData<GitHubUser>>()
        whenever(repository.searchUsersPaging("test")).thenReturn(flow)
        val result = useCase.paging("test")
        assertEquals(flow, result)
    }
}
