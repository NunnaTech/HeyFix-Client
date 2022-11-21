package com.pandadevs.heyfix_client.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pandadevs.heyfix_client.R
import com.pandadevs.heyfix_client.data.model.MyGeocoder
import com.pandadevs.heyfix_client.databinding.ActivityLoadingBinding

class LoadingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoadingBinding
    private lateinit var myGeocoder: MyGeocoder

    companion object {
        const val UBICATION_SELECTED: String = "LOCATION_SELECTED"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myGeocoder = intent.getSerializableExtra(UBICATION_SELECTED) as MyGeocoder
        binding.tvDirection.text = myGeocoder.address
        binding.btnCancelService.setOnClickListener { confirmCancelService() }
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