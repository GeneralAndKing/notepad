package com.np.notepad.activity

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.np.notepad.R
import com.np.notepad.adapter.QDRecyclerViewAdapter
import com.np.notepad.databinding.FragmentCollapsingTopbarLayoutBinding
import com.np.notepad.util.ConstUtils
import com.np.notepad.util.LoggerUtils
import com.qmuiteam.qmui.arch.QMUIActivity
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import com.qmuiteam.qmui.widget.QMUICollapsingTopBarLayout
import com.qmuiteam.qmui.widget.QMUITopBar

/**
 * 主界面
 * @author z f
 */
class MainActivity : QMUIActivity() {

    //绑定XML布局文件
    private lateinit var binding: FragmentCollapsingTopbarLayoutBinding
    var mRecyclerViewAdapter: QDRecyclerViewAdapter? = null
    var mPagerLayoutManager: LinearLayoutManager? = null
    var mRecyclerView: RecyclerView? = null
    var mCollapsingTopBarLayout: QMUICollapsingTopBarLayout? = null
    var mTopBar: QMUITopBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 沉浸式状态栏
        QMUIStatusBarHelper.translucent(this)
        initView()
        initTopBar()
        mPagerLayoutManager = LinearLayoutManager(this)
        mRecyclerView!!.layoutManager = mPagerLayoutManager
        mRecyclerViewAdapter = QDRecyclerViewAdapter()
        mRecyclerViewAdapter!!.itemCount = 6
        mRecyclerView!!.adapter = mRecyclerViewAdapter

        mCollapsingTopBarLayout!!.setScrimUpdateListener { animation ->
            LoggerUtils.i("scrim: " + animation.animatedValue)
        }

        mCollapsingTopBarLayout!!.addOnOffsetUpdateListener { layout, offset, expandFraction ->
            LoggerUtils.i("offset = $offset; expandFraction = $expandFraction")
        }
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
        //初始化view
//        binding.noteList.adapter = NoteItemAdapter(R.layout.note_item, ArrayList())
        mCollapsingTopBarLayout = binding.collapsingTopbarLayout
        mTopBar = binding.topbar
        mRecyclerView = binding.recyclerView
    }

    /**
     * 初始化bar
     */
    private fun initTopBar() {
        mTopBar!!.addLeftBackImageButton()
            .setOnClickListener {
                Toast.makeText(this, "点击返回", Toast.LENGTH_SHORT).show()
            }
        mCollapsingTopBarLayout!!.title = getString(R.string.app_name)
    }
}
