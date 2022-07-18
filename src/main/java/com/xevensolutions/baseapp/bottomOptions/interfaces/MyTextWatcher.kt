package com.xevensolutions.baseapp.bottomOptions.interfaces

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout

open class MyTextWatcher(var textInput: TextInputLayout) : TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun afterTextChanged(p0: Editable?) {
        textInput.error = null
    }
}