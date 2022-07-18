package com.xevensolutions.baseapp.fragments

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.viewbinding.ViewBinding
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import java.io.File

abstract class BaseMediaPickerFragment<Vb : ViewBinding> : BasePermissionsFragment<Vb>(),
    PickiTCallbacks {

    protected open fun onFilePathGenerated(path: String) {}

    protected open fun onImagePicked(imageUri: Uri?) {
        imageUri?.let { pickIt.getPath(it, Build.VERSION.SDK_INT) }
    }

    protected open fun onImageCaptured(imageUri: Uri?) {
        imageUri?.let { pickIt.getPath(it, Build.VERSION.SDK_INT) }
    }

    override fun onPermissionsGranted(isGranted: Boolean) {
        if (isGranted) {
            when (actionTaken) {
                Action.CAPTURE_IMAGE -> {
                    fileProvider?.let {
                        captureImage(it)
                    }
                }
                Action.PICK_IMAGE_FROM_GALLERY -> {
                    pickOnlyImageFromGallery(isMultipleImages)
                }
                else -> {
                }
            }
        } else
            showDenialBox()
    }

    private var isMultipleImages: Boolean = false
    private var fileProvider: String? = null
    private var capturedImageUri: Uri? = null
    private var actionTaken: Action = Action.NONE
    private val pickIt: PickiT by lazy { PickiT(context, this, activity) }

    private val mImagePickerLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { intent ->
            actionTaken = Action.NONE
            onImagePicked(intent?.data?.data)
        }

    private val mImageCaptureLauncher: ActivityResultLauncher<Uri> =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isCaptured ->
            actionTaken = Action.NONE
            if (isCaptured)
                onImageCaptured(capturedImageUri)
        }

    override fun onStart() {
        super.onStart()
        fileProvider = null
        actionTaken = Action.NONE
    }

    // Pick single Image only from Gallery
    protected fun pickOnlyImageFromGallery(multiImages: Boolean) {
        actionTaken = Action.PICK_IMAGE_FROM_GALLERY
        isMultipleImages = multiImages

        if (isPermissionsEnabled(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            val intent =
                Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    .apply {
                        type = "image/*"
                        putExtra(
                            Intent.EXTRA_MIME_TYPES,
                            arrayOf("image/jpeg", "image/jpg", "image/png")
                        )
                        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multiImages)
                    }

            mImagePickerLauncher.launch(intent)
        } else
            launchRuntimePermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    // Capture Image via Camera
    protected fun captureImage(fileProvider: String) {
        this.fileProvider = fileProvider
        actionTaken = Action.CAPTURE_IMAGE

        if (isPermissionsEnabled(Manifest.permission.CAMERA)) {
            try {
                val tempFile: File = File.createTempFile("img_", ".png", requireContext().cacheDir)
                if (!tempFile.exists()) tempFile.mkdir()
                capturedImageUri = FileProvider.getUriForFile(
                    requireContext(),
                    fileProvider,
                    tempFile
                )
                if (capturedImageUri != null) mImageCaptureLauncher.launch(capturedImageUri)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    "Something went wrong while opening Camera",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else
            launchRuntimePermissions(Manifest.permission.CAMERA)
    }

    enum class Action {
        NONE, CAPTURE_IMAGE, PICK_IMAGE_FROM_GALLERY
    }

    override fun PickiTonUriReturned() {

    }

    override fun PickiTonStartListener() {

    }

    override fun PickiTonProgressUpdate(progress: Int) {

    }

    override fun PickiTonCompleteListener(
        path: String?,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String?,
    ) {
        path?.let { onFilePathGenerated(it) }
    }

    override fun onBackPressed() {
        try {
            pickIt.deleteTemporaryFile(context)
        } catch (e: Exception) {
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        try {
            pickIt.deleteTemporaryFile(context)
        } catch (e: Exception) {
        }
        super.onDestroy()
    }


}