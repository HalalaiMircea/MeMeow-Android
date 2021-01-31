package ro.unibuc.cs.memeow.ui

import android.graphics.*
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import dagger.hilt.android.AndroidEntryPoint
import ro.unibuc.cs.memeow.R
import ro.unibuc.cs.memeow.databinding.FragmentEditTemplateBinding
import ro.unibuc.cs.memeow.injection.GlideApp

@AndroidEntryPoint
class EditTemplateFragment : Fragment(R.layout.fragment_edit_template) {
    private var _binding: FragmentEditTemplateBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditorViewModel by activityViewModels()

    private lateinit var editTextWatcher: EditTextWatcher
    private lateinit var publishDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        //https://developer.android.com/guide/navigation/navigation-conditional#kotlin
        val navController = findNavController()
        val currentBackStackEntry = navController.currentBackStackEntry!!
        val savedStateHandle = currentBackStackEntry.savedStateHandle
        savedStateHandle.getLiveData<Boolean>(LoginFragment.LOGIN_SUCCESSFUL)
            .observe(currentBackStackEntry, { success ->
                if (!success)
                    navController.popBackStack()
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentEditTemplateBinding.bind(view)
        editTextWatcher = EditTextWatcher(null)
        binding.editTextBox.addTextChangedListener(editTextWatcher)

        // Load the currently selected template from the view model
        GlideApp.with(this)
            .asBitmap()
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean
                ): Boolean = false

                override fun onResourceReady(
                    resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    val container = binding.imgTextContainer
                    // We do this once the image loaded successfully because we need its dimensions
                    // Call onPreDraw in order to get runtime measurements of views, else they are zero
                    container.doOnPreDraw {
                        val touchListener = CustomTouchListener(container.width, container.height)
                        // When image is loaded, add the first TextView automatically and set it as the target
                        // for editText's TextWatcher
                        addTextView(touchListener)
                        binding.buttonAddText.setOnClickListener { addTextView(touchListener) }
                    }
                    return false
                }
            })
            .load(viewModel.currentTemplate.imageUrl)
            .into(binding.imgMeme)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_editor, menu)

        publishDialog = AlertDialog.Builder(requireContext())
            .setTitle("Publish Confirmation")
            .setMessage("You cannot edit the meme after publishing. Are you sure?")
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val container = binding.imgTextContainer
                val bitmap = Bitmap.createBitmap(container.width, container.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                container.layout(container.left, container.top, container.right, container.bottom)
                container.draw(canvas)
                viewModel.uploadMemeImage(bitmap).observe(viewLifecycleOwner) {
                    val action = EditTemplateFragmentDirections.actionEditToViewMeme(it)
                    findNavController().navigate(action)
                }
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
            .create()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_publish) {
            if (viewModel.userRepository.isUserLoggedIn) {
                publishDialog.show()
            } else {
                findNavController().navigate(R.id.login_fragment)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.editTextBox.removeTextChangedListener(editTextWatcher)
        _binding = null
    }

    /**
     * Function that adds a new TextView to the FrameLayout. It sets its correct attributes
     * (layout params, hint, textSize, textColor, touch and click listeners).
     * Also changes the target of the TextWatcher to this new textView.
     */
    private fun addTextView(touchListener: CustomTouchListener): TextView {
        val textView = TextView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            hint = "Tap to edit"
            typeface = ResourcesCompat.getFont(requireContext(), DEFAULT_FONT_ID)
            textSize = DEFAULT_TEXT_SIZE
            setTextColor(DEFAULT_TEXT_COLOR)
            setHintTextColor(DEFAULT_TEXT_COLOR)
            setShadowLayer(7.5f, 1f, 1f, Color.BLACK)
            setOnTouchListener(touchListener)
            setOnClickListener(this@EditTemplateFragment::changeTextTarget)
        }

        binding.imgTextContainer.addView(textView)
        changeTextTarget(textView)
        return textView
    }

    /**
     * Automatically targets the new textView and copy it's text to the editText.
     * Focuses keyboard on the editText if not already
     */
    private fun changeTextTarget(view: View) {
        if (view is TextView) {
            editTextWatcher.target = view
            binding.editTextBox.setText(view.text)
        } else {
            Log.e(TAG, "changeTextTarget: $view is not a TextView")
        }
    }

    private class EditTextWatcher(var target: TextView?) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            target?.text = s.toString()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private class CustomTouchListener(
        private val screenWidth: Int, private val screenHeight: Int
    ) : View.OnTouchListener {
        private var dX: Float = 0f
        private var dY: Float = 0f
        private val clickThreshold: Int = 100

        override fun onTouch(view: View, event: MotionEvent): Boolean {
            val newX: Float
            val newY: Float
            if (event.action == MotionEvent.ACTION_UP && event.eventTime - event.downTime < clickThreshold) {
                view.performClick()
                return true
            }
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    newX = event.rawX + dX; newY = event.rawY + dY

                    if ((newX <= 0 || newX >= screenWidth - view.width) &&
                        (newY <= 0 || newY >= screenHeight - view.height)
                    ) {
                        return true
                    }
                    if (newX <= 0 || newX >= screenWidth - view.width) {
                        view.y = newY
                        return true
                    }
                    if (newY <= 0 || newY >= screenHeight - view.height) {
                        view.x = newX
                        return true
                    }
                    view.x = newX; view.y = newY
                }
            }
            return true
        }
    }

    companion object {
        private const val TAG = "EditTemplateFragment"
        private const val DEFAULT_TEXT_SIZE = 30f
        private const val DEFAULT_TEXT_COLOR = Color.WHITE
        private const val DEFAULT_FONT_ID = R.font.impact
    }
}