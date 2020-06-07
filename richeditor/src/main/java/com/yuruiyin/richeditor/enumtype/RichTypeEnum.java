package com.yuruiyin.richeditor.enumtype;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Title:
 * Description:
 *
 * @author yuruiyin
 * @version 2019-04-30
 */

@Retention(RetentionPolicy.SOURCE)
@StringDef({RichTypeEnum.BOLD, RichTypeEnum.ITALIC, RichTypeEnum.STRIKE_THROUGH, RichTypeEnum.UNDERLINE,
    RichTypeEnum.INLINE_IMAGE_SPAN, RichTypeEnum.BLOCK_HEADLINE, RichTypeEnum.BLOCK_QUOTE,
    RichTypeEnum.BLOCK_NORMAL_TEXT})
public @interface RichTypeEnum {

    /**
     * 加粗
     */
    String BOLD = "bold";

    /**
     * 斜体
     */
    String ITALIC = "italic";

    /**
     * 删除线
     */
    String STRIKE_THROUGH = "strike_through";

    /**
     * 下划线
     */
    String UNDERLINE = "underline";

    /**
     * 行内ImageSpan
     */
    String INLINE_IMAGE_SPAN = "inline_image_span";

    /**
     * 段落标题
     */
    String BLOCK_HEADLINE = "block_headline";

    /**
     * 段落引用
     */
    String BLOCK_QUOTE = "block_quote";

    /**
     * 段落普通文本（但是可能包含行内样式）
     */
    String BLOCK_NORMAL_TEXT = "block_normal_text";

}
