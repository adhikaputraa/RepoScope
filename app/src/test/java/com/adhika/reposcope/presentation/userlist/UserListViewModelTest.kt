package com.adhika.reposcope.presentation.userlist

import app.cash.turbine.test
import com.adhika.reposcope.domain.model.GitHubUser
import com.adhika.reposcope.domain.usecase.SearchUsersUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class UserListViewModelTest {
    private lateinit var viewModel: UserListViewModel
    private lateinit var searchUsersUseCase: SearchUsersUseCase
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        searchUsersUseCase = mock()
        viewModel = UserListViewModel(searchUsersUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onSearchQueryChanged triggers search and updates activeQuery on success`() = testScope.runTest {
        val users = listOf(GitHubUser("user1", "avatar1"), GitHubUser("user2", "avatar2"))
        whenever(searchUsersUseCase.invoke("test")).thenReturn(Result.success(users))
        whenever(searchUsersUseCase.paging("test")).thenReturn(flowOf())

        viewModel.onSearchQueryChanged("test")
        advanceUntilIdle()
        assertEquals("test", viewModel.activeQuery.value)
        assertEquals(null, viewModel.errorMessage.value)
    }

    @Test
    fun `onSearchQueryChanged sets errorMessage on failure`() = testScope.runTest {
        whenever(searchUsersUseCase.invoke("fail")).thenReturn(Result.failure(Exception("error")))
        whenever(searchUsersUseCase.paging("fail")).thenReturn(flowOf())

        viewModel.onSearchQueryChanged("fail")
        advanceUntilIdle()
        assertEquals("error", viewModel.errorMessage.value)
    }

    @Test
    fun `onSearchQueryChanged with blank query resets activeQuery and errorMessage`() = testScope.runTest {
        viewModel.onSearchQueryChanged("")
        advanceUntilIdle()
        assertEquals("", viewModel.activeQuery.value)
        assertEquals(null, viewModel.errorMessage.value)
    }

    @Test
    fun `retry does nothing when pendingQuery is blank`() = testScope.runTest {
        viewModel.onSearchQueryChanged("")
        advanceUntilIdle()
        // Should not call use case, so errorMessage remains null
        viewModel.retry()
        advanceUntilIdle()
        assertEquals(null, viewModel.errorMessage.value)
    }

    @Test
    fun `retry triggers search and updates activeQuery on success`() = testScope.runTest {
        val users = listOf(GitHubUser("user1", "avatar1"))
        whenever(searchUsersUseCase.invoke("retrytest")).thenReturn(Result.success(users))
        whenever(searchUsersUseCase.paging("retrytest")).thenReturn(flowOf())
        viewModel.onSearchQueryChanged("retrytest")
        advanceUntilIdle()
        // Set error to simulate previous failure
        viewModel.onSearchQueryChanged("fail")
        advanceUntilIdle()
        // Now retry with the last pending query ("fail")
        whenever(searchUsersUseCase.invoke("fail")).thenReturn(Result.success(users))
        viewModel.retry()
        advanceUntilIdle()
        assertEquals("fail", viewModel.activeQuery.value)
        assertEquals(null, viewModel.errorMessage.value)
    }

    @Test
    fun `retry sets errorMessage on failure`() = testScope.runTest {
        viewModel.onSearchQueryChanged("fail-again")
        advanceUntilIdle()
        whenever(searchUsersUseCase.invoke("fail-again")).thenReturn(Result.failure(Exception("retry error")))
        viewModel.retry()
        advanceUntilIdle()
        assertEquals("retry error", viewModel.errorMessage.value)
    }
}
