package at.nuceria.lessonsdemo.data.remote

import at.nuceria.lessonsdemo.data.remote.response.LessonsResponse
import retrofit2.http.GET

interface LessonsService {

    @GET("/api/lessons")
    suspend fun getLessons(): LessonsResponse

}
