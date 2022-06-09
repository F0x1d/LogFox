package com.f0x1d.logfox.utils

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import kotlin.math.roundToInt

class RecyclerViewDivider(context: Context, private val marginLeft: Int = 0, private val marginRight: Int = 0): ItemDecoration() {

    companion object {
        private const val TAG = "DividerItem"
        private val ATTRS = intArrayOf(R.attr.listDivider)
    }

    var drawable: Drawable?
    private val bounds = Rect()
    var shouldDraw: (Int, Int) -> Boolean = { current, max -> current != max - 1 }

    /**
     * Creates a divider [RecyclerView.ItemDecoration] that can be used with a
     * [LinearLayoutManager].
     *
     * @param context Current context, it will be used to access resources.
     */
    init {
        val a = context.obtainStyledAttributes(ATTRS)
        drawable = a.getDrawable(0)
        if (drawable == null) {
            Log.w(
                TAG, "@android:attr/listDivider was not set in the theme used for this "
                        + "DividerItemDecoration. Please set that attribute all call setDrawable()"
            )
        }
        a.recycle()
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null || drawable == null) {
            return
        }
        drawVertical(c, parent)
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        val left: Int
        val right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
        } else {
            left = 0
            right = parent.width
        }
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            if (!shouldDraw.invoke(i, childCount)) continue

            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, bounds)
            val bottom = bounds.bottom + child.translationY.roundToInt()
            val top = bottom - drawable!!.intrinsicHeight
            drawable!!.setBounds(left + marginLeft, top, right - marginRight, bottom)
            drawable!!.draw(canvas)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (drawable == null) {
            outRect[0, 0, 0] = 0
            return
        }

        outRect[0, 0, 0] = drawable!!.intrinsicHeight
    }
}