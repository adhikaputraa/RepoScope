package com.adhika.reposcope.di

import com.adhika.reposcope.domain.repository.GitHubRepository
import com.adhika.reposcope.domain.usecase.GetUserDetailUseCase
import com.adhika.reposcope.domain.usecase.GetUserReposUseCase
import com.adhika.reposcope.domain.usecase.SearchUsersUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Singleton
    fun provideSearchUsersUseCase(repository: GitHubRepository): SearchUsersUseCase =
        SearchUsersUseCase(repository)

    @Provides
    @Singleton
    fun provideGetUserDetailUseCase(repository: GitHubRepository): GetUserDetailUseCase =
        GetUserDetailUseCase(repository)

    @Provides
    @Singleton
    fun provideGetUserReposUseCase(repository: GitHubRepository): GetUserReposUseCase =
        GetUserReposUseCase(repository)
}
