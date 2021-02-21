package com.np.notepad.model.enums

import com.np.notepad.R

/**
 * app主题
 */
enum class ThemeSkinEnum(val code: Int, val styleId: Int, val des: String) {
        SKIN_BLUE(1, R.style.app_skin_blue, "蓝色主题"),
        SKIN_DARK(2, R.style.app_skin_dark, "黑色主题"),
        SKIN_WHITE(3, R.style.app_skin_white, "白色主题");

        companion object {
                /**
                 * 获取描述
                 */
                fun getDes(): Array<String> {
                        val desArray: Array<String> = Array(values().size) {""}
                        for ((i, theme) in values().withIndex()) {
                                desArray[i] = theme.des
                        }
                        return desArray
                }

                /**
                 * 获取code在values中坐标
                 */
                fun getValuesIndex(codeVal: Int?): Int {
                        for ((i, e) in values().withIndex()) {
                                if (e.code == codeVal) {
                                        return i
                                }
                        }
                        return 0
                }
        }
}
