package com.np.notepad.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import com.np.notepad.R
import com.np.notepad.activity.MainActivity
import com.np.notepad.adapter.NoteItemAdapter
import com.np.notepad.base.BaseFragment
import com.np.notepad.databinding.FragmentHomeLayoutBinding
import com.np.notepad.manager.DatabaseManager
import com.np.notepad.model.NoteItem
import com.np.notepad.model.enums.ItemSkinEnum
import com.np.notepad.util.ConstUtils.Companion.ITEM_ID
import com.qmuiteam.qmui.skin.QMUISkinManager
import com.qmuiteam.qmui.widget.dialog.QMUIDialog.CheckableDialogBuilder
import java.util.*

class HomeFragment : BaseFragment() {
    //绑定XML布局文件
    private lateinit var binding: FragmentHomeLayoutBinding
    //适配器
    private lateinit var mRecyclerViewAdapter: NoteItemAdapter
    //置顶数量
    private var topSize = DatabaseManager.getInstance().getToppingSize()
    //item数据
    private var notes = DatabaseManager.getInstance().getAll()
    // 图片地址
    private val imgUrls = mutableListOf(
        "https://fuss10.elemecdn.com/a/3f/3302e58f9a181d2509f3dc0fa68b0jpeg.jpeg",
        "https://fuss10.elemecdn.com/1/34/19aa98b1fcb2781c4fba33d850549jpeg.jpeg",
        "https://fuss10.elemecdn.com/0/6f/e35ff375812e6b0020b6b4e8f9583jpeg.jpeg",
        "https://fuss10.elemecdn.com/9/bb/e27858e973f5d7d3904835f46abbdjpeg.jpeg",
        "https://fuss10.elemecdn.com/d/e6/c4d93a3805b3ce3f323f7974e6f78jpeg.jpeg",
        "https://fuss10.elemecdn.com/3/28/bbf893f792f03a54408b3b7a7ebf0jpeg.jpeg",
        "https://fuss10.elemecdn.com/2/11/6535bcfb26e4c79b48ddde44f4b6fjpeg.jpeg")
    // 文案
    private val essay = mutableListOf(
        "时代的一粒灰\n\n落到个人头上\n\n就是一座山",
        "过春风十里\n\n尽荠麦青青",
        "要温柔起来\n\n像一朵穿裤子的云")

    override fun onCreateView(): View {
        binding = FragmentHomeLayoutBinding.inflate(
            LayoutInflater.from(activity), null, false
        )
        // 加载图像
        Glide.with(requireContext())
            .load(imgUrls[(0 until imgUrls.size).random()])
            .into(binding.homeImg)
        // 初始化top bar
        initTopBar()
        // 初始化数据
        initNotes()
        // 初始化列表视图
        initListView()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // 刷新列表
        refreshListView()
        // 判断默认列表需要显示还是隐藏
        decideEmptyViewShow()
    }

    /**
     * 初始化bar
     */
    private fun initTopBar() {
        binding.topbar.addRightImageButton(R.mipmap.icon_add, R.id.topbar_right_change_button)
            .setOnClickListener {
                startContentFragment(0)
            }
        binding.collapsingTopbarLayout.title = getString(R.string.app_name)
    }

    /**
     * 初始化内容列表
     */
    private fun initListView() {
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
            val item = notes[position]
            when(view.id) {
                R.id.textView -> startContentFragment(item.id)
                // 提醒
                R.id.btnRemind -> {
                    if (!item.remind) {
                        item.remind = true
                        DatabaseManager.getInstance().update(item)
                        // 通知
                        callNotificationService(item.id, true)
                    } else {
                        item.setToDefault("remind")
                        DatabaseManager.getInstance().update(item)
                        item.remind = false
                        // 通知
                        callNotificationService(item.id, false)
                    }
                    mRecyclerViewAdapter.notifyItemRangeChanged(position, 1)
                    closeSwipeMenu()
                }
                // 删除
                R.id.btnDelete -> {
                    removeItem(position, item.id)
                    callNotificationService(item.id, false)
                    // 判断默认列表需要显示还是隐藏
                    decideEmptyViewShow()}
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
                    for(background in ItemSkinEnum.values()) {
                        if (item.background == background.name) {
                            showSingleBackgroundDialog(
                                item,
                                position,
                                i,
                                ItemSkinEnum.getDes())
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
        decideEmptyViewShow()
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
        if (notes.contains(item)) {
            item.top = true
            //保存数据库
            DatabaseManager.getInstance().update(item)
            //判断是否是原位置顶
            if (position != topSize) {
                mRecyclerViewAdapter.notifyItemMoved(position, topSize)
                Collections.swap(notes, position, topSize)
            }
            mRecyclerViewAdapter.notifyItemRangeChanged(
                topSize, position - topSize + 1)
            //刷新置顶数量
            topSize++
        }
    }

    /**
     * 取消指定position置顶
     */
    private fun closeItemTop(item: NoteItem, position: Int) {
        if (notes.contains(item)) {
            item.setToDefault("top")
            //保存数据库
            DatabaseManager.getInstance().update(item)
            item.top = false
            if (position != topSize - 1) {
                //跟置顶最后一位交换位置
                mRecyclerViewAdapter.notifyItemMoved(position, topSize - 1)
                Collections.swap(notes, position, topSize - 1)
            }
            mRecyclerViewAdapter.notifyItemRangeChanged(
                position, topSize - position + 1)
            //刷新置顶数量
            topSize--
        }
    }

    /**
     * 初始化Notes
     */
    private fun initNotes() {
        //init top
        val topPosition = mutableListOf<Int>()
        for ((i, note) in notes.withIndex()) {
            if (note.top) {
                topPosition.add(i)
            }
        }
        for ((i, position) in topPosition.withIndex()) {
            if (i != position) {
                Collections.swap(notes, position, i)
            }
        }
    }

    /**
     * 选择item颜色弹窗
     * @param item 当前选择item
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
                if (item.background != ItemSkinEnum.values()[which].name) {
                    val textView = getViewByPosition(position)
                    textView.setBackgroundResource(ItemSkinEnum.values()[which].resId)
                    //保存到数据库
                    item.background = ItemSkinEnum.values()[which].name
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
        if (id != 0L) {
            val args = Bundle()
            args.putLong(ITEM_ID, id)
            fragment.arguments = args
        }
        startFragment(fragment)
    }

    /**
     * 刷新列表
     */
    private fun refreshListView() {
        if ((requireActivity() as MainActivity).isChange) {
            notes.clear()
            notes.addAll(DatabaseManager.getInstance().getAll())
            initNotes()
            mRecyclerViewAdapter.notifyDataSetChanged()
            (requireActivity() as MainActivity).isChange = false
        }
    }

    /**
     * 判断默认view是否显示
    */
    private fun decideEmptyViewShow() {
        if (notes.size == 0 || mRecyclerViewAdapter.data.size == 0) {
            binding.emptyView.show(null, essay[(0 until essay.size).random()])
        } else {
            binding.emptyView.hide()
        }
    }

    override fun translucentFull(): Boolean = true

    override fun canDragBack(): Boolean = false
}