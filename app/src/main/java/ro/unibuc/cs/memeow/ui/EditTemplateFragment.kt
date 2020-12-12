package ro.unibuc.cs.memeow.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextPaint
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import ro.unibuc.cs.memeow.R
import ro.unibuc.cs.memeow.databinding.FragmentEditTemplateBinding

class EditTemplateFragment : Fragment(R.layout.fragment_edit_template) {
    private var _binding: FragmentEditTemplateBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<EditorViewModel>()
    private val editorTextWatcher = EditTextWatcher(null)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentEditTemplateBinding.bind(view)
        val fullSizeImgUrl = viewModel.currentTemplate!!.url
        val editTextBox = binding.editTextBox
        val imgMeme = binding.imgMeme
        val container = binding.imgTextContainer

        editTextBox.addTextChangedListener(editorTextWatcher)

        // Load the currently selected template from the view model
        Glide.with(this)
            .asBitmap()
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean
                ): Boolean = false

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    // We do this once the image loaded successfully because we need its dimensions
                    // Call onPreDraw in order to get runtime measurements of views, else they are zero
                    container.doOnPreDraw {
                        val touchListener = CustomTouchListener(container.width, container.height)
                        // When image is loaded, add the first TextView automatically and set it as the target
                        // for editText's TextWatcher
                        addText(touchListener)
                        binding.buttonAddText.setOnClickListener { addText(touchListener) }
                    }
                    return false
                }
            })
            .load(GlideUrl(fullSizeImgUrl, TemplateListFragment.RecyclerAdapter.headers))
            .error(R.drawable.ic_baseline_broken_image_24)
            .into(imgMeme)
    }

    /**
     * Function that adds a new TextView to the FrameLayout. It sets its correct attributes
     * (layout params, hint, textSize, textColor, touch and click listeners).
     * Also changes the target of the TextWatcher to this new textView.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun addText(touchListener: CustomTouchListener) {
        val textView = TextView(context)
        textView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textView.hint = "Extra Text"
        textView.textSize = textSize
        textView.setTextColor(textColor)
        textView.setOnTouchListener(touchListener)
        textView.setOnClickListener(this::changeTextTarget)
        binding.imgTextContainer.addView(textView)

        changeTextTarget(textView)
    }

    /**
     * Automatically targets the new textView and copy it's text to the editText.
     * Focuses keyboard on the editText if not already
     */
    private fun changeTextTarget(view: View) {
        if (view is TextView) {
            editorTextWatcher.target = view
            binding.editTextBox.setText(view.text)
        } else {
            Log.e(TAG, "changeTextTarget: $view is not a TextView")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class EditTextWatcher(var target: TextView?) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            target!!.text = s
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    inner class CustomTouchListener(
        private val screenWidth: Int, private val screenHeight: Int
    ) : View.OnTouchListener {
        private var dX: Float = 0f
        private var dY: Float = 0f
        private val clickThreshold: Int = 100

        override fun onTouch(view: View, event: MotionEvent): Boolean {
            val newX: Float
            val newY: Float
            if (event.action == MotionEvent.ACTION_UP
                && event.eventTime - event.downTime < clickThreshold
            ) {
                view.performClick()
                return true
            }
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    newX = event.rawX + dX
                    newY = event.rawY + dY

                    if ((newX <= 0 || newX >= screenWidth - view.width)
                        && (newY <= 0 || newY >= screenHeight - view.height)
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
                    view.x = newX
                    view.y = newY
                }
            }
            return true
        }
    }

    companion object {
        private const val TAG = "EditTemplateFragment"
        private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.LINEAR_TEXT_FLAG)
        private const val textSize = 24f
        private const val textColor = Color.BLACK

        init {
            textPaint.style = Paint.Style.FILL
            textPaint.typeface = Typeface.SERIF
            textPaint.color = Color.BLACK
            textPaint.textSize = 30f
        }
    }
}