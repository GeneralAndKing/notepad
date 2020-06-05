package com.np.notepad.model

import android.graphics.Color
import com.np.notepad.model.enums.NoteTypeEnum
import java.util.*

/**
 * 笔记item
 * @author z f
 */
data class NoteItem (
    var id: Int = 0,
    var title: String = "",
    var content: String = "",
    var backgroundColor: Int = Color.WHITE,
    /**置顶*/
    var top: Boolean = false,
    /**强提醒*/
    var remind: Boolean = false,
    /**笔记类别*/
    var noteType: String = NoteTypeEnum.DEFAULT.code,
    var lastUpdateTime: Date = Date())