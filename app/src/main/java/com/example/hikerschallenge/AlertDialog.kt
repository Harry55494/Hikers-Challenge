package com.example.hikerschallenge

import android.app.AlertDialog
import android.content.Context

class AlertDialog(private val context: Context) {
    // Alert Dialog, custom class to show alerts

    val tag = "AlertDialog"

    fun showAlert(title: String, message: String){
        // Show standard alert with only OK button
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK"){ dialog, _ ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun showAlertOptions(title: String, message: String, positiveText: String, positiveAction: () -> Unit, negativeText: String, negativeAction: () -> Unit){
        // Show alert with two options, and custom actions for each
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(positiveText){ dialog, _ ->
            positiveAction()
            dialog.dismiss()
        }
        builder.setNegativeButton(negativeText){ dialog, _ ->
            negativeAction()
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}