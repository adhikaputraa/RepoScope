package com.adhika.reposcope.presentation.userdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adhika.reposcope.data.local.FavoriteRepoEntity
import com.adhika.reposcope.domain.model.GitHubRepo
import com.adhika.reposcope.domain.usecase.AddFavoriteRepoUseCase
import com.adhika.reposcope.domain.usecase.IsFavoriteRepoUseCase
import com.adhika.reposcope.domain.usecase.RemoveFavoriteRepoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepoFavoriteViewModel @Inject constructor(
    private val addFavoriteRepoUseCase: AddFavoriteRepoUseCase,
    private val removeFavoriteRepoUseCase: RemoveFavoriteRepoUseCase,
    private val isFavoriteRepoUseCase: IsFavoriteRepoUseCase
) : ViewModel() {
    private val _favoriteState = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val favoriteState: StateFlow<Map<String, Boolean>> = _favoriteState

    fun checkFavorite(htmlUrl: String) {
        viewModelScope.launch {
            val isFav = isFavoriteRepoUseCase(htmlUrl)
            _favoriteState.value = _favoriteState.value.toMutableMap().apply { put(htmlUrl, isFav) }
        }
    }

    fun addFavorite(repo: GitHubRepo, owner: String) {
        viewModelScope.launch {
            addFavoriteRepoUseCase(
                FavoriteRepoEntity(
                    htmlUrl = repo.htmlUrl,
                    name = repo.name,
                    description = repo.description,
                    language = repo.language,
                    stars = repo.stars,
                    isForked = repo.isForked,
                    owner = owner
                )
            )
            checkFavorite(repo.htmlUrl)
        }
    }

    fun removeFavorite(htmlUrl: String) {
        viewModelScope.launch {
            removeFavoriteRepoUseCase(
                FavoriteRepoEntity(
                    htmlUrl = htmlUrl,
                    name = "", description = null, language = null, stars = 0, isForked = false, owner = ""
                )
            )
            checkFavorite(htmlUrl)
        }
    }
}
