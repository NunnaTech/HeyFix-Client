package com.pandadevs.heyfix_client.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pandadevs.heyfix_client.R
import com.pandadevs.heyfix_client.data.model.CategoryModel
import com.pandadevs.heyfix_client.data.model.MyGeocoder
import com.pandadevs.heyfix_client.data.model.UserSort
import com.pandadevs.heyfix_client.databinding.ActivityLoadingBinding
import com.pandadevs.heyfix_client.utils.SharedPreferenceManager
import com.pandadevs.heyfix_client.viewmodel.RequestServiceViewModel
import kotlinx.android.synthetic.main.item_history_service.view.*
import kotlinx.coroutines.launch

class LoadingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoadingBinding
    private lateinit var myGeocoder: MyGeocoder
    private lateinit var myCategoryModel: CategoryModel
    private lateinit var viewModel: RequestServiceViewModel
    private lateinit var userId: String

    companion object {
        const val UBICATION_SELECTED: String = "LOCATION_SELECTED"
        const val CATEGORY_SELECTED: String = "CATEGORY_SELECTED"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userId = SharedPreferenceManager(this).getUser()!!.id
        initView()
        initObservers()

    }

    private fun initView() {
        viewModel = ViewModelProvider(this)[RequestServiceViewModel::class.java]
        myGeocoder = intent.getSerializableExtra(UBICATION_SELECTED) as MyGeocoder
        myCategoryModel = intent.getSerializableExtra(CATEGORY_SELECTED) as CategoryModel
        binding.tvDirection.text = myGeocoder.address
        binding.tvCategory.text = myCategoryModel.name
        binding.btnCancelService.setOnClickListener { confirmCancelService() }
        lifecycleScope.launch { viewModel.searchRequestService(myCategoryModel, myGeocoder) }
    }

    private fun initObservers() {
        viewModel.usersFound.observe(this) {
            if (it.isEmpty()) {
                workerNotFound()
            } else {
                awaitResponse(it)
            }
        }
        viewModel.result.observe(this) {
            if (it.isNotEmpty()) {
                goToRequestServiceActivity(it.toString())
            }else{
                workerNotFound()
            }
        }
    }

    private fun goToRequestServiceActivity(hired_service: String) {
        val intent = Intent(this, RequestServiceActivity::class.java)
        intent.putExtra(RequestServiceActivity.HIRED_SERVICE, hired_service)
        startActivity(intent)
    }

    private fun workerNotFound() {
        binding.lProgress.visibility = android.view.View.GONE
        binding.tvLookingFor.text = "No se encontró a ningún"
        binding.tvLookingFor.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.md_theme_light_tertiary
            )
        )
        binding.givLoading.setImageResource(R.drawable.not_found)
    }

    private fun awaitResponse(users: MutableList<UserSort>) {
        lifecycleScope.launch {
            viewModel.waitForResponse(userId, myGeocoder, users)
        }
    }

    private fun confirmCancelService() {
        MaterialAlertDialogBuilder(this)
            .setTitle("¿Cancelar servicio?")
            .setMessage("Se eliminará la solicitud de tu servicio")
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Sí, cancelar") { _, _ -> finish() }
            .show()
    }
}