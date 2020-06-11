package com.np.notepad.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import com.np.notepad.R
import com.np.notepad.adapter.NoteItemAdapter
import com.np.notepad.base.BaseFragment
import com.np.notepad.databinding.FragmentHomeLayoutBinding
import com.np.notepad.manager.DatabaseManager
import com.np.notepad.model.NoteItem
import com.np.notepad.model.enums.BackgroundTypeEnum
import com.np.notepad.service.NotificationService
import com.np.notepad.util.ConstUtils.Companion.ITEM_ID
import com.np.notepad.util.LoggerUtils
import com.np.notepad.util.StringUtils
import com.qmuiteam.qmui.skin.QMUISkinManager
import com.qmuiteam.qmui.widget.dialog.QMUIDialog.CheckableDialogBuilder
import java.util.*

class HomeFragment : BaseFragment() {
    //绑定XML布局文件
    private lateinit var binding: FragmentHomeLayoutBinding
    //适配器
    private lateinit var mRecyclerViewAdapter: NoteItemAdapter
    private var initDataTop: Int = 0
    //置顶数量
    private var topSize: Int =
        DatabaseManager.getInstance().getToppingSize()

    override fun onCreateView(): View {
        binding = FragmentHomeLayoutBinding.inflate(
            LayoutInflater.from(activity), null, false
        )
        initTopBar()
        initList()
        LoggerUtils.i("topSize=$topSize")
        return binding.root
    }

    /**
     * 初始化bar
     */
    private fun initTopBar() {
        binding.topbar.addRightImageButton(R.mipmap.icon_add, R.id.topbar_right_change_button)
            .setOnClickListener {
//                startContentFragment(0)
                val noteItem = NoteItem()
                noteItem.title = UUID.randomUUID().toString()
                //获取置顶数量
                mRecyclerViewAdapter.addData(topSize, noteItem)
                //保存到数据库
                DatabaseManager.getInstance().save(noteItem)
                binding.emptyView.hide()
            }
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
        val notes = DatabaseManager.getInstance().getAll()
        mRecyclerViewAdapter = NoteItemAdapter(
            R.layout.note_item,
            notes,
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
                // 提醒
                R.id.btnRemind -> {
                    //通知Intent init
                    val it = Intent(requireActivity(), NotificationService::class.java)
                    it.putExtra(ITEM_ID, item.id)
                    if (!item.remind) {
                        startRemindService(it, position, item)
                    } else {
                        stopRemindService(it, position, item)
                    }
                    closeSwipeMenu()
                }
                // 删除
                R.id.btnDelete -> removeItem(position, item.id)
                // 置顶
                R.id.btnTopping -> {
                    if (!item.top) {
                        openItemTop(item, position)
                    }
                    else {
                        closeItemTop(item, position)
                    }
                    closeSwipeMenu()
                }
                // 换肤
                R.id.btnSkin -> {
                    var i = 0
                    for(background in BackgroundTypeEnum.values()) {
                        if (item.background == background.name) {
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
        //配置默认view
        if (notes.size == 0) {
            binding.emptyView.show(
                false,
                "未创建笔记",
                null,
                "点击创建笔记"
            ) { Toast.makeText(context, "尚未实现", Toast.LENGTH_SHORT).show()}
        } else {
            binding.emptyView.hide()
        }
    }

    /**
     * 启动提醒服务
     */
    private fun startRemindService(intent: Intent, position: Int, item: NoteItem) {
        item.remind = true
        //设置图标
        setViewIcon(position, R.id.btnRemind, R.drawable.btn_remind_close)
        DatabaseManager.getInstance().update(item)
        requireActivity().startService(intent)
    }

    /**
     * 停止提醒服务
     */
    private fun stopRemindService(intent: Intent, position: Int, item: NoteItem) {
        item.setToDefault("remind")
        //设置图标
        setViewIcon(position, R.id.btnRemind, R.drawable.btn_remind)
        DatabaseManager.getInstance().update(item)
        requireActivity().startService(intent)
    }

    /**
     * 删除item
     */
    private fun removeItem(position: Int, id: Long) {
        mRecyclerViewAdapter.remove(position)
        DatabaseManager.getInstance().delete(id)
    }

    /**
     * 设置指定位置item置顶
     * @param item 当前选择背景的item
     * @param position item位置
     */
    private fun openItemTop(item: NoteItem, position: Int) {
        val viewByPosition = getViewByPosition(position)
        viewByPosition.text = StringUtils.getTitleHtmlText(
            requireContext(), item.title, item.lastUpdateTime, true)
        if (!item.top) {
            item.top = true
            mRecyclerViewAdapter.notifyItemMoved(position, 0)
            binding.recyclerView.scrollToPosition(0)
            //保存数据库
            DatabaseManager.getInstance().update(item)
            //刷新置顶数量
            topSize = DatabaseManager.getInstance().getToppingSize()
            //设置图标
            setViewIcon(position, R.id.btnTopping, R.drawable.btn_topping_close)
        }
    }

    /**
     * 取消指定position置顶
     */
    private fun closeItemTop(item: NoteItem, position: Int) {
        val viewByPosition = getViewByPosition(position)
        viewByPosition.text = StringUtils.getTitleHtmlText(
            requireContext(), item.title, item.lastUpdateTime, false)
        if (item.top) {
            item.setToDefault("top")
            //保存数据库
            DatabaseManager.getInstance().update(item)
            //设置图标
            setViewIcon(position, R.id.btnTopping, R.drawable.btn_topping)
            mRecyclerViewAdapter.notifyItemMoved(position, topSize - 1)
            //刷新置顶数量
            topSize = DatabaseManager.getInstance().getToppingSize()
        }
    }

    /**
     * 初始化置顶
     */
    private fun initTop() {
        mRecyclerViewAdapter.noteItemTops.sort()
        for((i, position) in mRecyclerViewAdapter.noteItemTops.withIndex()) {
            mRecyclerViewAdapter.setItemPosition(position, i)
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
                if (item.background != BackgroundTypeEnum.values()[which].name) {
                    val textView = getViewByPosition(position)
                    textView.setBackgroundResource(BackgroundTypeEnum.values()[which].resId)
                    //保存到数据库
                    item.background = BackgroundTypeEnum.values()[which].name
                    DatabaseManager.getInstance().update(item)
                }
                closeSwipeMenu()
            }
            .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show()
    }

    /**
     * 关闭侧滑菜单
     */
    private fun closeSwipeMenu() {
        if (SwipeMenuLayout.getViewCache().isSwipeEnable) {
            SwipeMenuLayout.getViewCache().smoothClose()
        }
    }

    /**
     * 设置指定位置的按钮图标
     */
    private fun setViewIcon(position: Int, viewId: Int, resId: Int) {
        val viewByPosition = mRecyclerViewAdapter.getViewByPosition(
            binding.recyclerView,
            position,
            viewId
        )!!
        viewByPosition.setBackgroundResource(resId)
    }

    /**
     * 获取itemView
     */
    private fun getViewByPosition(position: Int): TextView =
        mRecyclerViewAdapter.getViewByPosition(
            binding.recyclerView,
            position,
            R.id.textView
        ) as TextView

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

//    override fun onResume() {
//        super.onResume()
////        val noteItem = NoteItem()
////        noteItem.title = "新增"
////        noteItem.lastUpdateTime = Date()
////        //后续需要读取数据库获取置顶数量
////        mRecyclerViewAdapter.addData(0, noteItem)
////        //保存到数据库
//    }

    override fun translucentFull(): Boolean = true

    override fun canDragBack(): Boolean = false

    /**
     * 上一页
     */
    override fun onLastFragmentFinish(): Any = null!!
}