package com.np.notepad.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.np.notepad.manager.DatabaseManager
import com.np.notepad.manager.NotificationManager
import com.np.notepad.model.NoteItem
import com.np.notepad.util.ConstUtils
import com.np.notepad.util.LoggerUtils

class NotificationService: Service() {

  private lateinit var model: NoteItem

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  //Service被创建时调用
  override fun onCreate() {
    LoggerUtils.i("NotificationService启动")
    super.onCreate()
  }

  //Service被启动时调用
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    LoggerUtils.i("NotificationService onStartCommand")
    val longExtra = intent!!.getLongExtra(ConstUtils.ITEM_ID, 0)
    val find = DatabaseManager.getInstance().find(longExtra)
    if (find != null) {
      model = find
      NotificationManager.getInstance().showNotification(model)
    } else {
      LoggerUtils.e("NotificationService:查无id")
    }
    return super.onStartCommand(intent, flags, startId)
  }

  //Service被关闭之前回调
  override fun onDestroy() {
    NotificationManager.getInstance().cancelNotification(model.id)
    LoggerUtils.i("NotificationService停止")
    super.onDestroy()
  }
}