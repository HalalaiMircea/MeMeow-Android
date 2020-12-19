package ro.unibuc.cs.memeow.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ro.unibuc.cs.memeow.R
import ro.unibuc.cs.memeow.databinding.FragmentProfileBinding

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val args: ProfileFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //https://developer.android.com/guide/navigation/navigation-conditional#kotlin
        val navController = findNavController()
        val currentBackStackEntry = navController.currentBackStackEntry!!
        val savedStateHandle = currentBackStackEntry.savedStateHandle
        savedStateHandle.getLiveData<Boolean>(LoginFragment.LOGIN_SUCCESSFUL)
            .observe(currentBackStackEntry, { success ->
                if (!success) {
                    val startDestination = navController.graph.startDestination
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(startDestination, true)
                        .build()
                    navController.navigate(startDestination, null, navOptions)
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentProfileBinding.bind(view)

        // Redirect to login when user isn't logged in
        userViewModel.loggedInState.observe(viewLifecycleOwner, { loggedState ->
            if (!loggedState) {
                findNavController().navigate(R.id.login_fragment)
            }
        })
        //TODO: Figure out a way to pass navArgs to viewModel via constructor
        profileViewModel.getProfile(args.profileUUID).observe(viewLifecycleOwner, { profile ->
            Glide.with(this@ProfileFragment)
                .load(profile.iconUrl)
                .circleCrop()
                .error(R.drawable.ic_baseline_broken_image_24)
                .into(binding.imageProfile)

            val fullName = profile.firstName + " " + profile.lastName
            with(binding) {
                textName.text = fullName
                textLevel.text = getString(R.string.level_d, profile.currentLevel)
                textExp.text = getString(R.string.xp_dd, profile.currentXp, 100)
                barExp.max = 100
                barExp.progress = profile.currentLevel / 100
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}