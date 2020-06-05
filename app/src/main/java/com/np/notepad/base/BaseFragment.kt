package com.np.notepad.base

import android.content.Context
import com.np.notepad.fragment.HomeFragment
import com.qmuiteam.qmui.arch.QMUIFragment
import com.qmuiteam.qmui.arch.SwipeBackLayout
import com.qmuiteam.qmui.util.QMUIDisplayHelper

abstract class BaseFragment : QMUIFragment() {

    override fun backViewInitOffset(
        context: Context,
        dragDirection: Int,
        moveEdge: Int
    ): Int {
        return if (moveEdge == SwipeBackLayout.EDGE_TOP || moveEdge == SwipeBackLayout.EDGE_BOTTOM) 0
        else QMUIDisplayHelper.dp2px(context, 100)
    }

    override fun onLastFragmentFinish(): Any {
        return HomeFragment()
    }
}