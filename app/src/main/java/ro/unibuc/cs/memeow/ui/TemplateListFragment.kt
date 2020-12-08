package ro.unibuc.cs.memeow.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dagger.hilt.android.AndroidEntryPoint
import ro.unibuc.cs.memeow.R
import ro.unibuc.cs.memeow.databinding.FragmentTemplateListBinding
import ro.unibuc.cs.memeow.databinding.LayoutTemplateItemBinding
import ro.unibuc.cs.memeow.model.MemeTemplate


/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class TemplateListFragment : Fragment(R.layout.fragment_template_list) {

    private var _binding: FragmentTemplateListBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<EditorViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentTemplateListBinding.bind(view)

        // Set the adapter
        val templateList = binding.templateList
        val adapter = RecyclerAdapter()
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

    class RecyclerAdapter :
        PagingDataAdapter<MemeTemplate, RecyclerAdapter.ViewHolder>(DIFF_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(
                LayoutTemplateItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val currentItem = getItem(position)
            if (currentItem != null) {
                holder.bind(currentItem)
            }
        }

        class ViewHolder(private val binding: LayoutTemplateItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

            init {
                binding.root.setOnClickListener {
                    val action = TemplateListFragmentDirections.actionSelectTemplate()
                    it.findNavController().navigate(action)
                }
            }

            fun bind(template: MemeTemplate) {
                val url = GlideUrl(
                    template.thumbnailUrl, LazyHeaders.Builder()
                        .addHeader("User-Agent", "your-user-agent")
                        .build()
                )
                Glide.with(itemView)
                    .load(url)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_baseline_signal_wifi_off_24)
                    .into(binding.templateImg)
                binding.templateTitle.text = template.title
            }

            override fun toString(): String {
                return super.toString() + " '" + binding.templateTitle + "'"
            }
        }

        companion object {
            private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MemeTemplate>() {
                override fun areItemsTheSame(oldItem: MemeTemplate, newItem: MemeTemplate) =
                    oldItem.id == newItem.id

                override fun areContentsTheSame(oldItem: MemeTemplate, newItem: MemeTemplate) =
                    oldItem == newItem
            }
        }
    }
}