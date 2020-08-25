package cn.cleartv.icu.utils

import android.content.Context
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import androidx.preference.EditTextPreference

class PasswordEditTextPreference : EditTextPreference {

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr, 0)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?) : super(context, null)

    init {
        summaryProvider = SimpleSummaryProvider.getInstance()
        setOnBindEditTextListener {
            it.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            it.transformationMethod = PasswordTransformationMethod.getInstance()
            it.selectAll()
        }
    }

    // Hide password by stars
    override fun getSummary(): CharSequence {
        return if (text.isNullOrBlank()) super.getSummary() else "******"
    }
}