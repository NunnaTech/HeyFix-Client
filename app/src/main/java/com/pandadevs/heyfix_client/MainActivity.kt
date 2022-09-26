package com.pandadevs.heyfix_client

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pandadevs.heyfix_client.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}