package com.pandadevs.heyfix_client.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.GeoPoint
import com.pandadevs.heyfix_client.R
import com.pandadevs.heyfix_client.data.model.CategoryModel
import com.pandadevs.heyfix_client.data.model.MyGeocoder
import com.pandadevs.heyfix_client.databinding.ActivityMapsBinding
import com.pandadevs.heyfix_client.provider.UserLastProvider
import com.pandadevs.heyfix_client.utils.SnackbarShow
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
    private var ubicationSelected = MyGeocoder(address = "", latitude = 0.0, longitude = 0.0)

    companion object {
        const val CATEGORY = "CATEGORY"
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isLocationPermissionGranted()
        requestLocationPermission()
        categoryModel = intent.getSerializableExtra(CATEGORY) as CategoryModel
        binding.tvCategory.text = categoryModel.name
        binding.tbApp.setNavigationOnClickListener { finish() }
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    private fun isLocationPermissionGranted(): Boolean = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED


    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            SnackbarShow.showSnackbar(binding.root, "Ve ajustes a dar permisos de localización")
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.isMyLocationEnabled = true
                } else {
                    SnackbarShow.showSnackbar(binding.root, "Activar la localización en ajustes")
                }
            }
        }
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
        if (!::mMap.isInitialized) return
        if (isLocationPermissionGranted()) {
            mMap.isMyLocationEnabled = true
            @SuppressLint("MissingPermission")
            val lastKnownLocation = locationManager.getLastKnownLocation(locationProvider)
            currentUserLocation = LatLng(lastKnownLocation!!.latitude, lastKnownLocation.longitude)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUserLocation, 17f))
            cameraListener()
            UserLastProvider.setLastCurrentPosition(this, GeoPoint(currentUserLocation.latitude, currentUserLocation.longitude))
            binding.btnSearchDirection.setOnClickListener { searchByAddress() }
            binding.btnSaveUbication.setOnClickListener { saveUbication() }
        } else {
            requestLocationPermission()
        }
    }


    private fun searchByAddress() {
        val address = binding.etDirections.text.toString()
        if (address.isEmpty()) {
            binding.etDirectionsLayout.error = "Debe ingresar una dirección"
        } else {
            binding.etDirectionsLayout.error = null
            CoroutineScope(Dispatchers.IO).launch {
                val geocoder = Geocoder(this@MapsActivity)
                val addresses = geocoder.getFromLocationName(address, 1)
                runOnUiThread {
                    try {
                        if (addresses.size > 0) {
                            val firstAddress = addresses[0]
                            ubicationSelected.address = address
                            ubicationSelected.latitude = firstAddress.latitude
                            ubicationSelected.longitude = firstAddress.longitude
                            val latlong =
                                LatLng(ubicationSelected.latitude, ubicationSelected.longitude)
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 17f))
                        }
                    } catch (e: Exception) {
                        Log.e("MapsActivity", "searchByAddress: ${e.message}")
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
                ubicationSelected.address = addresses[0].getAddressLine(0)
                ubicationSelected.latitude = lat
                ubicationSelected.longitude = long
                val arrayDirections = addresses.map { it.getAddressLine(0) }
                runOnUiThread {
                    try {
                        setOnAutoComplete(arrayDirections)
                    } catch (ex: Exception) {
                        Log.e("MapsActivity", ex.message.toString())
                    }
                }
            }
        }
    }

    private fun saveUbication() {
        val intent = Intent(this, LoadingActivity::class.java)
        intent.putExtra(LoadingActivity.UBICATION_SELECTED, ubicationSelected)
        intent.putExtra(LoadingActivity.CATEGORY_SELECTED, categoryModel)
        startActivity(intent)
    }

    private fun setOnAutoComplete(list: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        binding.etDirections.setAdapter(adapter)
        binding.etDirections.showDropDown()
        binding.etDirections.setText(list[0], false)
    }
}