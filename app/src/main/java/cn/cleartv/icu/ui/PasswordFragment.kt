package cn.cleartv.icu.ui

import cn.cleartv.icu.BaseDialogFragment
import cn.cleartv.icu.R
import kotlinx.android.synthetic.main.fragment_password.*

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/25
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class PasswordFragment : BaseDialogFragment(){
    override fun viewLayoutRes(): Int {
        return R.layout.fragment_password
    }

    override fun afterInflateView() {
        btn_confirm.setOnClickListener {
            if(et_password.text.toString().trim() == "543210"){
                dismiss()
                startActivity(SettingsActivity::class.java)
            }else{
                toast("密码错误")
            }
        }
    }
}