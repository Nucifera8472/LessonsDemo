package at.nuceria.lessonsdemo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import at.nuceria.lessonsdemo.data.model.LessonEvent

@Database(entities = [LessonEvent::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lessonEventDao(): LessonEventDao
}
