package com.pandadevs.heyfix_client.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.pandadevs.heyfix_client.R
import com.pandadevs.heyfix_client.databinding.ActivityRateBinding

class RateActivity : AppCompatActivity() {
    lateinit var binding: ActivityRateBinding
    lateinit var buttons: ArrayList<ImageButton>
    private var rate = 0

    companion object {
        const val ID_SERVICE_HIRED = "ID_SERVICE_HIRED"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tbApp.setNavigationOnClickListener { finish() }
        initView()
    }

    private fun initView() {
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
    }
}