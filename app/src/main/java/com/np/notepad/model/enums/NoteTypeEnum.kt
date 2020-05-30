package com.np.notepad.model.enums


/**
 * 笔记类型
 * @author z f
 */
enum class NoteTypeEnum(val code: String) {
    /**常规*/
    DEFAULT("DEFAULT");

    /**
     * 获取参数String的枚举类型
     */
    fun getEnumByCode(codeVal: String): NoteTypeEnum {
        for (noteType in values()) {
            if (noteType.code == codeVal) {
                return noteType
            }
        }
        //默认
        return DEFAULT
    }


}