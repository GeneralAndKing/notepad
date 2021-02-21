package com.np.notepad.model

import com.np.notepad.model.enums.ItemSkinEnum
import org.litepal.crud.LitePalSupport
import java.util.*

/**
 * 笔记item
 * @author z f
 */
data class NoteItem (
  var id: Long = 0,
  var title: String = "",
  var content: String = "",
  /**置顶*/
    var top: Boolean = false,
  /**强提醒*/
    var remind: Boolean = false,
  /**笔记类别*/
    var background: String = ItemSkinEnum.GREEN.name,
  var lastUpdateTime: Date = Date()
): LitePalSupport()