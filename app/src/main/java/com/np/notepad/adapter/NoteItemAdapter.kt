package com.np.notepad.adapter

import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.np.notepad.R
import com.np.notepad.model.NoteItem
import com.np.notepad.model.enums.BackgroundTypeEnum
import com.np.notepad.util.StringUtils

/**
 * 记事本列表的适配器
 * @author z f
 */
class NoteItemAdapter(
    layoutId:Int,
    data:MutableList<NoteItem>? = null,
    context: Context
): BaseQuickAdapter<NoteItem, BaseViewHolder>(layoutId, data) {

    init {
        mContext = context
    }

    override fun convert(helper: BaseViewHolder, item: NoteItem?) {
        val backgroundEnum = BackgroundTypeEnum.getEnumByName(item!!.background)
        backgroundEnum.resId.let {
            var text =
                StringUtils.getTitleHtmlText(mContext, item.title, item.lastUpdateTime, false)
            // 置顶
            if (item.top) {
                text = StringUtils.getTitleHtmlText(mContext, item.title, item.lastUpdateTime, true)
                //改变按钮
                helper.setBackgroundRes(R.id.btnTopping, R.drawable.btn_topping_close)
            } else {
                //防止图标错乱
                helper.setBackgroundRes(R.id.btnTopping, R.drawable.btn_topping)
            }
            // 提醒
            if (item.remind) {
                //改变按钮
                helper.setBackgroundRes(R.id.btnRemind, R.drawable.btn_remind_close)
            } else {
                //防止图标错乱
                helper.setBackgroundRes(R.id.btnRemind, R.drawable.btn_remind)
            }
            helper.setText(R.id.textView, text)
                .setBackgroundRes(R.id.textView, it)
                .addOnClickListener(
                    R.id.textView,
                    R.id.btnDelete,
                    R.id.btnRemind,
                    R.id.btnSkin,
                    R.id.btnTopping)
        }
    }
}