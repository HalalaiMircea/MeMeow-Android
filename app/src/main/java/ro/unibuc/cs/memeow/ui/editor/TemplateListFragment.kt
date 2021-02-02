package ro.unibuc.cs.memeow.ui.editor

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ro.unibuc.cs.memeow.R
import ro.unibuc.cs.memeow.databinding.LayoutGenericListBinding
import ro.unibuc.cs.memeow.databinding.LayoutTemplateItemBinding
import ro.unibuc.cs.memeow.injection.GlideApp
import ro.unibuc.cs.memeow.model.MemeTemplate
import ro.unibuc.cs.memeow.util.MarginItemDecoration
import ro.unibuc.cs.memeow.util.MyLoadStateAdapter

@AndroidEntryPoint
class TemplateListFragment : Fragment(R.layout.layout_generic_list) {
    private var _binding: LayoutGenericListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditorViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = LayoutGenericListBinding.bind(view)

        val spanCount = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            SPAN_COUNT_LANDSCAPE else SPAN_COUNT_PORTRAIT

        val adapter = RecyclerAdapter(viewModel, this::onRecyclerItemClick, this::onRecyclerItemLongClick)

        // Configure footer to span across multiple columns
        val gridLayoutManager = GridLayoutManager(context, spanCount)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int =
                if (adapter.getItemViewType(position) == RecyclerAdapter.VIEW_HOLDER_TYPE) 1 else spanCount
        }

        // Show retry button and errors in recycler view layout
        adapter.addLoadStateListener { loadStates ->
            with(binding) {
                progressBar.isVisible = loadStates.source.refresh is LoadState.Loading
                recyclerView.isVisible = loadStates.source.refresh is LoadState.NotLoading
                buttonRetry.isVisible = loadStates.source.refresh is LoadState.Error
                textViewError.isVisible = loadStates.source.refresh is LoadState.Error

                // In case of no result query for recycler view
                if (loadStates.source.refresh is LoadState.NotLoading &&
                    loadStates.append.endOfPaginationReached &&
                    adapter.itemCount < 1
                ) {
                    recyclerView.isVisible = false
                    textViewEmpty.isVisible = true
                } else {
                    textViewEmpty.isVisible = false
                }
            }
        }
        // Finish recyclerView setup
        with(binding.recyclerView) {
            this.adapter = adapter.withLoadStateFooter(MyLoadStateAdapter { adapter.retry() })
            layoutManager = gridLayoutManager
            addItemDecoration(
                MarginItemDecoration(
                    resources.getDimensionPixelOffset(R.dimen.recycler_view_item_spacing), spanCount
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
            override fun onQueryTextSubmit(query: String): Boolean {
                binding.recyclerView.scrollToPosition(0)
                viewModel.searchTemplate(query)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?) = true
        })
        // Hacky way to reset the query, but the API didn't give me any choice
        searchView.findViewById<View>(R.id.search_close_btn)
            .setOnClickListener {
                viewModel.searchTemplate("")
                searchView.setQuery("", false)
            }
    }

    private fun onRecyclerItemClick(template: MemeTemplate) {
        if (viewModel.userCurrentLevel >= template.minRequiredLevel) {
            viewModel.currentTemplate = template
            val action = TemplateListFragmentDirections.actionSelectTemplate()
            findNavController().navigate(action)
        } else {
            Snackbar.make(
                binding.root,
                "You require at least level ${template.minRequiredLevel}",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun onRecyclerItemLongClick(template: MemeTemplate): Boolean {
        val action = TemplateListFragmentDirections.actionNavCreateMemeToMemeList(template.templateName)
        findNavController().navigate(action)
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class RecyclerAdapter(
        private val viewModel: EditorViewModel,
        private val onClick: (MemeTemplate) -> Unit,
        private val onLongClick: (MemeTemplate) -> Boolean
    ) :
        PagingDataAdapter<MemeTemplate, RecyclerAdapter.ViewHolder>(DIFF_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = LayoutTemplateItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val currentItem = getItem(position)
            if (currentItem != null) holder.bind(currentItem)
        }

        override fun getItemViewType(position: Int) =
            if (position == itemCount) MyLoadStateAdapter.ViewHolder.TYPE else VIEW_HOLDER_TYPE

        inner class ViewHolder(private val binding: LayoutTemplateItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

            private lateinit var template: MemeTemplate

            init {
                binding.root.setOnClickListener { onClick(template) }
                binding.root.setOnLongClickListener { onLongClick(template) }
            }

            fun bind(template: MemeTemplate) {
                this.template = template
                GlideApp.with(itemView)
                    .load(template.imageUrl).centerCrop().into(binding.templateImg)
                binding.templateTitle.text = template.templateName
                itemView.alpha = if (viewModel.userCurrentLevel < template.minRequiredLevel) 0.5f else 1f
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
            const val VIEW_HOLDER_TYPE: Int = 69
        }
    }

    companion object {
        private const val SPAN_COUNT_PORTRAIT = 2
        private const val SPAN_COUNT_LANDSCAPE = 4
        private const val TAG = "TemplateListFragment"
    }
}