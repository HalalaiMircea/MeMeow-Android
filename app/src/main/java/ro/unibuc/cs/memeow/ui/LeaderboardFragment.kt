package ro.unibuc.cs.memeow.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ro.unibuc.cs.memeow.R
import ro.unibuc.cs.memeow.databinding.LayoutGenericListBinding
import ro.unibuc.cs.memeow.databinding.LayoutLeaderboardItemBinding
import ro.unibuc.cs.memeow.injection.GlideApp
import ro.unibuc.cs.memeow.model.Ranking
import ro.unibuc.cs.memeow.util.MarginItemDecoration

@AndroidEntryPoint
class LeaderboardFragment : Fragment(R.layout.layout_generic_list) {
    private var _binding: LayoutGenericListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LeaderboardViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = LayoutGenericListBinding.bind(view)

        val adapter = RecyclerAdapter { profileUuid ->
            val action = LeaderboardFragmentDirections.actionLeaderboardToProfile(profileUuid)
            findNavController().navigate(action)
        }
        with(binding.recyclerView) {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                MarginItemDecoration(
                    resources.getDimensionPixelOffset(R.dimen.recycler_view_item_spacing), 1
                )
            )
            setHasFixedSize(true)
        }
        binding.progressBar.isVisible = true
        viewModel.rankings.observe(viewLifecycleOwner, {
            binding.progressBar.isVisible = false
            adapter.submitList(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class RecyclerAdapter(val itemClick: (String) -> Unit) :
        ListAdapter<Ranking, RecyclerAdapter.ViewHolder>(DIFF_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutLeaderboardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = getItem(position)
            if (item != null) {
                holder.bind(item)
                holder.profileUuid = item.userResponse.profileUuid
            }
        }

        private inner class ViewHolder(val binding: LayoutLeaderboardItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

            lateinit var profileUuid: String

            init {
                binding.imageProfile.setOnClickListener { itemClick(profileUuid) }
            }

            fun bind(item: Ranking) {
                val fullName = item.userResponse.firstName + " " + item.userResponse.lastName
                binding.textFullName.text = fullName
                binding.textRank.text = binding.root.context.getString(R.string.rank_d, item.leaderboardPlace)
                GlideApp.with(itemView)
                    .load(item.userResponse.iconUrl).circleCrop().into(binding.imageProfile)
            }
        }

        companion object {
            private val DIFF_CALLBACK: DiffUtil.ItemCallback<Ranking> =
                object : DiffUtil.ItemCallback<Ranking>() {
                    override fun areItemsTheSame(oldItem: Ranking, newItem: Ranking) =
                        oldItem.leaderboardPlace == newItem.leaderboardPlace

                    override fun areContentsTheSame(oldItem: Ranking, newItem: Ranking) =
                        oldItem == newItem
                }
        }
    }

    companion object {
        private const val TAG = "LeaderboardFragment"
    }
}
