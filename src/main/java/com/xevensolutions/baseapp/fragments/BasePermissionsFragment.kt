package com.xevensolutions.baseapp.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.viewbinding.ViewBinding

abstract class BasePermissionsFragment<Vb : ViewBinding> : BaseFragment<Vb>() {

    protected open fun onPermissionsGranted(isGranted: Boolean) {}

    private val mPermissionRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            var isAllGranted = true
            it.entries.forEach { result ->
                isAllGranted = result.value
                if (!isAllGranted)
                    return@forEach
            }

            onPermissionsGranted(isAllGranted)
        }

    // Ask Runtime Permissions
    protected fun launchRuntimePermissions(vararg permissions: String) {
        if (!isPermissionsEnabled(*permissions))
            mPermissionRequestLauncher.launch(permissions)
    }

    protected fun isPermissionsEnabled(vararg permissions: String): Boolean {
        var enabled = false
        permissions.forEach { permission ->
            enabled = ActivityCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
            if (!enabled)
                return@forEach
        }

        return enabled
    }

    protected fun showDenialBox() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Permissions Required!")
        builder.setMessage("Permissions are required to proceed.")
        builder.setCancelable(false)
        builder.setPositiveButton("Goto Settings") { dialogInterface, _ ->
            dialogInterface.dismiss()
            gotoSettings(requireContext())
        }
        builder.create().show()
    }

    private fun gotoSettings(context: Context) {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri: Uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}