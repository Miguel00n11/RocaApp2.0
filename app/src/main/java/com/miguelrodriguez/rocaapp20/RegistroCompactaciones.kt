package com.miguelrodriguez.rocaapp20

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import java.util.Calendar

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.miguelrodriguez.rocaapp20.Recycler.ClaseCala
import java.util.Objects

class RegistroCompactaciones : AppCompatActivity() {

    private lateinit var dataReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var etObra: EditText
    private lateinit var etFecha: EditText
    private lateinit var etCapa: EditText
    private lateinit var etTramo: EditText

    private lateinit var cala2: ClaseCala

    private lateinit var tvNumeroReporteCompactacion: TextView

    private lateinit var btnCancelar: Button
    private lateinit var btnGuardar: Button
    private lateinit var btnVerCalendarioCompactaciones: Button

    private lateinit var personal: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_compactaciones)

        initComponet()


        initUI()

        personal = MainActivity.NombreUsuarioCompanion
        ConsultarUltimoRegistro()


    }

    private fun saveLocally(obra: String, fecha: String, personal: String, numeroReporte: Int) {
        // Obtener una lista existente de registros locales o crear una nueva
        val registrosLocales = getLocalRecords()

        // Agregar el nuevo registro a la lista
        val nuevoRegistro = Registro(obra, fecha, personal, numeroReporte)
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

    private fun syncDataWithFirebase(numeroReporte: Int) {
        // Verificar si hay conexión a Internet
        // Puedes usar una biblioteca como Connectivity Manager para esto

        // Obtener registros locales
        val registrosLocales = getLocalRecords()

        // Sincronizar cada registro con Firebase Realtime Database
        for (registro in registrosLocales) {

            // Generar una nueva clave única para cada registro
            val nuevaClave = dataReference.push().key


            // Guardar el registro en Firebase Realtime Database
            dataReference.child(personal).child(nuevaClave!!).setValue(registro)

            // Eliminar el registro local después de la sincronización
            registrosLocales.remove(registro)
        }

        // Limpiar registros locales después de la sincronización
        saveLocalRecords(registrosLocales)
    }


    data class Registro(
        val obra: String, val fecha: String, val personal: String, val numeroReporte: Int
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

    }

    fun mostrarCalendario(view: View) {
        val calendario = Calendar.getInstance()
        val año = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH)
        val dia = calendario.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this, { _, year, month, dayOfMonth ->
                // Manejar la fecha seleccionada, por ejemplo, puedes mostrarla en el EditText
                val fechaSeleccionada = "$dayOfMonth/${month + 1}/$year"
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
        etObra = findViewById(R.id.etObraCompactacion)
        etFecha = findViewById(R.id.etFechaCompactacion)
        etCapa = findViewById(R.id.etCapaCompactacion)
        etTramo = findViewById(R.id.etTramoCompactacion)



        tvNumeroReporteCompactacion = findViewById(R.id.tvNumeroReporteCompactacion)

        btnCancelar = findViewById(R.id.btnCancelarRegistroCompactacion)
        btnGuardar = findViewById(R.id.btnGuardarRegistroCompactacion)
        btnVerCalendarioCompactaciones = findViewById(R.id.btnVerCalendarioCompactaciones)


    }

    private fun mostrarDialogo() {
        // Crea un objeto AlertDialog
        val builder = AlertDialog.Builder(this)

        // Configura el título y el mensaje del cuadro de diálogo
        builder.setTitle("Confirmación")
        builder.setMessage("¿Quieres proceder?")

        // Configura el botón positivo (sí)
        builder.setPositiveButton("Sí") { dialog, which ->

            var obra: String = etObra.text.toString()
            var fecha: String = etFecha.text.toString()
            var numeroReporte: Int = tvNumeroReporteCompactacion.text.toString().toInt()
//            var cala1: ClaseCala=cala2



            // Agregar un nuevo registro localmente
            saveLocally(obra, fecha, personal, numeroReporte )

            // Sincronizar los datos cuando hay conexión a Internet
            syncDataWithFirebase(numeroReporte)

            ConsultarUltimoRegistro()

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