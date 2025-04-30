package com.adhika.reposcope.domain.usecase

import com.adhika.reposcope.domain.repository.GitHubRepository

class GetUserReposPagingUseCase(private val repository: GitHubRepository) {
    operator fun invoke(username: String) = repository.getUserReposPaging(username)
}
