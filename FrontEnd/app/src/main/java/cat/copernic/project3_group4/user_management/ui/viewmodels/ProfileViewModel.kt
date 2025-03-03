package cat.copernic.project3_group4.user_management.ui.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.project3_group4.user_management.data.datasource.UserApiRest
import cat.copernic.project3_group4.user_management.data.datasource.UserRetrofitInstance
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ProfileViewModel : ViewModel() {
    var profileImageUri = mutableStateOf<Uri?>(null)

    fun uploadProfileImage(userId: Long, uri: Uri, context: Context) {
        viewModelScope.launch {
            val retrofit = UserRetrofitInstance.retrofitInstance
            val userApi = retrofit.create(UserApiRest::class.java)
            val file = uriToFile(uri, context) // Convierte Uri a archivo
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

            try {
                val response = userApi.updateProfileImage(userId, imagePart)
                if (response.isSuccessful) {
                    profileImageUri.value = uri
                    println("bien")
                } else {
                    Log.e("ProfileViewModel", "Error al actualizar imagen")
                }
            } catch (e: Exception) {
                println("excepcion")
                Log.e("ProfileViewModel", "Excepción: ${e.message}")
            }
        }
    }

    private fun uriToFile(uri: Uri, context: Context): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("profile", ".jpg", context.cacheDir)
        inputStream?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }
}
