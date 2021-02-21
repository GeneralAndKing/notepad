package com.np.notepad.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.fragment.app.FragmentContainerView
import com.np.notepad.R
import com.np.notepad.base.BaseFragmentActivity
import com.np.notepad.fragment.ContentFragment
import com.np.notepad.fragment.HomeFragment
import com.np.notepad.manager.SkinManager
import com.np.notepad.model.enums.ThemeSkinEnum
import com.qmuiteam.qmui.arch.SwipeBackLayout
import com.qmuiteam.qmui.arch.annotation.DefaultFirstFragment
import com.qmuiteam.qmui.arch.annotation.FirstFragments
import com.qmuiteam.qmui.arch.annotation.LatestVisitRecord
import com.qmuiteam.qmui.skin.QMUISkinHelper
import com.qmuiteam.qmui.skin.QMUISkinManager
import com.qmuiteam.qmui.skin.QMUISkinManager.OnSkinChangeListener
import com.qmuiteam.qmui.skin.QMUISkinValueBuilder
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.util.QMUIResHelper
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import com.qmuiteam.qmui.util.QMUIViewOffsetHelper
import com.qmuiteam.qmui.widget.QMUIRadiusImageView2
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.popup.QMUIPopup
import com.qmuiteam.qmui.widget.popup.QMUIPopups
import kotlin.math.sqrt

@FirstFragments(value = [
    HomeFragment::class,
    ContentFragment::class])
@DefaultFirstFragment(
    HomeFragment::class
)
@LatestVisitRecord
class MainActivity : BaseFragmentActivity() {

    // 是否刷新
    var isChange: Boolean = false

    companion object {
        private var mGlobalAction: QMUIPopup? = null
    }

    // 主题监听
    private val mOnSkinChangeListener =
        OnSkinChangeListener { _, _, newSkin ->
            if (newSkin == ThemeSkinEnum.SKIN_WHITE.code) {
                QMUIStatusBarHelper.setStatusBarLightMode(this)
            } else {
                QMUIStatusBarHelper.setStatusBarDarkMode(this)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        skinManager = QMUISkinManager.defaultInstance(this)
    }

    override fun onCreateRootView(fragmentContainerId: Int): RootView? {
        return CustomRootView(this, fragmentContainerId)
    }

    override fun onStart() {
        super.onStart()
        if (skinManager != null) {
            skinManager.addSkinChangeListener(mOnSkinChangeListener)
        }
    }

    override fun onStop() {
        super.onStop()
        if (skinManager != null) {
            skinManager.removeSkinChangeListener(mOnSkinChangeListener)
        }
    }

    @SuppressLint("ViewConstructor")
    internal class CustomRootView(
        context: Context?,
        fragmentContainerId: Int
    ) : RootView(context, fragmentContainerId) {
        private val fragmentContainer: FragmentContainerView = FragmentContainerView(context!!)
        private val globalBtn: QMUIRadiusImageView2
        private val globalBtnOffsetHelper: QMUIViewOffsetHelper
        private val btnSize: Int = QMUIDisplayHelper.dp2px(context, 56)
        private val touchSlop: Int
        private var touchDownX = 0f
        private var touchDownY = 0f
        private var lastTouchX = 0f
        private var lastTouchY = 0f
        private var isDragging = false
        private var isTouchDownInGlobalBtn = false
        override fun onLayout(
            changed: Boolean,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int
        ) {
            super.onLayout(changed, left, top, right, bottom)
            globalBtnOffsetHelper.onViewLayout()
        }

        override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
            val x = event.x
            val y = event.y
            val action = event.action
            if (action == MotionEvent.ACTION_DOWN) {
                isTouchDownInGlobalBtn = isDownInGlobalBtn(x, y)
                lastTouchX = x
                touchDownX = lastTouchX
                lastTouchY = y
                touchDownY = lastTouchY
            } else if (action == MotionEvent.ACTION_MOVE) {
                if (!isDragging && isTouchDownInGlobalBtn) {
                    val dx = (x - touchDownX).toInt()
                    val dy = (y - touchDownY).toInt()
                    if (sqrt(dx * dx + dy * dy.toDouble()) > touchSlop) {
                        isDragging = true
                    }
                }
                if (isDragging) {
                    var dx = (x - lastTouchX).toInt()
                    var dy = (y - lastTouchY).toInt()
                    val gx = globalBtn.left
                    val gy = globalBtn.top
                    val gw = globalBtn.width
                    val w = width
                    val gh = globalBtn.height
                    val h = height
                    if (gx + dx < 0) {
                        dx = -gx
                    } else if (gx + dx + gw > w) {
                        dx = w - gw - gx
                    }
                    if (gy + dy < 0) {
                        dy = -gy
                    } else if (gy + dy + gh > h) {
                        dy = h - gh - gy
                    }
                    globalBtnOffsetHelper.leftAndRightOffset =
                        globalBtnOffsetHelper.leftAndRightOffset + dx
                    globalBtnOffsetHelper.topAndBottomOffset =
                        globalBtnOffsetHelper.topAndBottomOffset + dy
                }
                lastTouchX = x
                lastTouchY = y
            } else if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
                isDragging = false
                isTouchDownInGlobalBtn = false
            }
            return isDragging
        }

        private fun isDownInGlobalBtn(x: Float, y: Float): Boolean {
            return globalBtn.left < x && globalBtn.right > x && globalBtn.top < y && globalBtn.bottom > y
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouchEvent(event: MotionEvent): Boolean {
            val x = event.x
            val y = event.y
            val action = event.action
            if (action == MotionEvent.ACTION_DOWN) {
                isTouchDownInGlobalBtn = isDownInGlobalBtn(x, y)
                lastTouchX = x
                touchDownX = lastTouchX
                lastTouchY = y
                touchDownY = lastTouchY
            } else if (action == MotionEvent.ACTION_MOVE) {
                if (!isDragging && isTouchDownInGlobalBtn) {
                    val dx = (x - touchDownX).toInt()
                    val dy = (y - touchDownY).toInt()
                    if (sqrt(dx * dx + dy * dy.toDouble()) > touchSlop) {
                        isDragging = true
                    }
                }
                if (isDragging) {
                    var dx = (x - lastTouchX).toInt()
                    var dy = (y - lastTouchY).toInt()
                    val gx = globalBtn.left
                    val gy = globalBtn.top
                    val gw = globalBtn.width
                    val w = width
                    val gh = globalBtn.height
                    val h = height
                    if (gx + dx < 0) {
                        dx = -gx
                    } else if (gx + dx + gw > w) {
                        dx = w - gw - gx
                    }
                    if (gy + dy < 0) {
                        dy = -gy
                    } else if (gy + dy + gh > h) {
                        dy = h - gh - gy
                    }
                    globalBtnOffsetHelper.leftAndRightOffset =
                        globalBtnOffsetHelper.leftAndRightOffset + dx
                    globalBtnOffsetHelper.topAndBottomOffset =
                        globalBtnOffsetHelper.topAndBottomOffset + dy
                }
                lastTouchX = x
                lastTouchY = y
            } else if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
                isDragging = false
                isTouchDownInGlobalBtn = false
            }
            return isDragging || super.onTouchEvent(event)
        }

        override fun getFragmentContainerView(): FragmentContainerView {
            return fragmentContainer
        }

        init {
            fragmentContainer.id = fragmentContainerId
            fragmentContainer.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                for (i in 0 until childCount) {
                    SwipeBackLayout.updateLayoutInSwipeBack(getChildAt(i))
                }
            }
            addView(
                fragmentContainer, LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            globalBtn = QMUIRadiusImageView2(context)
            // 图标
            globalBtn.setImageResource(R.mipmap.icon_theme)
            globalBtn.scaleType = ImageView.ScaleType.CENTER_INSIDE
            globalBtn.setRadiusAndShadow(
                btnSize / 2,
                QMUIDisplayHelper.dp2px(getContext(), 16), 0.4f
            )
            globalBtn.borderWidth = 1
            globalBtn.borderColor = QMUIResHelper.getAttrColor(
                context,
                R.attr.qmui_skin_support_color_separator
            )
            globalBtn.setBackgroundColor(
                QMUIResHelper.getAttrColor(
                    context,
                    R.attr.app_skin_common_background
                )
            )
            globalBtn.setOnClickListener { v -> showGlobalActionPopup(v) }
            val globalBtnLp = LayoutParams(btnSize, btnSize)
            globalBtnLp.gravity = Gravity.BOTTOM or Gravity.END
            globalBtnLp.bottomMargin = QMUIDisplayHelper.dp2px(context, 50)
            globalBtnLp.rightMargin = QMUIDisplayHelper.dp2px(context, 20)
            val builder = QMUISkinValueBuilder.acquire()
            builder.background(R.attr.app_skin_common_background)
            builder.border(R.attr.qmui_skin_support_color_separator)
            builder.tintColor(R.attr.app_skin_common_img_tint_color)
            QMUISkinHelper.setSkinValue(globalBtn, builder)
            builder.release()
            addView(globalBtn, globalBtnLp)
            globalBtnOffsetHelper = QMUIViewOffsetHelper(globalBtn)
            touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        }

        /**
         * 圆形悬浮窗点击触发
         */
        private fun showGlobalActionPopup(v: View?) {
            val data = listOf(
                "主题色")
            val adapter = ArrayAdapter(context, R.layout.simple_list_item, data)
            val onItemClickListener =
                AdapterView.OnItemClickListener { _, _, i, _ ->
                    if (i == 0) {
                        // 选项
                        val items = ThemeSkinEnum.getDes()
                        // 选择主题
                        QMUIDialog.CheckableDialogBuilder(context)
                            // 默认值
                            .setCheckedIndex(ThemeSkinEnum.getValuesIndex(SkinManager.getCurrentSkin()))
                            .setSkinManager(QMUISkinManager.defaultInstance(context))
                            .addItems(items) { dialog, which ->
                                // 选择
                                SkinManager.changeSkin(ThemeSkinEnum.values()[which].code)
                                dialog.dismiss()
                            }
                            .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show()
                    }
                    if (mGlobalAction != null) {
                        mGlobalAction!!.dismiss()
                    }
                }
            mGlobalAction = QMUIPopups.listPopup(
                context,
                QMUIDisplayHelper.dp2px(context, 250),
                QMUIDisplayHelper.dp2px(context, 300),
                adapter,
                onItemClickListener
            )
                .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .edgeProtection(QMUIDisplayHelper.dp2px(context, 10))
                .offsetYIfTop(QMUIDisplayHelper.dp2px(context, 5))
                .skinManager(QMUISkinManager.defaultInstance(context))
                .show(v!!)
        }
    }
}