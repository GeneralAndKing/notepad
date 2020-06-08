package com.np.notepad.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.np.notepad.R
import com.np.notepad.adapter.NoteItemAdapter
import com.np.notepad.base.BaseFragment
import com.np.notepad.databinding.FragmentHomeLayoutBinding
import com.np.notepad.manager.DatabaseManager
import com.np.notepad.model.NoteItem
import com.np.notepad.model.enums.BackgroundTypeEnum
import com.np.notepad.util.ConstUtils.Companion.ITEM_ID
import com.np.notepad.util.LoggerUtils
import com.qmuiteam.qmui.skin.QMUISkinManager
import com.qmuiteam.qmui.widget.dialog.QMUIDialog.CheckableDialogBuilder
import java.util.*


class HomeFragment : BaseFragment() {
    //绑定XML布局文件
    private lateinit var binding: FragmentHomeLayoutBinding
    //适配器
    private lateinit var mRecyclerViewAdapter: NoteItemAdapter
    private var initDataTop: Int = 0

    override fun onCreateView(): View {
        binding = FragmentHomeLayoutBinding.inflate(
            LayoutInflater.from(activity), null, false
        )
        initTopBar()
        initList()
        return binding.root
    }

    /**
     * 初始化bar
     */
    private fun initTopBar() {
        binding.topbar.addRightImageButton(R.mipmap.icon_add, R.id.topbar_right_change_button)
            .setOnClickListener {startContentFragment(0)}
        binding.collapsingTopbarLayout.title = getString(R.string.app_name)
        binding.collapsingTopbarLayout.setScrimUpdateListener { animation ->
            LoggerUtils.i("scrim: " + animation.animatedValue)
        }
        binding.collapsingTopbarLayout.addOnOffsetUpdateListener { _, offset, expandFraction ->
            LoggerUtils.i("offset = $offset; expandFraction = $expandFraction")
            if (initDataTop <= 1) {
                initDataTop ++
                initTop()
            }
        }
    }

    /**
     * 初始化内容列表
     */
    private fun initList() {
        mRecyclerViewAdapter = NoteItemAdapter(
            R.layout.note_item,
            DatabaseManager.getInstance().getAll(),
            10,
            requireContext())
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        //开启动画效果
        mRecyclerViewAdapter.openLoadAnimation()
        //设置动画效果
        mRecyclerViewAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN)
        //设置Item子控件点击事件
        mRecyclerViewAdapter.setOnItemChildClickListener { _, view, position ->
            val item = mRecyclerViewAdapter.getItem(position)!!
            when(view.id) {
                R.id.textView -> startContentFragment(item.id)
                R.id.btnRemind -> LoggerUtils.i("click btnRemind")
                R.id.btnDelete -> mRecyclerViewAdapter.remove(position)
                R.id.btnTopping -> setItemTop(item, position, view)
                R.id.btnSkin -> {
                    var i = 0
                    for(background in BackgroundTypeEnum.values()) {
                        if (item.background == background) {
                            showSingleBackgroundDialog(
                                item,
                                position,
                                i,
                                BackgroundTypeEnum.getDesArray())
                            break
                        }
                        i ++
                    }
                }
            }
        }
        //设置适配器
        binding.recyclerView.adapter = mRecyclerViewAdapter
    }

    /**
     * 选择item置顶
     * @param item 当前选择背景的item
     * @param position item位置
     */
    private fun setItemTop(item: NoteItem, position: Int, view: View) {
        //保存数据库
        if (!item.top) {
            item.top = true
//        DatabaseManager.getInstance().update(item)
            val viewByPosition: TextView = mRecyclerViewAdapter.getViewByPosition(
                binding.recyclerView,
                position,
                R.id.textView
            ) as TextView
            viewByPosition.text = viewByPosition.text.toString().plus("  ⇧")
            mRecyclerViewAdapter.notifyItemMoved(position, 0)
            binding.recyclerView.scrollToPosition(0)
        }
    }

    /**
     * 初始化置顶
     */
    private fun initTop() {
        LoggerUtils.i("initTop:"+mRecyclerViewAdapter.noteItemTops.size)
        mRecyclerViewAdapter.noteItemTops.sort()
        for(position in mRecyclerViewAdapter.noteItemTops) {
            val viewByPosition: TextView = mRecyclerViewAdapter.getViewByPosition(
                binding.recyclerView,
                position,
                R.id.textView
            ) as TextView
            viewByPosition.text = viewByPosition.text.toString().plus("  ⇧")
            mRecyclerViewAdapter.notifyItemMoved(position, 0)
        }
        binding.recyclerView.scrollToPosition(0)
    }

    /**
     * 选择背景颜色弹窗
     * @param item 当前选择背景的item
     * @param position item位置
     * @param checkedIndex 选中index
     * @param items 选择列表
     * @return int
     */
    private fun showSingleBackgroundDialog(item: NoteItem,
                                           position: Int,
                                           checkedIndex: Int,
                                           items: Array<String>) {
        CheckableDialogBuilder(activity)
            .setCheckedIndex(checkedIndex)
            .setSkinManager(QMUISkinManager.defaultInstance(context))
            .addItems(items) { dialog, which ->
                dialog.cancel()
                item.background = BackgroundTypeEnum.values()[which]
                //保存到数据库
//                DatabaseManager.getInstance().update(item)
                //刷新item
                mRecyclerViewAdapter.refreshNotifyItemChanged(position)
            }
            .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show()
    }

    /**
     * 跳转到编辑内容fragment
     */
    private fun startContentFragment(id: Long) {
        val fragment = ContentFragment()
        val args = Bundle()
        args.putLong(ITEM_ID, id)
        fragment.arguments = args
        startFragment(fragment)
    }

    override fun onResume() {
        super.onResume()
        val noteItem = NoteItem()
        noteItem.title = "新增"
        noteItem.lastUpdateTime = Date()
        //后续需要读取数据库获取置顶数量
        mRecyclerViewAdapter.addData(0, noteItem)
        //保存到数据库
    }

    override fun translucentFull(): Boolean = true

    override fun canDragBack(): Boolean = false

    /**
     * 上一页
     */
    override fun onLastFragmentFinish(): Any = null!!
}