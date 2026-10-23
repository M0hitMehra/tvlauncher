package com.tvlauncher.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Receives BOOT_COMPLETED to ensure the launcher is ready on device startup.
 * Android's own home stack management handles re-launching the default HOME
 * activity on boot; this receiver can be used for any post-boot initialization.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Optionally: clear any stale state, re-index apps, etc.
        }
    }
}
