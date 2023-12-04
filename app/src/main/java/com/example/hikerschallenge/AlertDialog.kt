package com.example.hikerschallenge

import android.app.AlertDialog
import android.content.Context

class AlertDialog(private val context: Context) {

    fun showAlert(title: String, message: String){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK"){dialog, which ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun showAlertOptions(title: String, message: String, positiveText: String, positiveAction: () -> Unit, negativeText: String, negativeAction: () -> Unit){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(positiveText){dialog, which ->
            positiveAction()
            dialog.dismiss()
        }
        builder.setNegativeButton(negativeText){dialog, which ->
            negativeAction()
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}