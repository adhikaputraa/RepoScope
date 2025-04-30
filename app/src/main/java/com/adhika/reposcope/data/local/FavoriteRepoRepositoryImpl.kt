package com.adhika.reposcope.data.local

import com.adhika.reposcope.domain.repository.FavoriteRepoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoriteRepoRepositoryImpl @Inject constructor(
    private val dao: FavoriteRepoDao
) : FavoriteRepoRepository {
    override fun getAllFavorites() = dao.getAllFavorites()
    override suspend fun addFavorite(repo: FavoriteRepoEntity) = dao.insertFavorite(repo)
    override suspend fun removeFavorite(repo: FavoriteRepoEntity) = dao.deleteFavorite(repo)
    override suspend fun isFavorite(htmlUrl: String) = dao.isFavorite(htmlUrl)
}
