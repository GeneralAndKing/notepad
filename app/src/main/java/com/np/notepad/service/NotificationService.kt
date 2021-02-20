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

  //当前item
  private lateinit var model: NoteItem
  //ids
  private val notificationIds = mutableListOf<Long>()

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
    try {
      val longExtra = intent!!.getLongExtra(ConstUtils.ITEM_ID, 0)
      if (notificationIds.contains(longExtra)) {
        NotificationManager.getInstance().cancelNotification(longExtra)
        if (notificationIds.size - 1 == 0) {
          stopSelf(startId)
        } else {
          notificationIds.remove(longExtra)
        }
      } else {
        val find = DatabaseManager.getInstance().find(longExtra)
        if (find != null) {
          model = find
          NotificationManager.getInstance().showNotification(model)
          notificationIds.add(model.id)
        } else {
          throw java.lang.Exception("查无此id")
        }
      }
    } catch (e: Exception) {
      val noteItem = NoteItem()
      noteItem.title = "异常日志"
      noteItem.content = e.message.toString()
      DatabaseManager.getInstance().save(noteItem)
    }
    return START_STICKY
  }

  //Service被关闭之前回调
  override fun onDestroy() {
    LoggerUtils.i("NotificationService停止")
    super.onDestroy()
  }
}