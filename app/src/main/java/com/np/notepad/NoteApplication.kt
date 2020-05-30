package com.np.notepad

import android.app.Application
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager

/**
 * app
 */
class NoteApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //手势返回需要注册 ActivityLifecycleCallbacks
        QMUISwipeBackActivityManager.init(this)
    }
}