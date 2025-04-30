package com.adhika.reposcope.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adhika.reposcope.data.local.FavoriteRepoEntity
import com.adhika.reposcope.domain.usecase.GetFavoriteReposUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FavoriteReposViewModel @Inject constructor(
    getFavoriteReposUseCase: GetFavoriteReposUseCase
) : ViewModel() {
    val favorites: StateFlow<List<FavoriteRepoEntity>> =
        getFavoriteReposUseCase().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
}
