package com.pandadevs.heyfix_client.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pandadevs.heyfix_client.databinding.ActivityRateBinding

class RateActivity : AppCompatActivity() {
    lateinit var binding : ActivityRateBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tbApp.setNavigationOnClickListener { finish() }
    }
}