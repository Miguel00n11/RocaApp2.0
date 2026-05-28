package com.miguelrodriguez.rocaapp20.mecanicas

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.miguelrodriguez.rocaapp20.R
import java.util.Locale

class MapPickerActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var marker: Marker? = null
    private var selected: LatLng? = null

    private val LOCATION_PERMISSION_CODE = 5010

    // Debug flag para detectar si los tiles se cargaron
    private var mapLoaded = false
    private val MAP_LOAD_TIMEOUT_MS = 6000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_picker)

        Log.d("MapPickerActivity", "onCreate started")

        // Verificar que Google Play Services esté disponible
        val gpsAvailability = com.google.android.gms.common.GoogleApiAvailability.getInstance()
        val resultCode = gpsAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != com.google.android.gms.common.ConnectionResult.SUCCESS) {
            Log.e("MapPickerActivity", "Google Play Services not available. Result code: $resultCode")
            if (gpsAvailability.isUserResolvableError(resultCode)) {
                gpsAvailability.getErrorDialog(this, resultCode, 9000)?.show()
            } else {
                Toast.makeText(this, "Este dispositivo no soporta Google Play Services", Toast.LENGTH_LONG).show()
            }
            return
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        if (mapFragment == null) {
            Log.e("MapPickerActivity", "Map fragment not found in layout")
        } else {
            mapFragment.getMapAsync(this)
        }

        findViewById<Button>(R.id.btnConfirmarUbicacion).setOnClickListener {
            val p = selected ?: run {
                Toast.makeText(this, "Toca el mapa para seleccionar una ubicación", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val address = try {
                Geocoder(this, Locale.getDefault()).getFromLocation(p.latitude, p.longitude, 1)
                    ?.firstOrNull()?.getAddressLine(0)
            } catch (e: Exception) {
                Log.e("MapPickerActivity", "Geocoding failed: ${e.message}")
                null
            }

            setResult(RESULT_OK, Intent().apply {
                putExtra("lat", p.latitude)
                putExtra("lng", p.longitude)
                putExtra("address", address ?: "")
            })
            finish()
        }

        // Configurar el botón para ir a la ubicación guardada
        findViewById<FloatingActionButton>(R.id.fabGoToSaved).setOnClickListener {
            val initialLat = intent.getDoubleExtra("initial_lat", Double.NaN)
            val initialLng = intent.getDoubleExtra("initial_lng", Double.NaN)

            if (!initialLat.isNaN() && !initialLng.isNaN()) {
                val savedPos = LatLng(initialLat, initialLng)
                moveCameraAndMark(savedPos)
                Toast.makeText(this, "Moviendo a ubicación guardada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No hay una ubicación guardada para este reporte", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        Log.d("MapPickerActivity", "onMapReady called")

        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
        map.mapType = GoogleMap.MAP_TYPE_NORMAL

        // Recuperar coordenadas iniciales si existen para el inicio automático
        val initialLat = intent.getDoubleExtra("initial_lat", Double.NaN)
        val initialLng = intent.getDoubleExtra("initial_lng", Double.NaN)

        if (!initialLat.isNaN() && !initialLng.isNaN()) {
            // Escenario A: Usar coordenadas proporcionadas al abrir
            val savedPos = LatLng(initialLat, initialLng)
            moveCameraAndMark(savedPos)
            if (hasLocationPermission()) {
                enableMyLocation()
            }
        } else {
            // Escenario B: Usar ubicación del usuario o fallback
            if (hasLocationPermission()) {
                enableMyLocation()
                centerOnLastLocation()
            } else {
                moveToMexico()
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    LOCATION_PERMISSION_CODE
                )
            }
        }

        // Detectar cuando el mapa terminó de cargar tiles
        map.setOnMapLoadedCallback {
            mapLoaded = true
            Log.d("MapPickerActivity", "Map tiles loaded successfully")
        }

        // Lanzar un timeout que avise si los tiles no se cargan
        Handler(Looper.getMainLooper()).postDelayed({
            if (!mapLoaded) {
                Log.w("MapPickerActivity", "Map tiles did not load within timeout.")
                Toast.makeText(this, "El mapa está tardando en cargar los detalles. Verifica tu conexión.", Toast.LENGTH_SHORT).show()
            }
        }, MAP_LOAD_TIMEOUT_MS)

        map.setOnMapClickListener { moveCameraAndMark(it) }
        map.setOnMapLongClickListener { moveCameraAndMark(it) }
    }

    @SuppressWarnings("MissingPermission")
    private fun enableMyLocation() {
        if (hasLocationPermission()) {
            try {
                map.isMyLocationEnabled = true
                Log.d("MapPickerActivity", "My location enabled successfully")
            } catch (e: SecurityException) {
                Log.e("MapPickerActivity", "SecurityException enabling my location: ${e.message}")
            }
        } else {
            Log.w("MapPickerActivity", "Cannot enable my location - permission not granted")
        }
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

    @SuppressWarnings("MissingPermission")
    private fun centerOnLastLocation() {
        if (!hasLocationPermission()) {
            Log.w("MapPickerActivity", "centerOnLastLocation called without permission")
            moveToMexico()
            return
        }

        val fused = LocationServices.getFusedLocationProviderClient(this)
        fused.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                Log.d("MapPickerActivity", "Got last location: ${loc.latitude}, ${loc.longitude}")
                val userLatLng = LatLng(loc.latitude, loc.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 16f))
            } else {
                Log.d("MapPickerActivity", "Last location is null, moving to Mexico fallback")
                moveToMexico()
            }
        }.addOnFailureListener { e ->
            Log.e("MapPickerActivity", "Failed to get last location: ${e.message}")
            moveToMexico()
        }
    }

    private fun moveToMexico() {
        // Centrar y añadir marcador fallback en México
        val mexico = LatLng(23.6345, -102.5528)
        try {
            marker?.remove()
            marker = map.addMarker(
                MarkerOptions()
                    .position(mexico)
                    .title("México")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            )
        } catch (_: Exception) {}
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mexico, 5f))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MapPickerActivity", "Location permission granted by user")
                if (::map.isInitialized) {
                    enableMyLocation()
                    centerOnLastLocation()
                }
            } else {
                Log.w("MapPickerActivity", "Location permission denied by user")
                Toast.makeText(this, "Permiso de ubicación denegado. Selecciona un punto manualmente.", Toast.LENGTH_LONG).show()
                moveToMexico()
            }
        }
    }
}
