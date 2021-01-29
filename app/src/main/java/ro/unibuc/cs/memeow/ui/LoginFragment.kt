package ro.unibuc.cs.memeow.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import com.facebook.*
import com.facebook.login.LoginResult
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ro.unibuc.cs.memeow.R
import ro.unibuc.cs.memeow.api.MemeowApi
import ro.unibuc.cs.memeow.databinding.FragmentLoginBinding
import ro.unibuc.cs.memeow.model.FacebookAuthUser
import ro.unibuc.cs.memeow.model.ServerAuthResponse
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var callbackManager: CallbackManager
    @Inject lateinit var memeowApi: MemeowApi

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentLoginBinding.bind(view)
        savedStateHandle = findNavController().previousBackStackEntry!!.savedStateHandle
        savedStateHandle.set(LOGIN_SUCCESSFUL, false)

        val accessTokenTracker: AccessTokenTracker = object : AccessTokenTracker() {
            override fun onCurrentAccessTokenChanged(oldToken: AccessToken?, currentToken: AccessToken?) {
                if (oldToken == null) {
                    Log.d(TAG, "Logged in with Facebook SDK")
                } else if (currentToken == null) {
                    Log.d(TAG, "Logged out with Facebook SDK, removing stored JWT...")
                    userViewModel.removeJwtToken()
                }
            }
        }
        val loginFbButton = binding.loginFacebookButton
        loginFbButton.fragment = this
        loginFbButton.setPermissions(listOf("email"))
        callbackManager = CallbackManager.Factory.create()

        loginFbButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val userId = result.accessToken.userId
                val fbAuthUser = FacebookAuthUser(result.accessToken.token, userId)
                memeowApi.facebookAuth(fbAuthUser).enqueue(MemeowAPICallback())
            }

            override fun onCancel() {
                Toast.makeText(context, "Login canceled by user", Toast.LENGTH_LONG).show()
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(context, "Login failed: $error", Toast.LENGTH_LONG).show()
            }
        })
        accessTokenTracker.startTracking()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    inner class MemeowAPICallback : Callback<ServerAuthResponse> {
        override fun onResponse(call: Call<ServerAuthResponse>, response: Response<ServerAuthResponse>) {
            if (response.isSuccessful) {
                userViewModel.saveJwtToken(response.body()!!.jwtToken)
                savedStateHandle.set(LOGIN_SUCCESSFUL, true)
                findNavController().popBackStack()
            } else {
                val message = "MeMeow service not available. Try again later... code ${response.code()}"
                Log.e(TAG, "onResponse: $message")
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<ServerAuthResponse>, t: Throwable) {
            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show()
            Log.e(TAG, "onFailure: $t")
        }
    }

    companion object {
        private const val TAG = "LoginFragment"
        const val LOGIN_SUCCESSFUL = "LOGIN_SUCCESSFUL"
    }
}