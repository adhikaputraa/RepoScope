package com.adhika.reposcope.domain.usecase

import com.adhika.reposcope.domain.model.GitHubUserDetail
import com.adhika.reposcope.domain.repository.GitHubRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class GetUserDetailUseCaseTest {
    private lateinit var repository: GitHubRepository
    private lateinit var useCase: GetUserDetailUseCase
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        kotlinx.coroutines.Dispatchers.setMain(testDispatcher)
        repository = mock()
        useCase = GetUserDetailUseCase(repository)
    }

    @After
    fun tearDown() {
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    @Test
    fun `invoke returns success when repository returns user detail`() = testScope.runTest {
        val detail = GitHubUserDetail("user1", "User One", "avatar1", 10, 5)
        whenever(repository.getUserDetail("user1")).thenReturn(Result.success(detail))
        val result = useCase.invoke("user1")
        assertTrue(result.isSuccess)
        assertEquals(detail, result.getOrNull())
    }

    @Test
    fun `invoke returns failure when repository returns error`() = testScope.runTest {
        val exception = Exception("not found")
        whenever(repository.getUserDetail("user2")).thenReturn(Result.failure(exception))
        val result = useCase.invoke("user2")
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
