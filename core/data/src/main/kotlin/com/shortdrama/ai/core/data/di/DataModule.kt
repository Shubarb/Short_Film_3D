package com.shortdrama.ai.core.data.di

import com.shortdrama.ai.core.data.remote.FilmRemoteDataSource
import com.shortdrama.ai.core.data.remote.FirebaseFilmRemoteDataSource
import com.shortdrama.ai.core.data.repository.FilmRepositoryImpl
import com.shortdrama.ai.core.domain.repository.FilmRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindFilmRemoteDataSource(impl: FirebaseFilmRemoteDataSource): FilmRemoteDataSource

    @Binds
    abstract fun bindFilmRepository(impl: FilmRepositoryImpl): FilmRepository
}
