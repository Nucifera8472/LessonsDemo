package at.nuceria.lessonsdemo.ui.main

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import at.nuceria.lessonsdemo.R
import at.nuceria.lessonsdemo.data.Resource
import at.nuceria.lessonsdemo.data.managers.NoLessonsAvailableError
import at.nuceria.lessonsdemo.data.remote.response.Lesson
import at.nuceria.lessonsdemo.data.remote.response.TextBlock
import at.nuceria.lessonsdemo.databinding.MainFragmentBinding
import at.nuceria.lessonsdemo.ui.MainViewModel
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

    private var expectedInputText = ""

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
                viewModel.getNextUnfinishedLesson()
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
            clearLessonView()
            val inputTextBlockIndex = data.getInputTextBlockIndex()
            toggleButtonState(inputTextBlockIndex == -1)
            val referencedIds = IntArray(data.content.size)
            for (i in data.content.indices) {
                val textBlock = data.content[i]
                if (i == inputTextBlockIndex)
                    expectedInputText = textBlock.text
                val textView = createTextView(textBlock, i == inputTextBlockIndex)
                textView.id = View.generateViewId()
                root.addView(textView)
                referencedIds[i] = textView.id
            }
            textFlowView.referencedIds = referencedIds
            button.setOnClickListener {
                viewModel.trackLessonFinished(data.id)
            }
        }
    }

    private fun clearLessonView() {
        val ids = binding.lessonView.textFlowView.referencedIds
        ids.forEach {
            val view = binding.root.findViewById<View>(it)
            binding.lessonView.root.removeView(view)
        }
    }

    private fun toggleButtonState(enabled: Boolean) {
        binding.lessonView.button.isEnabled = enabled
        val color = if (enabled) R.color.secondaryColor
        else R.color.primaryColor
        context?.let { context ->
            binding.lessonView.button.setTextColor(ContextCompat.getColor(context, color))
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val enteredText = p0?.toString()
            toggleButtonState(enteredText == expectedInputText)
        }

        override fun afterTextChanged(p0: Editable?) {}
    }

    private fun createTextView(textBlock: TextBlock, editable: Boolean): TextView {
        val textView = if (editable) {
            EditText(context).apply {
                addTextChangedListener(textWatcher)
            }
        } else {
            TextView(context).apply {
                text = textBlock.text
            }
        }
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
        textView.setTextColor(Color.parseColor(textBlock.color))
        textView.typeface = Typeface.MONOSPACE
        return textView
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

fun Lesson.getInputTextBlockIndex(): Int {
    if (input == null) return -1
    // the start index of this text block in the full text
    var textBlockStartIndex = 0
    content.forEachIndexed { index, textBlock ->
        if (textBlockStartIndex == (input.startIndex))
            return index

        textBlockStartIndex += textBlock.text.length
    }
    // no valid text block found
    return -1
}
