package com.miguelrodriguez.rocaapp20.mecanicas

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.miguelrodriguez.rocaapp20.R
import java.util.Locale

class MapPickerActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var marker: Marker? = null
    private var selected: LatLng? = null

    private val LOCATION_PERMISSION_CODE = 5010

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_picker)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key))
        }

        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
            .getMapAsync(this)

        val ac = supportFragmentManager
            .findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        ac.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG))
        ac.setCountries("MX") // opcional
        ac.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                place.latLng?.let { moveCameraAndMark(it) }
            }
            override fun onError(status: com.google.android.gms.common.api.Status) {
                Toast.makeText(this@MapPickerActivity, "Error: ${status.statusMessage}", Toast.LENGTH_SHORT).show()
            }
        })

        findViewById<Button>(R.id.btnConfirmarUbicacion).setOnClickListener {
            val p = selected ?: run {
                Toast.makeText(this, "Toca el mapa o busca un lugar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val address = try {
                Geocoder(this, Locale.getDefault()).getFromLocation(p.latitude, p.longitude, 1)
                    ?.firstOrNull()?.getAddressLine(0)
            } catch (_: Exception) { null }

            setResult(RESULT_OK, Intent().apply {
                putExtra("lat", p.latitude)
                putExtra("lng", p.longitude)
                putExtra("address", address ?: "")
            })
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true

        if (hasLocationPermission()) {
            try { map.isMyLocationEnabled = true } catch (_: SecurityException) {}
            centerOnLastLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
            moveToMexico()
        }

        map.setOnMapClickListener { moveCameraAndMark(it) }
        map.setOnMapLongClickListener { moveCameraAndMark(it) }
    }

    private fun moveCameraAndMark(latLng: LatLng) {
        selected = latLng
        marker?.remove()
        marker = map.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("Ubicación seleccionada")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
    }

    private fun hasLocationPermission(): Boolean {
        val f = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val c = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return f == PackageManager.PERMISSION_GRANTED && c == PackageManager.PERMISSION_GRANTED
    }

    private fun centerOnLastLocation() {
        val fused = LocationServices.getFusedLocationProviderClient(this)
        fused.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(loc.latitude, loc.longitude), 16f))
            } else moveToMexico()
        }.addOnFailureListener { moveToMexico() }
    }

    private fun moveToMexico() {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(23.6345, -102.5528), 5f))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (::map.isInitialized) {
                try { map.isMyLocationEnabled = true } catch (_: SecurityException) {}
                centerOnLastLocation()
            }
        }
    }
}
