package com.np.notepad.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.np.notepad.R
import com.np.notepad.activity.MainActivity
import com.np.notepad.base.BaseFragment
import com.np.notepad.databinding.FragmentContentLayoutBinding
import com.np.notepad.manager.DatabaseManager
import com.np.notepad.model.NoteItem
import com.np.notepad.model.enums.ItemSkinEnum
import com.np.notepad.util.ConstUtils
import com.np.notepad.util.Log
import com.qmuiteam.qmui.alpha.QMUIAlphaImageButton
import com.yuruiyin.richeditor.enumtype.RichTypeEnum
import com.yuruiyin.richeditor.model.StyleBtnVm
import kotlinx.android.synthetic.main.fragment_content_layout.*

class ContentFragment : BaseFragment(), View.OnFocusChangeListener, View.OnClickListener {
  //绑定XML布局文件
  private lateinit var binding: FragmentContentLayoutBinding
  //item
  private lateinit var noteItem: NoteItem
  //保存按钮
  private lateinit var rightImageButton: QMUIAlphaImageButton
  //itemId
  private var id: Long = 0

  override fun onCreateView(): View {
    binding = FragmentContentLayoutBinding.inflate(LayoutInflater.from(activity), null, false)
    initTopBar()
    Log.i(arguments.toString())
    if (arguments != null) {
      id = (arguments as Bundle).getLong(ConstUtils.ITEM_ID)
    }
//    registerEvents()
    initEdit()
    return binding.root
  }

  override fun translucentFull(): Boolean = true

  override fun onLastFragmentFinish(): Any {
    return HomeFragment()
  }

  // 焦点监听
  override fun onFocusChange(v: View?, hasFocus: Boolean) {
    if (v != null) {
      if (v.id == R.id.etTitle || v.id == R.id.richEditText) {
        rightImageButton.visibility = View.VISIBLE
      }
    }
  }

  // 点击事件监听
  override fun onClick(v: View?) {
    if (v != null) {
      if (v.id == R.id.richEditText)
        rightImageButton.visibility = View.VISIBLE
    }
  }

  private fun initEdit() {
    if (id != 0L) {
      noteItem = DatabaseManager.getInstance().find(id)!!
      binding.etTitle.setText(noteItem.title)
      binding.richEditText.setText(noteItem.content)
    } else {
      noteItem = NoteItem()
    }
    // 监听焦点
    binding.etTitle.onFocusChangeListener = this
    binding.richEditText.onFocusChangeListener = this
    binding.richEditText.setOnClickListener(this)
  }

  /**
   * 初始化bar
   */
  private fun initTopBar() {
    binding.topbar.addLeftBackImageButton()
      .setOnClickListener { popBackStack() }
    binding.topbar.setTitle("编辑事件")
    rightImageButton = binding.topbar.addRightImageButton(R.drawable.icon_confirm, R.id.topbar_right_change_button)
    rightImageButton.visibility = View.INVISIBLE
    rightImageButton.setOnClickListener {
      saveNotepad()
      rightImageButton.visibility = View.INVISIBLE
    }
  }

  private fun saveNotepad() {
    noteItem.title = binding.etTitle.text.toString()
    noteItem.content = binding.richEditText.text.toString()
    // 随机选取背景色
    noteItem.background = ItemSkinEnum.values()[(ItemSkinEnum.values().indices).random()].name
    if (id != 0L) {
      DatabaseManager.getInstance().update(noteItem)
      // 判断是否需要刷新通知
      if (noteItem.remind) {
        callNotificationService(noteItem.id, true)
      }
    } else {
      DatabaseManager.getInstance().save(noteItem)
    }
    (requireActivity() as MainActivity).isChange = true
  }

  private fun registerEvents() {
    // 清空内容
    btnClearContent.setOnClickListener {
      richEditText.clearContent()
    }
    // 粗体
    initBold()
    // 斜体
    initItalic()
    // 删除线
    initStrikeThrough()
    // 下划线
    initUnderline()
    // 标题
    initHeadline()
    // 引用
    initBlockQuote()
  }

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