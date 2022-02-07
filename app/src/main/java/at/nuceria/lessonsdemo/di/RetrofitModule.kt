package at.nuceria.lessonsdemo.di

import at.nuceria.lessonsdemo.data.remote.LessonsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * This module is responsible for creating the necessary instances for api communication.
 */

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    private const val BASE_URL = "https://mimochallenge.azurewebsites.net"

    @Provides
    @Singleton
    fun provideLessonsService(retrofit: Retrofit): LessonsService {
        return retrofit.create(LessonsService::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        jsonConverterFactory: Converter.Factory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(jsonConverterFactory) // to customize the (de)serialization strategy to use kotlinx serialization
            .client(okHttpClient)
            .build()
    }
}
