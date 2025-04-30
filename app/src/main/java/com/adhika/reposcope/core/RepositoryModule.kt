package com.adhika.reposcope.core

import com.adhika.reposcope.data.repository.GitHubRepositoryImpl
import com.adhika.reposcope.domain.repository.GitHubRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindGitHubRepository(
        implementation: GitHubRepositoryImpl
    ): GitHubRepository
}