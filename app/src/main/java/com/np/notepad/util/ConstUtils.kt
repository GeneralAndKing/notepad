package com.np.notepad.util

/**
 * 常量工具类
 * @author z f
 */
class ConstUtils private constructor() {
    companion object {
        /**
         * APP名称
         */
        const val APP_NAME: String = "记得做"

        const val ITEM_ID: String = "itemId"

        const val NOTIFICATION_TICKER: String = "您有待做的项目~"
        /**
         * 通知的request值
         */
        const val NOTIFICATION_REQUEST: Int = 1
    }
}