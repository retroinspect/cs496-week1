package com.example.sample

import android.app.Activity
import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

object Util {
    fun hideKeyboard(context: Context?, root: View) {
        val imm =
            context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(
            root.windowToken, 0
        )
    }

    fun showKeyboard(context: Context?, editText: View) {
        val imm =
            context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(
            editText, InputMethodManager.SHOW_IMPLICIT
        )

    }


    fun isEnterPressedDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null) {
            return (event.action == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)
        }
        return false
    }

    fun keyPressedDown(event: KeyEvent?): Boolean {
        if (event != null) {
            return (event.action == KeyEvent.ACTION_DOWN)
        }
        return false
    }

}