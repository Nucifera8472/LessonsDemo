package at.nuceria.lessonsdemo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LessonEvent(
    @PrimaryKey(autoGenerate = false) val lessonId: Long,
    val startTimeStamp: Long,
    val endTimeStamp: Long,
)
