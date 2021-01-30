package ro.unibuc.cs.memeow.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ro.unibuc.cs.memeow.R
import ro.unibuc.cs.memeow.databinding.FragmentHomeBinding
import ro.unibuc.cs.memeow.injection.GlideApp

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentHomeBinding.bind(view)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}