package com.miguelrodriguez.rocaapp20.vigas

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.miguelrodriguez.rocaapp20.MainActivity
import com.miguelrodriguez.rocaapp20.R
import com.miguelrodriguez.rocaapp20.Recycler.ClaseCala
import com.miguelrodriguez.rocaapp20.Recycler.ClaseObra
import com.miguelrodriguez.rocaapp20.Recycler.ClaseObraMecanica
import com.miguelrodriguez.rocaapp20.Recycler.EstratosAdapter
import com.miguelrodriguez.rocaapp20.Recycler.Imagenes.ClaseImagenes
import com.miguelrodriguez.rocaapp20.Recycler.Imagenes.ImageAdapter
import com.miguelrodriguez.rocaapp20.RegistroCompactaciones
import com.miguelrodriguez.rocaapp20.ReportesCompactaciones
import com.miguelrodriguez.rocaapp20.ReportesMuestreoMaterial
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegistroVigas : AppCompatActivity() {
    //    private val usuariosRef: DatabaseReference

    private val databaseInventario: FirebaseDatabase = FirebaseDatabase.getInstance()

    private lateinit var dataReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var personal: String
    private lateinit var llave: String
    private lateinit var binding: TextInputLayout


    private lateinit var spnMolde1: Spinner
    private lateinit var spnMolde2: Spinner
    private lateinit var spnMolde3: Spinner
//    private lateinit var spnMolde4: Spinner
    private lateinit var spnTipoMuestreoVigas: Spinner
    private lateinit var spnTipoConcretoVigas: Spinner

    private lateinit var tvNumeroReportesVigas: TextView
    private lateinit var etObraVigas: EditText
    private lateinit var etClienteVigas: EditText
    private lateinit var etLocalizacionVigas: EditText
    private lateinit var etFechaCompactacion: EditText

    private lateinit var etElementoColadoVigas: EditText
    private lateinit var etUbicacionVigas: EditText
    private lateinit var etFCVigas: EditText
    private lateinit var etVolumenVigas: EditText
    private lateinit var etVolumenMuestraVigas: EditText


    private lateinit var etEdadVigas: EditText
    private lateinit var etTMAVigas: EditText
    private lateinit var etConcreteraVigas: EditText
    private lateinit var etHOPropVigas: EditText
    private lateinit var etAditivoVigas: EditText
    private lateinit var etMuestraVigas: EditText
    private lateinit var etOllaVigas: EditText
    private lateinit var etRemisionVigas: EditText
    private lateinit var etRevenimientoProyVigas: EditText
    private lateinit var etRevenimientoObt1Vigas: EditText
    private lateinit var etRevenimientoObt2Vigas: EditText
    private lateinit var etTemperaturaVigas: EditText

    private lateinit var etMolde1Vigas: EditText
    private lateinit var etMolde2Vigas: EditText
    private lateinit var etMolde3Vigas: EditText
//    private lateinit var etMolde4Vigas: EditText

    private lateinit var etHoraSalidaVigas: EditText
    private lateinit var etHorallegadaVigas: EditText
    private lateinit var etHoraMuestreoVigas: EditText
    private lateinit var etObservacionesVigas: EditText

    private lateinit var etCarretillaVigas: EditText
    private lateinit var etConoVigas: EditText
    private lateinit var etVarillaVigas: EditText
    private lateinit var etMazoVigas: EditText
    private lateinit var etTermometroVigas: EditText
    private lateinit var etCucharonVigas: EditText
    private lateinit var etPlacaVigas: EditText
    private lateinit var etFlexometroVigas: EditText
    private lateinit var etEnrasadorVigas: EditText

    private lateinit var btnGuardarRegistroVigas: Button
    private lateinit var btnCancelarRegistroVigas: Button

    private lateinit var reporteSelecionado: ClaseObraVigas

    private var editar: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_vigas)
        InitComponent()
        InitUI()

    }


    private fun InitComponent() {


        dataReference = FirebaseDatabase.getInstance().reference


        sharedPreferences = getPreferences(Context.MODE_PRIVATE)

        binding = findViewById(R.id.tilFechaMuestreoCompactaciones)


        tvNumeroReportesVigas = findViewById(R.id.tvNumeroReporteVigas)
        personal = MainActivity.NombreUsuarioCompanion
        llave = dataReference.push().key.toString()
        editar = ReportesVigas.editar
        spnMolde1 = findViewById(R.id.spnMolde1)
        spnMolde2 = findViewById(R.id.spnMolde2)
        spnMolde3 = findViewById(R.id.spnMolde3)
//        spnMolde4 = findViewById(R.id.spnMolde4)
        spnTipoMuestreoVigas = findViewById(R.id.spnTipoMuestreoVigas)
        spnTipoConcretoVigas = findViewById(R.id.spnTipoConcretoVigas)
        etFechaCompactacion = findViewById(R.id.etFechaCompactacion)
        etEdadVigas = findViewById(R.id.etEdadVigas)

        etObraVigas = findViewById(R.id.etObraVigas)
        etClienteVigas = findViewById(R.id.etClienteVigas)
        etLocalizacionVigas = findViewById(R.id.etLocalizacionVigas)
        etElementoColadoVigas = findViewById(R.id.etElementoColadoVigas)
        etUbicacionVigas = findViewById(R.id.etUbicacionVigas)
        etFCVigas = findViewById(R.id.etFCVigas)
        etVolumenVigas = findViewById(R.id.etVolumenVigas)
        etVolumenMuestraVigas = findViewById(R.id.etVolumenMuestraVigas)
        etTMAVigas = findViewById(R.id.etTMAVigas)
        etConcreteraVigas = findViewById(R.id.etConcreteraVigas)
        etHOPropVigas = findViewById(R.id.etHOPropVigas)
        etAditivoVigas = findViewById(R.id.etAditivoVigas)
        etRemisionVigas = findViewById(R.id.etRemisionVigas)
        etMuestraVigas = findViewById(R.id.etMuestraVigas)
        etOllaVigas = findViewById(R.id.etOllaVigas)
        etRevenimientoProyVigas = findViewById(R.id.etRevenimientoProyVigas)
        etRevenimientoObt1Vigas = findViewById(R.id.etReveniminetoObt1Vigas)
        etRevenimientoObt2Vigas = findViewById(R.id.etReveniminetoObt2Vigas)
        etTemperaturaVigas = findViewById(R.id.etTemperaturaVigas)
        etMolde1Vigas = findViewById(R.id.etMolde1Vigas)
        etMolde2Vigas = findViewById(R.id.etMolde2Vigas)
        etMolde3Vigas = findViewById(R.id.etMolde3Vigas)
//        etMolde4Vigas = findViewById(R.id.etMolde4Vigas)

        etHoraSalidaVigas = findViewById(R.id.etHoraSalidaVigas)
        etHorallegadaVigas = findViewById(R.id.etHorallegadaVigas)
        etHoraMuestreoVigas = findViewById(R.id.etHoraMuestreoVigas)
        etObservacionesVigas = findViewById(R.id.etObservacionesVigas)

        etCarretillaVigas = findViewById(R.id.etCarretillaVigas)
        etConoVigas = findViewById(R.id.etConoVigas)
        etVarillaVigas = findViewById(R.id.etVarillaVigas)
        etMazoVigas = findViewById(R.id.etMazoVigas)
        etTermometroVigas = findViewById(R.id.etTermometroVigas)
        etCucharonVigas = findViewById(R.id.etCucharonVigas)
        etPlacaVigas = findViewById(R.id.etPlacaVigas)
        etFlexometroVigas = findViewById(R.id.etFlexometroVigas)
        etEnrasadorVigas = findViewById(R.id.etEnrasadorVigas)

        btnGuardarRegistroVigas = findViewById(R.id.btnGuardarRegistroVigas)
        btnCancelarRegistroVigas = findViewById(R.id.btnCancelarRegistroVigas)

        reporteSelecionado = ReportesVigas.reporteSelecionado


        val sharedPreferences1 = getSharedPreferences("carretilla", Context.MODE_PRIVATE)

// Obtener el nombre guardado
        val nombreGuardado = sharedPreferences1.getString("nombre", "")

        etCarretillaVigas.setText(nombreGuardado)


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

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("Error al obtener la lista de elementos: ${databaseError.message}")
                }
            })

        }


        if (!editar) {
            FechaDeHoy()

            cargarItemsEstadosVigas()
            cargarTipoDeMuestreo()
            consultarValores()

        }

    }

    private fun cargarObraSeleccionada(reporteSelecionado: ClaseObraVigas) {

        tvNumeroReportesVigas.setText(reporteSelecionado.id.toString())
        etObraVigas.setText(reporteSelecionado.Obra)
        etClienteVigas.setText(reporteSelecionado.Cliente)
        etLocalizacionVigas.setText(reporteSelecionado.localizacion)
        etFechaCompactacion.setText(reporteSelecionado.fecha)

        etElementoColadoVigas.setText(reporteSelecionado.elementoColado)
        etUbicacionVigas.setText(reporteSelecionado.ubicacion)
        etFCVigas.setText(reporteSelecionado.fc.toString())
        etVolumenVigas.setText(reporteSelecionado.volumenTotal.toString())
        etVolumenMuestraVigas.setText(reporteSelecionado.volumenMuestra.toString())
        etEdadVigas.setText(reporteSelecionado.edad.toString())
        etTMAVigas.setText(reporteSelecionado.tma.toString())
        etConcreteraVigas.setText(reporteSelecionado.concretera)
        etHOPropVigas.setText(reporteSelecionado.proporciones)
        etAditivoVigas.setText(reporteSelecionado.aditivo)
        etRemisionVigas.setText(reporteSelecionado.remision)

        etMuestraVigas.setText(reporteSelecionado.muestra.toString())
        etOllaVigas.setText(reporteSelecionado.olla.toString())
        etRevenimientoProyVigas.setText(reporteSelecionado.revenimientoDis.toString())
        etRevenimientoObt1Vigas.setText(reporteSelecionado.revenimientoR1.toString())
        etRevenimientoObt2Vigas.setText(reporteSelecionado.revenimientoR2.toString())
        etTemperaturaVigas.setText(reporteSelecionado.temperatura.toString())
        etMolde1Vigas.setText(reporteSelecionado.Molde1.toString())
        etMolde2Vigas.setText(reporteSelecionado.Molde2.toString())
        etMolde3Vigas.setText(reporteSelecionado.Molde3.toString())
//        etMolde4Vigas.setText(reporteSelecionado.Molde4.toString())
        etHoraSalidaVigas.setText(reporteSelecionado.horaSalida)
        etHorallegadaVigas.setText(reporteSelecionado.horaLLegada)
        etHoraMuestreoVigas.setText(reporteSelecionado.horaMuestreo)
        etObservacionesVigas.setText(reporteSelecionado.observaciones)

        etCarretillaVigas.setText(reporteSelecionado.carretilla)
        etConoVigas.setText(reporteSelecionado.cono)
        etVarillaVigas.setText(reporteSelecionado.varilla)
        etMazoVigas.setText(reporteSelecionado.mazo)
        etTermometroVigas.setText(reporteSelecionado.termometro)
        etCucharonVigas.setText(reporteSelecionado.cucharon)
        etPlacaVigas.setText(reporteSelecionado.placa)
        etFlexometroVigas.setText(reporteSelecionado.flexometro)
        etEnrasadorVigas.setText(reporteSelecionado.enrasador)

        spnTipoConcretoVigas.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    cargarItemsTipoDeMuestreo(spnTipoConcretoVigas.selectedItem.toString())

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // No se utiliza en este ejemplo
                }
            }

        cargarItemsEstadosVigas()
        cargarTipoDeMuestreo()




        llave = reporteSelecionado.llave


        val tipoMuestreo1 = reporteSelecionado.tipoMuestreo
        val adapter1 = spnTipoMuestreoVigas.adapter

        for (i in 0 until adapter1.count) {
            if (adapter1.getItem(i).toString() == tipoMuestreo1) {
                spnTipoMuestreoVigas.setSelection(i)
                break
            }
        }
        val tipoResistencia = reporteSelecionado.tipoResistencia
        val adapter2 = spnTipoConcretoVigas.adapter

        for (i in 0 until adapter2.count) {
            if (adapter2.getItem(i).toString() == tipoResistencia) {
                spnTipoConcretoVigas.setSelection(i)
                break
            }
        }

        val estadoMolde1 = reporteSelecionado.estadoMolde1
        val adapterestadoMolde1 = spnMolde1.adapter

        for (i in 0 until adapterestadoMolde1.count) {
            if (adapterestadoMolde1.getItem(i).toString() == estadoMolde1) {
                spnMolde1.setSelection(i)
                break
            }
        }
        val estadoMolde2 = reporteSelecionado.estadoMolde2
        val adapterestadoMolde2 = spnMolde2.adapter

        for (i in 0 until adapterestadoMolde2.count) {
            if (adapterestadoMolde2.getItem(i).toString() == estadoMolde2) {
                spnMolde2.setSelection(i)
                break
            }
        }

        val estadoMolde3 = reporteSelecionado.estadoMolde3
        val adapterestadoMolde3 = spnMolde3.adapter

        for (i in 0 until adapterestadoMolde3.count) {
            if (adapterestadoMolde3.getItem(i).toString() == estadoMolde3) {
                spnMolde3.setSelection(i)
                break
            }
        }
//        val estadoMolde4 = reporteSelecionado.estadoMolde4
//        val adapterestadoMolde4 = spnMolde4.adapter
//
//        for (i in 0 until adapterestadoMolde4.count) {
//            if (adapterestadoMolde4.getItem(i).toString() == estadoMolde4) {
//                spnMolde4.setSelection(i)
//                break
//            }
//        }


    }

    private fun InitUI() {
        binding.setEndIconOnClickListener {
            mostrarCalendario(findViewById(R.id.spnMolde1))

        }
        spnMolde1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No se utiliza en este ejemplo
            }
        }
        spnTipoConcretoVigas.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    cargarItemsTipoDeMuestreo(spnTipoConcretoVigas.selectedItem.toString())

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // No se utiliza en este ejemplo
                }
            }

        etHoraSalidaVigas.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hora = calendar.get(Calendar.HOUR_OF_DAY)
            val minuto = calendar.get(Calendar.MINUTE)

            // Crea un TimePickerDialog con la hora actual como predeterminada
            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    // Actualiza el texto del EditText con la hora seleccionada por el usuario
                    val horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute)
                    etHoraSalidaVigas.setText(horaSeleccionada)
                },
                hora,
                minuto,
                true
            )

            // Muestra el dialogo de selección de hora
            timePickerDialog.show()
        }
        etHorallegadaVigas.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hora = calendar.get(Calendar.HOUR_OF_DAY)
            val minuto = calendar.get(Calendar.MINUTE)

            // Crea un TimePickerDialog con la hora actual como predeterminada
            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    // Actualiza el texto del EditText con la hora seleccionada por el usuario
                    val horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute)
                    etHorallegadaVigas.setText(horaSeleccionada)
                },
                hora,
                minuto,
                true
            )

            // Muestra el dialogo de selección de hora
            timePickerDialog.show()
        }
        etHoraMuestreoVigas.setOnClickListener {

            val calendar = Calendar.getInstance()
            val hora = calendar.get(Calendar.HOUR_OF_DAY)
            val minuto = calendar.get(Calendar.MINUTE)

            // Crea un TimePickerDialog con la hora actual como predeterminada
            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    // Actualiza el texto del EditText con la hora seleccionada por el usuario
                    val horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute)
                    etHoraMuestreoVigas.setText(horaSeleccionada)
                },
                hora,
                minuto,
                true
            )

            // Muestra el dialogo de selección de hora
            timePickerDialog.show()
        }
        btnGuardarRegistroVigas.setOnClickListener {
            mostrarDialogo()

        }
        btnCancelarRegistroVigas.setOnClickListener {


            onBackPressed()
        }
    }


    private fun consultarValores() {
        val inventarioRef: DatabaseReference =
            databaseInventario.getReference("personal").child("inventario").child(personal)
        // Agrega un listener para manejar el resultado de la consulta en el nodo "personal"
        inventarioRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                etCarretillaVigas.setText(
                    dataSnapshot.child("carretilla").getValue(Int::class.java).toString()
                )
                etConoVigas.setText(
                    dataSnapshot.child("cono").getValue(Int::class.java).toString()
                )
                etVarillaVigas.setText(
                    dataSnapshot.child("varilla").getValue(Int::class.java).toString()
                )
                etMazoVigas.setText(
                    dataSnapshot.child("mazo").getValue(Int::class.java).toString()
                )
                etTermometroVigas.setText(
                    dataSnapshot.child("termometro").getValue(Int::class.java).toString()
                )
                etCucharonVigas.setText(
                    dataSnapshot.child("cucharon").getValue(Int::class.java).toString()
                )
                etPlacaVigas.setText(
                    dataSnapshot.child("placa").getValue(Int::class.java).toString()
                )
                etFlexometroVigas.setText(
                    dataSnapshot.child("flexometro").getValue(Int::class.java).toString()
                )
                etEnrasadorVigas.setText(
                    dataSnapshot.child("enrasador").getValue(Int::class.java).toString()
                )


            }

            override fun onCancelled(error: DatabaseError) {
                // Maneja errores
                Log.w("TAG", "Error al leer datos en el nodo 'personal'.", error.toException())
            }
        })
    }

    private fun mostrarDialogo() {
        // Crea un objeto AlertDialog66
        val builder = AlertDialog.Builder(this)

        // Configura el título y el mensaje del cuadro de diálogo
        builder.setTitle("Confirmación")
        builder.setMessage("¿Deseas guardar este reporte?")

        // Configura el botón positivo (sí)
        builder.setPositiveButton("Sí") { dialog, which ->
            if (etObraVigas.text.toString()==null||etObraVigas.text.toString()=="")
            {Toast.makeText(this, "Agregar nombre de obra", Toast.LENGTH_SHORT).show()
                return@setPositiveButton}
            try {


                val obra: String = etObraVigas.text.toString()
                val cliente: String = etClienteVigas.text.toString()
                val localizacion: String = etLocalizacionVigas.text.toString()
                val fecha: String = etFechaCompactacion.text.toString()
                val numeroReporte: Int = tvNumeroReportesVigas.text.toString().toInt()
                val tipoMuestreo: String = spnTipoMuestreoVigas.selectedItem.toString()

                val elementoColado: String = etElementoColadoVigas.text.toString()
                val ubicacion: String = etUbicacionVigas.text.toString()
                val fc: Double = etFCVigas.text.toString().toDouble()
                val volumenTotal: Double = etVolumenVigas.text.toString().toDouble()
                val volumenMuestra: Double = etVolumenMuestraVigas.text.toString().toDouble()
                val tipoResistencia: String = spnTipoConcretoVigas.selectedItem.toString()
                val edad: Int = etEdadVigas.text.toString().toInt()
                val tma: Double = etTMAVigas.text.toString().toDouble()
                val concretera = etConcreteraVigas.text.toString()
                val proporciones = etHOPropVigas.text.toString()
                val aditivo = etAditivoVigas.text.toString()
                val remision = etRemisionVigas.text.toString()

                val muestra = etMuestraVigas.text.toString().toInt()
                val olla = etOllaVigas.text.toString()
                val revenimientoDis = etRevenimientoProyVigas.text.toString().toDouble()
                val revenimientoR1 = etRevenimientoObt1Vigas.text.toString().toDouble()
                val revenimientoR2 = etRevenimientoObt2Vigas.text.toString().toDouble()
                val temperatura = etTemperaturaVigas.text.toString().toDouble()
                val molde1 = etMolde1Vigas.text.toString().toInt()
                val molde2 = etMolde2Vigas.text.toString().toInt()
                val molde3 = etMolde3Vigas.text.toString().toInt()
//                val molde4 = etMolde4Vigas.text.toString().toInt()
                val estadoMolde1 = spnMolde1.selectedItem.toString()
                val estadoMolde2 = spnMolde2.selectedItem.toString()
                val estadoMolde3 = spnMolde3.selectedItem.toString()
//                val estadoMolde4 = spnMolde4.selectedItem.toString()
                val horaSalida = etHoraSalidaVigas.text.toString()
                val horaLLegada = etHorallegadaVigas.text.toString()
                val horaMuestreo = etHoraMuestreoVigas.text.toString()
                val observaciones = etObservacionesVigas.text.toString()

                val carretilla = etCarretillaVigas.text.toString()
                val cono = etConoVigas.text.toString()
                val varilla = etVarillaVigas.text.toString()
                val mazo = etMazoVigas.text.toString()
                val termometro = etTermometroVigas.text.toString()
                val cucharon = etCucharonVigas.text.toString()
                val placa = etPlacaVigas.text.toString()
                val flexometro = etFlexometroVigas.text.toString()
                val enrasador = etEnrasadorVigas.text.toString()
                val validado: Boolean = false

                var llave = reporteSelecionado.llave


                // Agregar un nuevo registro localmente
                saveLocally(
                    obra,
                    cliente,
                    localizacion,
                    fecha,
                    personal,
                    numeroReporte,
                    tipoMuestreo,
                    elementoColado,
                    ubicacion,
                    fc,
                    volumenTotal,
                    volumenMuestra,
                    tipoResistencia,
                    edad,
                    tma,
                    concretera,
                    proporciones,
                    aditivo,
                    remision,
                    muestra,
                    olla,
                    revenimientoDis,
                    revenimientoR1,
                    revenimientoR2,
                    temperatura,
                    molde1,
                    molde2,
                    molde3,
//                    molde4,
                    estadoMolde1,
                    estadoMolde2,
                    estadoMolde3,
//                    estadoMolde4,
                    horaSalida,
                    horaLLegada,
                    horaMuestreo,
                    observaciones,
                    carretilla,
                    cono,
                    varilla,
                    mazo,
                    termometro,
                    cucharon,
                    placa,
                    flexometro,
                    enrasador,
                    validado,

                    llave
                )


                // Sincronizar los datos cuando hay conexión a Internet
                syncDataWithFirebase(numeroReporte, editar)

//                ConsultarUltimoRegistro()

                llave = dataReference.push().key.toString()
                Toast.makeText(this, "Reporte guardado correctamente.", Toast.LENGTH_LONG).show()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "ERROR. llenar correctamente los campos", Toast.LENGTH_SHORT)
                    .show()
                mostrarAlertaArchivoNoGuardado()

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

    private fun mostrarAlertaArchivoNoGuardado() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Llenar correctamente los campos.")
        builder.setPositiveButton("Aceptar") { _, _ ->
            // Aquí puedes realizar acciones adicionales al aceptar la alerta, si es necesario
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }


    private fun syncDataWithFirebase(
        numeroReporte: Int,
        accion: Boolean
    ) {
        // Verificar si hay conexión a Internet
        // Puedes usar una biblioteca como Connectivity Manager para esto

        // Obtener registros locales
        val registrosLocales = getLocalRecords()

        // Sincronizar cada registro con Firebase Realtime Database
        for (registro in registrosLocales) {

            // Generar una nueva clave única para cada registro
//            val nuevaClave = dataReference.push().key
//            llave=nuevaClave!!


            val reportesReferencia = dataReference.child("Reportes").child(personal)
            val invetarioReferencia = dataReference.child("Reportes").child(personal)


            reportesReferencia.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

//
//                    val nuevoNumeroReporte = snapshot.childrenCount.toInt()
//                    val totalReportes = tvNumeroReporteCompactacion.text

                    try {


                        if (accion == true) {
                            // Guardar el registro en Firebase Realtime Database
                            dataReference.child("Vigas").child("Reportes").child(personal)
                                .child(reporteSelecionado.llave)
                                .setValue(registro)
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("cono").setValue(etConoVigas.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("carretilla")
                                .setValue(etCarretillaVigas.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("varilla")
                                .setValue(etVarillaVigas.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("mazo").setValue(etMazoVigas.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("termometro")
                                .setValue(etTermometroVigas.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("cucharon")
                                .setValue(etCucharonVigas.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("placa").setValue(etPlacaVigas.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("flexometro")
                                .setValue(etFlexometroVigas.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("enrasador")
                                .setValue(etEnrasadorVigas.text.toString().toInt())
                            onBackPressed()
                        } else {
                            // Guardar el registro en Firebase Realtime Database
                            registro.llave = llave
                            dataReference.child("Vigas").child("Reportes").child(personal)
                                .child(llave)
                                .setValue(registro)
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("cono").setValue(etConoVigas.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("carretilla")
                                .setValue(etCarretillaVigas.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("varilla")
                                .setValue(etVarillaVigas.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("mazo").setValue(etMazoVigas.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("termometro")
                                .setValue(etTermometroVigas.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("cucharon")
                                .setValue(etCucharonVigas.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("placa").setValue(etPlacaVigas.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("flexometro")
                                .setValue(etFlexometroVigas.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("enrasador")
                                .setValue(etEnrasadorVigas.text.toString().toInt())


                        }
                        if (accion == true) {
                            // Guardar el registro en Firebase Realtime Database
                            dataReference.child("Vigas").child("Respaldo").child(personal)
                                .child(reporteSelecionado.llave)
                                .setValue(registro)
                            onBackPressed()
                        } else {
                            // Guardar el registro en Firebase Realtime Database
                            registro.llave = llave
                            dataReference.child("Vigas").child("Respaldo").child(personal)
                                .child(llave)
                                .setValue(registro)
                            // aqui quitamos el pressed back para que no salga del reporte cuando se guarde por primera vez
                            onBackPressed()

                        }
//                        Toast.makeText(this, "Reporte guardado correctamente.", Toast.LENGTH_LONG).show()
                    } catch (e: NumberFormatException) {
//                        Toast.makeText(this, "ERROR. llenar correctamente los campos", Toast.LENGTH_SHORT)
//                            .show()
                        mostrarAlertaArchivoNoGuardado()

                        return
                    }


                }

                override fun onCancelled(error: DatabaseError) {


                }
            })


            // Eliminar el registro local después de la sincronización
            registrosLocales.remove(registro)
        }

        // Limpiar registros locales después de la sincronización
        saveLocalRecords(registrosLocales)
    }

    private fun saveLocally(
        obra: String,
        cliente: String,
        localizacion: String,
        fecha: String,
        personal: String,
        numeroReporte: Int,
        tipoMuestreo: String,

        elementoColado: String,
        ubicacion: String,
        fc: Double,
        volumenTotal: Double,
        volumenMuestra: Double,
        tipoResistencia: String,
        edad: Int,
        tma: Double,
        concretera: String,
        proporciones: String,
        aditivo: String,
        remision: String,

        muestra: Int,
        olla: String,
        revenimientoDis: Double,
        revenimientoR1: Double,
        revenimientoR2: Double,
        temperatura: Double,
        molde1: Int,
        molde2: Int,
        molde3: Int,
//        molde4: Int,
        estadoMolde1: String,
        estadoMolde2: String,
        estadoMolde3: String,
//        estadoMolde4: String,
        horaSalida: String,
        horaLLegada: String,
        horaMuestreo: String,
        observaciones: String,

        carretilla: String,
        cono: String,
        varilla: String,
        mazo: String,
        termometro: String,
        cucharon: String,
        placa: String,
        flexometro: String,
        enrasador: String,
        validado: Boolean,

        llave: String
    ) {
        // Obtener una lista existente de registros locales o crear una nueva
        val registrosLocales = getLocalRecords()

        // Agregar el nuevo registro a la lista
        val nuevoRegistro = Registro(
            obra,
            cliente,
            localizacion,
            fecha,
            personal,
            numeroReporte,
            tipoMuestreo,

            elementoColado,
            ubicacion,
            fc,
            volumenTotal,
            volumenMuestra,
            tipoResistencia,
            edad,
            tma,
            concretera,
            proporciones,
            aditivo,
            remision,

            muestra,
            olla,
            revenimientoDis,
            revenimientoR1,
            revenimientoR2,
            temperatura,
            molde1,
            molde2,
            molde3,
//            molde4,
            estadoMolde1,
            estadoMolde2,
            estadoMolde3,
//            estadoMolde4,
            horaSalida,
            horaLLegada,
            horaMuestreo,
            observaciones,
            carretilla,
            cono,
            varilla,
            mazo,
            termometro,
            cucharon,
            placa,
            flexometro,
            enrasador,
            validado,
            llave
        )
        registrosLocales.add(nuevoRegistro)

        // Guardar la lista actualizada localmente
        saveLocalRecords(registrosLocales)
    }

    data class Registro(
        val obra: String,
        val cliente: String,
        val localizacion: String,
        val fecha: String,
        val personal: String,
        val numeroReporte: Int,
        val tipoMuestreo: String,

        val elementoColado: String,
        val ubicacion: String,
        val fc: Double,
        val volumenTotal: Double,
        val volumenMuestra: Double,
        val tipoResistencia: String,
        val edad: Int,
        val tma: Double,
        val concretera: String,
        val proporciones: String,
        val aditivo: String,
        val remision: String,

        val muestra: Int,
        val olla: String,
        val revenimientoDis: Double,
        val revenimientoR1: Double,
        val revenimientoR2: Double,
        val temperatura: Double,
        val molde1: Int,
        val molde2: Int,
        val molde3: Int,
//        val molde4: Int,
        val estadoMolde1: String,
        val estadoMolde2: String,
        val estadoMolde3: String,
//        val estadoMolde4: String,
        val horaSalida: String,
        val horaLLegada: String,
        val horaMuestreo: String,
        val observaciones: String,

        val carretilla: String,
        val cono: String,
        val varilla: String,
        val mazo: String,
        val termometro: String,
        val cucharon: String,
        val placa: String,
        val flexometro: String,
        val enrasador: String,
        val validado: Boolean,

        var llave: String
    )

    private fun saveLocalRecords(registros: List<RegistroVigas.Registro>) {

        val registrosJson = Gson().toJson(registros)
        sharedPreferences.edit().putString("registros", registrosJson).apply()
    }

    private fun getLocalRecords(): MutableList<RegistroVigas.Registro> {
        val registrosJson = sharedPreferences.getString("registros", "[]")
        return Gson().fromJson(
            registrosJson,
            object : TypeToken<MutableList<RegistroVigas.Registro>>() {}.type
        )
            ?: mutableListOf()
    }

    private fun cargarItemsTipoDeMuestreo(spnTipoConcretoVigas: String) {
        if (editar){
            etEdadVigas.setText(reporteSelecionado.edad.toString())

        }else{
            if (spnTipoConcretoVigas == "Resistencia Normal") {
                etEdadVigas.setText("28")
            } else {
                etEdadVigas.setText("7")
            }
        }

    }

    private fun FechaDeHoy() {
        val calendario = Calendar.getInstance()
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        etFechaCompactacion.setText(formatoFecha.format(calendario.time))
    }

    private fun cargarItemsEstadosVigas() {
        val itemMuestreo = arrayOf("Bien", "Mal", "---")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, itemMuestreo)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnMolde1.adapter = adapter
        spnMolde2.adapter = adapter
        spnMolde3.adapter = adapter
//        spnMolde4.adapter = adapter

        val itemMuestreo1 = arrayOf(
            "Resistencia Normal",
            "Resistencia Rápida"
        )
        val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, itemMuestreo1)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnTipoConcretoVigas.adapter = adapter1
    }

    private fun cargarTipoDeMuestreo() {
        val itemMuestreo = arrayOf(
            "Olla de camión mezclador o agitador",
            "Mezcladora estacionarias (fijas y basculantes)",
            "Camión caja con o sin agitadores",
            "Camión de volteo", "Pavimentadora",
            "Otro"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, itemMuestreo)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnTipoMuestreoVigas.adapter = adapter

    }

    fun mostrarCalendario(view: View) {
        val calendario = Calendar.getInstance()
        val año = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH)
        val dia = calendario.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this, { _, year, month, dayOfMonth ->
                // Formatear la fecha seleccionada con dos dígitos para el día
                val fechaSeleccionada = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                etFechaCompactacion.setText(fechaSeleccionada)
            }, año, mes, dia
        )

        datePickerDialog.show()
    }

    override fun onBackPressed() {
        // Aquí puedes realizar acciones específicas cuando se presiona el botón de retroceso
        // Por ejemplo, puedes mostrar un cuadro de diálogo de confirmación o realizar alguna operación antes de cerrar la actividad
        // Puedes agregar tu lógica aquí o llamar al método super.onBackPressed() para cerrar la actividad sin ninguna acción adicional.
        if (editar == true) {
        }
        ReportesVigas.editar = false
        super.onBackPressed()
    }

}