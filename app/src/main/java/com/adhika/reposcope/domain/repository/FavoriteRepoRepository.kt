package com.adhika.reposcope.domain.repository

import com.adhika.reposcope.data.local.FavoriteRepoEntity
import kotlinx.coroutines.flow.Flow

interface FavoriteRepoRepository {
    fun getAllFavorites(): Flow<List<FavoriteRepoEntity>>
    suspend fun addFavorite(repo: FavoriteRepoEntity)
    suspend fun removeFavorite(repo: FavoriteRepoEntity)
    suspend fun isFavorite(htmlUrl: String): Boolean
}
