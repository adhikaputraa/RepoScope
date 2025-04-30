package com.adhika.reposcope.data.local

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

class FavoriteRepoRepositoryImplTest {
    private lateinit var dao: FavoriteRepoDao
    private lateinit var repository: FavoriteRepoRepository

    @Before
    fun setup() {
        dao = mock()
        repository = FavoriteRepoRepositoryImpl(dao)
    }

    @Test
    fun `getAllFavorites delegates to dao`() = runTest {
        val repoList = listOf(FavoriteRepoEntity("url1", "Repo1", "desc1", "Kotlin", 10, false, "owner1"))
        whenever(dao.getAllFavorites()).thenReturn(flowOf(repoList))
        val result = repository.getAllFavorites().single()
        assertEquals(repoList, result)
    }

    @Test
    fun `addFavorite delegates to dao`() = runTest {
        val repo = FavoriteRepoEntity("url1", "Repo1", "desc1", "Kotlin", 10, false, "owner1")
        repository.addFavorite(repo)
        verify(dao).insertFavorite(repo)
    }

    @Test
    fun `removeFavorite delegates to dao`() = runTest {
        val repo = FavoriteRepoEntity("url1", "Repo1", "desc1", "Kotlin", 10, false, "owner1")
        repository.removeFavorite(repo)
        verify(dao).deleteFavorite(repo)
    }

    @Test
    fun `isFavorite delegates to dao`() = runTest {
        whenever(dao.isFavorite("url1")).thenReturn(true)
        val result = repository.isFavorite("url1")
        assertEquals(true, result)
    }
}
