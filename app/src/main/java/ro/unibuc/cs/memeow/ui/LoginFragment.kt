package ro.unibuc.cs.memeow.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import ro.unibuc.cs.memeow.R
import ro.unibuc.cs.memeow.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var callbackManager: CallbackManager

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
                info.text = "User ID:$userId\nAuth Token:${result.accessToken.token}"
                Toast.makeText(context, "User ID: $userId", Toast.LENGTH_LONG).show()
                Glide.with(this@LoginFragment)
                    .load("http://graph.facebook.com/$userId/picture?type=square")
                    .error(R.drawable.ic_baseline_broken_image_24)
                    .into(profile)
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