package com.miguelrodriguez.rocaapp20.mecanicas

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.miguelrodriguez.rocaapp20.Recycler.ClaseEstratos
import com.miguelrodriguez.rocaapp20.Recycler.EstratosAdapter
import com.miguelrodriguez.rocaapp20.Recycler.Imagenes.ClaseImagenes
import com.miguelrodriguez.rocaapp20.Recycler.Imagenes.ImageAdapter
import java.io.File
import java.util.Calendar

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.Switch
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputLayout
import com.miguelrodriguez.rocaapp20.MainActivity
import com.miguelrodriguez.rocaapp20.R
import com.miguelrodriguez.rocaapp20.R.id.switchHayNAF
import com.miguelrodriguez.rocaapp20.ReportesCompactaciones
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistroMecanica : AppCompatActivity() {
    private lateinit var dataReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

    private val CAMERA_PERMISSION_REQUEST_CODE = 1002
    private val REQUEST_IMAGE_CAPTURE = 2

    private var debeMostrarDialogoEstratos = true

    private var isMantenimientoImageSelection = false
    private val newImagesMantenimientoList = mutableListOf<String>() // Lista solo para imágenes nuevas
    private val newImagesList = mutableListOf<String>() // Lista solo para imágenes nuevas

    private lateinit var spnMuestreo: Spinner
//    private lateinit var spnEstudioMuestreo: Spinner
    private lateinit var etObraMuestreoMecanica: EditText
    private lateinit var etClienteMuestreoMecanica: EditText
    private lateinit var etLocalizacionMuestreoMecanica: EditText
    private lateinit var etAtencionMuestreoMecanica: EditText
    private lateinit var etFechaMuestreoMecanica: EditText
    private lateinit var etSondeoNumMuestreoMecanica: EditText
    private lateinit var etUbicacionMuestreoMecanica: EditText
    private lateinit var switchHayNAF: SwitchMaterial
    private lateinit var etProfundidadMuestreoMecanica: EditText
    private lateinit var etProfundidadNAFMuestreoMecanica: EditText
    private lateinit var etProfundidadNAFMuestreoMecanicaLabel: TextInputLayout
    private lateinit var etHoraMuestreoMecanica: EditText

    private lateinit var etEstacionMuestreoMecanica: EditText
    private lateinit var fbNuevoEstrato: FloatingActionButton
    private lateinit var btnGuardarRegistroMuestreoMecanica: Button
    private lateinit var btnCancelarRegistroMuestreoMecanica: Button
    private lateinit var tvLatitud: TextView
    private lateinit var tvLongitud: TextView
    private lateinit var binding: TextInputLayout

    private lateinit var llave: String
    private lateinit var tvNumeroReporteMuestreoMecanica: TextView
    private lateinit var reporteSelecionadoMuestroMaterial: ClaseObraMecanica
    private lateinit var personal: String
    private lateinit var reporteSelecionado: ClaseObraMecanica
    private var editar: Boolean = false
    private var siNo: Boolean = false

    private lateinit var rvMuestreoEstratos: RecyclerView
    private lateinit var EstratosAdapter: EstratosAdapter
    private lateinit var listaEstratosAdapter: MutableList<ClaseEstratos>
    private var listaEstratosmutableListOf: MutableList<ClaseEstratos> = mutableListOf()

    //    private var listaImagenesmutableListOf: MutableList<String> = mutableListOf()
    private var listaEstratosOriginal: MutableList<ClaseEstratos> = mutableListOf()
    private lateinit var estratoNuevo: ClaseEstratos

    private val imageList =
        mutableListOf<String>() // Lista para almacenar las rutas de las imágenes
    private lateinit var rvImagenesMecanica: RecyclerView
    private lateinit var imageAdapter: ImageAdapter

    private lateinit var storageReference: StorageReference
    private val imagePaths = mutableListOf<String>()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_muestreo_material)


        // Inicializar RecyclerView y Adapter
        rvImagenesMecanica = findViewById(R.id.rvImagenesMecanica)
        imageAdapter = ImageAdapter(imageList,
            onImageDelete = { position -> onImageDelete(position) })
        rvImagenesMecanica.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rvImagenesMecanica.adapter = imageAdapter



        storageReference = FirebaseStorage.getInstance().reference

        val btnSelectImages: Button = findViewById(R.id.btnSelectImages)
        btnSelectImages.setOnClickListener {

            isMantenimientoImageSelection = true
            openImageChooser()
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val btnGuardarUbicacion: Button = findViewById(R.id.btnGuardadUbicacion)
        btnGuardarUbicacion.setOnClickListener {
            if (checkLocationPermission()) {
                obtenerUbicacionActual()
            } else {
                solicitarPermisoUbicacion()
            }
        }


        InitComponent()
        InitUI()


    }

    private fun checkLocationPermission(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )

        return fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun solicitarPermisoUbicacion() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun obtenerUbicacionActual() {
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        // Aquí puedes utilizar la ubicación actual (location)
                        var latitud = it.latitude
                        var longitud = it.longitude

                        tvLatitud.setText(latitud.toString())
                        tvLongitud.setText(longitud.toString())

//                        this.latitud=latitud.toString()
//                        this.longitud=longitud.toString()

                        // Guardar la ubicación en Firebase Realtime Database

//                        guardarUbicacionEnFirebase(latitud, longitud)

//                        // Crear una URI para la ubicación
//                        val uri = "geo:$latitud,$longitud?q=$latitud,$longitud"
//                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
//
//                        // Verificar si hay aplicaciones que pueden manejar la intención
//                        if (intent.resolveActivity(packageManager) != null) {
//                            startActivity(intent)
//                        } else {
//                            Toast.makeText(
//                                this,
//                                "No se encontró ninguna aplicación para manejar la ubicación",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
                    } ?: run {
                        Toast.makeText(
                            this,
                            "No se pudo obtener la ubicación actual",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            solicitarPermisoUbicacion()
        }
    }

    private fun guardarUbicacionEnFirebase(latitud: Double, longitud: Double) {
        // Obtiene la referencia al nodo "ubicaciones" (puedes cambiar el nombre según tus necesidades)
        val ubicacionesRef = FirebaseDatabase.getInstance().getReference("ReportesMecanicas")

        // Obtiene el ID del usuario actual (puedes personalizar según tu aplicación)
//        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Crea un nuevo nodo bajo "ubicaciones" usando el ID del usuario
        val usuarioUbicacionRef = ubicacionesRef.child(personal).child(llave).child("ubicaciones")

        // Guarda las coordenadas de la ubicación en el nodo del usuario
        usuarioUbicacionRef.setValue(LocationData(latitud, longitud))
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Ubicación guardada en Firebase",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Error al guardar la ubicación en Firebase",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // Clase para representar los datos de ubicación
    data class LocationData(var latitud: Double, var longitud: Double)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                obtenerUbicacionActual()
            } else {
                Toast.makeText(
                    this,
                    "Permiso de ubicación denegado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun onImageDelete(position: Int) {
        // Crea un objeto AlertDialog66
        val builder = AlertDialog.Builder(this)

        // Configura el título y el mensaje del cuadro de diálogo
        builder.setTitle("Confirmación")
        builder.setMessage("¿Deseas eliminar esta imagen?")

        // Configura el botón positivo (sí)
        builder.setPositiveButton("Sí") { dialog, which ->


            imageList.removeAt(position)

            updateTask()


        }

        // Configura el botón negativo (no)
        builder.setNegativeButton("No") { dialog, which ->
            return@setNegativeButton
            // Código a ejecutar si el usuario hace clic en No
        }

        // Muestra el cuadro de diálogo
        builder.show()

    }

    private fun onImageDeleteActualizando(
        imageRef: DatabaseReference,
        position: Int,
        storageRef: StorageReference
    ) {
        // Crea un objeto AlertDialog66
        val builder = AlertDialog.Builder(this)


        // Configura el título y el mensaje del cuadro de diálogo
        builder.setTitle("Confirmación")
        builder.setMessage("¿Deseas eliminar esta imagen de la base de datos?")

        // Configura el botón positivo (sí)
        builder.setPositiveButton("Sí") { dialog, which ->

//            siNo = true
            imageRef.removeValue()
            storageRef.delete()
            imageList.removeAt(position)
            imageAdapter.notifyDataSetChanged()

            recreate()


        }

        // Configura el botón negativo (no)
        builder.setNegativeButton("No") { dialog, which ->
//            siNo = false
            return@setNegativeButton

        }

        // Muestra el cuadro de diálogo
        builder.show()

    }

    private fun openImageChooser() {
        val options = arrayOf("Tomar foto", "Seleccionar de la galería")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Elige una opción")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> checkCameraPermission() // Verifica el permiso antes de abrir la cámara
                1 -> selectImagesFromGallery()
            }
        }
        builder.show()
    }
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            openCamera() // Abre la cámara si el permiso ya ha sido otorgado
        }
    }
    private fun selectImagesFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, PICK_IMAGES_REQUEST)
    }

    private var photoUri: Uri? = null

    private fun openCamera() {
        if (imageList.size >= 3) {
            Toast.makeText(this, "Solo puedes cargar hasta 3 imágenes.", Toast.LENGTH_SHORT).show()
            return
        }
        val photoFile = createImageFile() // Crea el archivo temporal
        photoFile?.let {
            photoUri = FileProvider.getUriForFile(
                this,
                "com.miguelrodriguez.rocaapp20.fileprovider",
                it
            )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }
    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", "jpg", storageDir)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGES_REQUEST) {
                val targetList = if (isMantenimientoImageSelection) imageList else imageList
                val targetAdapter = if (isMantenimientoImageSelection) imageAdapter else imageAdapter

                if (targetList.size >= 3) {
                    Toast.makeText(this, "Solo puedes cargar hasta 3 imágenes.", Toast.LENGTH_SHORT).show()
                    return
                }

                if (data?.clipData != null) {
                    val clipData = data.clipData
                    val imagesToAdd = clipData!!.itemCount.coerceAtMost(3 - targetList.size)
                    for (i in 0 until imagesToAdd) {
                        val imageUri = clipData.getItemAt(i).uri
                        targetList.add(imageUri.toString())
                        if (isMantenimientoImageSelection){newImagesMantenimientoList.add(imageUri.toString())}else{newImagesList.add(imageUri.toString())}

//                        newImagesList.add(imageUri.toString())
                    }
                } else if (data?.data != null) {
                    if (targetList.size < 3) {
                        val imageUri = data.data
                        targetList.add(imageUri.toString())
                        if (isMantenimientoImageSelection){newImagesMantenimientoList.add(imageUri.toString())}else{newImagesList.add(imageUri.toString())}
//                        newImagesList.add(imageUri.toString())
                        targetAdapter.notifyDataSetChanged()

                    } else {
                        Toast.makeText(this, "Solo puedes cargar hasta 3 imágenes.", Toast.LENGTH_SHORT).show()
                    }
                }
                targetAdapter.notifyDataSetChanged()

            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                val targetList = if (isMantenimientoImageSelection) imageList else imageList
                val targetAdapter = if (isMantenimientoImageSelection) imageAdapter else imageAdapter

                if (targetList.size < 3) {
                    photoUri?.let {
                        targetList.add(it.toString())
                        if (isMantenimientoImageSelection){newImagesMantenimientoList.add(it.toString())}else{newImagesList.add(it.toString())}

//                        newImagesList.add(it.toString())
                        targetAdapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(this, "Solo puedes cargar hasta 3 imágenes.", Toast.LENGTH_SHORT).show()
                }
            }

            isMantenimientoImageSelection = false // Restablecer la bandera después de procesar las imágenes
        }
    }




    companion object {
        const val PICK_IMAGES_REQUEST = 1
    }


    private fun InitComponent() {
        binding=findViewById(R.id.tilFechaMuestreoMecanica)

        listaEstratosOriginal.clear()
        reporteSelecionado = ReportesMuestreoMaterial.reporteSelecionadoMuestroMaterial
        editar = ReportesMuestreoMaterial.editarMuestreoMaterial

//        spnMuestreo = findViewById(R.id.spnMuestreo)
//        spnEstudioMuestreo = findViewById(R.id.spnEstudioMuestreo)
        etFechaMuestreoMecanica = findViewById(R.id.etFechaMuestreoMecanica)
        etObraMuestreoMecanica = findViewById(R.id.etObraMuestreoMecanica)
        etClienteMuestreoMecanica = findViewById(R.id.etClienteMuestreoMecanica)
        etLocalizacionMuestreoMecanica = findViewById(R.id.etLocalizacionMuestreoMecanica)
        etAtencionMuestreoMecanica = findViewById(R.id.etAtencionMuestreoMecanica)
//        etFechaMuestreoMecanica = findViewById(R.id.etFechaMuestreoMecanica)
        etSondeoNumMuestreoMecanica = findViewById(R.id.etSondeoNumMuestreoMecanica)
        etUbicacionMuestreoMecanica = findViewById(R.id.etUbicacionMuestreoMecanica)


        switchHayNAF = findViewById(R.id.switchHayNAF)

        etProfundidadMuestreoMecanica = findViewById(R.id.etProfundidadMuestreoMecanica)
        etProfundidadNAFMuestreoMecanica = findViewById(R.id.etProfundidadNAFMuestreoMecanica)
        etProfundidadNAFMuestreoMecanicaLabel = findViewById(R.id.etProfundidadNAFMuestreoMecanicaLabel)

        etProfundidadNAFMuestreoMecanicaLabel.visibility = View.GONE

        etHoraMuestreoMecanica = findViewById(R.id.etHoraMuestreoMecanica)

//        etEstacionMuestreoMecanica = findViewById(R.id.etEstacionMuestreoMecanica)
        rvMuestreoEstratos = findViewById(R.id.rvMuestreoEstratos)
        rvImagenesMecanica = findViewById(R.id.rvImagenesMecanica)
        fbNuevoEstrato = findViewById(R.id.fbNuevoEstrato)
        btnGuardarRegistroMuestreoMecanica = findViewById(R.id.btnGuardarRegistroMuestreoMecanica)
        btnCancelarRegistroMuestreoMecanica = findViewById(R.id.btnCancelarRegistroMuestreoMecanica)
        tvNumeroReporteMuestreoMecanica = findViewById(R.id.tvNumeroReporteMuestreoMecanica)
        tvLatitud = findViewById(R.id.tvLatitud)
        tvLongitud = findViewById(R.id.tvLongitud)

        personal = MainActivity.NombreUsuarioCompanion
        editar = ReportesMuestreoMaterial.editarMuestreoMaterial

        dataReference = FirebaseDatabase.getInstance().reference
        sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        reporteSelecionadoMuestroMaterial =
            ReportesMuestreoMaterial.reporteSelecionadoMuestroMaterial

//        cargarItemsMuestreo()
        etHoraMuestreoMecanica.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hora = calendar.get(Calendar.HOUR_OF_DAY)
            val minuto = calendar.get(Calendar.MINUTE)

            // Crea un TimePickerDialog con la hora actual como predeterminada
            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    // Actualiza el texto del EditText con la hora seleccionada por el usuario
                    val horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute)
                    etHoraMuestreoMecanica.setText(horaSeleccionada)
                },
                hora,
                minuto,
                true
            )

            // Muestra el dialogo de selección de hora
            timePickerDialog.show()
        }
        if (editar == true) {
            cargarObraSeleccionada(reporteSelecionado)

            // Obtener referencia a la imagen en Firebase Storage
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference.child(llave)


            val imageRef = dataReference.child("ImagenesMecanicas").child(personal).child(llave)
//            Toast.makeText(this, storageRef.toString(), Toast.LENGTH_SHORT).show()

// Suponiendo que tienes una lista de rutas de imágenes llamada imagePaths
            imageRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val tempList = mutableListOf<String>()
                    val ListaDeImagenes = mutableListOf<ClaseImagenes>()


                    for (elementoSnapshot in dataSnapshot.children) {
                        val elemento = elementoSnapshot.getValue(String::class.java)
                        println("Elemento: $elemento")

                        elemento?.let {
                            tempList.add(it)
                            ListaDeImagenes.add(ClaseImagenes(it, elementoSnapshot.key.toString()))
                        }
                    }

                    imageList.clear()
                    imageList.addAll(tempList)

                    // Configuramos el adaptador y notificamos los cambios
                    imageAdapter = ImageAdapter(imageList) { position ->

                        if (position < ListaDeImagenes.size) {
                            val nombreArchivo = ListaDeImagenes[position].NombreArchivo
                            onImageDeleteActualizando(
                                imageRef.child(nombreArchivo),
                                position,
                                storageRef.child("$nombreArchivo.jpg")
                            )
                        } else {
                            // Solo eliminar localmente porque esta imagen no fue subida
                            imageList.removeAt(position)
                            imageAdapter.notifyDataSetChanged()
                        }


                    }
                    rvImagenesMecanica.adapter = imageAdapter
                    imageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("Error al obtener la lista de elementos: ${databaseError.message}")
                }
            })

            etProfundidadNAFMuestreoMecanicaLabel.visibility =if (switchHayNAF.isChecked) View.VISIBLE else View.GONE

        }

    }

    private fun cargarObraSeleccionada(reporteSelecionado: ClaseObraMecanica) {

        tvNumeroReporteMuestreoMecanica.setText(reporteSelecionado.id.toString())
        etObraMuestreoMecanica.setText(reporteSelecionado.Obra)
        etClienteMuestreoMecanica.setText(reporteSelecionado.cliente)
        etLocalizacionMuestreoMecanica.setText(reporteSelecionado.localizacion)
        etAtencionMuestreoMecanica.setText(reporteSelecionado.atencion)
        etFechaMuestreoMecanica.setText(reporteSelecionado.fecha)
        etSondeoNumMuestreoMecanica.setText(reporteSelecionado.sondeo_num)
        etUbicacionMuestreoMecanica.setText(reporteSelecionado.ubicacion)
        switchHayNAF.isChecked = reporteSelecionado.naf
        etProfundidadMuestreoMecanica.setText(reporteSelecionado.profundidad_muestreo)
        etProfundidadNAFMuestreoMecanica.setText(reporteSelecionado.profundidad_naf)
        etHoraMuestreoMecanica.setText(reporteSelecionado.hora)



//        etEstacionMuestreoMecanica.setText(reporteSelecionado.estacion)
        tvLatitud.setText(reporteSelecionado.latitud)
        tvLongitud.setText(reporteSelecionado.longitud)
        llave = reporteSelecionado.llave


        listaEstratosmutableListOf = reporteSelecionado.listaEstratos

        EstratosAdapter =
            EstratosAdapter(reporteSelecionado.listaEstratos,
                onEstratoSelected = { position -> onEstratoSelected(position) },
                onItemDelete = { position -> onItemDelete(position) })
        rvMuestreoEstratos.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rvMuestreoEstratos.adapter = EstratosAdapter

    }

    private fun InitUI() {
        if (!editar) {FechaDeHoy()}

        binding.setEndIconOnClickListener {
            mostrarCalendarioMuestreoMecanica(findViewById(R.id.tilFechaMuestreoMecanica))

        }
        btnCancelarRegistroMuestreoMecanica.setOnClickListener {

            restaurarDatosOriginales()
            ReportesCompactaciones.editar = false


            onBackPressed()

        }
//        spnMuestreo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//
//                cargarItemsEstudioMuestreo(spnMuestreo.selectedItem.toString())
//
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                // No se utiliza en este ejemplo
//            }
//        }
        btnGuardarRegistroMuestreoMecanica.setOnClickListener {
            mostrarDialogo()
        }

        fbNuevoEstrato.setOnClickListener { showDialog() }

        EstratosAdapter = EstratosAdapter(listaEstratosmutableListOf,
            onEstratoSelected = { position -> onEstratoSelected(position) },
            onItemDelete = { position -> onItemDelete(position) })
        rvMuestreoEstratos.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rvMuestreoEstratos.adapter = EstratosAdapter

        llave = dataReference.push().key.toString()

        listaEstratosOriginal.addAll(listaEstratosmutableListOf)

        listaEstratosOriginal.forEachIndexed { index, elemento ->
            // Puedes realizar alguna lógica para determinar la nueva numeración
            val nuevaNumeracion = index  // Sumar 1 para empezar desde 1, si es necesario

            // Reemplazar la numeración en cada objeto
            elemento.idEstrato = nuevaNumeracion
        }


        switchHayNAF.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                etProfundidadNAFMuestreoMecanicaLabel.visibility = View.VISIBLE
            } else {
//                etProfundidadNAFMuestreoMecanica.setText("") // Limpia el campo si el usuario apaga el switch
                etProfundidadNAFMuestreoMecanicaLabel.visibility = View.GONE
            }
        }

    }

    private fun restaurarDatosOriginales() {
        listaEstratosmutableListOf.clear()
        listaEstratosmutableListOf.addAll(listaEstratosOriginal)
        updateTask()
    }

    override fun onBackPressed() {
        // Aquí puedes realizar acciones específicas cuando se presiona el botón de retroceso
        // Por ejemplo, puedes mostrar un cuadro de diálogo de confirmación o realizar alguna operación antes de cerrar la actividad
        // Puedes agregar tu lógica aquí o llamar al método super.onBackPressed() para cerrar la actividad sin ninguna acción adicional.
        if (editar == true) {
            restaurarDatosOriginales()
        }
        ReportesMuestreoMaterial.editarMuestreoMaterial = false
        super.onBackPressed()
    }

    private fun mostrarDialogo() {
        // Crea un objeto AlertDialog66
        val builder = AlertDialog.Builder(this)

        // Configura el título y el mensaje del cuadro de diálogo
        builder.setTitle("Confirmación")
        builder.setMessage("¿Deseas guardar este reporte?")

        // Configura el botón positivo (sí)
        builder.setPositiveButton("Sí") { dialog, which ->

            try {

                val obra: String = etObraMuestreoMecanica.text.toString()
                val cliente: String = etClienteMuestreoMecanica.text.toString()
                val localizacion: String = etLocalizacionMuestreoMecanica.text.toString()
                val atencion: String = etAtencionMuestreoMecanica.text.toString()
                val fecha: String = etFechaMuestreoMecanica.text.toString()

                val numeroReporte: Int = tvNumeroReporteMuestreoMecanica.text.toString().toInt()
                val sondeo_num: String = etSondeoNumMuestreoMecanica.text.toString()
                val ubicacion: String = etUbicacionMuestreoMecanica.text.toString()
                val naf: Boolean = switchHayNAF.isChecked
                val profundidad_muestreo: String = etProfundidadMuestreoMecanica.text.toString()
                var profundidad_naf: String = etProfundidadNAFMuestreoMecanica.text.toString()
                if (naf==false){

                    profundidad_naf="---"
                }
                val hora: String = etHoraMuestreoMecanica.text.toString()
//                val estacion: String = etEstacionMuestreoMecanica.text.toString()
//                val tipoMuestreo: String = spnMuestreo.selectedItem.toString()
//                val estudioMuestreo: String = spnEstudioMuestreo.selectedItem.toString()
                var latitud: String = tvLatitud.text.toString()
                var longitud: String = tvLongitud.text.toString()
                var llave = reporteSelecionadoMuestroMaterial.llave


                // Agregar un nuevo registro localmente
                saveLocally(
                    obra,
                    cliente,
                    localizacion,
                    atencion,
                    personal,
                    numeroReporte,
                    fecha,
                    sondeo_num,
                    ubicacion,
                    naf,
                    profundidad_muestreo,
                    profundidad_naf,
                    hora,
                    llave,
//                    tipoMuestreo,
                    latitud,longitud,
                    listaEstratosmutableListOf,
                    imageList
                )


                subirImagenesAFirebaseStorage(editar)
                // Subir imágenes a Firebase Storage
                // Sincronizar los datos cuando hay conexión a Internet
                syncDataWithFirebase(numeroReporte, listaEstratosmutableListOf, editar)

                listaEstratosmutableListOf.clear()
//                this.llave = dataReference.push().key.toString()
                updateTask()
//                guardarUbicacionEnFirebase(latitud, longitud)

//                imageList.clear()
                tvLatitud.setText(null)
                tvLongitud.setText(null)



                Toast.makeText(this, "Reporte guardado correctamente.", Toast.LENGTH_LONG).show()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "llenar correctamente los campos", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

        }


        // Configura el botón negativo (no)
        builder.setNegativeButton("No") { dialog, which ->
            return@setNegativeButton
            // Código a ejecutar si el usuario hace clic en No
        }

        // Muestra el cuadro de diálogo
        builder.show()
    }

    private fun getLocalRecords(): MutableList<Registro> {
        val registrosJson = sharedPreferences.getString("registros", "[]")
        return Gson().fromJson(registrosJson, object : TypeToken<MutableList<Registro>>() {}.type)
            ?: mutableListOf()
    }

   private fun syncDataWithFirebase(
        numeroReporte: Int,
        listaEstratos: List<ClaseEstratos>,
        accion: Boolean
    ) {
       val registrosLocales = getLocalRecords()
       val registrosSubidosConExito = mutableListOf<Registro>()

       for (registro in registrosLocales) {
           val reportesReferencia = dataReference.child("Reportes").child(personal)

           reportesReferencia.addListenerForSingleValueEvent(object : ValueEventListener {
               override fun onDataChange(snapshot: DataSnapshot) {

                   val destino = if (accion) reporteSelecionadoMuestroMaterial.llave else llave
                   val refPrincipal = dataReference.child("Mecanicas").child("ReportesMecanicas").child(personal).child(destino)
                   val refRespaldo  = dataReference.child("Mecanicas").child("RespaldoMecanicas").child(personal).child(destino)

                   registro.llave = destino // Actualiza llave si es nuevo

                   refPrincipal.setValue(registro)
                   refRespaldo.setValue(registro)

                   // ✅ Añadir a la lista de subidos con éxito
                   registrosSubidosConExito.add(registro)

                   // ✅ Si ya es el último, depura
                   if (registrosSubidosConExito.size == registrosLocales.size) {
                       saveLocalRecords(emptyList())
                       Log.d("Sync", "Registros locales eliminados tras sincronización exitosa.")
                   }

                   if (accion) onBackPressed()
               }

               override fun onCancelled(error: DatabaseError) {
                   Toast.makeText(applicationContext, "Error al sincronizar con Firebase", Toast.LENGTH_SHORT).show()
               }
           })
       }

        // Limpiar registros locales después de la sincronización
        saveLocalRecords(registrosLocales)

    }

    private fun saveLocalRecords(registros: List<Registro>) {

        val registrosJson = Gson().toJson(registros)
        sharedPreferences.edit().putString("registros", registrosJson).apply()

    }

    private fun subirImagenesAFirebaseStorage(accion: Boolean) {

        val registrosLocales = getLocalRecords()

        if (accion) {
            // Código para el caso de acción verdadera
            llave = reporteSelecionadoMuestroMaterial.llave
            val downloadUrls =
                mutableListOf<ClaseImagenes>() // Lista para almacenar las URLs de descarga

//            dataReference.child("ImagenesMecanicas").child(personal).child(llave).removeValue()

            val listaRef = dataReference.child("ImagenesMecanicas").child(personal).child(llave)

            listaRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Itera sobre los hijos y agrega los valores reales al ArrayList
                    for (childSnapshot in dataSnapshot.children) {
                        val value = childSnapshot.getValue(String::class.java)
                        value?.let {
                            downloadUrls.add(ClaseImagenes(value, childSnapshot.key.toString()))
                        }
                    }

                    // Ahora 'downloadUrls' contiene los valores de la lista
                    println("Número de elementos en la lista: ${downloadUrls.size}")
                    // Aquí puedes realizar cualquier otra operación con la lista
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("Error al obtener el conteo de elementos: ${databaseError.message}")
                }
            })



            for ((index, imageUri) in imageList.withIndex()) {
                if (imageUri.startsWith("http")) {
                    // Esta imagen ya está subida, la puedes omitir o registrar directamente
                    continue
                }

                val fileName = obtenerNombreArchivoDesdeRuta(imageUri)

                val imageFileName = "$fileName.jpg"
                val imageRef = storageReference.child("$llave/$imageFileName")

//                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(imageUri))
//                val stream = ByteArrayOutputStream()
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream) // 60 = calidad (ajustable)

//                val byteArray = stream.toByteArray()

                val uri = Uri.parse(imageUri)
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                

// Redimensionar respetando la relación de aspecto, max 1024 px por lado
                val maxDim = 1024
                val ratio = bitmap.width.toFloat() / bitmap.height
                val (newWidth, newHeight) = if (bitmap.width > bitmap.height) {
                    Pair(maxDim, (maxDim / ratio).toInt())
                } else {
                    Pair((maxDim * ratio).toInt(), maxDim)
                }

                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)

// Comprimir a JPEG (calidad ajustable: 0-100)
                val stream = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream) // 60% calidad
                val byteArray = stream.toByteArray()

// Subir como bytes comprimidos a Firebase Storage
                val uploadTask: UploadTask = imageRef.putBytes(byteArray)



                uploadTask.addOnSuccessListener { taskSnapshot ->
                    // Imagen subida exitosamente
                    // Puedes obtener la URL de la imagen con taskSnapshot.storage.downloadUrl
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        // Aquí obtienes la URL de descarga
                        val downloadUrl = uri.toString()

                        // Agregar la URL a la lista
                        downloadUrls.add(ClaseImagenes(downloadUrl, fileName))

                        // Si has subido todas las imágenes, puedes hacer algo con la lista de URLs
                        if (downloadUrls.size == imageList.size) {
                            // Aquí puedes trabajar con la lista completa de URLs
                            // Por ejemplo, subir la lista a otra ubicación en Firebase Database
                            subirUrlsAFirebaseDatabase(downloadUrls)
                        }
                    }
//                    Toast.makeText(this,downloadUrls[0].NombreArchivo , Toast.LENGTH_SHORT).show()

                }.addOnFailureListener {
                    // Manejar el fallo de la subida
//                    Toast.makeText(
//                        this,
//                        "Error al subir la imagen en editar $index",
//                        Toast.LENGTH_SHORT
//                    ).show()
                }
            }

        } else {
            // Código para el caso de acción falsa

            val downloadUrls =
                mutableListOf<ClaseImagenes>() // Lista para almacenar las URLs de descarga

            for ((index, imageUri) in imageList.withIndex()) {
                val fileName = obtenerNombreArchivoDesdeRuta(imageUri)
                val imageFileName = "$fileName.jpg"
//                val imageRef = storageReference.child("$llave/$imageFileName")
                val imageRef = storageReference.child(llave).child("$imageFileName")
                println(imageRef)

                val uri = Uri.parse(imageUri)
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

// Redimensionar respetando la relación de aspecto, max 1024 px por lado
                val maxDim = 1024
                val ratio = bitmap.width.toFloat() / bitmap.height
                val (newWidth, newHeight) = if (bitmap.width > bitmap.height) {
                    Pair(maxDim, (maxDim / ratio).toInt())
                } else {
                    Pair((maxDim * ratio).toInt(), maxDim)
                }

                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)

// Comprimir a JPEG (calidad ajustable: 0-100)
                val stream = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream) // 60% calidad
                val byteArray = stream.toByteArray()

// Subir como bytes comprimidos a Firebase Storage
                val uploadTask: UploadTask = imageRef.putBytes(byteArray)


                uploadTask.addOnSuccessListener { taskSnapshot ->
                    // Imagen subida exitosamente
                    // Puedes obtener la URL de la imagen con taskSnapshot.storage.downloadUrl
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        // Aquí obtienes la URL de descarga
                        val downloadUrl = uri.toString()

                        val nombreImagen = File(uri.toString())
                        // Agregar la URL a la lista
                        downloadUrls.add(ClaseImagenes(downloadUrl, fileName))

                        // Si has subido todas las imágenes, puedes hacer algo con la lista de URLs
                        if (downloadUrls.size == imageList.size) {
                            // Aquí puedes trabajar con la lista completa de URLs
                            // Por ejemplo, subir la lista a otra ubicación en Firebase Database
//                            Toast.makeText(this, imageFileName, Toast.LENGTH_SHORT).show()

                            subirUrlsAFirebaseDatabase(downloadUrls)

                            imageList.clear()
                            imageAdapter.notifyDataSetChanged()
                        }
                    }
                }.addOnFailureListener {
                    // Manejar el fallo de la subida
//                    Toast.makeText(this, "Error al subir la imagen $index", Toast.LENGTH_SHORT)
//                        .show()
                }
            }
        }


    }

    private fun obtenerNombreArchivoDesdeRuta(rutaCompleta: String): String {
        val file = File(rutaCompleta)
        return file.name
    }

    // Función para subir la lista de URLs a Firebase Database (puedes adaptarla según tus necesidades)
    private fun subirUrlsAFirebaseDatabase(downloadUrls: List<ClaseImagenes>) {
        // Subir la lista de URLs a Firebase Database
        // Puedes adaptar esto según tu estructura de datos y lógica de la base de datos
        val database = FirebaseDatabase.getInstance()
        val databaseReference =
            database.reference.child("ImagenesMecanicas").child(personal).child(llave)

        // Limpiar la base de datos antes de agregar nuevas URLs (si es necesario)
        databaseReference.removeValue()

        // Agregar las URLs a la base de datos
        for ((index, url) in downloadUrls.withIndex()) {
            databaseReference.child(url.NombreArchivo).setValue(url.ImagenUrl)

        }
    }

    private fun saveLocally(
        obra: String,
        cliente: String,
        localizacion: String,
        atencion: String,
        personal: String,
        numeroReporte: Int,
        fecha: String,
        sondeo_num: String,
        ubicacion: String,
        naf: Boolean,
        profundidad_muestreo: String,
        profundidad_naf: String,
        hora: String,

        llave: String,
//        tipo_muestreo: String,
        latitud: String,
        longitud: String,
        listaEstratos: MutableList<ClaseEstratos>,
        listaImagenes: MutableList<String>


    ) {
        // Obtener una lista existente de registros locales o crear una nueva
        val registrosLocales = getLocalRecords()

        // Agregar el nuevo registro a la lista
        val nuevoRegistro = Registro(
            obra,
            cliente,
            localizacion,
            atencion,
            personal,
            numeroReporte,
            fecha,
            sondeo_num,
            ubicacion,
            naf,
            profundidad_muestreo,
            profundidad_naf,
            hora,
            llave,
//            tipo_muestreo,
            latitud,longitud,
            listaEstratos,
            listaImagenes
        )
        registrosLocales.add(nuevoRegistro)

        // Guardar la lista actualizada localmente
        saveLocalRecords(registrosLocales)
    }

    private fun showDialog() {


        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_nuevo_estrato_mecanica)

        val btnGuardarEstrato: Button =
            dialog.findViewById(R.id.btnGuardarEstrato)

//        val spnTipoMuestreoMuestreoMecanica: Spinner =dialog.findViewById(R.id.spnTipoMuestreoMuestreoMecanica)
        val etProfundidadInicioMuestreoMecanica: EditText = dialog.findViewById(R.id.etProfundidadInicioMuestreoMecanica)
        val etProfundidadFinalMuestreoMecanica: EditText = dialog.findViewById(R.id.etProfundidadFinalMuestreoMecanica)
        val etProfunididadMuestreoMuestreoMecanica: EditText = dialog.findViewById(R.id.etProfunididadMuestreoMuestreoMecanica)
        val etClasificacionVisualMuestreoMecanica: EditText = dialog.findViewById(R.id.etClasificacionVisualMuestreoMecanica)
        val etObservacionesMuestreoMecanica: EditText = dialog.findViewById(R.id.etObservacionesMuestreoMecanica)


        val spnTipoMuestreoMuestreoMecanica: AutoCompleteTextView = dialog.findViewById(R.id.actvTipoMuestreoMuestreoMecanica)
        val itemMuestreo = arrayOf("Alterado", "Inalterado", "Visual")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, itemMuestreo)
        spnTipoMuestreoMuestreoMecanica.setAdapter(adapter)
        spnTipoMuestreoMuestreoMecanica.setText("Alterado", false)



        btnGuardarEstrato.setOnClickListener {

            try {
                if (etProfundidadInicioMuestreoMecanica.text == null || etClasificacionVisualMuestreoMecanica.text == null) {return@setOnClickListener}
                val tipo_muestreo = spnTipoMuestreoMuestreoMecanica.text.toString()
                val Profunidad_inicio = etProfundidadInicioMuestreoMecanica.text.toString().toDouble()
                val Profunidad_final = etProfundidadFinalMuestreoMecanica.text.toString().toDouble()
                val Profundidad_muestreo = etProfunididadMuestreoMuestreoMecanica.text.toString().toDouble()
                val Clasificacion_visual = etClasificacionVisualMuestreoMecanica.text.toString()
                val Observaciones = etObservacionesMuestreoMecanica.text.toString()


                estratoNuevo = ClaseEstratos(
                    listaEstratosmutableListOf.count(),
                    tipo_muestreo,
                    Profunidad_inicio,
                    Profunidad_final,
                    Profundidad_muestreo,
                    Clasificacion_visual,
                    Observaciones
                )
                listaEstratosmutableListOf.add(estratoNuevo)

                updateTask()

                dialog.hide()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "llenar correctamente los campos", Toast.LENGTH_SHORT).show()
                mostrarAlertaArchivoNoGuardado("llenar correctamente los campos")
                return@setOnClickListener
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this, "La MVSM debe ser diferente a 0.", Toast.LENGTH_SHORT).show()
                mostrarAlertaArchivoNoGuardado("La MVSM debe ser diferente a 0.")

                return@setOnClickListener
            }

        }

        dialog.show()

    }
    private fun mostrarAlertaArchivoNoGuardado(texto:String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(texto)
        builder.setPositiveButton("Aceptar") { _, _ ->
            // Aquí puedes realizar acciones adicionales al aceptar la alerta, si es necesario
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }


    private fun onEstratoSelected(position: Int) {

        showDialog(listaEstratosmutableListOf[position], position)

    }
    private fun showDialog(estratoSelecionado: ClaseEstratos, indice: Int) {


        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_nuevo_estrato_mecanica)

        val btnGuardarEstrato: Button =
            dialog.findViewById(R.id.btnGuardarEstrato)

//        val spnTipoMuestreoMuestreoMecanica: Spinner =dialog.findViewById(R.id.spnTipoMuestreoMuestreoMecanica)
        val etProfundidadInicioMuestreoMecanica: EditText = dialog.findViewById(R.id.etProfundidadInicioMuestreoMecanica)
        val etProfundidadFinalMuestreoMecanica: EditText = dialog.findViewById(R.id.etProfundidadFinalMuestreoMecanica)
        val etProfunididadMuestreoMuestreoMecanica: EditText = dialog.findViewById(R.id.etProfunididadMuestreoMuestreoMecanica)
        val etClasificacionVisualMuestreoMecanica: EditText = dialog.findViewById(R.id.etClasificacionVisualMuestreoMecanica)
        val etObservacionesMuestreoMecanica: EditText = dialog.findViewById(R.id.etObservacionesMuestreoMecanica)


        val spnTipoMuestreoMuestreoMecanica: AutoCompleteTextView = dialog.findViewById(R.id.actvTipoMuestreoMuestreoMecanica)
        val itemMuestreo = arrayOf("Alterado", "Inalterado", "Visual")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, itemMuestreo)
        spnTipoMuestreoMuestreoMecanica.setAdapter(adapter)




// Aquí sí puedes acceder a estratoSelecionado
        val tipoMuestreo = estratoSelecionado.tipo_muestreo
        val index = itemMuestreo.indexOfFirst { it.equals(tipoMuestreo, ignoreCase = true) }

        if (index >= 0) {
            spnTipoMuestreoMuestreoMecanica.setText(estratoSelecionado.tipo_muestreo, false)
        } else {
            Toast.makeText(this, "Tipo de muestreo no encontrado: $tipoMuestreo", Toast.LENGTH_SHORT).show()
        }




        try {
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "llenar correctamente los campos", Toast.LENGTH_SHORT).show()
            return
        }

//        spnTipoMuestreoMuestreoMecanica.setText(estratoSelecionado.nombre)

//        spnTipoMuestreoMuestreoMecanica.setSelection(estratoSelecionado.tipo_muestreo.toString())
        etProfundidadInicioMuestreoMecanica.setText(estratoSelecionado.profundidad_inicio.toString())
        etProfundidadFinalMuestreoMecanica.setText(estratoSelecionado.profundidad_final.toString())
        etProfunididadMuestreoMuestreoMecanica.setText(estratoSelecionado.profundidad_muestreo.toString())
        etClasificacionVisualMuestreoMecanica.setText(estratoSelecionado.clasificacion_visual)
        etObservacionesMuestreoMecanica.setText(estratoSelecionado.observaciones)

        btnGuardarEstrato.setOnClickListener {

            try {

                val tipo_muestreo = spnTipoMuestreoMuestreoMecanica.text.toString()
                val profundidad_inicio = etProfundidadInicioMuestreoMecanica.text.toString().toDouble()
                val profundidad_final = etProfundidadFinalMuestreoMecanica.text.toString().toDouble()
                val profundidad_muestreo = etProfunididadMuestreoMuestreoMecanica.text.toString().toDouble()
                val clasificacion_visual = etClasificacionVisualMuestreoMecanica.text.toString()
                val observaciones = etObservacionesMuestreoMecanica.text.toString()

//                val espesor = etEspesorEstrato.text.toString().toDouble()

                estratoNuevo = ClaseEstratos(
                    indice,
                    tipo_muestreo,
                    profundidad_inicio,
                    profundidad_final,
                    profundidad_muestreo,
                    clasificacion_visual,
                    observaciones
                )
                listaEstratosmutableListOf.set(indice, estratoNuevo)


                updateTask()

                dialog.hide()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "llenar correctamente los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this, "La MVSM debe ser diferente a 0.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }

        dialog.show()

    }

    private fun onItemDelete(position: Int) {

        // Crea un objeto AlertDialog66
        val builder = AlertDialog.Builder(this)

        // Configura el título y el mensaje del cuadro de diálogo
        builder.setTitle("Confirmación")
        builder.setMessage("¿Deseas eliminar este estrato?")

        // Configura el botón positivo (sí)
        builder.setPositiveButton("Sí") { dialog, which ->

            listaEstratosmutableListOf.removeAt(position)
            listaEstratosmutableListOf.forEachIndexed { index, elemento ->
                // Puedes realizar alguna lógica para determinar la nueva numeración
                val nuevaNumeracion = index  // Sumar 1 para empezar desde 1, si es necesario

                // Reemplazar la numeración en cada objeto
                elemento.idEstrato = nuevaNumeracion
            }
            EstratosAdapter.notifyDataSetChanged()


        }

        // Configura el botón negativo (no)
        builder.setNegativeButton("No") { dialog, which ->
            return@setNegativeButton
            // Código a ejecutar si el usuario hace clic en No
        }

        // Muestra el cuadro de diálogo
        builder.show()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ?: false
    }

    private fun updateTask() {
        EstratosAdapter.notifyDataSetChanged()
        imageAdapter.notifyDataSetChanged()


    }



    fun mostrarCalendarioMuestreoMecanica(view: View) {
        val calendario = Calendar.getInstance()
        val año = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH)
        val dia = calendario.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this, { _, year, month, dayOfMonth ->
                // Formatear la fecha seleccionada con dos dígitos para el día
                val fechaSeleccionada = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                etFechaMuestreoMecanica.setText(fechaSeleccionada)
            }, año, mes, dia
        )

        datePickerDialog.show()
    }

    private fun FechaDeHoy() {
        val calendario = Calendar.getInstance()
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        etFechaMuestreoMecanica.setText(formatoFecha.format(calendario.time))
    }

    data class Registro(
        val Obra:String,
        val cliente:String,
        val localizacion:String,
        val atencion:String,
        val personal:String,
        val numeroReporte:Int,
        val fecha:String,
        val sondeo_num:String,
        val ubicacion:String,
        val naf:Boolean,
        val profundidad_muestreo:String,
        val profundidad_naf:String,
        val hora:String,
        var llave:String,
//        var tipo_muestreo:String,
        var latitud:String,
        var longitud:String,
        val listaEstratos:MutableList<ClaseEstratos>,
        val listaImagenes:MutableList<String>
    )
}
