package com.adhika.reposcope.domain.usecase

import androidx.paging.PagingData
import com.adhika.reposcope.domain.model.GitHubRepo
import com.adhika.reposcope.domain.repository.GitHubRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class GetUserReposPagingUseCaseTest {
    private lateinit var repository: GitHubRepository
    private lateinit var useCase: GetUserReposPagingUseCase

    @Before
    fun setup() {
        repository = mock()
        useCase = GetUserReposPagingUseCase(repository)
    }

    @Test
    fun `invoke returns flow from repository`() = runTest {
        val username = "octocat"
        val repoList = listOf(
            GitHubRepo("repo1", "desc1", "Kotlin", 10, "url1", false),
            GitHubRepo("repo2", "desc2", "Java", 5, "url2", true)
        )
        val pagingData = PagingData.from(repoList)
        whenever(repository.getUserReposPaging(username, null)).thenReturn(flowOf(pagingData))

        val resultFlow = useCase(username)
        val result = resultFlow.first()
        // PagingData does not expose items directly, so we check type
        assert(result == pagingData)
    }
}
