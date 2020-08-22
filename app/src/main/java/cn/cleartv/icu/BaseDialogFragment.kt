package cn.cleartv.icu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
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
abstract class BaseDialogFragment : DialogFragment(), OnKeyDownHandlerHelper.FragmentOnKeyDownHandler {


    override fun onAttach(context: Context) {
        Timber.d("onAttach:${this}")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onViewCreated:${this}")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("onCreateView:${this}")
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(viewLayoutRes(), container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Timber.d("onActivityCreated:${this}")
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("onViewCreated:${this}")
        super.onViewCreated(view, savedInstanceState)
        afterInflateView()
    }

    override fun onStart() {
        Timber.d("onStart:${this}")
        super.onStart()
    }

    override fun onResume() {
        Timber.d("onResume:${this}")
        super.onResume()
    }

    override fun onPause() {
        Timber.d("onPause:${this}")
        super.onPause()
    }

    override fun onStop() {
        Timber.d("onStop:${this}")
        super.onStop()
    }

    override fun onDestroyView() {
        Timber.d("onDestroyView:${this}")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Timber.d("onDestroy:${this}")
        super.onDestroy()
    }

    override fun onDetach() {
        Timber.d("onDetach:${this}")
        super.onDetach()
    }


    @LayoutRes
    protected abstract fun viewLayoutRes(): Int

    protected abstract fun afterInflateView()

    private fun showFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction().show(fragment).commit()
    }

    protected fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fl_content, fragment, fragment.javaClass.simpleName)
            .commitAllowingStateLoss()
    }

    protected fun addFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .hide(this)
            .add(R.id.fl_content, fragment, fragment.javaClass.simpleName)
            .addToBackStack(fragment.javaClass.simpleName)
            .show(fragment)
            .commitAllowingStateLoss()
    }

    protected fun removeFragment(fragment: Fragment): Boolean {
        parentFragmentManager.let {
            if (it.backStackEntryCount > 1) {
                Timber.i("removeFragment:$fragment")
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