package com.fajarproject.travels.util.fileUtil

/**
 * Create by Fajar Adi Prasetyo on 13/01/2020.
 */
interface FileUtilCallbacks {
    fun FileUtilonStartListener()
    fun FileUtilonProgressUpdate(progress: Int)
    fun FileUtilonCompleteListener(
        path: String?,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String?
    )
}