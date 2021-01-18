package com.example.speechtomessage

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DividerDecoration(context: Context):RecyclerView.ItemDecoration() {



    private val mDivider: Drawable

    init{
        val a = context.obtainStyledAttributes(ATTRS.toIntArray())
        mDivider = a.getDrawable(0)!!
        a.recycle()
    }
    private fun getOrientation(parent:RecyclerView):Int {
        val layoutManager:LinearLayoutManager
        try
        {
            layoutManager = parent.getLayoutManager() as LinearLayoutManager
        }
        catch (e:ClassCastException) {
            throw IllegalStateException(("DividerDecoration can only be used with a " + "LinearLayoutManager."), e)
        }
        return layoutManager.getOrientation()
    }
    override fun onDraw(c:Canvas, parent:RecyclerView, state:RecyclerView.State) {
        super.onDraw(c, parent, state)
        if (getOrientation(parent) == VERTICAL_LIST)
        {
            drawVertical(c, parent)
        }
        else
        {
            drawHorizontal(c, parent)
        }
    }
    fun drawVertical(c:Canvas, parent:RecyclerView) {
        val left = parent.getPaddingLeft()
        val right = parent.getWidth() - parent.getPaddingRight()
        val recyclerViewTop = parent.getPaddingTop()
        val recyclerViewBottom = parent.getHeight() - parent.getPaddingBottom()
        val childCount = parent.getChildCount()
        for (i in 0 until childCount)
        {
            val child = parent.getChildAt(i)
            val params = child
                    .getLayoutParams() as RecyclerView.LayoutParams
            val top = Math.max(recyclerViewTop, child.getBottom() + params.bottomMargin)
            val bottom = Math.min(recyclerViewBottom, top + mDivider.getIntrinsicHeight())
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }
    fun drawHorizontal(c: Canvas, parent:RecyclerView) {
        val top = parent.getPaddingTop()
        val bottom = parent.getHeight() - parent.getPaddingBottom()
        val recyclerViewLeft = parent.getPaddingLeft()
        val recyclerViewRight = parent.getWidth() - parent.getPaddingRight()
        val childCount = parent.getChildCount()
        for (i in 0 until childCount)
        {
            val child = parent.getChildAt(i)
            val params = child
                    .getLayoutParams() as RecyclerView.LayoutParams
            val left = Math.max(recyclerViewLeft, child.getRight() + params.rightMargin)
            val right = Math.min(recyclerViewRight, left + mDivider.getIntrinsicHeight())
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }
    override fun getItemOffsets(outRect: Rect, view: View, parent:RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (getOrientation(parent) == VERTICAL_LIST)
        {
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight())
        }
        else
        {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0)
        }
    }
    companion object {
        private val ATTRS : Array<Int>  = arrayOf(android.R.attr.listDivider)
        val HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL
        val VERTICAL_LIST = LinearLayoutManager.VERTICAL
    }
}
