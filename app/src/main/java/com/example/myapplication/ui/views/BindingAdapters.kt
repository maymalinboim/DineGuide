package com.example.myapplication.ui.views

import androidx.core.widget.doOnTextChanged
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.R
import com.google.android.material.textfield.TextInputEditText

@BindingAdapter("android:text")
fun setText(customTextInput: CustomTextInput, text: String) {
    if (customTextInput.text != text) {
        customTextInput.text = text
    }
}

@BindingAdapter("android:text")
fun setText(customTextInput: CustomTextInput, text: LiveData<String>) {
    if (customTextInput.text != text.value) {
        customTextInput.text = text.value ?: ""
    }
}

@InverseBindingAdapter(attribute = "android:text")
fun getText(customTextInput: CustomTextInput): String {
    return customTextInput.text
}


@BindingAdapter("android:textAttrChanged")
fun setTextWatcher(customTextInput: CustomTextInput, textAttrChanged: InverseBindingListener) {
    customTextInput.findViewById<TextInputEditText>(R.id.text_input_edit_text)
        .doOnTextChanged { _, _, _, _ ->
            textAttrChanged.onChange()
        }
}

@BindingAdapter("helperTextEnabled")
fun setHelperTextEnabled(customTextInput: CustomTextInput, enabled: Boolean) {
    customTextInput.helperTextEnabled = enabled
}

@InverseBindingAdapter(attribute = "helperTextEnabled")
fun getHelperTextEnabled(customTextInput: CustomTextInput): Boolean {
    return customTextInput.helperTextEnabled
}

@BindingAdapter("helperTextEnabledAttrChanged")
fun setHelperTextEnabledListener(customTextInput: CustomTextInput, attrChange: InverseBindingListener) {
    customTextInput.findViewById<TextInputEditText>(R.id.text_input_edit_text)
        .doOnTextChanged { _, _, _, _ ->
            attrChange.onChange()
        }
}

@BindingAdapter("helperText")
fun setHelperText(customTextInput: CustomTextInput, helperText: String?) {
    customTextInput.helperText = helperText ?: ""
}

@InverseBindingAdapter(attribute = "helperText")
fun getHelperText(customTextInput: CustomTextInput): String? {
    return customTextInput.helperText
}

@BindingAdapter("helperTextAttrChanged")
fun setHelperTextWatcher(customTextInput: CustomTextInput, helperTextAttrChanged: InverseBindingListener) {
    customTextInput.findViewById<TextInputEditText>(R.id.text_input_edit_text)
        .doOnTextChanged { _, _, _, _ ->
            helperTextAttrChanged.onChange()
        }
}