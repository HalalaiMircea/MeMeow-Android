package ro.unibuc.cs.memeow.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Decoration for items in recycler view. Thanks to [Dmitry L.](https://stackoverflow.com/a/29168276)
 */
class MarginItemDecoration(
    private val gridSpacingPx: Int,
    private val spanCount: Int,
) : RecyclerView.ItemDecoration() {

    private var needLeftSpacing = false

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val padding =
            parent.width / spanCount - ((parent.width - gridSpacingPx.toFloat() * (spanCount - 1)) / spanCount).toInt()
        val itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition < spanCount) {
            outRect.top = 0
        } else {
            outRect.top = gridSpacingPx
        }
        if (itemPosition % spanCount == 0) {
            outRect.left = 0
            outRect.right = padding
            needLeftSpacing = true
        } else if ((itemPosition + 1) % spanCount == 0) {
            needLeftSpacing = false
            outRect.right = 0
            outRect.left = padding
        } else if (needLeftSpacing) {
            needLeftSpacing = false
            outRect.left = gridSpacingPx - padding
            if ((itemPosition + 2) % spanCount == 0) {
                outRect.right = gridSpacingPx - padding
            } else {
                outRect.right = gridSpacingPx / 2
            }
        } else if ((itemPosition + 2) % spanCount == 0) {
            needLeftSpacing = false
            outRect.left = gridSpacingPx / 2
            outRect.right = gridSpacingPx - padding
        } else {
            needLeftSpacing = false
            outRect.left = gridSpacingPx / 2
            outRect.right = gridSpacingPx / 2
        }
        outRect.bottom = 0
    }
}