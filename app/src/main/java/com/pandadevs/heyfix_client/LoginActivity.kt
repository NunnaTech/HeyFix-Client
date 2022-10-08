package com.pandadevs.heyfix_client

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pandadevs.heyfix_client.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnRegister.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
        binding.btnLogin.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
    }
}