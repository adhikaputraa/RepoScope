package com.adhika.reposcope.domain.usecase

import com.adhika.reposcope.domain.model.GitHubUserDetail
import com.adhika.reposcope.domain.repository.GitHubRepository

class GetUserDetailUseCase(private val repository: GitHubRepository) {
    suspend operator fun invoke(username: String) = repository.getUserDetail(username)
}
