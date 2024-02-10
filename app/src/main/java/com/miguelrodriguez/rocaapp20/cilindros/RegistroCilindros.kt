package com.miguelrodriguez.rocaapp20.cilindros

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

class RegistroCilindros : AppCompatActivity() {
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
    private lateinit var spnMolde4: Spinner
    private lateinit var spnTipoMuestreoCilindros: Spinner
    private lateinit var spnTipoConcretoCilindros: Spinner

    private lateinit var tvNumeroReporteCilindros: TextView
    private lateinit var etObraCilindros: EditText
    private lateinit var etClienteCilindros: EditText
    private lateinit var etFechaCompactacion: EditText

    private lateinit var etElementoColadoCilindros: EditText
    private lateinit var etUbicacionCilindros: EditText
    private lateinit var etFCCilindros: EditText
    private lateinit var etVolumenCilindros: EditText

    private lateinit var etEdadCilindros: EditText
    private lateinit var etTMACilindros: EditText
    private lateinit var etConcreteraCilindros: EditText
    private lateinit var etHOPropCilindros: EditText
    private lateinit var etAditivoCilindros: EditText
    private lateinit var etMuestraCilindros: EditText
    private lateinit var etRemisionCilindros: EditText
    private lateinit var etRevenimientoProyCilindros: EditText
    private lateinit var etRevenimientoObt1Cilindros: EditText
    private lateinit var etRevenimientoObt2Cilindros: EditText
    private lateinit var etTemperaturaCilindros: EditText

    private lateinit var etMolde1Cilindros: EditText
    private lateinit var etMolde2Cilindros: EditText
    private lateinit var etMolde3Cilindros: EditText
    private lateinit var etMolde4Cilindros: EditText

    private lateinit var etHoraSalidaCilindros: EditText
    private lateinit var etHorallegadaCilindros: EditText
    private lateinit var etHoraMuestreoCilindros: EditText
    private lateinit var etObservacionesCilindros: EditText

    private lateinit var etCarretillaCilindros: EditText
    private lateinit var etConoCilindros: EditText
    private lateinit var etVarillaCilindros: EditText
    private lateinit var etMazoCilindros: EditText
    private lateinit var etTermometroCilindros: EditText
    private lateinit var etCucharonCilindros: EditText
    private lateinit var etPlacaCilindros: EditText
    private lateinit var etFlexometroCilindros: EditText
    private lateinit var etEnrasadorCilindros: EditText

    private lateinit var btnGuardarRegistroCilindros: Button
    private lateinit var btnCancelarRegistroCilindros: Button

    private lateinit var reporteSelecionado: ClaseObraCilindros

    private var editar: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_cilindros)

        InitComponent()
        InitUI()

    }


    private fun InitComponent() {


        dataReference = FirebaseDatabase.getInstance().reference


        sharedPreferences = getPreferences(Context.MODE_PRIVATE)

        binding = findViewById(R.id.tilFechaMuestreoCompactaciones)


        tvNumeroReporteCilindros = findViewById(R.id.tvNumeroReporteCilindros)
        personal = MainActivity.NombreUsuarioCompanion
        llave = dataReference.push().key.toString()
        editar = ReporteCilindros.editar
        spnMolde1 = findViewById(R.id.spnMolde1)
        spnMolde2 = findViewById(R.id.spnMolde2)
        spnMolde3 = findViewById(R.id.spnMolde3)
        spnMolde4 = findViewById(R.id.spnMolde4)
        spnTipoMuestreoCilindros = findViewById(R.id.spnTipoMuestreoCilindros)
        spnTipoConcretoCilindros = findViewById(R.id.spnTipoConcretoCilindros)
        etFechaCompactacion = findViewById(R.id.etFechaCompactacion)
        etEdadCilindros = findViewById(R.id.etEdadCilindros)

        etObraCilindros = findViewById(R.id.etObraCilindros)
        etClienteCilindros = findViewById(R.id.etClienteCilindros)
        etElementoColadoCilindros = findViewById(R.id.etElementoColadoCilindros)
        etUbicacionCilindros = findViewById(R.id.etUbicacionCilindros)
        etFCCilindros = findViewById(R.id.etFCCilindros)
        etVolumenCilindros = findViewById(R.id.etVolumenCilindros)
        etTMACilindros = findViewById(R.id.etTMACilindros)
        etConcreteraCilindros = findViewById(R.id.etConcreteraCilindros)
        etHOPropCilindros = findViewById(R.id.etHOPropCilindros)
        etAditivoCilindros = findViewById(R.id.etAditivoCilindros)
        etRemisionCilindros = findViewById(R.id.etRemisionCilindros)
        etMuestraCilindros = findViewById(R.id.etMuestraCilindros)
        etRevenimientoProyCilindros = findViewById(R.id.etRevenimientoProyCilindros)
        etRevenimientoObt1Cilindros = findViewById(R.id.etReveniminetoObt1Cilindros)
        etRevenimientoObt2Cilindros = findViewById(R.id.etReveniminetoObt2Cilindros)
        etTemperaturaCilindros = findViewById(R.id.etTemperaturaCilindros)
        etMolde1Cilindros = findViewById(R.id.etMolde1Cilindros)
        etMolde2Cilindros = findViewById(R.id.etMolde2Cilindros)
        etMolde3Cilindros = findViewById(R.id.etMolde3Cilindros)
        etMolde4Cilindros = findViewById(R.id.etMolde4Cilindros)

        etHoraSalidaCilindros = findViewById(R.id.etHoraSalidaCilindros)
        etHorallegadaCilindros = findViewById(R.id.etHorallegadaCilindros)
        etHoraMuestreoCilindros = findViewById(R.id.etHoraMuestreoCilindros)
        etObservacionesCilindros = findViewById(R.id.etObservacionesCilindros)

        etCarretillaCilindros = findViewById(R.id.etCarretillaCilindros)
        etConoCilindros = findViewById(R.id.etConoCilindros)
        etVarillaCilindros = findViewById(R.id.etVarillaCilindros)
        etMazoCilindros = findViewById(R.id.etMazoCilindros)
        etTermometroCilindros = findViewById(R.id.etTermometroCilindros)
        etCucharonCilindros = findViewById(R.id.etCucharonCilindros)
        etPlacaCilindros = findViewById(R.id.etPlacaCilindros)
        etFlexometroCilindros = findViewById(R.id.etFlexometroCilindros)
        etEnrasadorCilindros = findViewById(R.id.etEnrasadorCilindros)

        btnGuardarRegistroCilindros = findViewById(R.id.btnGuardarRegistroCilindros)
        btnCancelarRegistroCilindros = findViewById(R.id.btnCancelarRegistroCilindros)

        reporteSelecionado = ReporteCilindros.reporteSelecionado


        val sharedPreferences1 = getSharedPreferences("carretilla", Context.MODE_PRIVATE)

// Obtener el nombre guardado
        val nombreGuardado = sharedPreferences1.getString("nombre", "")

        etCarretillaCilindros.setText(nombreGuardado)


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

            cargarItemsEstadosCilindros()
            cargarTipoDeMuestreo()
            consultarValores()

        }

    }

    private fun cargarObraSeleccionada(reporteSelecionado: ClaseObraCilindros) {

        tvNumeroReporteCilindros.setText(reporteSelecionado.id.toString())
        etObraCilindros.setText(reporteSelecionado.Obra)
        etClienteCilindros.setText(reporteSelecionado.Cliente)
        etFechaCompactacion.setText(reporteSelecionado.fecha)

        etElementoColadoCilindros.setText(reporteSelecionado.elementoColado)
        etUbicacionCilindros.setText(reporteSelecionado.ubicacion)
        etFCCilindros.setText(reporteSelecionado.fc.toString())
        etVolumenCilindros.setText(reporteSelecionado.volumenTotal.toString())
        etEdadCilindros.setText(reporteSelecionado.edad.toString())
        etTMACilindros.setText(reporteSelecionado.tma.toString())
        etConcreteraCilindros.setText(reporteSelecionado.concretera)
        etHOPropCilindros.setText(reporteSelecionado.proporciones)
        etAditivoCilindros.setText(reporteSelecionado.aditivo)
        etRemisionCilindros.setText(reporteSelecionado.remision)

        etMuestraCilindros.setText(reporteSelecionado.muestra.toString())
        etRevenimientoProyCilindros.setText(reporteSelecionado.revenimientoDis.toString())
        etRevenimientoObt1Cilindros.setText(reporteSelecionado.revenimientoR1.toString())
        etRevenimientoObt2Cilindros.setText(reporteSelecionado.revenimientoR2.toString())
        etTemperaturaCilindros.setText(reporteSelecionado.temperatura.toString())
        etMolde1Cilindros.setText(reporteSelecionado.Molde1.toString())
        etMolde2Cilindros.setText(reporteSelecionado.Molde2.toString())
        etMolde3Cilindros.setText(reporteSelecionado.Molde3.toString())
        etMolde4Cilindros.setText(reporteSelecionado.Molde4.toString())
        etHoraSalidaCilindros.setText(reporteSelecionado.horaSalida)
        etHorallegadaCilindros.setText(reporteSelecionado.horaLLegada)
        etHoraMuestreoCilindros.setText(reporteSelecionado.horaMuestreo)
        etObservacionesCilindros.setText(reporteSelecionado.observaciones)

        etCarretillaCilindros.setText(reporteSelecionado.carretilla)
        etConoCilindros.setText(reporteSelecionado.cono)
        etVarillaCilindros.setText(reporteSelecionado.varilla)
        etMazoCilindros.setText(reporteSelecionado.mazo)
        etTermometroCilindros.setText(reporteSelecionado.termometro)
        etCucharonCilindros.setText(reporteSelecionado.cucharon)
        etPlacaCilindros.setText(reporteSelecionado.placa)
        etFlexometroCilindros.setText(reporteSelecionado.flexometro)
        etEnrasadorCilindros.setText(reporteSelecionado.enrasador)

        spnTipoConcretoCilindros.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    cargarItemsTipoDeMuestreo(spnTipoConcretoCilindros.selectedItem.toString())

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // No se utiliza en este ejemplo
                }
            }

        cargarItemsEstadosCilindros()
        cargarTipoDeMuestreo()




        llave = reporteSelecionado.llave


        val tipoMuestreo1 = reporteSelecionado.tipoMuestreo
        val adapter1 = spnTipoMuestreoCilindros.adapter

        for (i in 0 until adapter1.count) {
            if (adapter1.getItem(i).toString() == tipoMuestreo1) {
                spnTipoMuestreoCilindros.setSelection(i)
                break
            }
        }
        val tipoResistencia = reporteSelecionado.tipoResistencia
        val adapter2 = spnTipoConcretoCilindros.adapter

        for (i in 0 until adapter2.count) {
            if (adapter2.getItem(i).toString() == tipoResistencia) {
                spnTipoConcretoCilindros.setSelection(i)
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
        val estadoMolde4 = reporteSelecionado.estadoMolde4
        val adapterestadoMolde4 = spnMolde4.adapter

        for (i in 0 until adapterestadoMolde4.count) {
            if (adapterestadoMolde4.getItem(i).toString() == estadoMolde4) {
                spnMolde4.setSelection(i)
                break
            }
        }


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
        spnTipoConcretoCilindros.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    cargarItemsTipoDeMuestreo(spnTipoConcretoCilindros.selectedItem.toString())

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // No se utiliza en este ejemplo
                }
            }

        etHoraSalidaCilindros.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hora = calendar.get(Calendar.HOUR_OF_DAY)
            val minuto = calendar.get(Calendar.MINUTE)

            // Crea un TimePickerDialog con la hora actual como predeterminada
            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    // Actualiza el texto del EditText con la hora seleccionada por el usuario
                    val horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute)
                    etHoraSalidaCilindros.setText(horaSeleccionada)
                },
                hora,
                minuto,
                true
            )

            // Muestra el dialogo de selección de hora
            timePickerDialog.show()
        }
        etHorallegadaCilindros.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hora = calendar.get(Calendar.HOUR_OF_DAY)
            val minuto = calendar.get(Calendar.MINUTE)

            // Crea un TimePickerDialog con la hora actual como predeterminada
            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    // Actualiza el texto del EditText con la hora seleccionada por el usuario
                    val horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute)
                    etHorallegadaCilindros.setText(horaSeleccionada)
                },
                hora,
                minuto,
                true
            )

            // Muestra el dialogo de selección de hora
            timePickerDialog.show()
        }
        etHoraMuestreoCilindros.setOnClickListener {

            val calendar = Calendar.getInstance()
            val hora = calendar.get(Calendar.HOUR_OF_DAY)
            val minuto = calendar.get(Calendar.MINUTE)

            // Crea un TimePickerDialog con la hora actual como predeterminada
            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    // Actualiza el texto del EditText con la hora seleccionada por el usuario
                    val horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute)
                    etHoraMuestreoCilindros.setText(horaSeleccionada)
                },
                hora,
                minuto,
                true
            )

            // Muestra el dialogo de selección de hora
            timePickerDialog.show()
        }
        btnGuardarRegistroCilindros.setOnClickListener {
            mostrarDialogo()

        }
        btnCancelarRegistroCilindros.setOnClickListener {


            onBackPressed()
        }
    }


    private fun consultarValores() {
        val inventarioRef: DatabaseReference =
            databaseInventario.getReference("personal").child("inventario").child(personal)
        // Agrega un listener para manejar el resultado de la consulta en el nodo "personal"
        inventarioRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                etCarretillaCilindros.setText(
                    dataSnapshot.child("carretilla").getValue(Int::class.java).toString()
                )
                etConoCilindros.setText(
                    dataSnapshot.child("cono").getValue(Int::class.java).toString()
                )
                etVarillaCilindros.setText(
                    dataSnapshot.child("varilla").getValue(Int::class.java).toString()
                )
                etMazoCilindros.setText(
                    dataSnapshot.child("mazo").getValue(Int::class.java).toString()
                )
                etTermometroCilindros.setText(
                    dataSnapshot.child("termometro").getValue(Int::class.java).toString()
                )
                etCucharonCilindros.setText(
                    dataSnapshot.child("cucharon").getValue(Int::class.java).toString()
                )
                etPlacaCilindros.setText(
                    dataSnapshot.child("placa").getValue(Int::class.java).toString()
                )
                etFlexometroCilindros.setText(
                    dataSnapshot.child("flexometro").getValue(Int::class.java).toString()
                )
                etEnrasadorCilindros.setText(
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

            try {


                val obra: String = etObraCilindros.text.toString()
                val cliente: String = etClienteCilindros.text.toString()
                val fecha: String = etFechaCompactacion.text.toString()
                val numeroReporte: Int = tvNumeroReporteCilindros.text.toString().toInt()
                val tipoMuestreo: String = spnTipoMuestreoCilindros.selectedItem.toString()

                val elementoColado: String = etElementoColadoCilindros.text.toString()
                val ubicacion: String = etUbicacionCilindros.text.toString()
                val fc: Double = etFCCilindros.text.toString().toDouble()
                val volumenTotal: Double = etVolumenCilindros.text.toString().toDouble()
                val tipoResistencia: String = spnTipoConcretoCilindros.selectedItem.toString()
                val edad: Int = etEdadCilindros.text.toString().toInt()
                val tma: Double = etTMACilindros.text.toString().toDouble()
                val concretera = etConcreteraCilindros.text.toString()
                val proporciones = etHOPropCilindros.text.toString()
                val aditivo = etAditivoCilindros.text.toString()
                val remision = etRemisionCilindros.text.toString()

                val muestra = etMuestraCilindros.text.toString().toInt()
                val revenimientoDis = etRevenimientoProyCilindros.text.toString().toDouble()
                val revenimientoR1 = etRevenimientoObt1Cilindros.text.toString().toDouble()
                val revenimientoR2 = etRevenimientoObt2Cilindros.text.toString().toDouble()
                val temperatura = etTemperaturaCilindros.text.toString().toDouble()
                val molde1 = etMolde1Cilindros.text.toString().toInt()
                val molde2 = etMolde2Cilindros.text.toString().toInt()
                val molde3 = etMolde3Cilindros.text.toString().toInt()
                val molde4 = etMolde4Cilindros.text.toString().toInt()
                val estadoMolde1 = spnMolde1.selectedItem.toString()
                val estadoMolde2 = spnMolde2.selectedItem.toString()
                val estadoMolde3 = spnMolde3.selectedItem.toString()
                val estadoMolde4 = spnMolde4.selectedItem.toString()
                val horaSalida = etHoraSalidaCilindros.text.toString()
                val horaLLegada = etHorallegadaCilindros.text.toString()
                val horaMuestreo = etHoraMuestreoCilindros.text.toString()
                val observaciones = etObservacionesCilindros.text.toString()

                val carretilla = etCarretillaCilindros.text.toString()
                val cono = etConoCilindros.text.toString()
                val varilla = etVarillaCilindros.text.toString()
                val mazo = etMazoCilindros.text.toString()
                val termometro = etTermometroCilindros.text.toString()
                val cucharon = etCucharonCilindros.text.toString()
                val placa = etPlacaCilindros.text.toString()
                val flexometro = etFlexometroCilindros.text.toString()
                val enrasador = etEnrasadorCilindros.text.toString()
                val validado: Boolean = false

                var llave = reporteSelecionado.llave


                // Agregar un nuevo registro localmente
                saveLocally(
                    obra,
                    cliente,
                    fecha,
                    personal,
                    numeroReporte,
                    tipoMuestreo,
                    elementoColado,
                    ubicacion,
                    fc,
                    volumenTotal,
                    tipoResistencia,
                    edad,
                    tma,
                    concretera,
                    proporciones,
                    aditivo,
                    remision,
                    muestra,
                    revenimientoDis,
                    revenimientoR1,
                    revenimientoR2,
                    temperatura,
                    molde1,
                    molde2,
                    molde3,
                    molde4,
                    estadoMolde1,
                    estadoMolde2,
                    estadoMolde3,
                    estadoMolde4,
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
                            dataReference.child("Cilindros").child("Reportes").child(personal)
                                .child(reporteSelecionado.llave)
                                .setValue(registro)
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("cono").setValue(etConoCilindros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("carretilla")
                                .setValue(etCarretillaCilindros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("varilla")
                                .setValue(etVarillaCilindros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("mazo").setValue(etMazoCilindros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("termometro")
                                .setValue(etTermometroCilindros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("cucharon")
                                .setValue(etCucharonCilindros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("placa").setValue(etPlacaCilindros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("flexometro")
                                .setValue(etFlexometroCilindros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("enrasador")
                                .setValue(etEnrasadorCilindros.text.toString().toInt())
                            onBackPressed()
                        } else {
                            // Guardar el registro en Firebase Realtime Database
                            registro.llave = llave
                            dataReference.child("Cilindros").child("Reportes").child(personal)
                                .child(llave)
                                .setValue(registro)
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("cono").setValue(etConoCilindros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("carretilla")
                                .setValue(etCarretillaCilindros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("varilla")
                                .setValue(etVarillaCilindros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("mazo").setValue(etMazoCilindros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("termometro")
                                .setValue(etTermometroCilindros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("cucharon")
                                .setValue(etCucharonCilindros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("placa").setValue(etPlacaCilindros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("flexometro")
                                .setValue(etFlexometroCilindros.text.toString().toInt())
                            dataReference.child("personal").child("inventario").child(personal)
                                .child("enrasador")
                                .setValue(etEnrasadorCilindros.text.toString().toInt())


                        }
                        if (accion == true) {
                            // Guardar el registro en Firebase Realtime Database
                            dataReference.child("Cilindros").child("Respaldo").child(personal)
                                .child(reporteSelecionado.llave)
                                .setValue(registro)
                            onBackPressed()
                        } else {
                            // Guardar el registro en Firebase Realtime Database
                            registro.llave = llave
                            dataReference.child("Cilindros").child("Respaldo").child(personal)
                                .child(llave)
                                .setValue(registro)

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
        fecha: String,
        personal: String,
        numeroReporte: Int,
        tipoMuestreo: String,

        elementoColado: String,
        ubicacion: String,
        fc: Double,
        volumenTotal: Double,
        tipoResistencia: String,
        edad: Int,
        tma: Double,
        concretera: String,
        proporciones: String,
        aditivo: String,
        remision: String,

        muestra: Int,
        revenimientoDis: Double,
        revenimientoR1: Double,
        revenimientoR2: Double,
        temperatura: Double,
        molde1: Int,
        molde2: Int,
        molde3: Int,
        molde4: Int,
        estadoMolde1: String,
        estadoMolde2: String,
        estadoMolde3: String,
        estadoMolde4: String,
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
            fecha,
            personal,
            numeroReporte,
            tipoMuestreo,

            elementoColado,
            ubicacion,
            fc,
            volumenTotal,
            tipoResistencia,
            edad,
            tma,
            concretera,
            proporciones,
            aditivo,
            remision,

            muestra,
            revenimientoDis,
            revenimientoR1,
            revenimientoR2,
            temperatura,
            molde1,
            molde2,
            molde3,
            molde4,
            estadoMolde1,
            estadoMolde2,
            estadoMolde3,
            estadoMolde4,
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
        val fecha: String,
        val personal: String,
        val numeroReporte: Int,
        val tipoMuestreo: String,

        val elementoColado: String,
        val ubicacion: String,
        val fc: Double,
        val volumenTotal: Double,
        val tipoResistencia: String,
        val edad: Int,
        val tma: Double,
        val concretera: String,
        val proporciones: String,
        val aditivo: String,
        val remision: String,

        val muestra: Int,
        val revenimientoDis: Double,
        val revenimientoR1: Double,
        val revenimientoR2: Double,
        val temperatura: Double,
        val molde1: Int,
        val molde2: Int,
        val molde3: Int,
        val molde4: Int,
        val estadoMolde1: String,
        val estadoMolde2: String,
        val estadoMolde3: String,
        val estadoMolde4: String,
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

    private fun saveLocalRecords(registros: List<RegistroCilindros.Registro>) {

        val registrosJson = Gson().toJson(registros)
        sharedPreferences.edit().putString("registros", registrosJson).apply()
    }

    private fun getLocalRecords(): MutableList<RegistroCilindros.Registro> {
        val registrosJson = sharedPreferences.getString("registros", "[]")
        return Gson().fromJson(
            registrosJson,
            object : TypeToken<MutableList<RegistroCilindros.Registro>>() {}.type
        )
            ?: mutableListOf()
    }

    private fun cargarItemsTipoDeMuestreo(spnTipoConcretoCilindros: String) {
        if (spnTipoConcretoCilindros == "Resistencia Normal") {
            etEdadCilindros.setText("28")
        } else {
            etEdadCilindros.setText("7")
        }
    }

    private fun FechaDeHoy() {
        val calendario = Calendar.getInstance()
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        etFechaCompactacion.setText(formatoFecha.format(calendario.time))
    }

    private fun cargarItemsEstadosCilindros() {
        val itemMuestreo = arrayOf("Bien", "Mal", "---")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, itemMuestreo)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnMolde1.adapter = adapter
        spnMolde2.adapter = adapter
        spnMolde3.adapter = adapter
        spnMolde4.adapter = adapter

        val itemMuestreo1 = arrayOf(
            "Resistencia Normal",
            "Resistencia Rápida"
        )
        val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, itemMuestreo1)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnTipoConcretoCilindros.adapter = adapter1
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
        spnTipoMuestreoCilindros.adapter = adapter

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
        ReporteCilindros.editar = false
        super.onBackPressed()
    }

}