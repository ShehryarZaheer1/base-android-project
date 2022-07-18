package com.xevensolutions.baseapp.customViews

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.xevensolutions.baseapp.R
import com.xevensolutions.baseapp.utils.DateUtils

class MyTextInputEditText : LinearLayout {

    private var isDatePicker: Boolean = false
    private var isTimePicker: Boolean = false
    private var isTime24HourFormat: Boolean = false
    private var hintText: String = ""
    var dateValueText: String = ""
    var pickedTimeText: String = ""

    constructor(context: Context?) : this(context!!, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setUpView(context, attrs)
    }

    lateinit var progressBar: ProgressBar
    lateinit var frameLayout: FrameLayout
    lateinit var textInputLayout: TextInputLayout
    lateinit var textInputEditText: TextInputEditText

    private fun setUpView(context: Context, attrs: AttributeSet?) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MyTextInputEditText)
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val frameLayout = layoutInflater.inflate(
            R.layout.my_text_input_edittext,
            this,
            false
        ) as FrameLayout

        textInputLayout = frameLayout.findViewById(R.id.textInputLayout)
        textInputEditText = frameLayout.findViewById(R.id.textInputEditText)
        progressBar = frameLayout.findViewById(R.id.progressBar)
        orientation = VERTICAL

        val hint = attributes.getString(R.styleable.MyTextInputEditText_hint)
        val value = attributes.getString(R.styleable.MyTextInputEditText_text)
         val inputType = attributes.getInt(
            R.styleable.MyTextInputEditText_android_inputType,
            EditorInfo.TYPE_NULL
        )
        val isFocusable = attributes.getBoolean(R.styleable.MyTextInputEditText_isFocusable, true)
        isDatePicker =
            attributes.getBoolean(R.styleable.MyTextInputEditText_isDatePicker, false)
        isTimePicker =
            attributes.getBoolean(R.styleable.MyTextInputEditText_isTimePicker, false)
        isTime24HourFormat =
            attributes.getBoolean(R.styleable.MyTextInputEditText_is24HourFormat, false)

        textInputLayout.hint = hint
        if (hintText.isNotEmpty())
            textInputLayout.hint = hintText

        textInputEditText.setText(value)
        textInputEditText.isFocusable = isFocusable

        if (inputType != EditorInfo.TYPE_NULL)
            textInputEditText.inputType = inputType

        if (isDatePicker)
            setClickListener {
                DateUtils.pickServerDate(
                    context
                ) { formattedDate, _ ->
                    dateValueText = formattedDate
                    textInputEditText.setText(formattedDate.toClientDate(false))
                }
            }

        if (isTimePicker) {
            setClickListener {
                DateUtils.pickTime(context, false) { time, _ ->
                    pickedTimeText = formatTime(time)
                    textInputEditText.setText(pickedTimeText)
                }
            }
        }

        textInputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                textInputLayout.error = null
            }
        })

        addView(frameLayout)
        attributes.recycle()
    }

    fun setValue(value: String?) {
        textInputEditText.setText(
            if (isDatePicker) value?.toClientDate(true)
            else if (isTimePicker)
                formatTime(value)
            else value
        )

        if (isDatePicker && value != null)
            dateValueText = value

        if (isTimePicker && value != null)
            pickedTimeText = formatTime(value)
    }

    // Return Formatted Time
    private fun formatTime(time: String?): String {
        var format = DateUtils.TIME_FORMAT_AM_PM

        if (isTime24HourFormat)
            format = DateUtils.TIME_24H_FORMAT

        return DateUtils.changeFormat(
            time,
            DateUtils.TIME_FORMAT_AM_PM,
            format
        )
    }

    fun setHint(value: String?) {

        if (this::textInputLayout.isInitialized)
            textInputLayout.hint = value

        if (value != null)
            hintText = value
    }

    fun getText(): String {
        if (isDatePicker)
            return dateValueText
        else if (isTimePicker)
            return pickedTimeText
        return textInputEditText.text.toString().trim()
    }

    fun setClickListener(listener: OnClickListener) {
        textInputEditText.setOnClickListener(listener)
    }

    fun showLoading() {
        if (this::progressBar.isInitialized)
            progressBar.visibility = View.VISIBLE
    }

    fun dismissLoading() {
        if (this::progressBar.isInitialized)
            progressBar.visibility = View.GONE
    }

    fun setError(error: String?) {
        textInputLayout.error = error
    }

    fun setRequiredError() {
        setError(context.getString(R.string.required))
    }

    fun setFieldFocusable(isFocusable: Boolean) {
        if (textInputEditText != null)
            textInputEditText.isFocusable = isFocusable
    }
}

fun String.toClientDate(convertFromUTC: Boolean): String {
    var value = this
    if (convertFromUTC)
        value = DateUtils.getLocalDateStringFromUTC(value)
    return DateUtils.getFormattedDateToShow(value)
}
