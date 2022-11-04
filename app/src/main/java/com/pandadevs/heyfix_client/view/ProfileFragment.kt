package com.pandadevs.heyfix_client.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentActivity
import com.google.android.material.textfield.TextInputLayout
import com.pandadevs.heyfix_client.databinding.FragmentProfileBinding
import com.pandadevs.heyfix_client.utils.SnackbarShow
import com.pandadevs.heyfix_client.utils.Validations.fieldNotEmpty
import com.pandadevs.heyfix_client.utils.Validations.fieldRegexName

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageUrl: Uri
    private var isNotEmpty = false
    private var editsInputsList: List<TextInputLayout> = listOf()
    private var areCorrectFieldsList: MutableList<Boolean> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        editsInputsList = listOf(binding.etName, binding.etLastName, binding.etPhone)
        areCorrectFieldsList = mutableListOf(false, false, false)
        binding.btnChangePass.setOnClickListener { goToActivity(ChangePasswordActivity::class.java) }
        binding.btnAbout.setOnClickListener { goToActivity(AboutActivity::class.java) }
        binding.btnSave.setOnClickListener { checkFields() }
        activeEventListenerOnEditText()
        // change profile picture
        binding.btnProfilePicture.setOnClickListener { requestPermission() }
        return binding.root
    }
    private fun requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            when{
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE

                ) == PackageManager.PERMISSION_GRANTED -> {
                    pickUpFromGallery()
                }
                else ->  requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else{
            pickUpFromGallery()
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->

        if (isGranted){
            pickUpFromGallery()
        }else{
            Toast.makeText(
                requireContext(),
                "Permisos denegados",
                Toast.LENGTH_SHORT).show()
        }
    }

    private val startForActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            val data = result.data?.data
            imageUrl = data!!
            isNotEmpty = true
            binding.circleImageView.setImageURI(data)
        }
    }
    private fun pickUpFromGallery(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startForActivityResult.launch(intent)
    }
    private fun checkFields() {
        if (areCorrectFieldsList.none { !it }) {
            SnackbarShow.showSnackbar(binding.root, "Guardado exitoso")
        } else {
            editsInputsList.forEachIndexed { index, it ->
                if (!areCorrectFieldsList[index]) it.error = "* Requerido"
            }
        }
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

        binding.etPhone.editText?.doOnTextChanged { text, _, _, _ ->
            areCorrectFieldsList[2] = fieldNotEmpty(editsInputsList[2], text.toString(), 10)
        }

    }

    private fun goToActivity(cls: Class<*>) {
        activity?.startActivity(Intent(context, cls))
    }


}