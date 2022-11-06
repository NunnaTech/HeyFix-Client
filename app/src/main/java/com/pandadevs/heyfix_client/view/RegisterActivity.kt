package com.pandadevs.heyfix_client.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.pandadevs.heyfix_client.R
import com.pandadevs.heyfix_client.data.model.UserPost
import com.pandadevs.heyfix_client.databinding.ActivityRegisterBinding
import com.pandadevs.heyfix_client.utils.SnackbarShow
import com.pandadevs.heyfix_client.utils.Validations.fieldNotEmpty
import com.pandadevs.heyfix_client.utils.Validations.fieldRegexEmail
import com.pandadevs.heyfix_client.utils.Validations.fieldRegexName
import com.pandadevs.heyfix_client.viewmodel.RegisterViewModel
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var google: GoogleSignInClient
    private lateinit var binding: ActivityRegisterBinding
    private var editsInputsList: List<TextInputLayout> = listOf()
    private var areCorrectFieldsList: MutableList<Boolean> = mutableListOf()
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        editsInputsList = listOf(
            binding.etName,
            binding.etLastName,
            binding.etEmail,
            binding.etPhone,
            binding.etNewPassword,
            binding.etRepeatNewPassword
        )
        areCorrectFieldsList = mutableListOf(false, false, false, false, false, false)

        // Variables for Google Sign-in
        val options = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken("61020705136-mufc3648s89a2ajcip1sd45e4r85p2of.apps.googleusercontent.com")
            .requestEmail()
            .build()
        google = GoogleSignIn.getClient(this,options)

        binding.tbApp.setNavigationOnClickListener { finish() }
        binding.btnRegister.setOnClickListener { checkFields() }
        binding.btnGoogle.setOnClickListener {
            google.signOut()
            google.silentSignIn()
            val intent = google.signInIntent
            getResult.launch(intent)
        }
        activeEventListenerOnEditText()
        initObservers()
    }

    private fun checkFields() {
        if (areCorrectFieldsList.none { !it } && checkPasswordsFields()) {

            if (binding.etNewPassword.editText?.text.toString()
                == binding.etRepeatNewPassword.editText?.text.toString()){
                var names = binding.etLastName.editText?.text.toString().split(" ")
                var user = UserPost(
                    binding.etName.editText?.text.toString(),
                    names[0],
                    second_surname = if (names.size > 1) names[1] else "" ,
                    true,
                    true,
                    binding.etEmail.editText?.text.toString(),
                    binding.etPhone.editText?.text.toString(),
                    picture = "",
                    ranked_avg = 0.0,
                    transport = "",
                    category_id = "",
                    current_position = "",
                )
                lifecycleScope.launch {
                    viewModel.registerData(user,binding.etNewPassword.editText?.text.toString())
                }
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }else{
                SnackbarShow.showSnackbar(binding.root,"Error, las contraseñas deben ser iguales")
            }

        } else {
            editsInputsList.forEachIndexed { index, it ->
                if (!areCorrectFieldsList[index])
                    it.error = "* Requerido"
            }
        }
    }

    private fun checkPasswordsFields(): Boolean {
        val areCorrectPasswords =
            editsInputsList[4].editText?.text.toString() == editsInputsList[5].editText?.text.toString()
        if (!areCorrectPasswords) {
            editsInputsList[4].error = "* Requerido"
            editsInputsList[5].error = "* Requerido"
            SnackbarShow.showSnackbar(binding.root, "Las contraseñas no coinciden")
        }
        return areCorrectPasswords
    }

    private fun activeEventListenerOnEditText() {
        binding.etName.editText?.doOnTextChanged { text, _, _, _ ->
            areCorrectFieldsList[0] =
                fieldNotEmpty(editsInputsList[0], text.toString(), 2) && fieldRegexName(
                    editsInputsList[0],
                    text.toString()
                )
        }

        binding.etLastName.editText?.doOnTextChanged { text, _, _, _ ->
            areCorrectFieldsList[1] =
                fieldNotEmpty(editsInputsList[1], text.toString(), 2) && fieldRegexName(
                    editsInputsList[1],
                    text.toString()
                )
        }


        binding.etEmail.editText?.doOnTextChanged { text, _, _, _ ->
            areCorrectFieldsList[2] =
                fieldNotEmpty(editsInputsList[4], text.toString()) && fieldRegexEmail(
                    editsInputsList[2],
                    text.toString()
                )
        }

        binding.etPhone.editText?.doOnTextChanged { text, _, _, _ ->
            areCorrectFieldsList[3] = fieldNotEmpty(editsInputsList[3], text.toString(), 10)
        }

        binding.etNewPassword.editText?.doOnTextChanged { text, _, _, _ ->
            areCorrectFieldsList[4] = fieldNotEmpty(editsInputsList[4], text.toString())
        }

        binding.etRepeatNewPassword.editText?.doOnTextChanged { text, _, _, _ ->
            areCorrectFieldsList[5] = fieldNotEmpty(editsInputsList[5], text.toString())
        }
    }

    fun initObservers(){
        viewModel.result.observe(this){
            SnackbarShow.showSnackbar(binding.root,it)
        }

        viewModel.error.observe(this){
           SnackbarShow.showSnackbar(binding.root,it)
        }
    }


    fun registerWithGoogle(user:UserPost){
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document()
            .set(user)
            .addOnSuccessListener {
                SnackbarShow.showSnackbar(binding.root,"Registro Exitoso")
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                SnackbarShow.showSnackbar(binding.root,"El Registro de Datos fue Invalido")
            }
    }

    val getResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            var account = task.getResult(ApiException::class.java)
            var names = account.familyName.toString().split(" ")
            var data = UserPost(
                account.givenName.toString(),
                names[0],
                second_surname = if (names.size > 1) names[1] else "" ,
                true,
                true,
                account.email.toString(),
                phone_number = "",
                account.photoUrl.toString(),
                ranked_avg = 0.0,
                transport = "",
                category_id = "",
                current_position = ""
            )
            registerWithGoogle(data)
        }
    }
}