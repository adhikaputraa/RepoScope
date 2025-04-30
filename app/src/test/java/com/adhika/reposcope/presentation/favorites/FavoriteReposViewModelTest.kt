package com.adhika.reposcope.presentation.favorites

import app.cash.turbine.test
import com.adhika.reposcope.data.local.FavoriteRepoEntity
import com.adhika.reposcope.domain.usecase.GetFavoriteReposUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteReposViewModelTest {
    private lateinit var viewModel: FavoriteReposViewModel
    private lateinit var getFavoriteReposUseCase: GetFavoriteReposUseCase
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getFavoriteReposUseCase = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `favorites emits data from use case`() = testScope.runTest {
        val repoList = listOf(
            FavoriteRepoEntity("url1", "Repo1", "desc1", "Kotlin", 10, false, "owner1"),
            FavoriteRepoEntity("url2", "Repo2", "desc2", "Java", 5, true, "owner2")
        )
        whenever(getFavoriteReposUseCase.invoke()).thenReturn(flowOf(repoList))
        viewModel = FavoriteReposViewModel(getFavoriteReposUseCase)
        viewModel.favorites.test {
            assertEquals(emptyList<FavoriteRepoEntity>(), awaitItem()) // initial emission
            assertEquals(repoList, awaitItem()) // actual data from use case
            cancelAndIgnoreRemainingEvents()
        }
    }
}
