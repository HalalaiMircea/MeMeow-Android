package ro.unibuc.cs.memeow.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ro.unibuc.cs.memeow.R
import ro.unibuc.cs.memeow.databinding.FragmentEditTemplateBinding

class EditTemplateFragment : Fragment(R.layout.fragment_edit_template) {
    private var binding: FragmentEditTemplateBinding? = null

    companion object {
        fun newInstance() = EditTemplateFragment()
    }

    private lateinit var viewModel: EditTemplateViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentEditTemplateBinding.bind(view)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditTemplateViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}