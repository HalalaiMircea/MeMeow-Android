package ro.unibuc.cs.memeow.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dagger.hilt.android.AndroidEntryPoint
import ro.unibuc.cs.memeow.R
import ro.unibuc.cs.memeow.databinding.FragmentTemplateListBinding
import ro.unibuc.cs.memeow.databinding.LayoutTemplateItemBinding
import ro.unibuc.cs.memeow.model.MemeTemplate

@AndroidEntryPoint
class TemplateListFragment : Fragment(R.layout.fragment_template_list) {
    private var _binding: FragmentTemplateListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditorViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentTemplateListBinding.bind(view)

        val templateList = binding.templateList
        val adapter = RecyclerAdapter(viewModel)
        templateList.layoutManager = GridLayoutManager(context, 2)
        templateList.adapter = adapter
        templateList.setHasFixedSize(true)

        viewModel.templates.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class RecyclerAdapter(private val viewModel: EditorViewModel) :
        PagingDataAdapter<MemeTemplate, RecyclerAdapter.ViewHolder>(DIFF_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = LayoutTemplateItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return ViewHolder(binding, viewModel)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val currentItem = getItem(position)
            if (currentItem != null) {
                holder.bind(currentItem)
            }
        }

        class ViewHolder(
            private val binding: LayoutTemplateItemBinding,
            private val viewModel: EditorViewModel
        ) : RecyclerView.ViewHolder(binding.root) {

            private lateinit var template: MemeTemplate

            init {
                binding.root.setOnClickListener {
                    viewModel.currentTemplate = template
                    val action = TemplateListFragmentDirections.actionSelectTemplate()
                    it.findNavController().navigate(action)
                }
            }

            fun bind(template: MemeTemplate) {
                Glide.with(itemView)
                    .load(template.imageUrl)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_baseline_broken_image_24)
                    .into(binding.templateImg)
                binding.templateTitle.text = template.templateName
                this.template = template
            }

            override fun toString() =
                super.toString() + " '" + binding.templateTitle + "'"
        }

        companion object {
            private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MemeTemplate>() {
                override fun areItemsTheSame(oldItem: MemeTemplate, newItem: MemeTemplate) =
                    oldItem.templateName == newItem.templateName

                override fun areContentsTheSame(oldItem: MemeTemplate, newItem: MemeTemplate) =
                    oldItem == newItem
            }
        }
    }
}