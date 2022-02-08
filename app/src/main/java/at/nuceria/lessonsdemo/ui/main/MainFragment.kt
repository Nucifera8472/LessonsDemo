package at.nuceria.lessonsdemo.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import at.nuceria.lessonsdemo.R
import at.nuceria.lessonsdemo.data.Resource
import at.nuceria.lessonsdemo.data.remote.response.Lesson
import at.nuceria.lessonsdemo.databinding.MainFragmentBinding
import at.nuceria.lessonsdemo.ui.MainViewModel
import at.nuceria.lessonsdemo.ui.NoLessonsAvailableError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber


@AndroidEntryPoint
class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel by activityViewModels<MainViewModel>()

    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        lifecycleScope.launch {
            // repeatOnLifecycle launches the block in a new coroutine every time the
            // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Trigger the flow and start listening for values.
                viewModel.getLesson()
                viewModel.currentLesson.collect { onNewDataReceived(it) }
            }
        }
        return view
    }


    private fun onNewDataReceived(resource: Resource<Lesson?>) {
        Timber.d("onNewDataReceived")
        if (resource.data != null) {
            binding.lessonView.root.visibility = View.VISIBLE
            showLesson(resource.data)
        } else {
            binding.lessonView.root.visibility = View.GONE
        }

        if (resource is Resource.Loading) {
            if (resource.data != null) {
                binding.progressbar.visibility = View.VISIBLE
            } else {
                binding.progressbar.visibility = View.VISIBLE
            }
        } else {
            binding.progressbar.visibility = View.GONE
            binding.progressbar.visibility = View.GONE
        }

        if (resource is Resource.Error && resource.data == null) {
            binding.errorView.root.visibility = View.VISIBLE
            showError(resource.error)
        } else {
            binding.errorView.root.visibility = View.GONE
        }
    }

    private fun showLesson(data: Lesson) {
        binding.lessonView.run {
            root.visibility = View.VISIBLE
            // for initial testing
            textView.text = data.content.joinToString { it.text }
        }
    }

    private fun showError(exception: Throwable?) {
        Timber.e(exception)
        val message = when (exception) {
            is NoLessonsAvailableError -> getString(R.string.no_lessons_available)
            is HttpException -> getString(R.string.fetching_lessons_failed)
            else -> getString(R.string.something_went_wrong)
        }
        showError(message)
    }

    private fun showError(message: String) {
        binding.errorView.message.text = message
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

}
