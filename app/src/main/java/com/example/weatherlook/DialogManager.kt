package com.example.weatherlook

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AlertDialogLayout
import com.example.weatherlook.databinding.EnterLocationDialogBinding

object DialogManager {
    fun locationSettingsDialog(context: Context, lisener: GPSListener){
        val builder = AlertDialog.Builder(context)
        val dialog= builder.create()
        dialog.setTitle("Enable location?")
        dialog.setMessage("Location is disabled now, so you want to continue and switch it on?")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK"){_,_->
            lisener.onClick()
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel"){_,_->
            dialog.dismiss()
        }
        dialog.show()
    }

    fun enterNewPalce(context: Context, lisener: NewPlaceEntered) {
        var dialog = Dialog(context)
        dialog.setContentView(R.layout.enter_location_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)

        var edTxt = dialog.findViewById(R.id.enterPlace) as EditText
        val btn = dialog.findViewById<Button>(R.id.SearchButton)
        btn.setOnClickListener{
            val str = edTxt.text.toString()
            lisener.placeSetter(str)
            dialog.dismiss()
        }
        dialog.show()
    }

    interface GPSListener{
        fun onClick(){

        }
    }

    interface NewPlaceEntered{
        fun placeSetter(newPlace: String){

        }
    }
}