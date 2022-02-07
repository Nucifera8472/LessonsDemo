package at.nuceria.lessonsdemo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.nuceria.lessonsdemo.data.remote.LessonsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val lessonsService: LessonsService) : ViewModel() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }

    fun testEndpoint() = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
        val lessons = lessonsService.getLessons()
    }

}
