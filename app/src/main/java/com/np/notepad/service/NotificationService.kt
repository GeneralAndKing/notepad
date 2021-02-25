package com.np.notepad.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.np.notepad.manager.DatabaseManager
import com.np.notepad.manager.NotificationManager
import com.np.notepad.manager.PreferenceManager
import com.np.notepad.util.ConstUtils

class NotificationService: Service() {
  private var receiver: LockScreenReceiver? = null
  //ids
  private var notificationIds = mutableSetOf<String>()

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  //Service被创建时调用
  override fun onCreate() {
    super.onCreate()
    // 初始化通知ids
    notificationIds.addAll(PreferenceManager.getInstance().noticeIds)
    //注册锁屏广播接收者
    receiver = LockScreenReceiver()
    val intentFilter = IntentFilter()
    intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
    registerReceiver(receiver, intentFilter)
  }

  //Service被启动时调用
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    try {
      val longExtra = intent!!.getLongExtra(ConstUtils.ITEM_ID, 0)
      val show = intent.getBooleanExtra(ConstUtils.SHOW_OR_NOT, false)
      // 显示
      if (show) {
        val find = DatabaseManager.getInstance().find(longExtra)
        if (find != null) {
          // 显示通知
          NotificationManager.getInstance().showNotification(find)
          notificationIds.add(find.id.toString())
          // 保存本地
          PreferenceManager.getInstance().noticeIds = notificationIds
        }
      } else {
        // 存在则关闭通知
        if (notificationIds.contains(longExtra.toString())) {
          notificationIds.remove(longExtra.toString())
          NotificationManager.getInstance().cancelNotification(longExtra)
          // 保存本地
          PreferenceManager.getInstance().noticeIds = notificationIds
          // 判断是否销毁
          if (notificationIds.size == 0) {
            stopSelf(startId)
          }
        }
      }
    } catch (e: Exception) {
      DatabaseManager.getInstance().log(e.message.toString())
    }
    return START_STICKY
  }

  //Service被关闭之前回调
  override fun onDestroy() {
    // 保存本地
    PreferenceManager.getInstance().noticeIds = notificationIds
    notificationIds.clear()
    //取消锁屏的广播监听
    unregisterReceiver(receiver)
    super.onDestroy()
  }

  /**
   * 锁屏广播接收
   */
  private class LockScreenReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
      // 如果是锁屏且配置为开启
      if (Intent.ACTION_SCREEN_OFF == intent!!.action && PreferenceManager.getInstance().lockScreenNotice) {
        val ids: List<Long> = mutableListOf()
        PreferenceManager.getInstance().noticeIds.toSet().forEach{id -> ids.plus(id)}
        val all = DatabaseManager.getInstance().getAllById(ids.toLongArray())
        for (item in all) {
          NotificationManager.getInstance().showNotification(item)
        }
      }
    }
  }
}