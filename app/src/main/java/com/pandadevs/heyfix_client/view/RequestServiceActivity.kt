package com.pandadevs.heyfix_client.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pandadevs.heyfix_client.R
import com.pandadevs.heyfix_client.data.model.RateChatModel
import com.pandadevs.heyfix_client.databinding.ActivityRequestServiceBinding
import com.pandadevs.heyfix_client.viewmodel.HiredServiceViewModel
import kotlinx.coroutines.launch

class RequestServiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestServiceBinding
    private var idHiredService: String = ""
    private val hiredServiceViewModel: HiredServiceViewModel by viewModels()
    private var rateModel: RateChatModel = RateChatModel("", "", "", "", "", "")

    companion object {
        const val ID_SERVICE_HIRED = "ID_SERVICE_HIRED"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initObservers()
    }

    private fun initView() {
        idHiredService = intent.getStringExtra(ID_SERVICE_HIRED) ?: ""
        binding.tbApp.setNavigationOnClickListener { finish() }
        lifecycleScope.launch {
            hiredServiceViewModel.getDataHiredService(idHiredService)
            hiredServiceViewModel.isThereACurrentService(idHiredService)
        }
    }

    private fun initObservers() {
        hiredServiceViewModel.isThereACurrentService.observe(this) {
            if (it != null) {
                idHiredService = it.id
                rateModel.id = it.id
                rateModel.worker_name = it.worker_name
                binding.tvName.text = it.worker_name
                binding.btnCancelService.isEnabled = true
                binding.btnChat.isEnabled = true
                binding.btnMap.isEnabled = true
                binding.btnCancelService.setOnClickListener { confirmCancelService() }
                binding.btnChat.setOnClickListener {
                    goToChatActivity()
                }
                binding.btnMap.setOnClickListener {
                    goToMapActivity()
                }
                if (it.canceled && !it.completed) callBackServiceCanceled()
                if (it.arrived && !it.completed && !it.canceled) callBackServiceArrived()
                if (it.completed && !it.canceled) callBackServiceCompleted()
            }
        }

        hiredServiceViewModel.dataWorker.observe(this) {
            rateModel.worker_picture = it.picture
            rateModel.worker_transport = it.transport
            Glide.with(this).load(it.picture).into(binding.civPicture)
            binding.tvTransport.text = it.transport
            binding.btnCall.isEnabled = true
            binding.btnCall.setOnClickListener { _ -> openCall(it.phone_number) }
        }

        hiredServiceViewModel.dataCategory.observe(this) {
            rateModel.category_icon = it.icon
            rateModel.category_name = it.name
            binding.tvCategory.text = it.name
            Glide.with(this).load(it.icon).into(binding.ivCategory)
        }
    }

    private fun goToMapActivity() {
        val intent = Intent(this, MapServiceActivity::class.java)
        intent.putExtra(MapServiceActivity.ID_HIRED_SERVICE, idHiredService)
        startActivity(intent)
    }

    private fun goToChatActivity() {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(ChatActivity.CHAT_MODEL, rateModel)
        startActivity(intent)
    }

    private fun callBackServiceCompleted() {
        MaterialAlertDialogBuilder(
            this,
            com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
        )
            .setTitle("Servicio finalizado")
            .setIcon(R.drawable.ilus_complete)
            .setCancelable(false)
            .setMessage("¡Muy bien! El servicio ha sido finalizado, puedes calificar al trabajador.")
            .setPositiveButton("Aceptar") { _, _ -> goToRateService() }
            .show()
    }

    private fun callBackServiceArrived() {
        MaterialAlertDialogBuilder(this, R.style.MyThemeOverlay_MaterialAlertDialog_Arrived)
            .setTitle("El trabajador ha llegado")
            .setIcon(R.drawable.ilus_arrived)
            .setCancelable(false)
            .setMessage("Vamos ve abrirle la puerta al trabajador que esta esperando.")
            .setPositiveButton("Aceptar") { _, _ -> }
            .show()
    }

    private fun callBackServiceCanceled() {
        MaterialAlertDialogBuilder(this, R.style.MyThemeOverlay_MaterialAlertDialog_Canceled)
            .setTitle("Servicio cancelado")
            .setIcon(R.drawable.ilus_cancel)
            .setCancelable(false)
            .setMessage("El servicio ha sido cancelado.")
            .setPositiveButton("Aceptar") { _, _ -> goToHomeActivity() }
            .show()
    }

    private fun confirmCancelService() {
        MaterialAlertDialogBuilder(this)
            .setTitle("¿Cancelar servicio?")
            .setMessage("Se eliminará el progreso del servicio")
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Sí, cancelar") { _, _ -> onCancelService() }
            .show()
    }

    private fun onCancelService() {
        lifecycleScope.launch {
            hiredServiceViewModel.cancelService(
                idHiredService,
                hiredServiceViewModel.isThereACurrentService.value!!.client_id,
                hiredServiceViewModel.isThereACurrentService.value!!.worker_id,
            )
        }
    }

    private fun goToHomeActivity() {
        val intent = Intent(this, MainActivity::class.java)
        finishAffinity()
        startActivity(intent)
    }

    private fun goToRateService() {
        val intent = Intent(this, RateActivity::class.java)
        intent.putExtra(RateActivity.RATE_MODEL, rateModel)
        startActivity(intent)
    }

    private fun openCall(phone_number: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${phone_number}")
        startActivity(intent)
    }
}