package ro.unibuc.cs.memeow.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ro.unibuc.cs.memeow.R
import ro.unibuc.cs.memeow.api.MemeowApi
import ro.unibuc.cs.memeow.databinding.FragmentLoginBinding
import ro.unibuc.cs.memeow.model.FacebookAuthUser
import ro.unibuc.cs.memeow.model.ServerAuthResponse

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var callbackManager: CallbackManager
    private val viewModel: LoginViewModel by viewModels<LoginViewModel>()
    private lateinit var fbAuthUser: FacebookAuthUser

    private val memeowApi = Retrofit.Builder()
        .baseUrl(MemeowApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(MemeowApi::class.java)
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
                fbAuthUser = FacebookAuthUser(result.accessToken.token, userId)
                info.text = "User ID:$userId\nAuth Token:${result.accessToken.token}"
                Toast.makeText(context, "User ID: $userId", Toast.LENGTH_LONG).show()
                memeowApi.facebookAuth(fbAuthUser).enqueue(object : Callback<ServerAuthResponse> {
                    override fun onResponse(
                        call: Call<ServerAuthResponse>,
                        response: Response<ServerAuthResponse>
                    ) {
                        println(response.body()?.jwtToken)
                    }

                    override fun onFailure(call: Call<ServerAuthResponse>, t: Throwable) {
                        println(t)
                    }

                })
               // Glide.with(this@LoginFragment)
                 //   .load("http://graph.facebook.com/$userId/picture?type=square")
                   // .error(R.drawable.ic_baseline_broken_image_24)
                    //.into(profile)
            }

            override fun onCancel() {
                info.text = "Login canceled"
                Toast.makeText(context, "Login canceled", Toast.LENGTH_LONG).show()
            }

            override fun onError(error: FacebookException) {
                info.text = "Login failed"
                Toast.makeText(context, "Login failed", Toast.LENGTH_LONG).show()
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
}