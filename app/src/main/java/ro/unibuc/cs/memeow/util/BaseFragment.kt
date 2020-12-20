package ro.unibuc.cs.memeow.util

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

const val BUNDLE_ARGS = "BUNDLE_ARGS"

open class BaseFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {

    override fun setArguments(args: Bundle?) {
        if (args != null) {
            super.setArguments(Bundle(args).apply {
                putBundle(BUNDLE_ARGS, args) // Wrap the arguments as BUNDLE_ARGS
            })
        } else {
            super.setArguments(null)
        }
    }
}