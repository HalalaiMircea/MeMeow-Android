package ro.unibuc.cs.memeow.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import ro.unibuc.cs.memeow.databinding.LayoutGenericListBinding
import ro.unibuc.cs.memeow.databinding.ListLoadStateFooterBinding

class MyLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<MyLoadStateAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        val binding = ListLoadStateFooterBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(binding, retry)
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    class ViewHolder(
        private val binding: ListLoadStateFooterBinding,
        private val retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonRetry.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            binding.apply {
                progressBar.isVisible = loadState is LoadState.Loading
                buttonRetry.isVisible = loadState !is LoadState.Loading
                textError.isVisible = loadState !is LoadState.Loading
            }
        }

        companion object {
            const val TYPE: Int = 1337
        }
    }
}

/** Show retry button and texts in the layout when loading fails */
fun PagingDataAdapter<*, *>.addGenericLoadStateListener(binding: LayoutGenericListBinding) {
    this.addLoadStateListener { loadStates ->
        with(binding) {
            progressBar.isVisible = loadStates.source.refresh is LoadState.Loading
            recyclerView.isVisible = loadStates.source.refresh is LoadState.NotLoading
            buttonRetry.isVisible = loadStates.source.refresh is LoadState.Error
            textViewError.isVisible = loadStates.source.refresh is LoadState.Error

            // In case of no result query for recycler view
            if (loadStates.source.refresh is LoadState.NotLoading &&
                loadStates.append.endOfPaginationReached &&
                this@addGenericLoadStateListener.itemCount < 1
            ) {
                recyclerView.isVisible = false
                textViewEmpty.isVisible = true
            } else {
                textViewEmpty.isVisible = false
            }
        }
    }
}