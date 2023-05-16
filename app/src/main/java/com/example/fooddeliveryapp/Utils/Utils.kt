package com.example.fooddeliveryapp.Utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import java.text.DecimalFormat


fun openPage(ctx: Context, url: String, urlWeb:String) {
    var intent: Intent
    try {
        intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    }
    catch (e: ActivityNotFoundException) {
        intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlWeb))
    }
    ctx.startActivity(intent)


}


fun openMap(url:String,context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity")
    context.startActivity(intent)
}


fun prettyCount(number: Number): String? {
    val suffix = charArrayOf(' ', 'K', 'M', 'B', 'T', 'P', 'E')
    val numValue = number.toLong()
    val value = Math.floor(Math.log10(numValue.toDouble())).toInt()
    val base = value / 3
    return if (value >= 3 && base < suffix.size) {
        DecimalFormat("#0.0").format(
            numValue / Math.pow(
                10.0,
                (base * 3).toDouble()
            )
        ) + suffix[base]
    } else {
        DecimalFormat("#,##0").format(numValue)
    }
}

fun openMail(context: Context,email:String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + email))
        intent.putExtra(Intent.EXTRA_SUBJECT, " ")
        intent.putExtra(Intent.EXTRA_TEXT, " ")
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context,"Invalid Email",Toast.LENGTH_SHORT).show()
    }
}

fun openPhone(context: Context,phoneNumber:String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        context.startActivity(intent)
    }
    catch (e: ActivityNotFoundException) {
        Toast.makeText(context,"Erreur",Toast.LENGTH_SHORT).show()
    }
}