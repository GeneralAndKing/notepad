package com.np.notepad

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.np.notepad.manager.SkinManager
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager
import org.litepal.LitePal

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
        LitePal.initialize(this)
        //手势返回
        QMUISwipeBackActivityManager.init(this)
        SkinManager.install(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            SkinManager.changeSkin(SkinManager.SKIN_DARK)
        } else if (SkinManager.getCurrentSkin() == SkinManager.SKIN_DARK) {
            SkinManager.changeSkin(SkinManager.SKIN_BLUE)
        }
    }
}