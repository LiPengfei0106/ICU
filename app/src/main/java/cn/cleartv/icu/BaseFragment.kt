package cn.cleartv.icu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import cn.cleartv.icu.utils.OnKeyDownHandlerHelper
import cn.cleartv.icu.utils.Utils

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/19
 *     desc   :
 *     version: 1.0
 * </pre>
 */
abstract class BaseFragment : Fragment(), OnKeyDownHandlerHelper.FragmentOnKeyDownHandler {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(viewLayoutRes(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        afterInflateView()
    }

    @LayoutRes
    protected abstract fun viewLayoutRes(): Int

    protected abstract fun afterInflateView()

    private fun showFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction().show(fragment).commit()
    }

    protected fun replaceFragment(fragment: BaseFragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.content, fragment, fragment.javaClass.simpleName)
            .commitAllowingStateLoss()
    }

    protected fun addFragment(fragment: BaseFragment) {
        parentFragmentManager.beginTransaction()
            .hide(this)
            .add(R.id.content, fragment, fragment.javaClass.simpleName)
            .addToBackStack(fragment.javaClass.simpleName)
            .commitAllowingStateLoss()
    }

    protected fun removeFragment(fragment: BaseFragment) {
        parentFragmentManager.let {
            if (it.backStackEntryCount > 1) {
                it.popBackStack()
                showFragment(it.fragments[it.backStackEntryCount - 1])
            }
        }
    }

    protected open fun startActivity(activityCls: Class<out Activity>) {
        startActivity(Intent(activity, activityCls))
    }

    protected open fun toast(msg: String) {
        Utils.showToast(msg)
    }

    protected open fun interceptBackPressed(): Boolean {
        return false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return interceptBackPressed() || OnKeyDownHandlerHelper.handleKeyDown(this, keyCode, event)
    }

}