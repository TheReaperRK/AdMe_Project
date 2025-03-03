import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.project3_group4.core.utils.createPartFromString
import cat.copernic.project3_group4.core.utils.uriToMultipartBodyPart
import cat.copernic.project3_group4.user_management.data.datasource.AuthRetrofitInstance
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    var name = mutableStateOf("")
    var email = mutableStateOf("")
    var phone = mutableStateOf("")
    var password = mutableStateOf("")
    var confirmPassword = mutableStateOf("")
    var selectedImageUri = mutableStateOf<Uri?>(null)

    // Errores
    var nameError = mutableStateOf<String?>(null)
    var emailError = mutableStateOf<String?>(null)
    var phoneError = mutableStateOf<String?>(null)
    var passwordError = mutableStateOf<String?>(null)
    var confirmPasswordError = mutableStateOf<String?>(null)

    fun validateFields(): Boolean {
        var isValid = true

        if (name.value.length > 60 || !name.value.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$"))) {
            nameError.value = "El nombre debe tener máximo 60 caracteres y solo letras."
            isValid = false

        } else nameError.value = null

        if (email.value.length > 60 || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
            emailError.value = "Correo inválido (máximo 60 caracteres)."
            isValid = false
        } else emailError.value = null

        if (!phone.value.matches(Regex("^[0-9]{9,15}$"))) {
            phoneError.value = "Teléfono inválido (entre 9 y 15 números)."
            isValid = false
        } else phoneError.value = null

        if (password.value.length < 8 || !password.value.any { it.isUpperCase() } ||
            !password.value.any { it.isLowerCase() } || !password.value.any { it.isDigit() } ||
            !password.value.any { !it.isLetterOrDigit() }) {
            passwordError.value = "Debe tener mínimo 8 caracteres, mayúsculas, minúsculas, números y un símbolo."
            isValid = false
        } else passwordError.value = null


        if (password.value != confirmPassword.value) {
            confirmPasswordError.value = "Las contraseñas no coinciden."
            isValid = false
        } else confirmPasswordError.value = null

        return isValid
    }

    fun register(context: android.content.Context, navController: androidx.navigation.NavController) {
        if (!validateFields()) return

        viewModelScope.launch {
            val emailPart = createPartFromString(email.value)
            val namePart = createPartFromString(name.value)
            val phonePart = createPartFromString(phone.value)
            val passwordPart = createPartFromString(password.value)
            val imagePart = selectedImageUri.value?.let { uriToMultipartBodyPart(context, it) }

            val response = AuthRetrofitInstance.authApi.register(namePart, emailPart, phonePart, passwordPart, imagePart)

            if (response.isSuccessful) {
                navController.navigate("login")
                Log.d("Register", "✅ Registro correcto")
            } else {
                Log.e("Register", "❌ Error en el Registro ")
                emailError.value = "Error en el registro, intente con otro correo."
            }
        }
    }

}
