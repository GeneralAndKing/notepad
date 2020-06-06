package com.np.notepad.adapter

import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.np.notepad.R
import com.np.notepad.model.NoteItem

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
                data.add(NoteItem())
            }
        }
    }

    override fun convert(helper: BaseViewHolder, item: NoteItem?) {
        helper.setText(R.id.textView, "" + (helper.adapterPosition + 1))
            .addOnClickListener(
                R.id.textView,
                R.id.btnDelete,
                R.id.btnRemind)
    }
}