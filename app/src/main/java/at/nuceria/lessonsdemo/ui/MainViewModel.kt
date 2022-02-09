package at.nuceria.lessonsdemo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.nuceria.lessonsdemo.data.Resource
import at.nuceria.lessonsdemo.data.managers.LessonsRepository
import at.nuceria.lessonsdemo.data.remote.response.Lesson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val lessonsRepository: LessonsRepository) :
    ViewModel() {

    private var lessonStartedTimeStamp: Long? = null

    // prevent state updates from other classes
    private val _currentLesson = MutableStateFlow<Resource<Lesson?>>(Resource.Loading(null))

    // the state flow data is can be collected by all fragments that share this view model without
    // launching the whole flow again because it is retained in the fragment scope. Data is also
    // retained across configuration changes
    val currentLesson: StateFlow<Resource<Lesson?>> = _currentLesson

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }

    fun getNextUnfinishedLesson() =
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _currentLesson.value = Resource.Loading(null)
            val lesson = lessonsRepository.getNextUnfinishedLesson()
            _currentLesson.value = if (lesson != null) {
                lessonStartedTimeStamp = System.currentTimeMillis()
                Resource.Success(lesson)
            } else {
                Resource.AllDone()
            }
        }

    fun trackLessonFinished(id: Long) =
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val now = System.currentTimeMillis()
            lessonsRepository.trackFinishedLesson(id, lessonStartedTimeStamp ?: now, now)
            getNextUnfinishedLesson()
        }
}
