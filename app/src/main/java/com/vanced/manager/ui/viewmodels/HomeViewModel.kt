package com.vanced.manager.ui.viewmodels

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.dezlum.codelabs.getjson.GetJson
import com.vanced.manager.R
import com.vanced.manager.utils.PackageHelper.isPackageInstalled

open class HomeViewModel(application: Application): AndroidViewModel(application) {

    private val connected: Boolean = GetJson().isConnected(application)

    private val vancedPkgName: String =
        if (getDefaultSharedPreferences(application).getString("vanced_variant", "Nonroot") == "Root") {
            "com.google.android.youtube"
        } else {
            "com.vanced.android.youtube"
        }
    //private val signaturePref = getDefaultSharedPreferences(application).getString("signature_status", "unavailable")

    val microgInstalled: Boolean = isPackageInstalled("com.mgoogle.android.gms", application.packageManager)
    val vancedInstalled: Boolean = isPackageInstalled(vancedPkgName, application.packageManager)

    val vancedInstalledVersion: String =
        if (vancedInstalled) {
            application.packageManager.getPackageInfo(vancedPkgName, 0).versionName
        } else {
            application.getString(R.string.unavailable)
        }


    val microgInstalledVersion: String =
        if (microgInstalled) {
            application.packageManager.getPackageInfo("com.mgoogle.android.gms", 0).versionName
        } else {
            application.getString(R.string.unavailable)
        }

    val vancedVersion: String =
        if (connected)
            GetJson().AsJSONObject("https://vanced.app/api/v1/vanced.json")
                .get("version").asString
        else
            application.getString(R.string.unavailable)

    val microgVersion: String =
        if (connected)
            GetJson().AsJSONObject("https://vanced.app/api/v1/microg.json")
                .get("version").asString
        else
            application.getString(R.string.unavailable)

    val isNonrootModeSelected: Boolean = getDefaultSharedPreferences(application).getString("vanced_variant", "Nonroot") == "Nonroot"

    val signatureString = application.getString(R.string.unavailable)
    val signatureStatusTxt: MutableLiveData<String> = MutableLiveData()

    fun openMicrogSettings() {
        try {
            val intent = Intent()
            intent.component = ComponentName(
                "com.mgoogle.android.gms",
                "org.microg.gms.ui.SettingsActivity"
            )
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(getApplication(), intent, null)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(getApplication(), "App not installed", Toast.LENGTH_SHORT).show()
        }
    }

    fun openUrl(Url: String) {
        val color: Int =
            when (Url) {
                "https://discord.gg/TUVd7rd" -> R.color.Discord
                "https://t.me/joinchat/AAAAAEHf-pi4jH1SDlAL4w" -> R.color.Telegram
                "https://twitter.com/YTVanced" -> R.color.Twitter
                "https://reddit.com/r/vanced" -> R.color.Reddit
                "https://vanced.app" -> R.color.Vanced
                "https://brave.com/van874" -> R.color.Brave
                else -> R.color.Vanced
            }

        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(getApplication(), color))
        val customTabsIntent = builder.build()
        customTabsIntent.intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        customTabsIntent.launchUrl(getApplication(), Uri.parse(Url))
    }

    init {
        signatureStatusTxt.value = signatureString
    }

}