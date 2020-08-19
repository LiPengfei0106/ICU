package cn.cleartv.icu.utils

import android.view.KeyEvent
import androidx.fragment.app.*
import androidx.viewpager.widget.ViewPager

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/19
 *     desc   :
 *     version: 1.0
 * </pre>
 */
object OnKeyDownHandlerHelper {
    /**
     * 将back事件分发给 FragmentManager 中管理的子Fragment，如果该 FragmentManager 中的所有Fragment都
     * 没有处理back事件，则尝试 FragmentManager.popBackStack()
     *
     * @return 如果处理了back键则返回 **true**
     * @see .handleKeyDown
     * @see .handleKeyDown
     */
    fun handleKeyDown(fragmentManager: FragmentManager, keyCode: Int, event: KeyEvent): Boolean {
        val fragments = fragmentManager.fragments
        for (i in fragments.indices.reversed()) {
            val child = fragments[i]
            if (child is FragmentOnKeyDownHandler) {
                if (isFragmentOnKeyDownHandled(child, keyCode, event)) {
                    return true
                }
            } else {
                if (handleKeyDown(child.childFragmentManager, keyCode, event)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 将back事件分发给Fragment中的子Fragment,
     * 该方法调用了 [.handleKeyDown]
     *
     * @return 如果处理了back键则返回 **true**
     */
    fun handleKeyDown(fragment: Fragment, keyCode: Int, event: KeyEvent): Boolean {
        return handleKeyDown(fragment.childFragmentManager, keyCode, event)
    }

    /**
     * 将back事件分发给Activity中的子Fragment,
     * 该方法调用了 [.handleKeyDown]
     *
     * @return 如果处理了back键则返回 **true**
     */
    fun handleKeyDown(fragmentActivity: FragmentActivity, keyCode: Int, event: KeyEvent): Boolean {
        return handleKeyDown(fragmentActivity.supportFragmentManager, keyCode, event)
    }

    /**
     * 将back事件分发给ViewPager中的Fragment,[.handleKeyDown] 已经实现了对ViewPager的支持，所以自行决定是否使用该方法
     *
     * @return 如果处理了back键则返回 **true**
     * @see .handleKeyDown
     * @see .handleKeyDown
     * @see .handleKeyDown
     */
    fun handleKeyDown(viewPager: ViewPager, keyCode: Int, event: KeyEvent): Boolean {
        val adapter = viewPager.adapter ?: return false
        return when (adapter) {
            is FragmentPagerAdapter -> {
                isFragmentOnKeyDownHandled(adapter.getItem(viewPager.currentItem), keyCode, event)
            }
            is FragmentStatePagerAdapter -> {
                isFragmentOnKeyDownHandled(adapter.getItem(viewPager.currentItem), keyCode, event)
            }
            else -> {
                false
            }
        }
    }

    /**
     * 判断Fragment是否处理了Back键
     *
     * @return 如果处理了back键则返回 **true**
     */
    fun isFragmentOnKeyDownHandled(fragment: Fragment, keyCode: Int, event: KeyEvent): Boolean {
        return fragment.isVisible
                && fragment.userVisibleHint //for ViewPager
                && (fragment is FragmentOnKeyDownHandler
                && (fragment as FragmentOnKeyDownHandler).onKeyDown(keyCode, event))
    }

    interface FragmentOnKeyDownHandler {
        fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean
    }
}