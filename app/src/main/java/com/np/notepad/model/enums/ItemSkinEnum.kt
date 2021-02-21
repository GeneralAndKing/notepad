package com.np.notepad.model.enums

import com.np.notepad.R

/**
 * 笔记背景类型
 * @author z f
 */
enum class ItemSkinEnum(val resId: Int, val des: String) {
    RED(R.drawable.item_red_background,"红色"),
    YELLOW(R.drawable.item_yellow_background,"黄色"),
    CYAN(R.drawable.item_cyan_background,"青色"),
    BLUE(R.drawable.item_blue_background,"蓝色"),
    GREEN(R.drawable.item_green_background,"绿色");

    companion object {
        /**
         * 获取DesArray
         */
        fun getDesArray(): Array<String> {
            val desArray: Array<String> = Array(values().size) {""}
            for ((i, noteType) in values().withIndex()) {
                desArray[i] = noteType.des
            }
            return desArray
        }

        /**
         * 根据name获取枚举
         */
        fun getEnumByName(codeVal: String?): ItemSkinEnum {
            for (e in values()) {
                if (e.name.equals(codeVal, ignoreCase = true)) {
                    return e
                }
            }
            return GREEN
        }
    }
}