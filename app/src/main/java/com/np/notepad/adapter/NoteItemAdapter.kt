package com.np.notepad.adapter

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.np.notepad.R
import com.np.notepad.model.NoteItem
import com.np.notepad.util.ColorUtils
import com.np.notepad.util.DateUtils
import java.util.*

/**
 * 记事本列表的适配器
 * @author z f
 */
class NoteItemAdapter(
    layoutId:Int,
    data:MutableList<NoteItem>? = null,
    itemCount:Int,
    context: Context
): BaseQuickAdapter<NoteItem, BaseViewHolder>(layoutId, data) {

    init {
        mContext = context
        if (data!!.size == 0 && itemCount != 0) {
            for (i in 1..itemCount) {
                val noteItem = NoteItem()
                noteItem.title = "标题"
                noteItem.lastUpdateTime = Date()
                data.add(noteItem)
            }
        }
    }

    override fun convert(helper: BaseViewHolder, item: NoteItem?) {
        // data color
        val colorString: String?
        // text
        val text: Spanned? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            colorString = ColorUtils.colorToString(
                mContext.resources.getColor(R.color.note_grey, null))
            Html.fromHtml(
                "<font color=\"#000000\">" + item?.title +
                  "</font><font color=\"" + colorString + "\"><small>" + DateUtils.
                  getDateTimeByFormat(item?.lastUpdateTime, DateUtils.DATE_TIME_FORMAT_1) +
                  "</small></font>"
                ,Html.FROM_HTML_MODE_COMPACT)
        } else {
            colorString = ColorUtils.colorToString(
                mContext.resources.getColor(R.color.note_grey))
            Html.fromHtml("<font color=\"#000000\">" + item?.title +
              "<br/></font><font color=\"" + colorString + "\"><small>" + DateUtils.
            getDateTimeByFormat(item?.lastUpdateTime, DateUtils.DATE_TIME_FORMAT_1) +
              "</small></font>")
        }
        helper.setText(R.id.textView, text)
            .addOnClickListener(
                R.id.textView,
                R.id.btnDelete,
                R.id.btnRemind)
    }
}