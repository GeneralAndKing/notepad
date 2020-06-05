package com.np.notepad

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.np.notepad.manager.QDSkinManager
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager

/**
 * app
 */
class NoteApplication : Application() {

    companion object {
        var context: Context? = null
        var openSkinMake = false
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        //手势返回
        QMUISwipeBackActivityManager.init(this)
        QDSkinManager.install(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            QDSkinManager.changeSkin(QDSkinManager.SKIN_DARK)
        } else if (QDSkinManager.getCurrentSkin() == QDSkinManager.SKIN_DARK) {
            QDSkinManager.changeSkin(QDSkinManager.SKIN_BLUE)
        }
    }
}