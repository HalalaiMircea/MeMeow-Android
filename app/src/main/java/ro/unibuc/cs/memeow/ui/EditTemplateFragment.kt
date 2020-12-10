package ro.unibuc.cs.memeow.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextPaint
import android.view.MotionEvent
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import ro.unibuc.cs.memeow.R
import ro.unibuc.cs.memeow.databinding.FragmentEditTemplateBinding

class EditTemplateFragment : Fragment(R.layout.fragment_edit_template) {
    private var _binding: FragmentEditTemplateBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<EditorViewModel>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentEditTemplateBinding.bind(view)
        val fullSizeImgUrl = viewModel.currentTemplate!!.url
        val editTextBox = binding.editTextBox
        val imgMeme = binding.imgMeme
        val textViewBox = binding.textViewBox
        val container = binding.imgTextContainer

        editTextBox.addTextChangedListener(
            onTextChanged = { text: CharSequence?, _: Int, _: Int, _: Int ->
                textViewBox.text = text
            })
        // Load the currently selected template from the view model
        Glide.with(this)
            .asBitmap()
            .load(GlideUrl(fullSizeImgUrl, TemplateListFragment.RecyclerAdapter.headers))
            .error(R.drawable.ic_baseline_broken_image_24)
            .into(imgMeme)

        // Do on pre-draw in order to get runtime measurements of views, else they are zero
        container.doOnPreDraw {
            textViewBox.setOnTouchListener(CustomTouchListener(container.width, container.height))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class CustomTouchListener(
        private val screenWidth: Int,
        private val screenHeight: Int
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

        init {
            textPaint.style = Paint.Style.FILL
            textPaint.typeface = Typeface.SERIF
            textPaint.color = Color.BLACK
            textPaint.textSize = 30f
        }
    }
}