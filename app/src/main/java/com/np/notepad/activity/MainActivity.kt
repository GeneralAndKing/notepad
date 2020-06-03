package com.np.notepad.activity

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.np.notepad.R
import com.np.notepad.adapter.NoteItemAdapter
import com.np.notepad.databinding.FragmentCollapsingTopbarLayoutBinding
import com.np.notepad.util.ConstUtils
import com.np.notepad.util.LoggerUtils
import com.qmuiteam.qmui.arch.QMUIActivity
import com.qmuiteam.qmui.util.QMUIStatusBarHelper

/**
 * 主界面
 * @author z f
 */
class MainActivity : QMUIActivity() {

    //绑定XML布局文件
    private lateinit var binding: FragmentCollapsingTopbarLayoutBinding
    //适配器
    private var mRecyclerViewAdapter: NoteItemAdapter = NoteItemAdapter(R.layout.note_item, ArrayList(), 10)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 沉浸式状态栏
        QMUIStatusBarHelper.translucent(this)
        initView()
        LoggerUtils.i(ConstUtils.APP_NAME)
    }

    override fun translucentFull(): Boolean {
        return true
    }

    /**
     * 初始化视图
     */
    private fun initView() {
        //初始化ViewBinding
        binding = FragmentCollapsingTopbarLayoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initTopBar()
        initList()
    }

    /**
     * 初始化bar
     */
    private fun initTopBar() {
        binding.topbar.addLeftBackImageButton()
            .setOnClickListener {
                Toast.makeText(this, "点击返回", Toast.LENGTH_SHORT).show()
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
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        //开启动画效果
        mRecyclerViewAdapter.openLoadAnimation()
        //设置动画效果
        mRecyclerViewAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN)
        //添加分割线
//        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        //设置适配器
        binding.recyclerView.adapter = mRecyclerViewAdapter
    }
}
