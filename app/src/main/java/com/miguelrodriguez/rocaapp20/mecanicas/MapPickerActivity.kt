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
        Log.d("MapPickerActivity", "Google Play Services available")

        // Inicializar Places API
        val apiKey = getString(R.string.google_maps_key)
        Log.d("MapPickerActivity", "Initializing Places API with key: ${apiKey.take(10)}...")

        if (!Places.isInitialized()) {
            try {
                Places.initialize(applicationContext, apiKey)
                Log.d("MapPickerActivity", "Places API initialized successfully")
            } catch (e: Exception) {
                Log.e("MapPickerActivity", "Failed to initialize Places API: ${e.message}")
            }
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        if (mapFragment == null) {
            Log.e("MapPickerActivity", "Map fragment not found in layout")
        } else {
            Log.d("MapPickerActivity", "Map fragment found, calling getMapAsync")
            mapFragment.getMapAsync(this)
        }

        val ac = supportFragmentManager
            .findFragmentById(R.id.autocomplete_fragment) as? AutocompleteSupportFragment

        if (ac != null) {
            ac.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG))
            ac.setCountries("MX") // opcional
            ac.setOnPlaceSelectedListener(object : PlaceSelectionListener {
                override fun onPlaceSelected(place: Place) {
                    Log.d("MapPickerActivity", "Place selected: ${place.name}")
                    place.latLng?.let { moveCameraAndMark(it) }
                }
                override fun onError(status: com.google.android.gms.common.api.Status) {
                    Log.e("MapPickerActivity", "Autocomplete error: ${status.statusMessage}")
                    Toast.makeText(this@MapPickerActivity, "Error: ${status.statusMessage}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Log.w("MapPickerActivity", "Autocomplete fragment not found")
        }

        findViewById<Button>(R.id.btnConfirmarUbicacion).setOnClickListener {
            val p = selected ?: run {
                Toast.makeText(this, "Toca el mapa o busca un lugar", Toast.LENGTH_SHORT).show()
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
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        Log.d("MapPickerActivity", "onMapReady called - Map object initialized")

        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
        map.mapType = GoogleMap.MAP_TYPE_NORMAL

        // Mostrar marcador inicial fallback INMEDIATAMENTE para que el usuario vea que algo funciona
        val mexico = LatLng(23.6345, -102.5528)
        marker?.remove()
        marker = map.addMarker(
            MarkerOptions()
                .position(mexico)
                .title("Haz click para cambiar ubicación")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mexico, 5f))
        selected = mexico  // Permitir seleccionar México por defecto
        Toast.makeText(this, "Mapa listo. Haz click para cambiar ubicación o busca un lugar.", Toast.LENGTH_LONG).show()

        // Detectar cuando el mapa terminó de cargar tiles
        map.setOnMapLoadedCallback {
            mapLoaded = true
            Log.d("MapPickerActivity", "Map tiles loaded successfully")
        }

        // Lanzar un timeout que avise si los tiles no se cargan
        Handler(Looper.getMainLooper()).postDelayed({
            if (!mapLoaded) {
                Log.w("MapPickerActivity", "Map tiles did not load within timeout. Check API key / Play Services / network.")

                // Mostrar mensaje mejorado con sugerencias
                val errorMsg = """
                    El mapa no cargó los tiles. Posibles causas:
                    1. API key con restricciones incorrectas
                    2. Facturación no habilitada en Google Cloud
                    3. Sin conexión a internet
                    4. Google Play Services no actualizado
                    
                    Puedes seguir usando el mapa en modo fallback. 
                    Haz click para seleccionar ubicación.
                """.trimIndent()

                Log.e("MapPickerActivity", errorMsg)
            }
        }, MAP_LOAD_TIMEOUT_MS)

        // Habilitar ubicación del usuario solo si tenemos permiso explícitamente
        if (hasLocationPermission()) {
            enableMyLocation()
            centerOnLastLocation()
        } else {
            Log.d("MapPickerActivity", "Location permission not granted, requesting...")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        }

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
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(loc.latitude, loc.longitude), 16f))
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
