package at.nuceria.lessonsdemo.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import at.nuceria.lessonsdemo.databinding.MainFragmentBinding
import at.nuceria.lessonsdemo.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint


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
        observeViewModel()

        return view
    }

    private fun observeViewModel() {
        viewModel.testEndpoint()
    }

}
