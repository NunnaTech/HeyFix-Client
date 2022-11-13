package com.pandadevs.heyfix_client.view

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.pandadevs.heyfix_client.databinding.ActivityChangePasswordBinding
import com.pandadevs.heyfix_client.utils.SharedPreferenceManager
import com.pandadevs.heyfix_client.utils.SnackbarShow
import com.pandadevs.heyfix_client.utils.Validations
import com.pandadevs.heyfix_client.utils.Validations.fieldNotEmpty

class   ChangePasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityChangePasswordBinding
    private var editsInputsList: List<TextInputLayout> = listOf()
    private var areCorrectFieldsList: MutableList<Boolean> = mutableListOf()
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("SHARED_PREF", 0)
        binding.tbApp.setNavigationOnClickListener { finish() }
        editsInputsList =
            listOf(binding.etEmail, binding.etNewPassword, binding.etRepeatNewPassword)
        areCorrectFieldsList = mutableListOf(false, false, false)
        activeEventListenerOnEditText()
        binding.btnChangePass.setOnClickListener { checkFields() }

    }

    private fun checkFields() {
        if(SharedPreferenceManager(this).getUser()?.active == true){
            System.out.println(SharedPreferenceManager(this).getUser())
            var user = FirebaseAuth.getInstance().currentUser
            var newPassword = binding.etNewPassword.editText.toString()
            if(user!=null){
                System.out.println(user.email)
                if (areCorrectFieldsList.none { !it } && checkPasswordsFields()) {
                    user!!.updatePassword(newPassword).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            System.out.println()
                            SnackbarShow.showSnackbar(binding.root, "Cambio exitoso")

                            FirebaseAuth.getInstance().signOut()
                            startActivity(
                                Intent(this, LoginActivity::class.java)
                            )
                        } else {
                            SnackbarShow.showSnackbar(binding.root, "Error al actualizar")
                            Toast.makeText(this, "Error " + task.exception, Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    editsInputsList.forEachIndexed { index, it ->
                        if (!areCorrectFieldsList[index])
                            it.error = "* Requerido"
                    }
                }
            }
        }


    }

    private fun checkPasswordsFields(): Boolean {
        val areCorrectPasswords =
            editsInputsList[1].editText?.text.toString() == editsInputsList[2].editText?.text.toString()
        if (!areCorrectPasswords) {
            editsInputsList[1].error = "* Requerido"
            editsInputsList[2].error = "* Requerido"
            SnackbarShow.showSnackbar(binding.root, "Las contraseñas no coinciden")
        }
        return areCorrectPasswords
    }

    private fun activeEventListenerOnEditText() {
        binding.etEmail.editText?.doOnTextChanged { text, _, _, _ ->
            areCorrectFieldsList[0] =
                fieldNotEmpty(editsInputsList[0], text.toString()) && Validations.fieldRegexEmail(
                    editsInputsList[0],
                    text.toString()
                )
        }
        binding.etNewPassword.editText?.doOnTextChanged { text, _, _, _ ->
            areCorrectFieldsList[1] = fieldNotEmpty(editsInputsList[1], text.toString())
        }
        binding.etRepeatNewPassword.editText?.doOnTextChanged { text, _, _, _ ->
            areCorrectFieldsList[2] = fieldNotEmpty(editsInputsList[2], text.toString())
        }
    }


}