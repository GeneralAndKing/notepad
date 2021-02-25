package com.np.notepad.base

import android.content.Context
import android.content.Intent
import com.np.notepad.service.NotificationService
import com.np.notepad.util.ConstUtils
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

    /**
     * 调用通知服务
     */
    fun callNotificationService(id: Long, show: Boolean) {
        val it = Intent(requireActivity(), NotificationService::class.java)
        it.putExtra(ConstUtils.ITEM_ID, id)
        it.putExtra(ConstUtils.SHOW_OR_NOT, show)
        requireActivity().startService(it)
    }
}