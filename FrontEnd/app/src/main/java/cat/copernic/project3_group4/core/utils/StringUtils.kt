package cat.copernic.project3_group4.core.utils

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

fun createPartFromString(value: String): RequestBody {
    return RequestBody.create("text/plain".toMediaTypeOrNull(), value)
}
