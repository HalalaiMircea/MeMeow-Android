package ro.unibuc.cs.memeow.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ro.unibuc.cs.memeow.R
import ro.unibuc.cs.memeow.databinding.FragmentHomeBinding
import ro.unibuc.cs.memeow.injection.GlideApp
import ro.unibuc.cs.memeow.util.ViewBindingFragment

@AndroidEntryPoint
class HomeFragment : ViewBindingFragment<FragmentHomeBinding>() {
    private val homeViewModel: HomeViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeBinding =
        FragmentHomeBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        homeViewModel.topRanking.observe(viewLifecycleOwner) {
            val fullName = it.userResponse.firstName + " " + it.userResponse.lastName
            binding.textTop.text = fullName
            GlideApp.with(this)
                .load(it.userResponse.iconUrl).circleCrop().into(binding.imageTop)
        }

        binding.imageTop.setOnClickListener {
            findNavController().navigate(R.id.nav_leaderboard)
        }
    }
}