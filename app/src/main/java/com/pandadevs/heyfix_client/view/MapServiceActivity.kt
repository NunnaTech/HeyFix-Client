package com.pandadevs.heyfix_client.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.pandadevs.heyfix_client.R
import com.pandadevs.heyfix_client.data.model.HiredServiceModel
import com.pandadevs.heyfix_client.databinding.ActivityMapServiceBinding
import com.pandadevs.heyfix_client.utils.SnackbarShow
import com.pandadevs.heyfix_client.viewmodel.HiredServiceViewModel
import kotlinx.coroutines.launch

class MapServiceActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapServiceBinding
    private val hiredServiceViewModel: HiredServiceViewModel by viewModels()
    private var idHiredService: String = ""

    companion object {
        const val ID_HIRED_SERVICE = "ID_HIRED_SERVICE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initObservers()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initView() {
        binding.tbApp.setNavigationOnClickListener { finish() }
        idHiredService = intent.getStringExtra(ID_HIRED_SERVICE) ?: ""
        SnackbarShow.showSnackbar(binding.root, "Espere trazando trayecto...")
        lifecycleScope.launch {
            hiredServiceViewModel.isThereACurrentService(idHiredService)
        }
    }

    private fun initObservers() {
        hiredServiceViewModel.isThereACurrentService.observe(this) {
            if (it != null) {
                setPolyLines(it)
            }
        }
    }

    private fun setPolyLines(hiredServiceModel: HiredServiceModel) {
        if (!::mMap.isInitialized) return
        mMap.clear()

        val clientUbication = LatLng(
            hiredServiceModel.client_ubication.latitude.toString().substring(0, 10).toDouble(),
            hiredServiceModel.client_ubication.longitude.toString().substring(0, 10).toDouble(),
        )
        val workerUbication = LatLng(
            hiredServiceModel.worker_ubication.latitude,
            hiredServiceModel.worker_ubication.longitude,
        )
        val clientMarket =
            MarkerOptions().position(clientUbication).title(hiredServiceModel.client_name)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ilus_pin_client))
        val workerMarket =
            MarkerOptions().position(workerUbication).title(hiredServiceModel.worker_name)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ilus_pin_worker))
        mMap.addMarker(clientMarket)
        mMap.addMarker(workerMarket)

        val polylineOptions = PolylineOptions()
            .add(clientUbication)
            .add(workerUbication)
            .width(15f)
            .color(ContextCompat.getColor(this,R.color.md_theme_light_primary))
        val polyline = mMap.addPolyline(polylineOptions)
        val patron = listOf(Dot(), Gap(10f), Dash(40f), Gap(10f))
        polyline.pattern = patron

        val builder: LatLngBounds.Builder = LatLngBounds.Builder()
        builder.include(clientMarket.position)
        builder.include(workerMarket.position)
        val bounds: LatLngBounds = builder.build()
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100)
        mMap.animateCamera(cameraUpdate)
        binding.lProgress.visibility = View.GONE
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }
}