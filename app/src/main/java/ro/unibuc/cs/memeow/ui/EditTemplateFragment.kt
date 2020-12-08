package ro.unibuc.cs.memeow.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ro.unibuc.cs.memeow.R
import ro.unibuc.cs.memeow.databinding.FragmentEditTemplateBinding

class EditTemplateFragment : Fragment(R.layout.fragment_edit_template) {

    private var _binding: FragmentEditTemplateBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: EditorViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentEditTemplateBinding.bind(view)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditorViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}