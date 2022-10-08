package com.pandadevs.heyfix_client

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pandadevs.heyfix_client.databinding.ActivityUbicationBinding

class UbicationActivity : AppCompatActivity() {
    lateinit var binding: ActivityUbicationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityUbicationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.tbApp.setNavigationOnClickListener { finish() }
        binding.btnImHere.setOnClickListener {
            startActivity(Intent(this, RequestServiceActivity::class.java))
        }
    }
}