package com.np.notepad.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.np.notepad.R
import com.np.notepad.adapter.NoteItemAdapter
import com.np.notepad.base.BaseFragment
import com.np.notepad.databinding.FragmentCollapsingTopbarLayoutBinding
import com.np.notepad.util.ConstUtils.Companion.ITEM_ID
import com.np.notepad.util.LoggerUtils
import java.util.*

class HomeFragment : BaseFragment() {
    //绑定XML布局文件
    private lateinit var binding: FragmentCollapsingTopbarLayoutBinding
    //适配器
    private var mRecyclerViewAdapter: NoteItemAdapter =
        NoteItemAdapter(R.layout.note_item, ArrayList(), 10)

    override fun onCreateView(): View {
        binding = FragmentCollapsingTopbarLayoutBinding.inflate(
            LayoutInflater.from(activity), null, false
        )
        initTopBar()
        initList()
        return binding.root
    }

    override fun translucentFull(): Boolean {
        return true
    }

    /**
     * 初始化bar
     */
    private fun initTopBar() {
        binding.topbar.addLeftBackImageButton()
            .setOnClickListener {
                Toast.makeText(context, "点击返回", Toast.LENGTH_SHORT).show()
            }
        binding.collapsingTopbarLayout.title = getString(R.string.app_name)

        binding.collapsingTopbarLayout.setScrimUpdateListener { animation ->
            LoggerUtils.i("scrim: " + animation.animatedValue)
        }

        binding.collapsingTopbarLayout.addOnOffsetUpdateListener { _, offset, expandFraction ->
            LoggerUtils.i("offset = $offset; expandFraction = $expandFraction")
        }
    }

    /**
     * 初始化内容列表
     */
    private fun initList() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        //开启动画效果
        mRecyclerViewAdapter.openLoadAnimation()
        //设置动画效果
        mRecyclerViewAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN)
        //添加分割线
//        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        //设置Item点击事件
        mRecyclerViewAdapter.setOnItemClickListener { _, _, position ->
            val item = mRecyclerViewAdapter.getItem(position)!!
            LoggerUtils.i(
                "onItemClick：$position"
            )
            val fragment = ContentFragment()
            val args = Bundle()
            args.putLong(ITEM_ID, item.id)
            fragment.arguments = args
            startFragment(fragment)
        }
        //设置适配器
        binding.recyclerView.adapter = mRecyclerViewAdapter
    }

    override fun canDragBack(): Boolean = false

    override fun onLastFragmentFinish(): Any = null!!
}