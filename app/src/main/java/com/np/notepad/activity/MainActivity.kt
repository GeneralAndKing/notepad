package com.np.notepad.activity

import android.os.Bundle
import com.np.notepad.R
import com.np.notepad.adapter.NoteItemAdapter
import com.np.notepad.databinding.ActivityMainBinding
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
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        //开启沉浸式
        QMUIStatusBarHelper.translucent(this)
        LoggerUtils.i(ConstUtils.APP_NAME)
    }

    /**
     * 初始化视图
     */
    private fun initView() {
        //初始化ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater);
        val view = binding.root
        setContentView(view)
        //初始化view
        binding.noteList.adapter = NoteItemAdapter(R.layout.note_item, ArrayList())
    }
}
