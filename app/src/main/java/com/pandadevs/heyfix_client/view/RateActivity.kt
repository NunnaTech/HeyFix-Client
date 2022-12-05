package com.pandadevs.heyfix_client.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pandadevs.heyfix_client.R
import com.pandadevs.heyfix_client.data.model.RateModel
import com.pandadevs.heyfix_client.databinding.ActivityRateBinding
import com.pandadevs.heyfix_client.utils.SnackbarShow
import com.pandadevs.heyfix_client.viewmodel.HiredServiceViewModel
import kotlinx.coroutines.launch

class RateActivity : AppCompatActivity() {
    lateinit var binding: ActivityRateBinding
    lateinit var buttons: ArrayList<ImageButton>
    lateinit var dataRateModel: RateModel
    private var rate: Int = 0
    private var review: String = ""
    private val hiredServiceViewModel: HiredServiceViewModel by viewModels()

    companion object {
        const val RATE_MODEL = "RATE_MODEL"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        dataRateModel = intent.getSerializableExtra(RATE_MODEL) as RateModel
        binding.tvName.text = dataRateModel.worker_name
        binding.tvCategory.text = dataRateModel.category_name
        binding.tvTransport.text = dataRateModel.worker_transport
        Glide.with(this).load(dataRateModel.category_icon).into(binding.ivCategory)
        Glide.with(this).load(dataRateModel.worker_picture).into(binding.civPicture)

        buttons = arrayListOf(
            binding.ibtnStar1,
            binding.ibtnStar2,
            binding.ibtnStar3,
            binding.ibtnStar4,
            binding.ibtnStar5
        )

        for (i in 0 until buttons.size) {
            buttons[i].setOnClickListener {
                rate = i + 1
                for (j in 0..i) buttons[j].setImageResource(R.drawable.ic_star)
                for (j in i + 1 until buttons.size) buttons[j].setImageResource(R.drawable.ic_star_border)
            }
        }

        binding.tbApp.setNavigationOnClickListener { goToHomeActivity() }
        binding.btnSkip.setOnClickListener { goToHomeActivity() }
        binding.btnRate.setOnClickListener { rateService() }
    }

    private fun rateService() {
        review = binding.etReview.editText?.text.toString()
        if (rate == 0 || review.isNullOrEmpty()) {
            SnackbarShow.showSnackbar(
                binding.root,
                "Se necesita una puntuación o comentario para calificar el servicio"
            )
        } else {
            lifecycleScope.launch { hiredServiceViewModel.rateHiredService(dataRateModel.id, rate, review) }
            MaterialAlertDialogBuilder(this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                .setTitle("¡Gracias por calificar el servicio!")
                .setIcon(R.drawable.ilus_thanks)
                .setCancelable(false)
                .setMessage("Esperamos que haya sido una buena experiencia y vuelvas a estar con nosotros.")
                .setPositiveButton("Aceptar") { _, _ -> goToHomeActivity() }
                .show()
        }
    }

    private fun goToHomeActivity() {
        val intent = Intent(this, MainActivity::class.java)
        finishAffinity()
        startActivity(intent)
    }
}