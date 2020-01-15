package com.fajarproject.travels.util.fileUtil

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import java.io.*
import java.lang.ref.WeakReference

/**
 * Create by Fajar Adi Prasetyo on 13/01/2020.
 */
internal class DownloadAsyncTask(
    uri: Uri,
    context: Context,
    private val callback: CallBackTask,
    private val filename: String
) :
    AsyncTask<Uri?, Int?, String?>() {
    private val mUri: Uri = uri
    private val mContext: WeakReference<Context> = WeakReference(context)
    private var pathPlusName: String? = null
    private var folder: File? = null
    private var returnCursor: Cursor? = null
    private var `is`: InputStream? = null
    private var extension: String? = null
    private var errorReason: String? = ""
    override fun onPreExecute() {
        callback.FileUtilonPreExecute()
        val context = mContext.get()
        if (context != null) {
            folder = context.getExternalFilesDir("Temp")
            returnCursor = context.contentResolver.query(mUri, null, null, null, null)
            val mime = MimeTypeMap.getSingleton()
            extension = mime.getExtensionFromMimeType(context.contentResolver.getType(mUri))
            try {
                `is` = context.contentResolver.openInputStream(mUri)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        val post = values[0]
        callback.FileUtilonProgressUpdate(post!!)
    }

    override fun doInBackground(vararg params: Uri?): String {
        var file: File? = null
        var size = -1
        try {
            try {
                if (returnCursor != null && returnCursor!!.moveToFirst()) {
                    if (mUri.scheme != null) if (mUri.scheme == "content") {
                        val sizeIndex =
                            returnCursor!!.getColumnIndex(OpenableColumns.SIZE)
                        size = returnCursor!!.getLong(sizeIndex).toInt()
                    } else if (mUri.scheme == "file") {
                        val ff = File(mUri.path!!)
                        size = ff.length().toInt()
                    }
                }
            } finally {
                if (returnCursor != null) returnCursor!!.close()
            }
            if (extension == null) {
                pathPlusName = folder.toString() + "/" + filename
                file = File(folder.toString() + "/" + filename)
            } else {
                pathPlusName = folder.toString() + "/" + filename + "." + extension
                file = File(folder.toString() + "/" + filename + "." + extension)
            }
            val bis = BufferedInputStream(`is`!!)
            val fos = FileOutputStream(file)
            val data = ByteArray(1024)
            var total: Long = 0
            var count: Int
            while (bis.read(data).also { count = it } != -1) {
                if (!isCancelled) {
                    total += count.toLong()
                    if (size != -1) {
                        publishProgress((total * 100 / size).toInt())
                    }
                    fos.write(data, 0, count)
                }
            }
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            Log.e("Pickit IOException = ", e.message!!)
            errorReason = e.message
        }
        return file!!.absolutePath
    }

    override fun onPostExecute(result: String?) {
        if (result == null) {
            callback.FileUtilonPostExecute(pathPlusName,
                wasDriveFile = true,
                wasSuccessful = false,
                reason = errorReason
            )
        } else {
            callback.FileUtilonPostExecute(pathPlusName,
                wasDriveFile = true,
                wasSuccessful = true,
                reason = ""
            )
        }
    }

}