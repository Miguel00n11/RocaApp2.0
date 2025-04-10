package com.miguelrodriguez.rocaapp20.morteros

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.miguelrodriguez.rocaapp20.MainActivity
import com.miguelrodriguez.rocaapp20.R
import com.miguelrodriguez.rocaapp20.Recycler.Imagenes.ClaseImagenes
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegistroMorteros : AppCompatActivity() {
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
    private lateinit var spnTipoMuestreoMorteros: Spinner
    private lateinit var spnTipoConcretoMorteros: Spinner

    private lateinit var tvNumeroReportesMorteros: TextView
    private lateinit var etObraMorteros: EditText
    private lateinit var etClienteMorteros: EditText
    private lateinit var etLocalizacionMorteros: EditText
    private lateinit var etFechaCompactacion: EditText

    private lateinit var etElementoColadoMorteros: EditText
    private lateinit var etUbicacionMorteros: EditText
    private lateinit var etFCMorteros: EditText
    private lateinit var etVolumenMorteros: EditText
    private lateinit var etVolumenMuestraMorteros: EditText


    private lateinit var etEdadMorteros: EditText
    private lateinit var etTMAMorteros: EditText
    private lateinit var etConcreteraMorteros: EditText
    private lateinit var etHOPropMorteros: EditText
    private lateinit var etAditivoMorteros: EditText
    private lateinit var etMuestraMorteros: EditText
    private lateinit var etOllaMorteros: EditText
    private lateinit var etRemisionMorteros: EditText
//    private lateinit var etRevenimientoProyMorteros: EditText
//    private lateinit var etRevenimientoObt1Morteros: EditText
//    private lateinit var etRevenimientoObt2Morteros: EditText
    private lateinit var etTemperaturaMorteros: EditText

    private lateinit var etMolde1Morteros: EditText
    private lateinit var etMolde2Morteros: EditText
    private lateinit var etMolde3Morteros: EditText
//    private lateinit var etMolde4Morteros: EditText

    private lateinit var etHoraSalidaMorteros: EditText
    private lateinit var etHorallegadaMorteros: EditText
    private lateinit var etHoraMuestreoMorteros: EditText
    private lateinit var etObservacionesMorteros: EditText

    private lateinit var etCarretillaMorteros: EditText
    private lateinit var etConoMorteros: EditText
    private lateinit var etVarillaMorteros: EditText
    private lateinit var etMazoMorteros: EditText
    private lateinit var etTermometroMorteros: EditText
    private lateinit var etCucharonMorteros: EditText
    private lateinit var etPlacaMorteros: EditText
    private lateinit var etFlexometroMorteros: EditText
    private lateinit var etEnrasadorMorteros: EditText

    private lateinit var btnGuardarRegistroMorteros: Button
    private lateinit var btnCancelarRegistroMorteros: Button

    private lateinit var reporteSelecionado: ClaseObraMorteros

    private var editar: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_morteros)
        InitComponent()
        InitUI()

    }


    private fun InitComponent() {


        dataReference = FirebaseDatabase.getInstance().reference


        sharedPreferences = getPreferences(Context.MODE_PRIVATE)

        binding = findViewById(R.id.tilFechaMuestreoCompactaciones)


        tvNumeroReportesMorteros = findViewById(R.id.tvNumeroReporteMorteros)
        personal = MainActivity.NombreUsuarioCompanion
        llave = dataReference.push().key.toString()
        editar = ReportesMorteros.editar
        spnMolde1 = findViewById(R.id.spnMolde1)
        spnMolde2 = findViewById(R.id.spnMolde2)
        spnMolde3 = findViewById(R.id.spnMolde3)
//        spnMolde4 = findViewById(R.id.spnMolde4)
        spnTipoMuestreoMorteros = findViewById(R.id.spnTipoMuestreoMorteros)
        spnTipoConcretoMorteros = findViewById(R.id.spnTipoConcretoMorteros)
        etFechaCompactacion = findViewById(R.id.etFechaCompactacion)
        etEdadMorteros = findViewById(R.id.etEdadMorteros)

        etObraMorteros = findViewById(R.id.etObraMorteros)
        etClienteMorteros = findViewById(R.id.etClienteMorteros)
        etLocalizacionMorteros = findViewById(R.id.etLocalizacionMorteros)
        etElementoColadoMorteros = findViewById(R.id.etElementoColadoMorteros)
        etUbicacionMorteros = findViewById(R.id.etUbicacionMorteros)
        etFCMorteros = findViewById(R.id.etFCMorteros)
        etVolumenMorteros = findViewById(R.id.etVolumenMorteros)
        etVolumenMuestraMorteros = findViewById(R.id.etVolumenMuestraMorteros)
//        etTMAMorteros = findViewById(R.id.etTMAMorteros)
        etConcreteraMorteros = findViewById(R.id.etConcreteraMorteros)
        etHOPropMorteros = findViewById(R.id.etHOPropMorteros)
        etAditivoMorteros = findViewById(R.id.etAditivoMorteros)
        etRemisionMorteros = findViewById(R.id.etRemisionMorteros)
        etMuestraMorteros = findViewById(R.id.etMuestraMorteros)
        etOllaMorteros = findViewById(R.id.etOllaMorteros)
//        etRevenimientoProyMorteros = findViewById(R.id.etRevenimientoProyMorteros)
//        etRevenimientoObt1Morteros = findViewById(R.id.etReveniminetoObt1Morteros)
//        etRevenimientoObt2Morteros = findViewById(R.id.etReveniminetoObt2Morteros)
        etTemperaturaMorteros = findViewById(R.id.etTemperaturaMorteros)
        etMolde1Morteros = findViewById(R.id.etMolde1Morteros)
        etMolde2Morteros = findViewById(R.id.etMolde2Morteros)
        etMolde3Morteros = findViewById(R.id.etMolde3Morteros)
//        etMolde4Morteros = findViewById(R.id.etMolde4Morteros)

        etHoraSalidaMorteros = findViewById(R.id.etHoraSalidaMorteros)
        etHorallegadaMorteros = findViewById(R.id.etHorallegadaMorteros)
        etHoraMuestreoMorteros = findViewById(R.id.etHoraMuestreoMorteros)
        etObservacionesMorteros = findViewById(R.id.etObservacionesMorteros)

        etCarretillaMorteros = findViewById(R.id.etCarretillaMorteros)
        etConoMorteros = findViewById(R.id.etConoMorteros)
        etVarillaMorteros = findViewById(R.id.etVarillaMorteros)
        etMazoMorteros = findViewById(R.id.etMazoMorteros)
        etTermometroMorteros = findViewById(R.id.etTermometroMorteros)
        etCucharonMorteros = findViewById(R.id.etCucharonMorteros)
        etPlacaMorteros = findViewById(R.id.etPlacaMorteros)
        etFlexometroMorteros = findViewById(R.id.etFlexometroMorteros)
        etEnrasadorMorteros = findViewById(R.id.etEnrasadorMorteros)

        btnGuardarRegistroMorteros = findViewById(R.id.btnGuardarRegistroMorteros)
        btnCancelarRegistroMorteros = findViewById(R.id.btnCancelarRegistroMorteros)

        reporteSelecionado = ReportesMorteros.reporteSelecionado


        val sharedPreferences1 = getSharedPreferences("carretilla", Context.MODE_PRIVATE)

// Obtener el nombre guardado
        val nombreGuardado = sharedPreferences1.getString("nombre", "")

        etCarretillaMorteros.setText(nombreGuardado)


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

            cargarItemsEstadosMorteros()
            cargarTipoDeMuestreo()
            consultarValores()

        }

    }

    private fun cargarObraSeleccionada(reporteSelecionado: ClaseObraMorteros) {

        tvNumeroReportesMorteros.setText(reporteSelecionado.id.toString())
        etObraMorteros.setText(reporteSelecionado.Obra)
        etClienteMorteros.setText(reporteSelecionado.Cliente)
        etLocalizacionMorteros.setText(reporteSelecionado.localizacion)
        etFechaCompactacion.setText(reporteSelecionado.fecha)

        etElementoColadoMorteros.setText(reporteSelecionado.elementoColado)
        etUbicacionMorteros.setText(reporteSelecionado.ubicacion)
        etFCMorteros.setText(reporteSelecionado.fc.toString())
        etVolumenMorteros.setText(reporteSelecionado.volumenTotal.toString())
        etVolumenMuestraMorteros.setText(reporteSelecionado.volumenMuestra.toString())
        etEdadMorteros.setText(reporteSelecionado.edad.toString())
//        etTMAMorteros.setText(reporteSelecionado.tma.toString())
        etConcreteraMorteros.setText(reporteSelecionado.concretera)
        etHOPropMorteros.setText(reporteSelecionado.proporciones)
        etAditivoMorteros.setText(reporteSelecionado.aditivo)
        etRemisionMorteros.setText(reporteSelecionado.remision)

        etMuestraMorteros.setText(reporteSelecionado.muestra.toString())
        etOllaMorteros.setText(reporteSelecionado.olla.toString())
//        etRevenimientoProyMorteros.setText(reporteSelecionado.revenimientoDis.toString())
//        etRevenimientoObt1Morteros.setText(reporteSelecionado.revenimientoR1.toString())
//        etRevenimientoObt2Morteros.setText(reporteSelecionado.revenimientoR2.toString())
        etTemperaturaMorteros.setText(reporteSelecionado.temperatura.toString())
        etMolde1Morteros.setText(reporteSelecionado.Molde1.toString())
        etMolde2Morteros.setText(reporteSelecionado.Molde2.toString())
        etMolde3Morteros.setText(reporteSelecionado.Molde3.toString())
//        etMolde4Morteros.setText(reporteSelecionado.Molde4.toString())
        etHoraSalidaMorteros.setText(reporteSelecionado.horaSalida)
        etHorallegadaMorteros.setText(reporteSelecionado.horaLLegada)
        etHoraMuestreoMorteros.setText(reporteSelecionado.horaMuestreo)
        etObservacionesMorteros.setText(reporteSelecionado.observaciones)

        etCarretillaMorteros.setText(reporteSelecionado.carretilla)
//        etConoMorteros.setText(reporteSelecionado.cono)
//        etVarillaMorteros.setText(reporteSelecionado.varilla)
        etMazoMorteros.setText(reporteSelecionado.mazo)
//        etTermometroMorteros.setText(reporteSelecionado.termometro)
        etCucharonMorteros.setText(reporteSelecionado.cucharon)
//        etPlacaMorteros.setText(reporteSelecionado.placa)
        etFlexometroMorteros.setText(reporteSelecionado.flexometro)
        etEnrasadorMorteros.setText(reporteSelecionado.enrasador)

        spnTipoConcretoMorteros.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    cargarItemsTipoDeMuestreo(spnTipoConcretoMorteros.selectedItem.toString())

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // No se utiliza en este ejemplo
                }
            }

        cargarItemsEstadosMorteros()
        cargarTipoDeMuestreo()




        llave = reporteSelecionado.llave


        val tipoMuestreo1 = reporteSelecionado.tipoMuestreo
        val adapter1 = spnTipoMuestreoMorteros.adapter

        for (i in 0 until adapter1.count) {
            if (adapter1.getItem(i).toString() == tipoMuestreo1) {
                spnTipoMuestreoMorteros.setSelection(i)
                break
            }
        }
        val tipoResistencia = reporteSelecionado.tipoResistencia
        val adapter2 = spnTipoConcretoMorteros.adapter

        for (i in 0 until adapter2.count) {
            if (adapter2.getItem(i).toString() == tipoResistencia) {
                spnTipoConcretoMorteros.setSelection(i)
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
        spnTipoConcretoMorteros.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    cargarItemsTipoDeMuestreo(spnTipoConcretoMorteros.selectedItem.toString())

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // No se utiliza en este ejemplo
                }
            }

        etHoraSalidaMorteros.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hora = calendar.get(Calendar.HOUR_OF_DAY)
            val minuto = calendar.get(Calendar.MINUTE)

            // Crea un TimePickerDialog con la hora actual como predeterminada
            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    // Actualiza el texto del EditText con la hora seleccionada por el usuario
                    val horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute)
                    etHoraSalidaMorteros.setText(horaSeleccionada)
                },
                hora,
                minuto,
                true
            )

            // Muestra el dialogo de selección de hora
            timePickerDialog.show()
        }
        etHorallegadaMorteros.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hora = calendar.get(Calendar.HOUR_OF_DAY)
            val minuto = calendar.get(Calendar.MINUTE)

            // Crea un TimePickerDialog con la hora actual como predeterminada
            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    // Actualiza el texto del EditText con la hora seleccionada por el usuario
                    val horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute)
                    etHorallegadaMorteros.setText(horaSeleccionada)
                },
                hora,
                minuto,
                true
            )

            // Muestra el dialogo de selección de hora
            timePickerDialog.show()
        }
        etHoraMuestreoMorteros.setOnClickListener {

            val calendar = Calendar.getInstance()
            val hora = calendar.get(Calendar.HOUR_OF_DAY)
            val minuto = calendar.get(Calendar.MINUTE)

            // Crea un TimePickerDialog con la hora actual como predeterminada
            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    // Actualiza el texto del EditText con la hora seleccionada por el usuario
                    val horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute)
                    etHoraMuestreoMorteros.setText(horaSeleccionada)
                },
                hora,
                minuto,
                true
            )

            // Muestra el dialogo de selección de hora
            timePickerDialog.show()
        }
        btnGuardarRegistroMorteros.setOnClickListener {
            mostrarDialogo()

        }
        btnCancelarRegistroMorteros.setOnClickListener {


            onBackPressed()
        }
    }


    private fun consultarValores() {
        val inventarioRef: DatabaseReference =
            databaseInventario.getReference("personal").child("inventario").child(personal)
        // Agrega un listener para manejar el resultado de la consulta en el nodo "personal"
        inventarioRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                etCarretillaMorteros.setText(
                    dataSnapshot.child("carretilla").getValue(Int::class.java).toString()
                )
                etConoMorteros.setText(
                    dataSnapshot.child("cono").getValue(Int::class.java).toString()
                )
                etVarillaMorteros.setText(
                    dataSnapshot.child("varilla").getValue(Int::class.java).toString()
                )
                etMazoMorteros.setText(
                    dataSnapshot.child("mazo").getValue(Int::class.java).toString()
                )
                etTermometroMorteros.setText(
                    dataSnapshot.child("termometro").getValue(Int::class.java).toString()
                )
                etCucharonMorteros.setText(
                    dataSnapshot.child("cucharon").getValue(Int::class.java).toString()
                )
                etPlacaMorteros.setText(
                    dataSnapshot.child("placa").getValue(Int::class.java).toString()
                )
                etFlexometroMorteros.setText(
                    dataSnapshot.child("flexometro").getValue(Int::class.java).toString()
                )
                etEnrasadorMorteros.setText(
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
            if (etObraMorteros.text.toString()==null||etObraMorteros.text.toString()=="")
            {
                Toast.makeText(this, "Agregar nombre de obra", Toast.LENGTH_SHORT).show()
                return@setPositiveButton}
            try {


                val obra: String = etObraMorteros.text.toString()
                val cliente: String = etClienteMorteros.text.toString()
                val localizacion: String = etLocalizacionMorteros.text.toString()
                val fecha: String = etFechaCompactacion.text.toString()
                val numeroReporte: Int = tvNumeroReportesMorteros.text.toString().toInt()
                val tipoMuestreo: String = spnTipoMuestreoMorteros.selectedItem.toString()

                val elementoColado: String = etElementoColadoMorteros.text.toString()
                val ubicacion: String = etUbicacionMorteros.text.toString()
                val fc: Double = etFCMorteros.text.toString().toDouble()
                val volumenTotal: Double = etVolumenMorteros.text.toString().toDouble()
                val volumenMuestra: Double = etVolumenMuestraMorteros.text.toString().toDouble()
                val tipoResistencia: String = spnTipoConcretoMorteros.selectedItem.toString()
                val edad: Int = etEdadMorteros.text.toString().toInt()
//                val tma: Double = etTMAMorteros.text.toString().toDouble()
                val concretera = etConcreteraMorteros.text.toString()
                val proporciones = etHOPropMorteros.text.toString()
                val aditivo = etAditivoMorteros.text.toString()
                val remision = etRemisionMorteros.text.toString()

                val muestra = etMuestraMorteros.text.toString().toInt()
                val olla = etOllaMorteros.text.toString()
//                val revenimientoDis = etRevenimientoProyMorteros.text.toString().toDouble()
//                val revenimientoR1 = etRevenimientoObt1Morteros.text.toString().toDouble()
//                val revenimientoR2 = etRevenimientoObt2Morteros.text.toString().toDouble()
                val temperatura = etTemperaturaMorteros.text.toString().toDouble()
                val molde1 = etMolde1Morteros.text.toString().toInt()
                val molde2 = etMolde2Morteros.text.toString().toInt()
                val molde3 = etMolde3Morteros.text.toString().toInt()
//                val molde4 = etMolde4Morteros.text.toString().toInt()
                val estadoMolde1 = spnMolde1.selectedItem.toString()
                val estadoMolde2 = spnMolde2.selectedItem.toString()
                val estadoMolde3 = spnMolde3.selectedItem.toString()
//                val estadoMolde4 = spnMolde4.selectedItem.toString()
                val horaSalida = etHoraSalidaMorteros.text.toString()
                val horaLLegada = etHorallegadaMorteros.text.toString()
                val horaMuestreo = etHoraMuestreoMorteros.text.toString()
                val observaciones = etObservacionesMorteros.text.toString()

                val carretilla = etCarretillaMorteros.text.toString()
                val cono = etConoMorteros.text.toString()
                val varilla = etVarillaMorteros.text.toString()
                val mazo = etMazoMorteros.text.toString()
                val termometro = etTermometroMorteros.text.toString()
                val cucharon = etCucharonMorteros.text.toString()
                val placa = etPlacaMorteros.text.toString()
                val flexometro = etFlexometroMorteros.text.toString()
                val enrasador = etEnrasadorMorteros.text.toString()
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
//                    tma,
                    concretera,
                    proporciones,
                    aditivo,
                    remision,
                    muestra,
                    olla,
//                    revenimientoDis,
//                    revenimientoR1,
//                    revenimientoR2,
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
//                    cono,
//                    varilla,
                    mazo,
//                    termometro,
                    cucharon,
//                    placa,
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
                            dataReference.child("Morteros").child("Reportes").child(personal)
                                .child(reporteSelecionado.llave)
                                .setValue(registro)
//                            dataReference.child("personal").child("inventario").child(personal)
//                                .child("cono").setValue(etConoMorteros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("carretilla")
                                .setValue(etCarretillaMorteros.text.toString().toInt())
//                            dataReference.child("personal").child("inventario").child(personal)
//                                .child("varilla")
//                                .setValue(etVarillaMorteros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("mazo").setValue(etMazoMorteros.text.toString().toInt())
//                            dataReference.child("personal").child("inventario").child(personal)
//                                .child("termometro")
//                                .setValue(etTermometroMorteros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("cucharon")
                                .setValue(etCucharonMorteros.text.toString().toInt())
//                            dataReference.child("personal").child("inventario").child(personal)
//                                .child("placa").setValue(etPlacaMorteros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("flexometro")
                                .setValue(etFlexometroMorteros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("enrasador")
                                .setValue(etEnrasadorMorteros.text.toString().toInt())
                            onBackPressed()
                        } else {
                            // Guardar el registro en Firebase Realtime Database
                            registro.llave = llave
                            dataReference.child("Morteros").child("Reportes").child(personal)
                                .child(llave)
                                .setValue(registro)
//                            dataReference.child("personal").child("inventario").child(personal)
//                                .child("cono").setValue(etConoMorteros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("carretilla")
                                .setValue(etCarretillaMorteros.text.toString().toInt())
//                            dataReference.child("personal").child("inventario").child(personal)
//                                .child("varilla")
//                                .setValue(etVarillaMorteros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("mazo").setValue(etMazoMorteros.text.toString().toInt())
//                            dataReference.child("personal").child("inventario").child(personal)
//                                .child("termometro")
//                                .setValue(etTermometroMorteros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("cucharon")
                                .setValue(etCucharonMorteros.text.toString().toInt())
//                            dataReference.child("personal").child("inventario").child(personal)
//                                .child("placa").setValue(etPlacaMorteros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("flexometro")
                                .setValue(etFlexometroMorteros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("enrasador")
                                .setValue(etEnrasadorMorteros.text.toString().toInt())


                        }
                        if (accion == true) {
                            // Guardar el registro en Firebase Realtime Database
                            dataReference.child("Morteros").child("Respaldo").child(personal)
                                .child(reporteSelecionado.llave)
                                .setValue(registro)
                            onBackPressed()
                        } else {
                            // Guardar el registro en Firebase Realtime Database
                            registro.llave = llave
                            dataReference.child("Morteros").child("Respaldo").child(personal)
                                .child(llave)
                                .setValue(registro)
                            // aqui quitamos el pressed back para que no salga del reporte cuando se guarde por primera vez
                            llave = dataReference.push().key.toString()
//                            etRevenimientoObt1Morteros.text=null
//                            etRevenimientoObt2Morteros.text=null
//                            etRevenimientoProyMorteros.text=null

                            etHoraMuestreoMorteros.text=null
                            etHoraSalidaMorteros.text=null
                            etHorallegadaMorteros.text=null
                            etObservacionesMorteros.text=null

                            etMolde1Morteros.text=null
                            etMolde2Morteros.text=null
                            etMolde3Morteros.text=null

                            etRemisionMorteros.text=null
                            etMuestraMorteros.setText((etMuestraMorteros.text.toString().toInt()+1).toString())
                            etOllaMorteros.text = null

//                            onBackPressed()

                        }
                    } catch (e: NumberFormatException) {
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
//        tma: Double,
        concretera: String,
        proporciones: String,
        aditivo: String,
        remision: String,

        muestra: Int,
        olla: String,
//        revenimientoDis: Double,
//        revenimientoR1: Double,
//        revenimientoR2: Double,
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
//        cono: String,
//        varilla: String,
        mazo: String,
//        termometro: String,
        cucharon: String,
//        placa: String,
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
//            tma,
            concretera,
            proporciones,
            aditivo,
            remision,

            muestra,
            olla,
//            revenimientoDis,
//            revenimientoR1,
//            revenimientoR2,
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
//            cono,
//            varilla,
            mazo,
//            termometro,
            cucharon,
//            placa,
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
//        val tma: Double,
        val concretera: String,
        val proporciones: String,
        val aditivo: String,
        val remision: String,

        val muestra: Int,
        val olla: String,
//        val revenimientoDis: Double,
//        val revenimientoR1: Double,
//        val revenimientoR2: Double,
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
//        val cono: String,
//        val varilla: String,
        val mazo: String,
//        val termometro: String,
        val cucharon: String,
//        val placa: String,
        val flexometro: String,
        val enrasador: String,
        val validado: Boolean,

        var llave: String
    )

    private fun saveLocalRecords(registros: List<RegistroMorteros.Registro>) {

        val registrosJson = Gson().toJson(registros)
        sharedPreferences.edit().putString("registros", registrosJson).apply()
    }

    private fun getLocalRecords(): MutableList<RegistroMorteros.Registro> {
        val registrosJson = sharedPreferences.getString("registros", "[]")
        return Gson().fromJson(
            registrosJson,
            object : TypeToken<MutableList<RegistroMorteros.Registro>>() {}.type
        )
            ?: mutableListOf()
    }

    private fun cargarItemsTipoDeMuestreo(spnTipoConcretoMorteros: String) {
        if (editar){
            etEdadMorteros.setText(reporteSelecionado.edad.toString())

        }else{
            if (spnTipoConcretoMorteros == "Resistencia Normal") {
                etEdadMorteros.setText("28")
            } else {
                etEdadMorteros.setText("7")
            }
        }

    }

    private fun FechaDeHoy() {
        val calendario = Calendar.getInstance()
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        etFechaCompactacion.setText(formatoFecha.format(calendario.time))
    }

    private fun cargarItemsEstadosMorteros() {
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
        spnTipoConcretoMorteros.adapter = adapter1
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
        spnTipoMuestreoMorteros.adapter = adapter

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
        ReportesMorteros.editar = false
        super.onBackPressed()
    }

}