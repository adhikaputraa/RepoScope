package com.adhika.reposcope.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteRepoDao {
    @Query("SELECT * FROM favorite_repos")
    fun getAllFavorites(): Flow<List<FavoriteRepoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(repo: FavoriteRepoEntity)

    @Delete
    suspend fun deleteFavorite(repo: FavoriteRepoEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_repos WHERE htmlUrl = :htmlUrl)")
    suspend fun isFavorite(htmlUrl: String): Boolean
}
