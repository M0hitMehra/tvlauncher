package com.tvlauncher.app.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext

class AppRepository(private val context: Context) {

    private val prefs = LauncherPreferences(context)

    val gridColumns: Flow<Int> = prefs.gridColumns
    val showClock: Flow<Boolean> = prefs.showClock
    val appOrder: Flow<List<String>> = prefs.appOrder
    val hiddenApps: Flow<Set<String>> = prefs.hiddenApps

    suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PackageManager.MATCH_ALL.toLong()
        } else {
            0L
        }

        val resolveInfoList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(flags))
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(intent, 0)
        }

        resolveInfoList
            .filter { it.activityInfo.packageName != context.packageName }
            .map { resolveInfo ->
                AppInfo(
                    packageName = resolveInfo.activityInfo.packageName,
                    label = resolveInfo.loadLabel(pm).toString(),
                    icon = resolveInfo.loadIcon(pm),
                    isSystem = (resolveInfo.activityInfo.applicationInfo.flags and
                            android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
                )
            }
            .sortedBy { it.label.lowercase() }
    }

    fun getVisibleApps(
        allApps: List<AppInfo>,
        hiddenApps: Set<String>,
        appOrder: List<String>
    ): List<AppInfo> {
        val visible = allApps.filter { it.packageName !in hiddenApps }
        if (appOrder.isEmpty()) return visible

        val orderMap = appOrder.withIndex().associate { (idx, pkg) -> pkg to idx }
        return visible.sortedWith(compareBy { orderMap[it.packageName] ?: Int.MAX_VALUE })
    }

    suspend fun setGridColumns(columns: Int) = prefs.setGridColumns(columns)
    suspend fun setShowClock(show: Boolean) = prefs.setShowClock(show)
    suspend fun setHiddenApps(packages: Set<String>) = prefs.setHiddenApps(packages)
    suspend fun setAppOrder(packages: List<String>) = prefs.setAppOrder(packages)

    suspend fun toggleHideApp(packageName: String, currentHidden: Set<String>) {
        val updated = if (packageName in currentHidden) {
            currentHidden - packageName
        } else {
            currentHidden + packageName
        }
        prefs.setHiddenApps(updated)
    }

    fun launchApp(packageName: String) {
        val launchIntent = context.packageManager.getLeanbackLaunchIntentForPackage(packageName)
            ?: context.packageManager.getLaunchIntentForPackage(packageName)
        launchIntent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(it)
        }
    }

    fun openSettings() {
        val intent = Intent(android.provider.Settings.ACTION_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
