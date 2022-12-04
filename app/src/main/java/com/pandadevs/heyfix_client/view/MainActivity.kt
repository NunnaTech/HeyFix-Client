package com.pandadevs.heyfix_client.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pandadevs.heyfix_client.R
import com.pandadevs.heyfix_client.databinding.ActivityMainBinding
import com.pandadevs.heyfix_client.provider.UserLastProvider
import com.pandadevs.heyfix_client.utils.SharedPreferenceManager
import com.pandadevs.heyfix_client.viewmodel.HiredServiceViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val hiredServiceViewModel: HiredServiceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navigation: BottomNavigationView = binding.bnvNavigation
        val navController = findNavController(R.id.fragmentNavHost)
        navigation.setupWithNavController(navController)

        initObservers()
    }

    private fun initObservers() {
        hiredServiceViewModel.isThereACurrentServiceBoolean.observe(this) {
            if (it != null) {
                val intent = Intent(this, RequestServiceActivity::class.java)
                intent.putExtra(RequestServiceActivity.ID_SERVICE_HIRED, it.id)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val userId = SharedPreferenceManager(this).getUser()!!.id
        setLastOnline()
        lifecycleScope.launch { hiredServiceViewModel.isThereACurrentServiceBoolean(userId) }
    }

    private fun setLastOnline() {
        UserLastProvider.setLastOnline(context = this@MainActivity)
    }
}