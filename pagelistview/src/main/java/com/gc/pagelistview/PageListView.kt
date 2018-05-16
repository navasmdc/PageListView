package com.gc.pagelistview

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView

/**
 * Created by navas on 9/5/18.
 */
class PageListView(context: Context?, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val scrollView = CustomScrollView(context)
    private val itemsContainer = FrameLayout(context)
    private val sizeView = LinearLayout(context)

    private val resizeContainer = ViewTreeObserver.OnGlobalLayoutListener {
        if (height != 0) resizeItemsContainer()
    }

    init {
        addView(itemsContainer)
        addView(scrollView)
        sizeView.orientation = LinearLayout.VERTICAL
        scrollView.addView(sizeView)
        scrollView.viewToScroll = {
            if (itemsContainer.childCount == 0) null
            else {
                Log.e("ITEM $it", if (it == itemsContainer.childCount) 0.toString()
                else ((itemsContainer.childCount) - it).toString())
                itemsContainer.getChildAt(
                        if (it == itemsContainer.childCount) 0
                        else (itemsContainer.childCount - 1) - it)
            }
        }

        scrollView.onItemChanged = {old, new ->
            Log.e("onItemChanged", "old $old  new $new")
            if (old < new ) scrollView.viewToScroll?.invoke(old)?.y = -scrollView.height.toFloat()
            else scrollView.viewToScroll?.invoke(old)?.y = 0f
        }
        itemsContainer.layoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        viewTreeObserver.addOnGlobalLayoutListener(resizeContainer)
    }

    var adapter: BaseAdapter? = null
        set(value) {
            field = value
            // TODO load the items
        }

    private fun resizeItemsContainer() {
        sizeView.addView(View(context))
//        sizeView.getChildAt(0).setBackgroundColor(Color.RED)
        sizeView.getChildAt(0).layoutParams = LinearLayout.LayoutParams(width, height * 4)
        itemsContainer.addView(View(context))
        itemsContainer.getChildAt(0).setBackgroundColor(Color.RED)
        itemsContainer.getChildAt(0).layoutParams = FrameLayout.LayoutParams(width, height)
        itemsContainer.addView(View(context))
        itemsContainer.getChildAt(1).setBackgroundColor(Color.GREEN)
        itemsContainer.getChildAt(1).layoutParams = FrameLayout.LayoutParams(width, height)
        itemsContainer.addView(View(context))
        itemsContainer.getChildAt(2).setBackgroundColor(Color.YELLOW)
        itemsContainer.getChildAt(2).layoutParams = FrameLayout.LayoutParams(width, height)
        itemsContainer.addView(View(context))
        itemsContainer.getChildAt(3).setBackgroundColor(Color.BLUE)
        itemsContainer.getChildAt(3).layoutParams = FrameLayout.LayoutParams(width, height)
//        scrollView.itemToMove = itemsContainer.getChildAt(1)
//        itemToMove?.y = height.toFloat()
        itemsContainer.viewTreeObserver.removeOnGlobalLayoutListener(resizeContainer)

    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return super.onTouchEvent(ev)
    }

//    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
//        super.onScrollChanged(l, t, oldl, oldt)
//        itemToMove?.y = itemToMove?.y!! - l - oldl
//    }


    private class CustomScrollView(context: Context?) : ScrollView(context) {

        var currentItem = 0

        internal var viewToScroll: ((view: Int) -> View?)? = null
        internal var onItemChanged: ((old: Int, new: Int) -> Unit)? = null

        override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
            super.onScrollChanged(l, t, oldl, oldt)
            viewToScroll?.invoke(t / height)?.y = -(t - ((t / height) * height)).toFloat()
            if (currentItem != t / height) {
                onItemChanged?.invoke(currentItem, t / height)
                currentItem = t / height
            }
        }


    }

}