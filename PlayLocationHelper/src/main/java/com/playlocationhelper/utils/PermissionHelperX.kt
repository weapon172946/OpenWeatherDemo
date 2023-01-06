package com.playlocationhelper.utils

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference

class PermissionHelperX(
    private val activity: Activity,
    private val permissions: Array<String>,
    private val onAllGranted: (() -> Unit)?,
    private val onAnyNotGranted: (() -> Unit)?,
    private val toastTextWhenOpenAppSettingsIfPermissionsPermanentlyDenied: String = "Please enable permissions from settings to proceed"
) {
    private val activityWeakReference = WeakReference(activity)

    companion object {

        private const val PERMISSION_REQUEST = 1243
        private const val SETTINGS_REQUEST = 1244

        fun areAllGranted(activity: Activity, permissions: Array<String>): Boolean {
            return permissions.all {
                ContextCompat.checkSelfPermission(
                    activity,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    fun request() {
        if (activityWeakReference.get() == null) {
            return
        }

        if (permissions.isEmpty() || (permissions.all {
                ContextCompat.checkSelfPermission(
                    activity,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            })) {
            onAllGranted?.invoke()
        } else {
            ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQUEST)
        }
    }

    fun onRequestPermissionsResult(requestCode: Int) {
        if (activityWeakReference.get() == null) {
            return
        }

        when (requestCode) {
            PERMISSION_REQUEST -> {
                when {
                    permissions.all {
                        ContextCompat.checkSelfPermission(
                            activity,
                            it
                        ) == PackageManager.PERMISSION_GRANTED
                    } -> {
                        onAllGranted?.invoke()
                    }
                    permissions.any {
                        (ContextCompat.checkSelfPermission(
                            activity,
                            it
                        ) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.shouldShowRequestPermissionRationale(
                            activity,
                            it
                        ).not())
                    } -> {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.fromParts("package", activity.packageName, null)
                        activity.startActivityForResult(intent, SETTINGS_REQUEST)
                        Toast.makeText(
                            activity,
                            toastTextWhenOpenAppSettingsIfPermissionsPermanentlyDenied,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                        onAnyNotGranted?.invoke()
                    }
                }
            }
        }
    }

    fun onActivityResult(requestCode: Int) {
        if (activityWeakReference.get() == null) {
            return
        }

        when (requestCode) {
            SETTINGS_REQUEST -> {
                if (permissions.all {
                        ContextCompat.checkSelfPermission(
                            activity,
                            it
                        ) == PackageManager.PERMISSION_GRANTED
                    }) {
                    onAllGranted?.invoke()
                } else {
                    onAnyNotGranted?.invoke()
                }
            }
        }
    }

}