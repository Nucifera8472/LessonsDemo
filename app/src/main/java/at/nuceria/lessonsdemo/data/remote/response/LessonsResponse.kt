package at.nuceria.lessonsdemo.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LessonsResponse(
    val lessons: List<Lesson>
)

@Serializable
data class Lesson(
    val id: Long,
    val content: List<TextBlock>,
    @SerialName("input")
    val input: InputRange? = null
)

/**
 * @param color Text color to display the text in
 */
@Serializable
data class TextBlock(
    val color: String,
    val text: String
)

@Serializable
data class InputRange(
    val startIndex: Int,
    @SerialName("endIndex")
    val endIndexInclusive: Int
)
