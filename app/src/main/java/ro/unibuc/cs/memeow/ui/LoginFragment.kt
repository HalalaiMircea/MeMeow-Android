package ro.unibuc.cs.memeow.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import ro.unibuc.cs.memeow.R
import ro.unibuc.cs.memeow.databinding.FragmentLoginBinding
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.squareup.picasso.Picasso

class LoginFragment: Fragment(R.layout.fragment_login) {
    private lateinit var callbackManager: CallbackManager
    private var _binding: FragmentLoginBinding?=null
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)
        val info = binding.info
        val loginFacebookButton = binding.loginFacebookButton
        val profile = binding.profile

        //FacebookSdk.sdkInitialize(context?.applicationContext)
        loginFacebookButton.fragment = this

        var email = "email";

        loginFacebookButton.setReadPermissions(listOf(email));

        callbackManager = CallbackManager.Factory.create()

        loginFacebookButton.registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                info.text = "User ID:" +  result?.getAccessToken()?.getUserId() + "\n" + "Auth Token:" + result?.getAccessToken()?.getToken()
                Toast.makeText(context,"User ID: ${result?.getAccessToken()?.getUserId()}",Toast.LENGTH_LONG).show()
                var img = "http://graph.facebook.com/" + result?.getAccessToken()?.getUserId() + "/picture?type=square"
                Picasso.get().load(img).into(profile)
            }

            override fun onCancel() {
                info.text = "Login canceled"
            }

            override fun onError(error: FacebookException?) {
                info.text = "Login failed"
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }


}