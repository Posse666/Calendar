package com.posse.kotlin1.calendar.utils

import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun checkPermission(
    requestCode: Int,
    fragment: Fragment,
    permission: String,
    title: String,
    message: String,
    positive: String,
    negative: String
): Permission {
    fragment.context?.let {
        when {
            ContextCompat.checkSelfPermission(it, permission) ==
                    PackageManager.PERMISSION_GRANTED -> {
                return Permission.GRANTED
            }
            fragment.shouldShowRequestPermissionRationale(permission) -> {
                AlertDialog.Builder(it)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(positive) { _, _ ->
                        requestPermission(fragment, permission, requestCode)
                    }
                    .setNegativeButton(negative) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
            else -> {
                requestPermission(fragment, permission, requestCode)
            }
        }
    }
    return Permission.NOT_GRANTED
}

private fun requestPermission(fragment: Fragment, permission: String, requestCode: Int) {
    fragment.requestPermissions(arrayOf(permission), requestCode)
}

fun checkPermissionsResult(
    fragment: Fragment,
    grantResults: IntArray,
    title: String,
    message: String,
    negative: String
): Permission {
    if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
    ) {
        return Permission.GRANTED
    } else {
        fragment.context?.let {
            AlertDialog.Builder(it)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(negative) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }
    return Permission.NOT_GRANTED
}

enum class Permission {
    GRANTED,
    NOT_GRANTED
}