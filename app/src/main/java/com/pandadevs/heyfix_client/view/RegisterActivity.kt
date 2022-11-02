package com.pandadevs.heyfix_client.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputLayout
import com.pandadevs.heyfix_client.R
import com.pandadevs.heyfix_client.databinding.ActivityRegisterBinding
import com.pandadevs.heyfix_client.utils.SnackbarShow
import com.pandadevs.heyfix_client.utils.Validations.fieldNotEmpty
import com.pandadevs.heyfix_client.utils.Validations.fieldRegexEmail
import com.pandadevs.heyfix_client.utils.Validations.fieldRegexName

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var editsInputsList: List<TextInputLayout> = listOf()
    private var areCorrectFieldsList: MutableList<Boolean> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        editsInputsList = listOf(
            binding.etName,
            binding.etLastName,
            binding.etEmail,
            binding.etPhone,
            binding.etNewPassword,
            binding.etRepeatNewPassword
        )
        areCorrectFieldsList = mutableListOf(false, false, false, false, false, false)

        binding.tbApp.setNavigationOnClickListener { finish() }
        binding.btnRegister.setOnClickListener { checkFields() }
        activeEventListenerOnEditText()

    }

    private fun checkFields() {
        if (areCorrectFieldsList.none { !it } && checkPasswordsFields()) {
            SnackbarShow.showSnackbar(binding.root, "Registro exitoso")
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


}