package com.fajarproject.travels.util.fileUtil

/**
 * Create by Fajar Adi Prasetyo on 13/01/2020.
 */
interface CallBackTask {
    fun FileUtilonPreExecute()
    fun FileUtilonProgressUpdate(progress: Int)
    fun FileUtilonPostExecute(
        path: String?,
        wasDriveFile: Boolean,
        wasSuccessful: Boolean,
        reason: String?
    )
}