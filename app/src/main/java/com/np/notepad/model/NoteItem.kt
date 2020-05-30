package com.np.notepad.model

import android.graphics.Color
import com.np.notepad.model.enums.NoteTypeEnum
import java.util.*

/**
 * 笔记item
 * @author z f
 */
data class NoteItem (
    private var id: Int = 0,
    private var title: String = "",
    private var content: String = "",
    private var backgroundColor: Int = Color.WHITE,
    /**置顶*/
    private var top: Boolean = false,
    /**强提醒*/
    private var remind: Boolean = false,
    /**笔记类别*/
    private var noteType: String = NoteTypeEnum.DEFAULT.code,
    private var lastUpdateTime: Date = Date())