package com.miguelrodriguez.rocaapp20

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import java.util.Calendar

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.miguelrodriguez.rocaapp20.Recycler.CalasAdapter
import com.miguelrodriguez.rocaapp20.Recycler.ClaseCala
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Objects
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class RegistroCompactaciones : AppCompatActivity() {


    private val listaCalasmutableListOf =
        mutableListOf(ClaseCala(1, "estacion", 1.0, 1.0, 1.0, 1.0))


    private lateinit var dataReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var etObra: EditText
    private lateinit var etFecha: EditText

    private lateinit var etCapa: EditText
    private lateinit var etTramo: EditText
    private lateinit var etSubTramo: EditText
    private lateinit var etcompactacionProyecto: EditText
    private lateinit var etMVSM: EditText
    private lateinit var etHumedad: EditText

    private lateinit var CalasAdapter: CalasAdapter
    private lateinit var rvCalas: RecyclerView


    private lateinit var tvNumeroReporteCompactacion: TextView

    private lateinit var btnCancelar: Button
    private lateinit var btnGuardar: Button
    private lateinit var btnVerCalendarioCompactaciones: Button

    private lateinit var fbNuevaCalaCompactacion: FloatingActionButton

    //    controladores del item Cala
    private lateinit var tvIdCalaCompactacion: TextView
    private lateinit var tvEstacionCalaCompactacion: TextView
    private lateinit var tvProfundidadCalaCompactacion: TextView
    private lateinit var tvMVSLCalaCompactacion: TextView
    private lateinit var tvHumedadCalaCompactacion: TextView
    private lateinit var tvPorcentajeCalaCompactacion: TextView
    
//    private lateinit var btnEliminarCalaCompactaciones: FloatingActionButton


    private lateinit var personal: String

    private lateinit var calaNueva: ClaseCala


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_compactaciones)

        listaCalasmutableListOf.clear()


        initComponet()

        initUI()

        personal = MainActivity.NombreUsuarioCompanion
        ConsultarUltimoRegistro()

    }

    private fun updateTask() {
        CalasAdapter.notifyDataSetChanged()
    }

    private fun saveLocally(
        obra: String,
        fecha: String,
        personal: String,
        numeroReporte: Int,
        listaCalas: List<ClaseCala>
    ) {
        // Obtener una lista existente de registros locales o crear una nueva
        val registrosLocales = getLocalRecords()

        // Agregar el nuevo registro a la lista
        val nuevoRegistro = Registro(obra, fecha, personal, numeroReporte, listaCalas)
        registrosLocales.add(nuevoRegistro)

        // Guardar la lista actualizada localmente
        saveLocalRecords(registrosLocales)
    }


    private fun getLocalRecords(): MutableList<Registro> {
        val registrosJson = sharedPreferences.getString("registros", "[]")
        return Gson().fromJson(registrosJson, object : TypeToken<MutableList<Registro>>() {}.type)
            ?: mutableListOf()
    }

    private fun saveLocalRecords(registros: List<Registro>) {
        val registrosJson = Gson().toJson(registros)
        sharedPreferences.edit().putString("registros", registrosJson).apply()
    }

    private fun syncDataWithFirebase(numeroReporte: Int, listaCalas: List<ClaseCala>) {
        // Verificar si hay conexión a Internet
        // Puedes usar una biblioteca como Connectivity Manager para esto

        // Obtener registros locales
        val registrosLocales = getLocalRecords()

        // Sincronizar cada registro con Firebase Realtime Database
        for (registro in registrosLocales) {

            // Generar una nueva clave única para cada registro
            val nuevaClave = dataReference.push().key


            // Guardar el registro en Firebase Realtime Database
            dataReference.child(personal).child(etObra.text.toString()).child(nuevaClave!!).setValue(registro)

            // Eliminar el registro local después de la sincronización
            registrosLocales.remove(registro)
        }

        // Limpiar registros locales después de la sincronización
        saveLocalRecords(registrosLocales)
    }

    data class Registro(
        val obra: String,
        val fecha: String,
        val personal: String,
        val numeroReporte: Int,
        val listaCalas: List<ClaseCala>
    )


    private fun ConsultarUltimoRegistro() {

// Obtén la referencia de la base de datos
        dataReference = FirebaseDatabase.getInstance().reference
        sharedPreferences = getPreferences(Context.MODE_PRIVATE)

        // Agrega un listener para contar el total de registros
        dataReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Obtiene el número total de registros
                tvNumeroReporteCompactacion.text =
                    (dataSnapshot.child(personal).childrenCount + 1).toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Error al leer la base de datos: ${databaseError.message}")
            }


        })

    }


    private fun initUI() {


        btnCancelar.setOnClickListener { onBackPressed() }
        btnGuardar.setOnClickListener {

            mostrarDialogo()


        }
        fbNuevaCalaCompactacion.setOnClickListener { showDialog() }

        CalasAdapter =
            CalasAdapter(listaCalasmutableListOf,
                onCalaSelected = { position -> onItemSelected(position) },
                onItemDelete = { position -> onItemDelete(position) })
        rvCalas.layoutManager = LinearLayoutManager(this)
        rvCalas.adapter = CalasAdapter

    }

    private fun onItemSelected(position: Int) {
//        Toast.makeText(this, position.toString(), Toast.LENGTH_SHORT).show()

        showDialog(listaCalasmutableListOf[position],position)

    }
    private fun onItemDelete(position: Int) {
        listaCalasmutableListOf.removeAt(position)
        updateTask()
    }
    private fun showDialog(calaSeleccionada:ClaseCala,indice:Int) {



        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_nueva_cala_compactacion)

        val btnGuardarCalaCompactacion: Button =
            dialog.findViewById(R.id.btnGuardarCalaCompactacion)

        val etEstacionCalaCompactacion: EditText =
            dialog.findViewById(R.id.etEstacionCalaCompactacion)
        val etProfCalaCompactacion: EditText = dialog.findViewById(R.id.etProfCalaCompactacion)
        val etMVSLCalaCompactacion: EditText = dialog.findViewById(R.id.etMVSLCalaCompactacion)
        val etHumedadLugarCalaCopactacion: EditText =
            dialog.findViewById(R.id.etHumedadLugarCalaCopactacion)

        etEstacionCalaCompactacion.setText(calaSeleccionada.Estacion)
        etProfCalaCompactacion.setText(calaSeleccionada.Profundidad.toString())
        etMVSLCalaCompactacion.setText(calaSeleccionada.MVSL.toString())
        etHumedadLugarCalaCopactacion.setText(calaSeleccionada.Humedad.toString())

        btnGuardarCalaCompactacion.setOnClickListener {

            val estacion = etEstacionCalaCompactacion.text.toString()
            val profundidad = etProfCalaCompactacion.text.toString().toDouble()
            val MSVL = etMVSLCalaCompactacion.text.toString().toDouble()
            val humedad = etHumedadLugarCalaCopactacion.text.toString().toDouble()
            val porcentajeCompactacion =
                (MSVL / etMVSM.text.toString().toDouble() * 100.0 * 100).roundToInt() / 100.0

            if (estacion.isEmpty()) {
                dialog.hide()

                return@setOnClickListener

            }

            calaNueva = ClaseCala(
                indice+1,
                estacion,
                profundidad,
                MSVL,
                humedad,
                porcentajeCompactacion
            )
            listaCalasmutableListOf.set(indice,calaNueva)


            updateTask()

            dialog.hide()

        }

        dialog.show()

    }


    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_nueva_cala_compactacion)

        val btnGuardarCalaCompactacion: Button =
            dialog.findViewById(R.id.btnGuardarCalaCompactacion)

        val etEstacionCalaCompactacion: EditText =
            dialog.findViewById(R.id.etEstacionCalaCompactacion)
        val etProfCalaCompactacion: EditText = dialog.findViewById(R.id.etProfCalaCompactacion)
        val etMVSLCalaCompactacion: EditText = dialog.findViewById(R.id.etMVSLCalaCompactacion)
        val etHumedadLugarCalaCopactacion: EditText =
            dialog.findViewById(R.id.etHumedadLugarCalaCopactacion)


        btnGuardarCalaCompactacion.setOnClickListener {

            val estacion = etEstacionCalaCompactacion.text.toString()
            val profundidad = etProfCalaCompactacion.text.toString().toDouble()
            val MSVL = etMVSLCalaCompactacion.text.toString().toDouble()
            val humedad = etHumedadLugarCalaCopactacion.text.toString().toDouble()
            val porcentajeCompactacion =
                (MSVL / etMVSM.text.toString().toDouble() * 100.0 * 100).roundToInt() / 100.0

            if (estacion.isEmpty()) {
                dialog.hide()

                return@setOnClickListener

            }

            calaNueva = ClaseCala(
                listaCalasmutableListOf.count() + 1,
                estacion,
                profundidad,
                MSVL,
                humedad,
                porcentajeCompactacion
            )
            listaCalasmutableListOf.add(calaNueva)


            updateTask()

            dialog.hide()

        }

        dialog.show()

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
                etFecha.setText(fechaSeleccionada)
            }, año, mes, dia
        )

        datePickerDialog.show()
    }

    private fun GuardarCompactacion(
        obra: String, fecha: String, capa: String, tramo: String, personal: String
    ) {
        // Obtén la referencia de la base de datos
        dataReference = FirebaseDatabase.getInstance().reference

        // Crear un objeto con los datos que deseas almacenar
        val data = HashMap<String, Any>()
        data["Tramo"] = tramo
        data["Fecha"] = fecha
        data["Capa"] = capa

        // Generar un nuevo nodo único en la base de datos
        val newChildRef =
            dataReference.child(personal).child(tvNumeroReporteCompactacion.text.toString())
        newChildRef.setValue(data)

    }

    private fun initComponet() {
        //REPORTE DE COMPACTACION
        etObra = findViewById(R.id.etObraCompactacion)
        etFecha = findViewById(R.id.etFechaCompactacion)

        FechaDeHoy()

        //DATOS DE LA PRUEBA
        etCapa = findViewById(R.id.etCapaCompactacion)
        etTramo = findViewById(R.id.etTramoCompactacion)
        etSubTramo = findViewById(R.id.etSubTramo)
        etcompactacionProyecto = findViewById(R.id.etcompactacionProyecto)
        etMVSM = findViewById(R.id.etMVSM)
        etHumedad = findViewById(R.id.etHumedad)
        tvNumeroReporteCompactacion = findViewById(R.id.tvNumeroReporteCompactacion)


        //REGISTRO DE CALAS
        rvCalas = findViewById(R.id.rvCalasCompactaciones)

        //BOTONES
        btnCancelar = findViewById(R.id.btnCancelarRegistroCompactacion)
        btnGuardar = findViewById(R.id.btnGuardarRegistroCompactacion)
        btnVerCalendarioCompactaciones = findViewById(R.id.btnVerCalendarioCompactaciones)
        fbNuevaCalaCompactacion = findViewById(R.id.fbNuevaCalaCompactacion)


    }

    private fun FechaDeHoy() {
        val calendario = Calendar.getInstance()
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        etFecha.setText(formatoFecha.format(calendario.time))
    }

    private fun mostrarDialogo() {
        // Crea un objeto AlertDialog
        val builder = AlertDialog.Builder(this)

        // Configura el título y el mensaje del cuadro de diálogo
        builder.setTitle("Confirmación")
        builder.setMessage("¿Deseas guardar este reporte?")

        // Configura el botón positivo (sí)
        builder.setPositiveButton("Sí") { dialog, which ->

            var obra: String = etObra.text.toString()
            var fecha: String = etFecha.text.toString()
            var numeroReporte: Int = tvNumeroReporteCompactacion.text.toString().toInt()

            // Agregar un nuevo registro localmente
            saveLocally(obra, fecha, personal, numeroReporte, listaCalasmutableListOf)

            // Sincronizar los datos cuando hay conexión a Internet
            syncDataWithFirebase(numeroReporte, listaCalasmutableListOf)

            ConsultarUltimoRegistro()
            listaCalasmutableListOf.clear()
            updateTask()
            Toast.makeText(this, "Reporte guardado correctamente.", Toast.LENGTH_LONG).show()

        }

        // Configura el botón negativo (no)
        builder.setNegativeButton("No") { dialog, which ->
            return@setNegativeButton
            // Código a ejecutar si el usuario hace clic en No
        }

        // Muestra el cuadro de diálogo
        builder.show()
    }


}