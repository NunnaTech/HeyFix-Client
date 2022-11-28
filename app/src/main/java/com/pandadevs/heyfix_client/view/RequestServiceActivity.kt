package com.pandadevs.heyfix_client.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pandadevs.heyfix_client.databinding.ActivityRequestServiceBinding

class RequestServiceActivity : AppCompatActivity() {

    companion object {
        const val HIRED_SERVICE = "HIRED_SERVICE"
    }

    private lateinit var binding: ActivityRequestServiceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tbApp.setNavigationOnClickListener { finish() }
        binding.btnChat.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        binding.btnRate.setOnClickListener {
            startActivity(Intent(this, RateActivity::class.java))
        }
    }
}