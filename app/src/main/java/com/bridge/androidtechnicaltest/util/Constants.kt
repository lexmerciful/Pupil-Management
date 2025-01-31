package com.bridge.androidtechnicaltest.util

import android.content.Context
import android.content.DialogInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object Constants {

    const val API_TIMEOUT: Long = 30
    const val BASE_URL = "https://androidtechnicaltestapi-test.bridgeinternationalacademies.com/"

    const val PUPIL = "pupil"
    const val PUPIL_LIST = "pupil_list"
    const val IS_EDIT = "is_edit"
    const val IS_UPDATE_SUCCESSFUL = "is_update_successful"
    const val IS_DELETE_SUCCESSFUL = "is_delete_successful"
    const val ADD_PUPIL_KEY = "add_pupil_key"
    const val EDIT_PUPIL_KEY = "edit_pupil_key"
    const val UPDATE_PUPIL_KEY = "update_pupil_key"

    fun showAlertDialog(
        context: Context,
        title: String,
        message: String,
        negativeButtonText: String = "OK",
        negativeButtonClickListener: () -> Unit = {},
        positiveButtonText: String = "",
        positiveButtonClickListener: () -> Unit = {},
        cancelable: Boolean = false,
        icon: Int = 0
    ) {
        val builder = MaterialAlertDialogBuilder(context)
        builder.setTitle(title)
            .setMessage(message)
            .setCancelable(cancelable)
            .setNegativeButton(negativeButtonText) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }

        if (icon != 0) {
            builder.setIcon(icon)
        }

        if (positiveButtonText.isNotBlank()) {
            builder.setPositiveButton(positiveButtonText) { _: DialogInterface?, _: Int -> positiveButtonClickListener.invoke() }
        }

        builder.setNegativeButton(negativeButtonText) { _: DialogInterface?, _: Int -> negativeButtonClickListener.invoke() }

        builder.create().show()
    }
}