package com.pandadevs.heyfix_client.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.pandadevs.heyfix_client.R
import com.pandadevs.heyfix_client.data.model.CategoryModel
import com.pandadevs.heyfix_client.databinding.ActivityMapsBinding
import kotlinx.android.synthetic.main.activity_maps.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var categoryModel: CategoryModel
    private lateinit var currentUserLocation: LatLng

    companion object {
        const val CATEGORY = "category"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categoryModel = intent.getSerializableExtra(CATEGORY) as CategoryModel

        binding.tvCategory.text = categoryModel.name
        binding.tbApp.setNavigationOnClickListener { finish() }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationProvider = LocationManager.NETWORK_PROVIDER
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap.isMyLocationEnabled = true // Activar mi ubicación
        @SuppressLint("MissingPermission")
        val lastKnownLocation = locationManager.getLastKnownLocation(locationProvider)
        currentUserLocation = LatLng(lastKnownLocation!!.latitude, lastKnownLocation.longitude)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUserLocation, 17f))
        cameraListener()
        binding.btnSearchDirection.setOnClickListener { searchByAddress() }
        binding.btnSaveUbication.setOnClickListener { saveUbication() }
    }

    private fun saveUbication() {
        startActivity(Intent(this, LoadingActivity::class.java))
    }

    private fun searchByAddress() {
        val address = binding.etDirections.text.toString()
        if(address.isEmpty()){
            binding.etDirectionsLayout.error = "Debe ingresar una dirección"
        }else{
            binding.etDirectionsLayout.error = null
            CoroutineScope(Dispatchers.IO).launch {
                var latLng = LatLng(0.0, 0.0)
                val geocoder = Geocoder(this@MapsActivity)
                val addresses = geocoder.getFromLocationName(address, 1)
                runOnUiThread {
                    if (addresses.size > 0) {
                        val firstAddress = addresses[0]
                        latLng = LatLng(firstAddress.latitude, firstAddress.longitude)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
                    }
                }
            }
        }
    }


    private fun cameraListener() {
        mMap.setOnCameraIdleListener {
            val lat = mMap.cameraPosition.target.latitude
            val long = mMap.cameraPosition.target.longitude
            getDirectionByLatLng(lat, long)
        }
    }

    private fun getDirectionByLatLng(lat: Double, long: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, long, 3)
            if (addresses.size > 0) {
                val arrayDirections = addresses.map { it.getAddressLine(0) }
                runOnUiThread {
                    setOnAutoComplete(arrayDirections)
                }
            }
        }
    }

    private fun setOnAutoComplete(list: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        binding.etDirections.setAdapter(adapter)
        binding.etDirections.showDropDown()
        binding.etDirections.setText(list[0], false)
    }
}