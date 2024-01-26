package com.miguelrodriguez.rocaapp20.cilindros

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.miguelrodriguez.rocaapp20.MainActivity
import com.miguelrodriguez.rocaapp20.R
import com.miguelrodriguez.rocaapp20.Recycler.ClaseCala
import com.miguelrodriguez.rocaapp20.Recycler.ClaseObra
import com.miguelrodriguez.rocaapp20.RegistroCompactaciones
import com.miguelrodriguez.rocaapp20.ReportesCompactaciones
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegistroCilindros : AppCompatActivity() {

    private lateinit var dataReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var personal: String
    private lateinit var llave: String



    private lateinit var spnMolde1: Spinner
    private lateinit var spnMolde2: Spinner
    private lateinit var spnMolde3: Spinner
    private lateinit var spnMolde4: Spinner
    private lateinit var spnTipoMuestreoCilindros: Spinner
    private lateinit var spnTipoConcretoCilindros: Spinner

    private lateinit var tvNumeroReporteCilindros: TextView
    private lateinit var etObraCilindros: EditText
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
    private lateinit var etRemisionCilindros: EditText
    private lateinit var etRevenimientoProyCilindros: EditText
    private lateinit var etReveniminetoObt1Cilindros: EditText
    private lateinit var etReveniminetoObt2Cilindros: EditText
    private lateinit var etTemperaturaCilindros: EditText

    private lateinit var etMolde1Cilindros: EditText
    private lateinit var etMolde2Cilindros: EditText
    private lateinit var etMolde3Cilindros: EditText
    private lateinit var etMolde4Cilindros: EditText

    private lateinit var etHoraSalidaCilindros: EditText
    private lateinit var etHorallegadaCilindros: EditText
    private lateinit var etHoraMuestreoCilindros: EditText

    private lateinit var btnGuardarRegistroCilindros: Button

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


        tvNumeroReporteCilindros = findViewById(R.id.tvNumeroReporteCilindros)
        personal = MainActivity.NombreUsuarioCompanion
        llave=dataReference.push().key.toString()
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
        etElementoColadoCilindros = findViewById(R.id.etElementoColadoCilindros)
        etUbicacionCilindros = findViewById(R.id.etUbicacionCilindros)
        etFCCilindros = findViewById(R.id.etFCCilindros)
        etVolumenCilindros = findViewById(R.id.etVolumenCilindros)
        etTMACilindros = findViewById(R.id.etTMACilindros)
        etConcreteraCilindros = findViewById(R.id.etConcreteraCilindros)
        etHOPropCilindros = findViewById(R.id.etHOPropCilindros)
        etAditivoCilindros = findViewById(R.id.etAditivoCilindros)
        etRemisionCilindros = findViewById(R.id.etRemisionCilindros)
        etRevenimientoProyCilindros = findViewById(R.id.etRevenimientoProyCilindros)
        etReveniminetoObt1Cilindros = findViewById(R.id.etReveniminetoObt1Cilindros)
        etReveniminetoObt2Cilindros = findViewById(R.id.etReveniminetoObt2Cilindros)
        etTemperaturaCilindros = findViewById(R.id.etTemperaturaCilindros)
        etMolde1Cilindros = findViewById(R.id.etMolde1Cilindros)
        etMolde2Cilindros = findViewById(R.id.etMolde2Cilindros)
        etMolde3Cilindros = findViewById(R.id.etMolde3Cilindros)
        etMolde4Cilindros = findViewById(R.id.etMolde4Cilindros)

        etHoraSalidaCilindros = findViewById(R.id.etHoraSalidaCilindros)
        etHorallegadaCilindros = findViewById(R.id.etHorallegadaCilindros)
        etHoraMuestreoCilindros = findViewById(R.id.etHoraMuestreoCilindros)

        btnGuardarRegistroCilindros = findViewById(R.id.btnGuardarRegistroCilindros)

        reporteSelecionado = ReporteCilindros.reporteSelecionado





        FechaDeHoy()
        cargarItemsEstadosCilindros()
        cargarTipoDeMuestreo()
    }

    private fun InitUI() {
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
        btnGuardarRegistroCilindros.setOnClickListener {
            mostrarDialogo()
        }
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
                val fecha: String = etFCCilindros.text.toString()
                val numeroReporte: Int = tvNumeroReporteCilindros.text.toString().toInt()
                val tipoMuestreo:String=spnTipoMuestreoCilindros.selectedItem.toString()

                val elementoColado: String = etElementoColadoCilindros.text.toString()
                val ubicacion: String = etUbicacionCilindros.text.toString()
                val fc: Double = etFCCilindros.text.toString().toDouble()
                val volumenTotal: Double = etVolumenCilindros.text.toString().toDouble()
                val tipoResistencia: String = spnTipoConcretoCilindros.selectedItem.toString()
                val edad: Int = etEdadCilindros.text.toString().toInt()
                val tma: Double = etEdadCilindros.text.toString().toDouble()
                val concretera = etConcreteraCilindros.text.toString()
                val proporciones = etHOPropCilindros.text.toString()
                val aditivo = etAditivoCilindros.text.toString()
                val remision = etRemisionCilindros.text.toString()

                val reveniminetoDis = etRevenimientoProyCilindros.text.toString().toDouble()
                val reveniminetoR1 = etReveniminetoObt1Cilindros.text.toString().toDouble()
                val reveniminetoR2 = etReveniminetoObt2Cilindros.text.toString().toDouble()
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

                var llave = reporteSelecionado.llave


                // Agregar un nuevo registro localmente
                saveLocally(
                    obra,
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
                    reveniminetoDis,
                    reveniminetoR1,
                    reveniminetoR2,
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
                    llave
                )


                // Sincronizar los datos cuando hay conexión a Internet
                syncDataWithFirebase(numeroReporte,  editar)

//                ConsultarUltimoRegistro()

                llave = dataReference.push().key.toString()
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

            reportesReferencia.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

//
//                    val nuevoNumeroReporte = snapshot.childrenCount.toInt()
//                    val totalReportes = tvNumeroReporteCompactacion.text

                    if (accion == true) {
                        // Guardar el registro en Firebase Realtime Database
                        dataReference.child("Cilindros").child("Reportes").child(personal)
                            .child(reporteSelecionado.llave)
                            .setValue(registro)
                        onBackPressed()
                    } else {
                        // Guardar el registro en Firebase Realtime Database
                        registro.llave = llave
                        dataReference.child("Cilindros").child("Reportes").child(personal)
                            .child(llave)
                            .setValue(registro)

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


                }

                override fun onCancelled(error: DatabaseError) {


                }
            })



            // Guardar el registro en Firebase Realtime Database
//            dataReference.child("Reportes").child(nuevaClave!!).setValue(registro)


            // Eliminar el registro local después de la sincronización
            registrosLocales.remove(registro)
        }

        // Limpiar registros locales después de la sincronización
        saveLocalRecords(registrosLocales)
    }

    private fun saveLocally(
        obra: String,
        fecha: String,
        personal: String,
        numeroReporte: Int,
        tipoMuestreo:String,

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

        reveniminetoDis: Double,
        reveniminetoR1: Double,
        reveniminetoR2: Double,
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

        llave: String
    ) {
        // Obtener una lista existente de registros locales o crear una nueva
        val registrosLocales = getLocalRecords()

        // Agregar el nuevo registro a la lista
        val nuevoRegistro = Registro(
            obra,
            fecha,
            personal,
            numeroReporte,
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

            reveniminetoDis,
            reveniminetoR1,
            reveniminetoR2,
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
            llave
        )
        registrosLocales.add(nuevoRegistro)

        // Guardar la lista actualizada localmente
        saveLocalRecords(registrosLocales)
    }

    data class Registro(
        val obra: String,
        val fecha: String,
        val personal: String,
        val numeroReporte: Int,

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

        val reveniminetoDis: Double,
        val reveniminetoR1: Double,
        val reveniminetoR2: Double,
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
}