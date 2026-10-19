package com.khatawat.diyn.sms

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager
import android.widget.Toast

object SmsHelper {

    /** Opens the user's default SMS app for confirmation. Does NOT require SEND_SMS permission. */
    fun openSmsApp(context: Context, phone: String, message: String) {
        val uri = Uri.parse("smsto:$phone")
        val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra("sms_body", message)
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "لا يوجد تطبيق رسائل", Toast.LENGTH_SHORT).show()
        }
    }

    /** Sends SMS directly via the radio. Requires runtime SEND_SMS permission. */
    fun sendDirect(context: Context, phone: String, message: String): Boolean {
        return try {
            val smsManager = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                context.getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }
            smsManager.divideMessage(message) // ensure not too long; ignored if shorter
            smsManager.sendMultipartTextMessage(phone, null, smsManager.divideMessage(message), null, null)
            true
        } catch (e: SecurityException) {
            Toast.makeText(context, "لا يوجد إذن لإرسال الرسائل", Toast.LENGTH_SHORT).show()
            false
        } catch (e: Exception) {
            Toast.makeText(context, "فشل الإرسال: ${e.message}", Toast.LENGTH_LONG).show()
            false
        }
    }
}
