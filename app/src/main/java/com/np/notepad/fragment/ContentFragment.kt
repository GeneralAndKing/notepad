package com.np.notepad.fragment

import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.np.notepad.R
import com.np.notepad.base.BaseFragment
import com.np.notepad.databinding.FragmentContentLayoutBinding
import com.np.notepad.util.LoggerUtils
import com.yuruiyin.richeditor.enumtype.RichTypeEnum
import com.yuruiyin.richeditor.model.StyleBtnVm
import kotlinx.android.synthetic.main.fragment_content_layout.*

class ContentFragment : BaseFragment() {
  //绑定XML布局文件
  private lateinit var binding: FragmentContentLayoutBinding

  override fun onCreateView(): View {
    binding = FragmentContentLayoutBinding.inflate(LayoutInflater.from(activity), null, false)
    initTopBar()
    if (arguments != null) {
      LoggerUtils.i(arguments.toString())
    }
    return binding.root
  }

    /**
     * 初始化bar
     */
    private fun initTopBar() {
        binding.topbar.addLeftBackImageButton()
            .setOnClickListener { popBackStack() }
        binding.topbar.setTitle("编辑事件")
        binding.topbar.addRightImageButton(R.drawable.icon_confirm, R.id.topbar_right_change_button)
            .setOnClickListener { LoggerUtils.i("点击了保存按钮") }
    }

  override fun translucentFull(): Boolean = true


  /**
   * 粗体
   */
  private fun initBold() {
    val styleBtnVm = StyleBtnVm.Builder()
      .setType(RichTypeEnum.BOLD)
      .setIvIcon(ivBold)
      .setIconNormalResId(R.mipmap.icon_bold_normal)
      .setIconLightResId(R.mipmap.icon_bold_light)
      .setClickedView(ivBold)
      .build()
    richEditText.initStyleButton(styleBtnVm)
  }

  /**
   * 斜体
   */
  private fun initItalic() {
    val styleBtnVm = StyleBtnVm.Builder()
      .setType(RichTypeEnum.ITALIC)
      .setIvIcon(ivItalic)
      .setIconNormalResId(R.mipmap.icon_italic_normal)
      .setIconLightResId(R.mipmap.icon_italic_light)
      .setClickedView(ivItalic)
      .build()

    richEditText.initStyleButton(styleBtnVm)
  }

  /**
   * 删除线
   */
  private fun initStrikeThrough() {
    val styleBtnVm = StyleBtnVm.Builder()
      .setType(RichTypeEnum.STRIKE_THROUGH)
      .setIvIcon(ivStrikeThrough)
      .setIconNormalResId(R.mipmap.icon_strikethrough_normal)
      .setIconLightResId(R.mipmap.icon_strikethrough_light)
      .setClickedView(ivStrikeThrough)
      .build()

    richEditText.initStyleButton(styleBtnVm)
  }

  /**
   * 下划线
   */
  private fun initUnderline() {
    val styleBtnVm = StyleBtnVm.Builder()
      .setType(RichTypeEnum.UNDERLINE)
      .setIvIcon(ivUnderline)
      .setIconNormalResId(R.mipmap.icon_underline_normal)
      .setIconLightResId(R.mipmap.icon_underline_light)
      .setClickedView(ivUnderline)
      .build()

    richEditText.initStyleButton(styleBtnVm)
  }

  /**
   * 标题
   */
  private fun initHeadline() {
    val styleBtnVm = StyleBtnVm.Builder()
      .setType(RichTypeEnum.BLOCK_HEADLINE)  // 指定为段落标题类型
      .setIvIcon(ivHeadline)       // 图标ImageView，用于修改高亮状态
      .setIconNormalResId(R.mipmap.icon_headline_normal)  // 正常图标
      .setIconLightResId(R.mipmap.icon_headline_light)    // 高亮图标
//      .setClickedView(vgHeadline)  // 指定被点击的view
//      .setTvTitle(tvHeadline)      // 按钮标题文字
      .setTitleNormalColor(
        ContextCompat.getColor(
          requireContext(),
          R.color.headline_normal_text_color
        )
      ) // 正常标题文字颜色
      .setTitleLightColor(
        ContextCompat.getColor(
          requireContext(),
          R.color.headline_light_text_color
        )
      )   // 高亮标题文字颜色
      .build()

    richEditText.initStyleButton(styleBtnVm)
  }

  /**
   * 下划线
   */
  private fun initBlockQuote() {
    val styleBtnVm = StyleBtnVm.Builder()
      .setType(RichTypeEnum.BLOCK_QUOTE)
      .setIvIcon(ivBlockquote)
      .setIconNormalResId(R.mipmap.icon_blockquote_normal)
      .setIconLightResId(R.mipmap.icon_blockquote_light)
      .setClickedView(ivBlockquote)
      .build()

    richEditText.initStyleButton(styleBtnVm)
  }
}