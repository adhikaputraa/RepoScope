package com.adhika.reposcope.core

import android.content.Context
import androidx.room.Room
import com.adhika.reposcope.data.local.AppDatabase
import com.adhika.reposcope.data.local.FavoriteRepoDao
import com.adhika.reposcope.data.local.FavoriteRepoRepositoryImpl
import com.adhika.reposcope.data.repository.GitHubRepositoryImpl
import com.adhika.reposcope.domain.repository.FavoriteRepoRepository
import com.adhika.reposcope.domain.repository.GitHubRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.Provides
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindModule {
    @Binds
    @Singleton
    abstract fun bindGitHubRepository(
        implementation: GitHubRepositoryImpl
    ): GitHubRepository
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryProvideModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "reposcope-db").build()

    @Provides
    fun provideFavoriteRepoDao(db: AppDatabase) = db.favoriteRepoDao()

    @Provides
    fun provideFavoriteRepoRepository(dao: FavoriteRepoDao): FavoriteRepoRepository =
        FavoriteRepoRepositoryImpl(dao)
}