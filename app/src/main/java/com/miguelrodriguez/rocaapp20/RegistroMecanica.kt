package com.miguelrodriguez.rocaapp20

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.miguelrodriguez.rocaapp20.Recycler.CalasAdapter
import com.miguelrodriguez.rocaapp20.Recycler.ClaseCala
import com.miguelrodriguez.rocaapp20.Recycler.ClaseEstratos
import com.miguelrodriguez.rocaapp20.Recycler.ClaseObra
import com.miguelrodriguez.rocaapp20.Recycler.ClaseObraMecanica
import com.miguelrodriguez.rocaapp20.Recycler.EstratosAdapter
import java.util.Calendar
import kotlin.math.roundToInt

class RegistroMecanica : AppCompatActivity() {
    private lateinit var dataReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences


    private lateinit var spnMuestreo: Spinner
    private lateinit var spnEstudioMuestreo: Spinner
    private lateinit var etObraMuestreoMecanica: EditText
    private lateinit var etFechaMuestreoMecanica: EditText
    private lateinit var etCapaMuestreoMecanica: EditText
    private lateinit var etTramoMuestreoMecanica: EditText
    private lateinit var etSubTramoMuestreoMecanica: EditText
    private lateinit var etProcedenciaMuestreoMecanica: EditText
    private lateinit var etLugarMuestreoMecanica: EditText
    private lateinit var etEstacionMuestreoMecanica: EditText
    private lateinit var fbNuevoEstrato: FloatingActionButton
    private lateinit var btnGuardarRegistroMuestreoMecanica: Button
    private lateinit var btnCancelarRegistroMuestreoMecanica: Button

    private lateinit var llave: String
    private lateinit var tvNumeroReporteMuestreoMecanica: TextView
    private lateinit var reporteSelecionadoMuestroMaterial: ClaseObraMecanica
    private lateinit var personal: String
    private lateinit var reporteSelecionado: ClaseObraMecanica
    private var editar: Boolean = false

    private lateinit var rvMuestreoEstratos: RecyclerView
    private lateinit var EstratosAdapter: EstratosAdapter
    private lateinit var listaEstratosAdapter: MutableList<ClaseEstratos>
    private var listaEstratosmutableListOf: MutableList<ClaseEstratos> = mutableListOf()
    private var listaEstratosOriginal: MutableList<ClaseEstratos> = mutableListOf()
    private lateinit var estratoNuevo: ClaseEstratos


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_muestreo_material)

        InitComponent()
        InitUI()
    }

    private fun InitComponent() {
        listaEstratosOriginal.clear()
        reporteSelecionado = ReportesMuestreoMaterial.reporteSelecionadoMuestroMaterial
        editar = ReportesMuestreoMaterial.editarMuestreoMaterial

        spnMuestreo = findViewById(R.id.spnMuestreo)
        spnEstudioMuestreo = findViewById(R.id.spnEstudioMuestreo)
        etFechaMuestreoMecanica = findViewById(R.id.etFechaMuestreoMecanica)
        etObraMuestreoMecanica = findViewById(R.id.etObraMuestreoMecanica)
        etCapaMuestreoMecanica = findViewById(R.id.etCapaMuestreoMecanica)
        etTramoMuestreoMecanica = findViewById(R.id.etTramoMuestreoMecanica)
        etSubTramoMuestreoMecanica = findViewById(R.id.etSubTramoMuestreoMecanica)
        etProcedenciaMuestreoMecanica = findViewById(R.id.etProcedenciaMuestreoMecanica)
        etLugarMuestreoMecanica = findViewById(R.id.etLugarMuestreoMecanica)
        etEstacionMuestreoMecanica = findViewById(R.id.etEstacionMuestreoMecanica)
        rvMuestreoEstratos = findViewById(R.id.rvMuestreoEstratos)
        fbNuevoEstrato = findViewById(R.id.fbNuevoEstrato)
        btnGuardarRegistroMuestreoMecanica = findViewById(R.id.btnGuardarRegistroMuestreoMecanica)
        btnCancelarRegistroMuestreoMecanica = findViewById(R.id.btnCancelarRegistroMuestreoMecanica)
        tvNumeroReporteMuestreoMecanica = findViewById(R.id.tvNumeroReporteMuestreoMecanica)

        personal = MainActivity.NombreUsuarioCompanion
        editar = ReportesMuestreoMaterial.editarMuestreoMaterial

        dataReference = FirebaseDatabase.getInstance().reference
        sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        reporteSelecionadoMuestroMaterial =
            ReportesMuestreoMaterial.reporteSelecionadoMuestroMaterial


        if (editar == true) {
            Toast.makeText(this, ReportesMuestreoMaterial.reporteSelecionadoMuestroMaterial.listaEstratos.count().toString(), Toast.LENGTH_SHORT).show()
            cargarObraSeleccionada(reporteSelecionado)
        }



    }

    private fun cargarObraSeleccionada(reporteSelecionado: ClaseObraMecanica) {

        tvNumeroReporteMuestreoMecanica.setText(reporteSelecionado.id.toString())
        etObraMuestreoMecanica.setText(reporteSelecionado.Obra)
        etFechaMuestreoMecanica.setText(reporteSelecionado.fecha)
        etCapaMuestreoMecanica.setText(reporteSelecionado.capa)
        etTramoMuestreoMecanica.setText(reporteSelecionado.tramo)
        etSubTramoMuestreoMecanica.setText(reporteSelecionado.subtramo)
        etProcedenciaMuestreoMecanica.setText(reporteSelecionado.procedencia)
        etEstacionMuestreoMecanica.setText(reporteSelecionado.estacion)
        llave = reporteSelecionado.llave
//        spnMuestreo.setText(reporteSelecionado.tipoMuestreo)
//        spnEstudioMuestreo.setText(reporteSelecionado.estudioMuestreo)
        listaEstratosmutableListOf = reporteSelecionado.listaEstratos

        EstratosAdapter =
            EstratosAdapter(reporteSelecionado.listaEstratos,
                onEstratoSelected = { position -> onEstratoSelected(position) },
                onItemDelete = { position -> onItemDelete(position) })
        rvMuestreoEstratos.layoutManager = LinearLayoutManager(this)
        rvMuestreoEstratos.adapter = EstratosAdapter

    }

    private fun InitUI() {
//        cargarItemsMuestreo()

        btnCancelarRegistroMuestreoMecanica.setOnClickListener {

            restaurarDatosOriginales()
            ReportesCompactaciones.editar = false


            onBackPressed()

        }
        spnMuestreo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                cargarItemsEstudioMuestreo(spnMuestreo.selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No se utiliza en este ejemplo
            }
        }
        btnGuardarRegistroMuestreoMecanica.setOnClickListener {
            mostrarDialogo()
        }

        fbNuevoEstrato.setOnClickListener { showDialog() }

        EstratosAdapter = EstratosAdapter(listaEstratosmutableListOf,
            onEstratoSelected = { position -> onEstratoSelected(position) },
            onItemDelete = { position -> onItemDelete(position) })
        rvMuestreoEstratos.layoutManager = LinearLayoutManager(this)
        rvMuestreoEstratos.adapter = EstratosAdapter

        llave = dataReference.push().key.toString()

        listaEstratosOriginal.addAll(listaEstratosmutableListOf)

        listaEstratosOriginal.forEachIndexed { index, elemento ->
            // Puedes realizar alguna lógica para determinar la nueva numeración
            val nuevaNumeracion = index  // Sumar 1 para empezar desde 1, si es necesario

            // Reemplazar la numeración en cada objeto
            elemento.idEstrato = nuevaNumeracion
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
        if (editar==true){restaurarDatosOriginales()}
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
                val fecha: String = etFechaMuestreoMecanica.text.toString()
                val numeroReporte: Int = tvNumeroReporteMuestreoMecanica.text.toString().toInt()
                val capa: String = etCapaMuestreoMecanica.text.toString()
                val Tramo: String = etTramoMuestreoMecanica.text.toString()
                val subTramo: String = etSubTramoMuestreoMecanica.text.toString()
                val procedencia: String = etProcedenciaMuestreoMecanica.text.toString()
                val lugarMuestreo: String = etLugarMuestreoMecanica.text.toString()
                val estacion: String = etEstacionMuestreoMecanica.text.toString()
                val tipoMuestreo: String = spnMuestreo.selectedItem.toString()
                val estudioMuestreo: String = spnEstudioMuestreo.selectedItem.toString()
                var llave = reporteSelecionadoMuestroMaterial.llave


                // Agregar un nuevo registro localmente
                saveLocally(
                    obra,
                    fecha,
                    personal,
                    numeroReporte,
                    listaEstratosmutableListOf,
                    capa,
                    Tramo,
                    subTramo,
                    procedencia,
                    lugarMuestreo,
                    estacion,
                    llave,
                    tipoMuestreo,
                    estudioMuestreo
                )


                // Sincronizar los datos cuando hay conexión a Internet
                syncDataWithFirebase(numeroReporte, listaEstratosmutableListOf, editar)

//                ConsultarUltimoRegistro()
                listaEstratosmutableListOf.clear()
                updateTask()
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
                        dataReference.child("ReportesMecanicas").child(personal)
                            .child(reporteSelecionadoMuestroMaterial.llave)
                            .setValue(registro)
                        onBackPressed()
                    } else {
                        // Guardar el registro en Firebase Realtime Database
                        registro.llave = llave
                        dataReference.child("ReportesMecanicas").child(personal)
                            .child(llave)
                            .setValue(registro)

                    }
                    if (accion == true) {
                        // Guardar el registro en Firebase Realtime Database
                        dataReference.child("RespaldoMecanicas").child(personal)
                            .child(reporteSelecionadoMuestroMaterial.llave)
                            .setValue(registro)
                        onBackPressed()
                    } else {
                        // Guardar el registro en Firebase Realtime Database
                        registro.llave = llave
                        dataReference.child("RespaldoMecanicas").child(personal)
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

    private fun saveLocalRecords(registros: List<Registro>) {

        val registrosJson = Gson().toJson(registros)
        sharedPreferences.edit().putString("registros", registrosJson).apply()
    }

    private fun saveLocally(
        obra: String,
        fecha: String,
        personal: String,
        numeroReporte: Int,
        listaEstratos: List<ClaseEstratos>,
        capa: String,
        Tramo: String,
        subTramo: String,
        procendecia: String,
        lugarMuestreo: String,
        estacion: String,
        llave: String,
        tipoMuestreo: String,
        estudioMuestreo: String


    ) {
        // Obtener una lista existente de registros locales o crear una nueva
        val registrosLocales = getLocalRecords()

        // Agregar el nuevo registro a la lista
        val nuevoRegistro = Registro(
            obra,
            fecha,
            personal,
            numeroReporte,
            capa,
            Tramo,
            subTramo,
            procendecia,
            lugarMuestreo,
            estacion,
            llave,
            tipoMuestreo,
            estudioMuestreo,
            listaEstratos
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

        val etNombreEstrato: EditText =
            dialog.findViewById(R.id.etNombreEstrato)
        val etEspesorEstrato: EditText = dialog.findViewById(R.id.etEspesorEstrato)



        btnGuardarEstrato.setOnClickListener {

            try {
                if (etNombreEstrato.text == null || etEspesorEstrato == null) {
                    return@setOnClickListener
                }
                val Nombre = etNombreEstrato.text.toString()
                val Espesor = etEspesorEstrato.text.toString().toDouble()


                estratoNuevo = ClaseEstratos(
                    listaEstratosmutableListOf.count(),
                    Nombre,
                    Espesor
                )
                listaEstratosmutableListOf.add(estratoNuevo)

//                llave=dataReference.push().key.toString()

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

    private fun onEstratoSelected(position: Int) {


    }

    private fun onItemDelete(position: Int) {
//        if (isNetworkAvailable()) {
//            val reportKey = listaEstratosmutableListOf[position].llave // Utiliza la clave única del informe
//
//            // Elimina el informe de la base de datos Firebase
//            deleteReport(reportKey)
//
//            // Elimina el informe de la lista local
//            listaEstratosmutableListOf.removeAt(position)
//
//            // Notifica al adaptador que los datos han cambiado
//            updateTask()
//        } else {
//            Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
//        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ?: false
    }

    //    private fun deleteReport(reportKey: String) {
//        val reportReference = dataReference.child(reportKey)
//
//        reportReference.removeValue()
//            .addOnSuccessListener {
//                Toast.makeText(this, "Informe eliminado exitosamente", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(
//                    this,
//                    "Error al eliminar el informe: ${e.message}",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//    }
    private fun updateTask() {
        EstratosAdapter.notifyDataSetChanged()
    }


    private fun cargarItemsMuestreo() {
        val itemMuestreo = arrayOf("Terracería", "Asfalto", "Acero", "Prefabricado")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, itemMuestreo)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnMuestreo.adapter = adapter
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

    private fun cargarItemsEstudioMuestreo(selectedOption: String) {
        // Handle different options as needed
        when (selectedOption) {
            "Terracería" -> {
                // Load items specific to "Terracería"
                val items = arrayOf(
                    "Base Hidráulica",
                    "Sub Base",
                    "Sub Rasante",
                    "Sub Yacente",
                    "Terraplen",
                    "Terreno Natural",
                    "Para Identificación"
                )
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnEstudioMuestreo.adapter = adapter
            }

            "Asfalto" -> {
                val items = arrayOf(
                    "Carpeta Asf.",
                    "Base Negra",
                    "Peso Vol.",
                    "Agregados",
                    "Sello",
                    "Emulsión",
                    "Otro"
                )
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnEstudioMuestreo.adapter = adapter
            }

            "Prefabricado" -> {
                val items = arrayOf(
                    "Compresión",
                    "Densidad",
                    "Absorción",
                    "Permeabilidad",
                    "Otro"
                )
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnEstudioMuestreo.adapter = adapter
            }

            "Acero" -> {
                val items = arrayOf(
                    "Tensión",
                    "Doblado",
                    "Otro"
                )
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnEstudioMuestreo.adapter = adapter
            }
            // Add cases for other options as needed
            else -> {
                // Default case or handle other options
            }
        }
    }

    data class Registro(
        val obra: String,
        val fecha: String,
        val personal: String,
        val numeroReporte: Int,
        val capa: String,
        val Tramo: String,
        val subTramo: String,
        val procedencia: String,
        val LugarMuestreo: String,
        val estacion: String,
        var llave: String,
        var tipoMuestreo: String,
        var estudioMuestreo: String,
        val listaEstratos: List<ClaseEstratos>
    )
}
