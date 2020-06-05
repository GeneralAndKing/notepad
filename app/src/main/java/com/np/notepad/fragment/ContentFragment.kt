package com.np.notepad.fragment

import android.view.LayoutInflater
import android.view.View
import com.np.notepad.R
import com.np.notepad.base.BaseFragment
import com.np.notepad.databinding.FragmentCollapsingTopbarLayoutBinding
import com.np.notepad.util.LoggerUtils

class ContentFragment: BaseFragment() {
    //绑定XML布局文件
    private lateinit var binding: FragmentCollapsingTopbarLayoutBinding

    override fun onCreateView(): View {
        binding = FragmentCollapsingTopbarLayoutBinding.inflate(LayoutInflater.from(activity), null, false)
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
            .setOnClickListener {popBackStack()}
        binding.collapsingTopbarLayout.title = getString(R.string.app_name)
    }

    override fun translucentFull(): Boolean {
        return true
    }
}