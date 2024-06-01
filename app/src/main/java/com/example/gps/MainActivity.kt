package com.example.gps

import android.Manifest
import android.content.pm.PackageManager
import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private var latitude: TextView? = null
    private var longitude: TextView? = null
    private var altitude: TextView? = null
    private var akurasi: TextView? = null
    private var alamat: TextView? = null
    private var btnFind: Button? = null
    private var locationProviderClient: FusedLocationProviderClient? = null
    private var geocoder: Geocoder? = null
    private var addresses: List<Address>? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        latitude = findViewById(R.id.latitude)
        longitude = findViewById(R.id.longitude)
        altitude = findViewById(R.id.altitude)
        alamat = findViewById(R.id.alamat)
        akurasi = findViewById(R.id.akurasi)
        btnFind = findViewById(R.id.btn_find)
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)
        geocoder = Geocoder(this, Locale.getDefault())

        btnFind?.setOnClickListener {
            getLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Izin lokasi tidak diaktifkan!", Toast.LENGTH_SHORT).show()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getLocation()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // get Permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 10)
            }
        } else {
            // get Location
            locationProviderClient!!.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    latitude!!.text = location.latitude.toString()
                    longitude!!.text = location.longitude.toString()
                    altitude!!.text = location.altitude.toString()
                    akurasi!!.text = location.accuracy.toString() + "%"
                    try {
                        addresses = geocoder!!.getFromLocation(location.latitude, location.longitude, 1)
                        alamat!!.text = addresses?.get(0)?.getAddressLine(0)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(applicationContext, "Lokasi tidak aktif!", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e: Exception ->
                Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
