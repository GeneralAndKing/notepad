package com.np.notepad.manager

import android.app.Notification
import android.app.NotificationChannel
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.ComponentName
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.np.notepad.NoteApplication
import com.np.notepad.R
import com.np.notepad.activity.MainActivity
import com.np.notepad.model.NoteItem
import com.np.notepad.util.ConstUtils

class NotificationManager private constructor() {
  private var mContext: Context = NoteApplication.context
  private var mNManager:android.app.NotificationManager

  init {
    mNManager = mContext.getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
  }

  companion object {
    fun getInstance() = SingleHolder.INSTANCE
  }

  fun showNotification(model: NoteItem) {
    //设置Intent 返回应用
    val it = Intent(Intent.ACTION_MAIN)
    it.addCategory(Intent.CATEGORY_LAUNCHER)
    it.component = ComponentName(mContext.packageName, MainActivity::class.java.canonicalName!!)
    //设置PendingIntent
    val pit: PendingIntent = PendingIntent.getActivity(
      mContext, ConstUtils.NOTIFICATION_REQUEST, it, FLAG_UPDATE_CURRENT)
    //设置图片,通知标题,发送时间,提示方式等属性
    val builder: NotificationCompat.Builder?
    builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      //8.0以上需要创建 Channel 渠道
      mNManager.createNotificationChannel(NotificationChannel(
        model.id.toString(), ConstUtils.APP_NAME,
        android.app.NotificationManager.IMPORTANCE_DEFAULT
      ))
      NotificationCompat.Builder(mContext, model.id.toString())
    } else {
      NotificationCompat.Builder(mContext)
    }
    val build = builder.setContentTitle(model.title)
      //当通知内容太长一行显示不了，可以使用setStyle
      .setStyle(NotificationCompat.BigTextStyle().bigText(model.content))
      //收到信息后状态栏显示的文字信息
      .setTicker(ConstUtils.NOTIFICATION_TICKER)
      //通知时间
      .setWhen(System.currentTimeMillis())
      .setSmallIcon(R.mipmap.ic_notepad)
      .setLargeIcon(BitmapFactory.decodeResource(mContext.resources, R.mipmap.ic_notepad))
      //true：点击通知栏，通知消失
      .setAutoCancel(false)
      .setContentIntent(pit)
      //通知默认的声音 震动 呼吸灯
      .setDefaults(NotificationCompat.DEFAULT_ALL)
      //设置优先级
      .setPriority(NotificationCompat.PRIORITY_MAX)
      .build()
    build.flags = Notification.FLAG_NO_CLEAR
    mNManager.notify(model.id.toInt(), build)
  }

  fun cancelNotification(id: Long) {
    mNManager.cancel(id.toInt())
  }

  private object SingleHolder {
    val INSTANCE: NotificationManager = NotificationManager()
  }
}