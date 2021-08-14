package com.shreyanshu.travelmedium

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AndroidBootListener : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            //MyIntentService.startActionFoo(context,"","")
            context.startService(Intent(context,MyService::class.java))
        }
    }
}
