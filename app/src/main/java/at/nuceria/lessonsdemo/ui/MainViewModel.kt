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

    // this will be fetched from the db later
    private var lastFinishedLesson = 6

    // prevent state updates from other classes
    private val _currentLesson = MutableStateFlow<Resource<Lesson?>>(Resource.Loading(null))

    // the state flow data is can be collected by all fragments that share this view model without
    // launching the whole flow again because it is retained in the fragment scope. Data is also
    // retained across configuration changes
    val currentLesson: StateFlow<Resource<Lesson?>> = _currentLesson

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }

    fun getLesson() = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
        val lessons = lessonsRepository.getLessons()
        if (lessons.isEmpty()) {
            _currentLesson.value = Resource.Error(NoLessonsAvailableError())
            return@launch
        }
        val nextUnfinishedLesson = lessons.firstOrNull { it.id > lastFinishedLesson }
        _currentLesson.value = if (nextUnfinishedLesson != null) {
            Resource.Success(nextUnfinishedLesson)
        } else {
            Resource.AllDone()
        }
    }
}

class NoLessonsAvailableError : Throwable()
