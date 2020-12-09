package ro.unibuc.cs.memeow.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ro.unibuc.cs.memeow.R
import ro.unibuc.cs.memeow.databinding.FragmentEditTemplateBinding

class EditTemplateFragment : Fragment(R.layout.fragment_edit_template) {

    private var _binding: FragmentEditTemplateBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<EditorViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentEditTemplateBinding.bind(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}