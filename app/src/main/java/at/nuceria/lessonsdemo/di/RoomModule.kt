package at.nuceria.lessonsdemo.di

import android.content.Context
import androidx.room.Room
import at.nuceria.lessonsdemo.data.local.AppDatabase
import at.nuceria.lessonsdemo.data.local.LessonEventDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Singleton
    @Provides
    internal fun providesRoomDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "app-db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providesLessonEventDao(appDatabase: AppDatabase): LessonEventDao {
        return appDatabase.lessonEventDao()
    }

}
