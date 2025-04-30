package com.adhika.reposcope.domain.usecase

import com.adhika.reposcope.data.local.FavoriteRepoEntity
import com.adhika.reposcope.domain.repository.FavoriteRepoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteReposUseCase @Inject constructor(
    private val repository: FavoriteRepoRepository
) {
    operator fun invoke(): Flow<List<FavoriteRepoEntity>> = repository.getAllFavorites()
}

class AddFavoriteRepoUseCase @Inject constructor(
    private val repository: FavoriteRepoRepository
) {
    suspend operator fun invoke(repo: FavoriteRepoEntity) = repository.addFavorite(repo)
}

class RemoveFavoriteRepoUseCase @Inject constructor(
    private val repository: FavoriteRepoRepository
) {
    suspend operator fun invoke(repo: FavoriteRepoEntity) = repository.removeFavorite(repo)
}

class IsFavoriteRepoUseCase @Inject constructor(
    private val repository: FavoriteRepoRepository
) {
    suspend operator fun invoke(htmlUrl: String) = repository.isFavorite(htmlUrl)
}
