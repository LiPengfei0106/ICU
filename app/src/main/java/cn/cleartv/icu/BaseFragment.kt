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
import timber.log.Timber

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


    override fun onAttach(context: Context) {
        Timber.d("onAttach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onViewCreated")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("onCreateView")
        return inflater.inflate(viewLayoutRes(), container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Timber.d("onActivityCreated")
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        afterInflateView()
    }

    override fun onStart() {
        Timber.d("onStart")
        super.onStart()
    }

    override fun onResume() {
        Timber.d("onResume")
        super.onResume()
    }

    override fun onPause() {
        Timber.d("onPause")
        super.onPause()
    }

    override fun onStop() {
        Timber.d("onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        Timber.d("onDestroyView")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Timber.d("onDestroy")
        super.onDestroy()
    }

    override fun onDetach() {
        Timber.d("onDetach")
        super.onDetach()
    }


    @LayoutRes
    protected abstract fun viewLayoutRes(): Int

    protected abstract fun afterInflateView()

    private fun showFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction().show(fragment).commit()
    }

    protected fun replaceFragment(fragment: BaseFragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fl_content, fragment, fragment.javaClass.simpleName)
            .commitAllowingStateLoss()
    }

    protected fun addFragment(fragment: BaseFragment) {
        parentFragmentManager.beginTransaction()
            .hide(this)
            .add(R.id.fl_content, fragment, fragment.javaClass.simpleName)
            .addToBackStack(fragment.javaClass.simpleName)
            .commitAllowingStateLoss()
    }

    protected fun removeFragment(fragment: BaseFragment): Boolean {
        parentFragmentManager.let {
            if (it.backStackEntryCount > 1) {
                it.popBackStack()
                showFragment(it.fragments[it.backStackEntryCount - 1])
                return true
            }
        }
        return false
    }

    protected open fun startActivity(activityCls: Class<out Activity>) {
        startActivity(Intent(activity, activityCls))
    }

    protected open fun toast(msg: String) {
        Utils.showToast(msg)
    }

    protected open fun interceptBackPressed(): Boolean {
        return removeFragment(this)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        Timber.d(event.toString())
        return interceptBackPressed() || OnKeyDownHandlerHelper.handleKeyDown(this, keyCode, event)
    }

}