package com.example.legalgpt.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

fun getFileName(context: Context, uri: Uri): String {
    return if (uri.scheme == "content") {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                cursor.getString(nameIndex) ?: "Unknown file"
            } else {
                "Unknown file"
            }
        } ?: "Unknown file"
    } else {
        uri.path?.substringAfterLast('/') ?: "Unknown file"
    }
}
