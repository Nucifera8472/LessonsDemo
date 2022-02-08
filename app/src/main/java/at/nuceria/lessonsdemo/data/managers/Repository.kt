package at.nuceria.lessonsdemo.data.managers

import at.nuceria.lessonsdemo.data.remote.LessonsService
import at.nuceria.lessonsdemo.data.remote.response.Lesson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LessonsRepository @Inject constructor(private val lessonsService: LessonsService) {

    // this should be cached in a database, then we could immediately fetch the next unfinished
    // lesson from the db with the respective query without keeping all lessons in memory
    private var lessons: List<Lesson> = emptyList()

    suspend fun getLessons(): List<Lesson> {
        if (lessons.isEmpty())
            lessons = fetchLessons()

        return lessons
    }

    private suspend fun fetchLessons(): List<Lesson> {
        return lessonsService.getLessons().lessons
    }

}
