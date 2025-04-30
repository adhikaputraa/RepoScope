package com.adhika.reposcope.domain.usecase

import com.adhika.reposcope.data.local.FavoriteRepoEntity
import com.adhika.reposcope.domain.repository.FavoriteRepoRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class FavoriteRepoUseCasesTest {
    private lateinit var repository: FavoriteRepoRepository

    @Before
    fun setup() {
        repository = mock()
    }

    @Test
    fun `GetFavoriteReposUseCase returns flow from repository`() = runTest {
        val repoList = listOf(FavoriteRepoEntity("url1", "Repo1", "desc1", "Kotlin", 10, false, "owner1"))
        whenever(repository.getAllFavorites()).thenReturn(flowOf(repoList))
        val useCase = GetFavoriteReposUseCase(repository)
        val result = useCase().single()
        assertEquals(repoList, result)
    }

    @Test
    fun `AddFavoriteRepoUseCase calls repository`() = runTest {
        val repo = FavoriteRepoEntity("url1", "Repo1", "desc1", "Kotlin", 10, false, "owner1")
        val useCase = AddFavoriteRepoUseCase(repository)
        useCase(repo)
        verify(repository).addFavorite(repo)
    }

    @Test
    fun `RemoveFavoriteRepoUseCase calls repository`() = runTest {
        val repo = FavoriteRepoEntity("url1", "Repo1", "desc1", "Kotlin", 10, false, "owner1")
        val useCase = RemoveFavoriteRepoUseCase(repository)
        useCase(repo)
        verify(repository).removeFavorite(repo)
    }

    @Test
    fun `IsFavoriteRepoUseCase calls repository`() = runTest {
        val useCase = IsFavoriteRepoUseCase(repository)
        whenever(repository.isFavorite("url1")).thenReturn(true)
        val result = useCase("url1")
        assertEquals(true, result)
    }
}
