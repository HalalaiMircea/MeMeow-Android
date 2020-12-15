package ro.unibuc.cs.memeow.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
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
    private val viewModel by viewModels<LoginViewModel>()

    private lateinit var callbackManager: CallbackManager
    @Inject lateinit var memeowApi: MemeowApi

    //TODO: Improve logging in process. Either remove the token when user logs out, or refresh the token
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentLoginBinding.bind(view)
        val info = binding.info
        val loginFbButton = binding.loginFacebookButton
        val profile = binding.profile

        loginFbButton.fragment = this
        loginFbButton.setPermissions(listOf("email"))
        callbackManager = CallbackManager.Factory.create()

        loginFbButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val userId = result.accessToken.userId
                val fbAuthUser = FacebookAuthUser(result.accessToken.token, userId)

                memeowApi.facebookAuth(fbAuthUser).enqueue(object : Callback<ServerAuthResponse> {
                    override fun onResponse(
                        call: Call<ServerAuthResponse>, response: Response<ServerAuthResponse>
                    ) {
                        val message =
                            if (response.isSuccessful) {
                                viewModel.saveJwtToken(response.body()!!.jwtToken)
                                "Successfully logged in with Facebook"
                            } else
                                "MeMeow service not available. Try again later..."
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        Log.i(TAG, "onResponse: $message code ${response.code()}")
                    }

                    override fun onFailure(call: Call<ServerAuthResponse>, t: Throwable) {
                        Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "onFailure: $t")
                    }
                })
            }

            override fun onCancel() {
                Toast.makeText(context, "Login canceled by user", Toast.LENGTH_LONG).show()
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(context, "Login failed: $error", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "LoginFragment"
    }
}