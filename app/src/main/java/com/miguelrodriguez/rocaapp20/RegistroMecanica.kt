package com.miguelrodriguez.rocaapp20

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import java.util.Calendar

class RegistroMecanica : AppCompatActivity() {

    private lateinit var spnMuestreo: Spinner
    private lateinit var spnEstudioMuestreo: Spinner
    private lateinit var etFechaMuestreoMecanica: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_muestreo_material)

        InitComponent()
        InitUI()
    }

    private fun InitComponent() {
        spnMuestreo = findViewById(R.id.spnMuestreo)
        spnEstudioMuestreo = findViewById(R.id.spnEstudioMuestreo)
        etFechaMuestreoMecanica = findViewById(R.id.etFechaMuestreoMecanica)
    }

    private fun InitUI() {
        cargarItemsMuestreo()

        spnMuestreo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                cargarItemsEstudioMuestreo(spnMuestreo.selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No se utiliza en este ejemplo
            }
        }
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
                    "Otro")
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
                    "Otro")
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnEstudioMuestreo.adapter = adapter
            }
            "Acero" -> {
                val items = arrayOf(
                    "Tensión",
                    "Doblado",
                    "Otro")
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
}