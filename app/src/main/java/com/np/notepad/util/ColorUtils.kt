package com.np.notepad.util

import androidx.annotation.ColorInt

class ColorUtils private constructor(){
  companion object {
    /**
     * 将 color 颜色值转换为十六进制字符串
     *
     * @param color 颜色值
     * @return 转换后的字符串
     */
    fun colorToString(@ColorInt color: Int): String? {
      val format = String.format("#%08X", color)
      return "#" + format.substring(3)
    }
  }
}