package com.mendes.testcreditcard.extensions

import android.text.InputFilter
import com.google.android.material.textfield.TextInputEditText
import java.util.ArrayList

fun TextInputEditText.setMaxLength(length: Int) {
    val inputFiltersList: MutableList<InputFilter> = ArrayList()
    val existingFilters: Array<InputFilter>? = filters
    if (existingFilters?.isNotEmpty() == true) {
        for (filter in existingFilters) {
            if (filter is InputFilter.LengthFilter) {
                continue
            }
            inputFiltersList.add(filter)
        }
    }

    inputFiltersList.add(InputFilter.LengthFilter(length))
    filters = inputFiltersList.toTypedArray()
}