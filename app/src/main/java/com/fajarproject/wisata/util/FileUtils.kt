package com.fajarproject.wisata.util

import android.content.ContentUris
import android.content.Context
import android.content.CursorLoader
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore.Images.Media
import java.io.*
import java.lang.Long


object FileUtils {
    fun isLocal(url: String?): Boolean {
        return url != null && !url.startsWith("http://") && !url.startsWith("https://")
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Old Google Photos.
     */
    fun isGoogleOldPhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is New Google Photos.
     */
    fun isGoogleNewPhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.contentprovider" == uri.authority
    }

    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    fun getPath(context: Context, uri: Uri): String? {
        // DocumentProvider

        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider

                if (isExternalStorageDocument(uri)) {
                    val docId: String = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }

                    // TODO handle non-primary volumes

                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {
                    val id: String? = DocumentsContract.getDocumentId(uri)
                    val contentUri: Uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id!!)
                    )
                    return getDataColumn(context, contentUri, null, null)
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    val docId: String = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(
                        split[1]
                    )
                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }
            }
            // MediaStore (and general)
            else if ("content".equals(uri.scheme, ignoreCase = true)) {

                // Return the remote address

                if (isGoogleOldPhotosUri(uri)) {
                    // return http path, then download file.

                    return uri.lastPathSegment
                } else if (isGoogleNewPhotosUri(uri)) {
                    return if (getDataColumn(context, uri, null, null) == null) {
                        getDataColumn(
                            context,
                            Uri.parse(getImageUrlWithAuthority(context, uri)),
                            null,
                            null
                        )
                    } else {
                        getDataColumn(context, uri, null, null)
                    }
                }
                return getDataColumn(context, uri, null, null)
            }
            // File
            else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }
        } else {
            val proj =
                arrayOf(Media.DATA)
            var result: String? = null
            val cursorLoader = CursorLoader(
                context,
                uri, proj, null, null, null
            )
            val cursor: Cursor? = cursorLoader.loadInBackground()
            if (cursor != null) {
                val column_index =
                    cursor.getColumnIndexOrThrow(Media.DATA)
                cursor.moveToFirst()
                result = cursor.getString(column_index)
            }
            return result
        }
        return null
    }

    fun getFile(context: Context, uri: Uri?): File? {
        if (uri != null) {
            val path = getPath(context, uri)
            if (path != null && isLocal(path)) {
                return File(path)
            }
        }
        return null
    }

    fun getImageUrlWithAuthority(
        context: Context,
        uri: Uri
    ): String? {
        var `is`: InputStream? = null
        if (uri.authority != null) {
            try {
                `is` = context.contentResolver.openInputStream(uri)
                val bmp: Bitmap = BitmapFactory.decodeStream(`is`)
                return writeToTempImageAndGetPathUri(context, bmp).toString()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } finally {
                try {
                    `is`!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    fun writeToTempImageAndGetPathUri(
        inContext: Context,
        inImage: Bitmap
    ): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(CompressFormat.JPEG, 100, bytes)
        val path: String? = Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }
}