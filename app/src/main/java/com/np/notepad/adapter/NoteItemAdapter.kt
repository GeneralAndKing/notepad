package com.np.notepad.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.np.notepad.model.NoteItem

/**
 * 记事本列表的适配器
 * @author z f
 */
class NoteItemAdapter(
    layoutId:Int,
    data:MutableList<NoteItem>? = null
): BaseQuickAdapter<NoteItem, BaseViewHolder>(layoutId, data){
    override fun convert(helper: BaseViewHolder, item: NoteItem?) {
        //To change body of created functions use File | Settings | File Templates.
    }
}