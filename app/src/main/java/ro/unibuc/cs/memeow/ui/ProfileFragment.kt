package ro.unibuc.cs.memeow.ui

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ro.unibuc.cs.memeow.R
import ro.unibuc.cs.memeow.databinding.FragmentProfileBinding
import ro.unibuc.cs.memeow.injection.GlideApp
import ro.unibuc.cs.memeow.model.Profile
import ro.unibuc.cs.memeow.util.BaseFragment
import java.text.DateFormat

@AndroidEntryPoint
class ProfileFragment : BaseFragment(R.layout.fragment_profile) {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navController = findNavController()
        val currentBackStackEntry = navController.currentBackStackEntry!!
        val savedStateHandle = currentBackStackEntry.savedStateHandle
        savedStateHandle.getLiveData<Boolean>(LoginFragment.LOGIN_SUCCESSFUL)
            .observe(currentBackStackEntry, { success ->
                if (!success) {
                    navController.popBackStack()
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentProfileBinding.bind(view)

        // Redirect to login when user isn't logged in, even if it's not his profile
        viewModel.repository.signedUserProfile.observe(viewLifecycleOwner, { ownProfile ->
            if (ownProfile == null) {
                val action = ProfileFragmentDirections.actionGlobalLogin()
                findNavController().navigate(action)
            } else {
                binding.lastMemeView.isVisible = false
                viewModel.profile.observe(viewLifecycleOwner, this::onLoggedIn)
            }
        })
    }

    private fun onLoggedIn(profile: Profile) {
        val fullName = profile.firstName + " " + profile.lastName
        val maxExp = XP_TABLE[profile.level.currentLevel]

        GlideApp.with(this)
            .load(profile.iconUrl).circleCrop().into(binding.imageProfile)

        with(binding) {
            textName.text = fullName
            textLevel.text = getString(R.string.level_d, profile.level.currentLevel)
            textExp.text = getString(R.string.xp_dd, profile.level.currentXp, maxExp)
            barExp.max = maxExp * 100
        }
        val startValue = profile.level.lastCurrentXp * 100
        val endValue = profile.level.currentXp * 100
        ObjectAnimator.ofInt(binding.barExp, "progress", startValue, endValue)
            .setDuration(600)
            .start()

        // We show and set the data only if user has a last meme
        profile.lastMeme?.let { memeObj ->
            binding.lastMemeView.isVisible = true
            GlideApp.with(this)
                .load(memeObj.memeUrl).centerCrop().into(binding.imageLastMeme)
            binding.textLastMemeDate.text = DateFormat.getDateInstance().format(memeObj.dateTimeUtc)
            binding.lastMemeView.setOnClickListener {
                val action = ProfileFragmentDirections.actionViewLastMeme(memeObj)
                findNavController().navigate(action)
            }
        }
        binding.buttonHistory.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileToMemeHistory(
                profileUUID = profile.profileUuid,
                username = profile.firstName
            )
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "ProfileFragment"
        private val XP_TABLE: IntArray = intArrayOf(0, 5, 10, 15, 25, 40, 60)
    }
}