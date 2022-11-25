package com.pandadevs.heyfix_client.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pandadevs.heyfix_client.data.model.CategoryModel
import com.pandadevs.heyfix_client.data.model.MyGeocoder
import com.pandadevs.heyfix_client.databinding.ActivityLoadingBinding
import com.pandadevs.heyfix_client.provider.RequestServiceProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoadingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoadingBinding
    private lateinit var myGeocoder: MyGeocoder
    private lateinit var myCategoryModel: CategoryModel

    companion object {
        const val UBICATION_SELECTED: String = "LOCATION_SELECTED"
        const val CATEGORY_SELECTED: String = "CATEGORY_SELECTED"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myGeocoder = intent.getSerializableExtra(UBICATION_SELECTED) as MyGeocoder
        myCategoryModel = intent.getSerializableExtra(CATEGORY_SELECTED) as CategoryModel
        binding.tvDirection.text = myGeocoder.address
        binding.tvCategory.text = myCategoryModel.name
        binding.btnCancelService.setOnClickListener { confirmCancelService() }
        RequestServiceProvider.searchRequestService(myCategoryModel, myGeocoder)
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