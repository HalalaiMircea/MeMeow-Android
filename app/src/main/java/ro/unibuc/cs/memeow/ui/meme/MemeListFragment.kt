package ro.unibuc.cs.memeow.ui.meme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ro.unibuc.cs.memeow.R
import ro.unibuc.cs.memeow.databinding.LayoutGenericListBinding
import ro.unibuc.cs.memeow.databinding.LayoutMemeItemBinding
import ro.unibuc.cs.memeow.injection.GlideApp
import ro.unibuc.cs.memeow.model.PostedMeme
import ro.unibuc.cs.memeow.util.ArgsFragment
import ro.unibuc.cs.memeow.util.MarginItemDecoration
import ro.unibuc.cs.memeow.util.MyLoadStateAdapter
import ro.unibuc.cs.memeow.util.addGenericLoadStateListener
import java.text.DateFormat
import javax.inject.Inject

@AndroidEntryPoint
class MemeListFragment : ArgsFragment(R.layout.layout_generic_list) {
    private var _binding: LayoutGenericListBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var viewModelFactory: MemeListViewModel.Factory
    private val viewModel: MemeListViewModel by viewModels {
        val prevDestination = findNavController().previousBackStackEntry?.destination?.id!!
        MemeListViewModel.provideFactory(viewModelFactory, this, arguments, prevDestination)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = LayoutGenericListBinding.bind(view)
        val adapter = RecyclerAdapter(this::onRecyclerItemClick)

        adapter.addGenericLoadStateListener(binding)

        // Finish recyclerView setup
        with(binding.recyclerView) {
            this.adapter = adapter.withLoadStateFooter(MyLoadStateAdapter { adapter.retry() })
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                MarginItemDecoration(
                    resources.getDimensionPixelOffset(R.dimen.recycler_view_item_spacing), 1
                )
            )
            setHasFixedSize(true)
        }
        binding.buttonRetry.setOnClickListener { adapter.retry() }
        viewModel.memeData.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onRecyclerItemClick(memeObj: PostedMeme) {
        val action = MemeListFragmentDirections.actionGlobalMemeFragment(memeObj)
        findNavController().navigate(action)
    }

    private class RecyclerAdapter(
        private val itemClick: (PostedMeme) -> Unit
    ) : PagingDataAdapter<PostedMeme, RecyclerAdapter.ViewHolder>(DIFF_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutMemeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            getItem(position)?.let { holder.bind(it) }
        }

        inner class ViewHolder(private val binding: LayoutMemeItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

            init {
                itemView.setOnClickListener {
                    if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                        getItem(bindingAdapterPosition)?.let { itemClick.invoke(it) }
                    }
                }
            }

            fun bind(meme: PostedMeme) {
                GlideApp.with(itemView)
                    .load(meme.memeUrl).centerCrop().into(binding.imageMeme)
                binding.textDate.text = DateFormat.getDateInstance().format(meme.dateTimeUtc)
            }
        }

        companion object {
            private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PostedMeme>() {
                override fun areItemsTheSame(oldItem: PostedMeme, newItem: PostedMeme) =
                    oldItem.memeBusinessId == newItem.memeBusinessId

                override fun areContentsTheSame(oldItem: PostedMeme, newItem: PostedMeme) =
                    oldItem == newItem
            }
        }
    }

    companion object {
        private const val TAG = "MemeListFragment"
    }
}