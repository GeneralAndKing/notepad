package com.np.notepad.util

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import com.np.notepad.R
import java.util.*

class StringUtils private constructor() {
  companion object {
    private const val TOP_CHAR = "  ⇧置顶"
    /**
     * 获取标题的html文本字符串
     * @param top 是否需要置顶
     */
    fun getTitleHtmlText(context: Context ,title: String, date: Date, top: Boolean): Spanned {
      // date
      var dateString = DateUtils.getDateTimeByFormat(date, DateUtils.DATE_TIME_FORMAT_1)
      if (top) {
        dateString = dateString.plus(TOP_CHAR)
      }
      // data color
      val colorString: String?
      // text
      return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        colorString = ColorUtils.colorToString(
          context.resources.getColor(R.color.note_date_color_grey, null))
        Html.fromHtml(
          "<font color=\"#000000\">"
            .plus(title)
            .plus("<br/></font><font color=\"$colorString\"><small>")
            .plus(dateString)
            .plus("</small></font>")
          , Html.FROM_HTML_MODE_COMPACT)
      } else {
        colorString = ColorUtils.colorToString(
          context.resources.getColor(R.color.note_date_color_grey))
        Html.fromHtml(
          "<font color=\"#000000\">"
            .plus(title)
            .plus("<br/></font><font color=\"$colorString\"><small>")
            .plus(dateString)
            .plus("</small></font>"))
      }
    }
  }
}