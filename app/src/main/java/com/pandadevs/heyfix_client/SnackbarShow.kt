package com.pandadevs.heyfix_client

import android.view.View
import com.google.android.material.snackbar.Snackbar

object SnackbarShow {

    fun showSnackbar(view: View, message: String) {
        val snackbar: Snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snackbar.view.translationY = -10f
        snackbar.show()
    }
}