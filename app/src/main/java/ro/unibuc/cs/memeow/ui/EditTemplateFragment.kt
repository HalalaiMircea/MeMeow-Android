package ro.unibuc.cs.memeow.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import ro.unibuc.cs.memeow.R
import ro.unibuc.cs.memeow.databinding.FragmentEditTemplateBinding

class EditTemplateFragment : Fragment(R.layout.fragment_edit_template) {
    private var _binding: FragmentEditTemplateBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<EditorViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentEditTemplateBinding.bind(view)
        val fullSizeImgUrl = viewModel.currentTemplate!!.url

        // Load the currently selected template from the view model
        Glide.with(this)
            .load(GlideUrl(fullSizeImgUrl, TemplateListFragment.RecyclerAdapter.headers))
            .centerCrop()
            .error(R.drawable.ic_baseline_broken_image_24)
            .into(binding.imgMeme)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "EditTemplateFragment"
    }
}