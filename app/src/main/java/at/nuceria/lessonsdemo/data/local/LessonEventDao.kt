package at.nuceria.lessonsdemo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import at.nuceria.lessonsdemo.data.model.LessonEvent

@Dao
interface LessonEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: LessonEvent)

    @Query("SELECT * FROM LessonEvent WHERE lessonId = :lessonId")
    fun getEventForId(lessonId: Long): LessonEvent?

    @Query("SELECT lessonId FROM LessonEvent ORDER BY lessonId DESC LIMIT 1")
    fun getLastFinishedLessonId(): Long?

}
