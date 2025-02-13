package cat.copernic.project3_group4.core.utils

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

fun uriToMultipartBodyPart(context: Context, uri: Uri): MultipartBody.Part {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return MultipartBody.Part.createFormData("image", "")
    val tempFile = File(context.cacheDir, "temp_image.jpg")
    val outputStream = FileOutputStream(tempFile)

    inputStream.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }

    val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), tempFile)
    return MultipartBody.Part.createFormData("image", tempFile.name, requestFile)
}
