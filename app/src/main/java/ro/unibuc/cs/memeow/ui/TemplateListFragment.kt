package ro.unibuc.cs.memeow.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ro.unibuc.cs.memeow.R
import ro.unibuc.cs.memeow.databinding.FragmentTemplateListBinding
import ro.unibuc.cs.memeow.databinding.LayoutTemplateItemBinding
import ro.unibuc.cs.memeow.injection.GlideApp
import ro.unibuc.cs.memeow.model.MemeTemplate
import ro.unibuc.cs.memeow.util.MarginItemDecoration
import ro.unibuc.cs.memeow.util.MyLoadStateAdapter

@AndroidEntryPoint
class TemplateListFragment : Fragment(R.layout.fragment_template_list) {
    private var _binding: FragmentTemplateListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditorViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentTemplateListBinding.bind(view)

        val adapter = RecyclerAdapter(viewModel)

        val spanCount =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                SPAN_COUNT_LANDSCAPE
            else
                SPAN_COUNT_PORTRAIT
        // Configure footer to span across multiple columns
        val gridLayoutManager = GridLayoutManager(context, spanCount)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int =
                if (adapter.getItemViewType(position) == RecyclerAdapter.TEMPLATE_VIEW_TYPE) 1 else spanCount
        }

        // Show retry button and errors in recycler view layout
        adapter.addLoadStateListener { loadStates ->
            binding.apply {
                progressBar.isVisible = loadStates.source.refresh is LoadState.Loading
                this.templateList.isVisible = loadStates.source.refresh is LoadState.NotLoading
                buttonRetry.isVisible = loadStates.source.refresh is LoadState.Error
                textViewError.isVisible = loadStates.source.refresh is LoadState.Error

                // In case of no result query for recycler view
                if (loadStates.source.refresh is LoadState.NotLoading &&
                    loadStates.append.endOfPaginationReached &&
                    adapter.itemCount < 1
                ) {
                    this.templateList.isVisible = false
                    textViewEmpty.isVisible = true
                } else {
                    textViewEmpty.isVisible = false
                }
            }
        }
        with(binding.templateList) {
            this.adapter = adapter.withLoadStateFooter(MyLoadStateAdapter { adapter.retry() })
            layoutManager = gridLayoutManager
            addItemDecoration(
                MarginItemDecoration(
                    resources.getDimensionPixelOffset(R.dimen.recycler_view_item_spacing),
                    spanCount
                )
            )
            setHasFixedSize(true)
        }

        binding.buttonRetry.setOnClickListener { adapter.retry() }

        viewModel.templates.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_templates, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    binding.templateList.scrollToPosition(0)
                    viewModel.searchTemplate(query)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
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

        override fun getItemViewType(position: Int): Int {
            return if (position == itemCount) NETWORK_VIEW_TYPE else TEMPLATE_VIEW_TYPE
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
                GlideApp.with(itemView)
                    .load(template.imageUrl)
                    .centerCrop()
                    .into(binding.templateImg)
                binding.templateTitle.text = template.templateName
                this.template = template
            }

            override fun toString() = super.toString() + " '" + binding.templateTitle + "'"
        }

        companion object {
            private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MemeTemplate>() {
                override fun areItemsTheSame(oldItem: MemeTemplate, newItem: MemeTemplate) =
                    oldItem.templateName == newItem.templateName

                override fun areContentsTheSame(oldItem: MemeTemplate, newItem: MemeTemplate) =
                    oldItem == newItem
            }
            const val NETWORK_VIEW_TYPE = 1337
            const val TEMPLATE_VIEW_TYPE = 69
        }
    }

    companion object {
        private const val SPAN_COUNT_PORTRAIT = 2
        private const val SPAN_COUNT_LANDSCAPE = 4
    }
}