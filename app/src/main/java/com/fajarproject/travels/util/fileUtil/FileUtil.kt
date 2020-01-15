package com.fajarproject.travels.util.fileUtil

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import java.io.File

/**
 * Create by Fajar Adi Prasetyo on 13/01/2020.
 */
@SuppressLint("DefaultLocale")

class FileUtil(private val context: Context, listener: FileUtilCallbacks) :
    CallBackTask {
    private val fileUtilCallbacks: FileUtilCallbacks = listener
    private var isDriveFile = false
    private var isFromUnknownProvider = false
    private var asyntask: DownloadAsyncTask? = null
    private var unknownProviderCalledBefore = false
    fun getPath(uri: Uri, APILevel: Int) {
        val returnedPath: String
        if (APILevel >= 19) { // Drive file was selected
            if (isOneDrive(uri) || isDropBox(uri) || isGoogleDrive(uri)) {
                isDriveFile = true
                downloadFile(uri, "tempFile")
            } else {
                returnedPath = Utils.getRealPathFromURI_API19(context, uri)!!
                //Get the file extension
                val mime = MimeTypeMap.getSingleton()
                val subStringExtension =
                    returnedPath.substring(returnedPath.lastIndexOf(".") + 1)
                val extensionFromMime =
                    mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
                // Path is null
                if (returnedPath == "") {
                    if (!unknownProviderCalledBefore) {
                        unknownProviderCalledBefore = true
                        if (uri.scheme != null && uri.scheme == ContentResolver.SCHEME_CONTENT) { //Then we check if the _data colomn returned null
                            if (Utils.errorReason() != null && Utils.errorReason().equals("dataReturnedNull")) {
                                isFromUnknownProvider = true
                                //Copy the file to the temporary folder
                                downloadFile(uri, getFileName(uri))
                                return
                            }
                        }
                    }
                    //Else an error occurred, get/set the reason for the error
                    fileUtilCallbacks.FileUtilonCompleteListener(
                        returnedPath,
                        wasDriveFile = false,
                        wasUnknownProvider = false,
                        wasSuccessful = false,
                        Reason = Utils.errorReason()
                    )
                } else {
                    if (subStringExtension != extensionFromMime && uri.scheme != null && uri.scheme == ContentResolver.SCHEME_CONTENT) {
                        val fileName =
                            returnedPath.substring(returnedPath.lastIndexOf("/") + 1)
                        isFromUnknownProvider = true
                        downloadFile(uri, fileName)
                        return
                    }
                    // Path can be returned, no need to make a "copy"
                    fileUtilCallbacks.FileUtilonCompleteListener(
                        returnedPath,
                        wasDriveFile = false,
                        wasUnknownProvider = false,
                        wasSuccessful = true,
                        Reason = ""
                    )
                }
            }
        } else { //Todo: Test API <19
            returnedPath = Utils.realpathfromuriBelowapi19(context, uri)
            fileUtilCallbacks.FileUtilonCompleteListener(returnedPath,
                wasDriveFile = false,
                wasUnknownProvider = false,
                wasSuccessful = true,
                Reason = ""
            )
        }
    }

    //Get the file name
    private fun getFileName(uri: Uri): String {
        val replaced =
            uri.toString().replace("%2F", "/").replace("%20", " ").replace("%3A", "/")
        var name = replaced.substring(replaced.lastIndexOf("/") + 1)
        if (name.indexOf(".") > 0) {
            name = name.substring(0, name.lastIndexOf("."))
        }
        return name
    }

    // Create a new file from the Uri that was selected
    private fun downloadFile(uri: Uri, fileName: String) {
        asyntask = DownloadAsyncTask(uri, context, this, fileName)
        asyntask!!.execute()
    }

    // End the "copying" of the file
    fun cancelTask() {
        if (asyntask != null) {
            asyntask!!.cancel(true)
            deleteTemporaryFile()
        }
    }

    fun wasLocalFileSelected(uri: Uri): Boolean {
        return !isDropBox(uri) && !isGoogleDrive(uri) && !isOneDrive(uri)
    }

    // Check different providers
    private fun isDropBox(uri: Uri): Boolean {
        return uri.toString().toLowerCase().contains("content://com.dropbox.android")
    }

    private fun isGoogleDrive(uri: Uri): Boolean {
        return uri.toString().toLowerCase().contains("com.google.android.apps")
    }

    private fun isOneDrive(uri: Uri): Boolean {
        return uri.toString().toLowerCase().contains("com.microsoft.skydrive.content")
    }

    // FileUtil callback Listeners
    override fun FileUtilonPreExecute() {
        fileUtilCallbacks.FileUtilonStartListener()
    }

    override fun FileUtilonProgressUpdate(progress: Int) {
        fileUtilCallbacks.FileUtilonProgressUpdate(progress)
    }

    override fun FileUtilonPostExecute(
        path: String?,
        wasDriveFile: Boolean,
        wasSuccessful: Boolean,
        reason: String?
    ) {
        unknownProviderCalledBefore = false
        if (wasSuccessful) {
            if (isDriveFile) {
                fileUtilCallbacks.FileUtilonCompleteListener(path,
                    wasDriveFile = true,
                    wasUnknownProvider = false,
                    wasSuccessful = true,
                    Reason = ""
                )
            } else if (isFromUnknownProvider) {
                fileUtilCallbacks.FileUtilonCompleteListener(path,
                    wasDriveFile = false,
                    wasUnknownProvider = true,
                    wasSuccessful = true,
                    Reason = ""
                )
            }
        } else {
            if (isDriveFile) {
                fileUtilCallbacks.FileUtilonCompleteListener(path,
                    wasDriveFile = true,
                    wasUnknownProvider = false,
                    wasSuccessful = false,
                    Reason = reason
                )
            } else if (isFromUnknownProvider) {
                fileUtilCallbacks.FileUtilonCompleteListener(path,
                    wasDriveFile = false,
                    wasUnknownProvider = true,
                    wasSuccessful = false,
                    Reason = reason
                )
            }
        }
    }

    // Delete the temporary folder
    fun deleteTemporaryFile() {
        val folder = context.getExternalFilesDir("Temp")
        if (folder != null) {
            if (deleteDirectory(folder)) {
                Log.i("FileUtil ", " deleteDirectory was called")
            }
        }
    }

    private fun deleteDirectory(path: File): Boolean {
        if (path.exists()) {
            val files = path.listFiles() ?: return false
            for (file in files) {
                if (file.isDirectory) {
                    deleteDirectory(file)
                } else {
                    val wasSuccessful = file.delete()
                    if (wasSuccessful) {
                        Log.i("Deleted ", "successfully")
                    }
                }
            }
        }
        return path.delete()
    }

}