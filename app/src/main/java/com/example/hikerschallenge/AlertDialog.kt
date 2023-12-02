package com.example.hikerschallenge

import android.content.Context
import android.app.AlertDialog

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

}