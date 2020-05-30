package com.np.notepad.activity

import android.content.Intent
import android.os.Bundle
import com.qmuiteam.qmui.arch.QMUIActivity

/**
 * 起始页
 * @author zf
 */
class LauncherActivity: QMUIActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this@LauncherActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}