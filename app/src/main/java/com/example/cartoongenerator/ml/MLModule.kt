package com.example.cartoongenerator.ml

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MLModule {
    
    @Provides
    @Singleton
    fun provideStyleTransferService(
        @ApplicationContext context: Context
    ): StyleTransferService = StyleTransferService(context)

    @Provides
    @Singleton
    fun provideFaceDetectionService(): FaceDetectionService = FaceDetectionService()
}