package com.pandadevs.heyfix_client

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.pandadevs.heyfix_client.databinding.ActivityRequestServiceBinding

class RequestServiceActivity : AppCompatActivity() {
    private lateinit var binding:ActivityRequestServiceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.chat.setOnClickListener {
            Toast.makeText(this, "Si funciona este boton de chat", Toast.LENGTH_SHORT).show()
        }

        binding.cancelar.setOnClickListener {
            Toast.makeText(this, "Si funciona este boton de cancelar", Toast.LENGTH_SHORT).show()
        }
    }
}