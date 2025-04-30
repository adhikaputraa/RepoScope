package com.adhika.reposcope.presentation.userdetail

import app.cash.turbine.test
import androidx.paging.PagingData
import com.adhika.reposcope.domain.model.GitHubUserDetail
import com.adhika.reposcope.domain.usecase.GetUserDetailUseCase
import com.adhika.reposcope.domain.usecase.GetUserReposPagingUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class UserDetailViewModelTest {
    private lateinit var viewModel: UserDetailViewModel
    private lateinit var getUserDetailUseCase: GetUserDetailUseCase
    private lateinit var getUserReposPagingUseCase: GetUserReposPagingUseCase
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getUserDetailUseCase = mock()
        getUserReposPagingUseCase = mock()
        // Mock paging to avoid null scope error
        whenever(getUserReposPagingUseCase.invoke(any())).thenReturn(flowOf(PagingData.empty()))
        viewModel = UserDetailViewModel(getUserDetailUseCase, getUserReposPagingUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState is Loading then Success when loadUserDetail succeeds`() = testScope.runTest {
        val user = GitHubUserDetail("user1", "User One", "avatar1", 10, 5)
        whenever(getUserDetailUseCase.invoke("user1")).thenReturn(Result.success(user))
        viewModel.uiState.test {
            viewModel.loadUserDetail("user1")
            // Loading state
            assert(awaitItem() is UserDetailUiState.Loading)
            // Success state
            val success = awaitItem()
            assert(success is UserDetailUiState.Success && success.user == user)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState is Loading then Error when loadUserDetail fails`() = testScope.runTest {
        whenever(getUserDetailUseCase.invoke("failuser")).thenReturn(Result.failure(Exception("not found")))
        viewModel.uiState.test {
            viewModel.loadUserDetail("failuser", avatarUrl = "avatarfail")
            // Loading state
            assert(awaitItem() is UserDetailUiState.Loading)
            // Error state
            val error = awaitItem()
            assert(error is UserDetailUiState.Error && error.message == "not found" && error.username == "failuser" && error.avatarUrl == "avatarfail")
            cancelAndIgnoreRemainingEvents()
        }
    }
}
