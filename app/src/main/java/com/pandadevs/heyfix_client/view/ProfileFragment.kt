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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.pandadevs.heyfix_client.data.model.UserGet
import com.pandadevs.heyfix_client.databinding.FragmentProfileBinding
import com.pandadevs.heyfix_client.utils.LoadingScreen
import com.pandadevs.heyfix_client.utils.SharedPreferenceManager
import com.pandadevs.heyfix_client.utils.SnackbarShow
import com.pandadevs.heyfix_client.utils.Validations.fieldNotEmpty
import com.pandadevs.heyfix_client.utils.Validations.fieldRegexName
import com.pandadevs.heyfix_client.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageUrl: Uri
    private var isNotEmpty = false
    private var editsInputsList: List<TextInputLayout> = listOf()
    private var areCorrectFieldsList: MutableList<Boolean> = mutableListOf()
    private lateinit var user: UserGet
    lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        editsInputsList =
            listOf(binding.etName, binding.etFirstSurname, binding.etSecondSurname, binding.etPhone)
        areCorrectFieldsList = mutableListOf(false, true, false, false)
        binding.btnChangePass.setOnClickListener { goToActivity(ChangePasswordActivity::class.java) }
        binding.btnAbout.setOnClickListener { goToActivity(AboutActivity::class.java) }
        binding.btnSave.setOnClickListener { checkFields() }
        activeEventListenerOnEditText()
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        binding.btnProfilePicture.setOnClickListener { requestPermission() }
        loadUserData()
        initObservers()
        binding.btnLogout.setOnClickListener { confirmCancelService() }
        return binding.root
    }

    private fun confirmCancelService() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("¿Cerrar sesión?")
            .setMessage("Se te mandará a la pantalla de iniciar sesión")
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Sí, cerrar sesión") { _, _ -> logout() }
            .show()
    }

    private fun logout() {
        SharedPreferenceManager(requireContext()).cleanShared()
        startActivity(Intent(requireContext(), SplashActivity::class.java))
        activity?.finish()
    }

    private fun updateData() {
        user.name = binding.etName.editText?.text.toString()
        user.first_surname = binding.etFirstSurname.editText?.text.toString()
        user.second_surname = binding.etSecondSurname.editText?.text.toString()
        user.phone_number = binding.etPhone.editText?.text.toString()
        if (isNotEmpty) {
            viewModel.updatePhotoUser(imageUrl, user)
        }
        viewModel.updateUserData(user)
        SharedPreferenceManager(requireContext()).saveUser(user)
        loadUserData()
    }

    private fun loadUserData() {
        user = SharedPreferenceManager(requireContext()).getUser()!!
        Glide.with(requireContext()).load(user.picture).into(binding.circleImageView)
        binding.txtUserName.text = "${user.name} ${user.first_surname}"
        binding.etEmail.editText?.setText(user.email)
        binding.etName.editText?.setText(user.name)
        binding.etFirstSurname.editText?.setText(user.first_surname)
        binding.etSecondSurname.editText?.setText(user.second_surname)
        binding.etPhone.editText?.setText(user.phone_number)
    }

    private fun initObservers() {
        viewModel.isDataProgress.observe(this) {
            if (it) {
                LoadingScreen.show(requireContext(), "Espere", false)
            } else {
                LoadingScreen.hide()
            }
            viewModel.result.observe(this) {
                SharedPreferenceManager(requireContext()).saveUser(user)!!
            }
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE

                ) == PackageManager.PERMISSION_GRANTED -> {
                    pickUpFromGallery()
                }
                else -> requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            pickUpFromGallery()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickUpFromGallery()
        } else {
            SnackbarShow.showSnackbar(binding.root, "Permisos denegados")
        }
    }

    private val startForActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.data
            imageUrl = data!!
            isNotEmpty = true
            binding.circleImageView.setImageURI(data)
        }
    }

    private fun pickUpFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startForActivityResult.launch(intent)
    }

    private fun checkFields() {
        if (areCorrectFieldsList.none { !it }) {
            SnackbarShow.showSnackbar(binding.root, "Guardado exitoso")
            updateData()
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

        binding.etFirstSurname.editText?.doOnTextChanged { text, _, _, _ ->
            areCorrectFieldsList[1] =
                fieldNotEmpty(editsInputsList[1], text.toString(), 2) && fieldRegexName(
                    editsInputsList[1],
                    text.toString()
                )
        }
        binding.etSecondSurname.editText?.doOnTextChanged { text, _, _, _ ->
            areCorrectFieldsList[2] =
                fieldRegexName(
                    editsInputsList[2],
                    text.toString()
                )
        }

        binding.etPhone.editText?.doOnTextChanged { text, _, _, _ ->
            areCorrectFieldsList[3] = fieldNotEmpty(editsInputsList[3], text.toString(), 10)
        }

    }

    private fun goToActivity(cls: Class<*>) {
        activity?.startActivity(Intent(context, cls))
    }
}