package com.miguelrodriguez.rocaapp20.cilindros

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import com.miguelrodriguez.rocaapp20.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegistroCilindros : AppCompatActivity() {

    private lateinit var spnMolde1: Spinner
    private lateinit var spnMolde2: Spinner
    private lateinit var spnMolde3: Spinner
    private lateinit var spnMolde4: Spinner
    private lateinit var spnTipoMuestreoCilindros: Spinner
    private lateinit var spnTipoConcretoCilindros: Spinner
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
    private lateinit var etRevDiseñoCilindros: EditText
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







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_cilindros)

        InitComponent()
        InitUI()

    }

    private fun InitComponent() {
        spnMolde1 = findViewById(R.id.spnMolde1)
        spnMolde2 = findViewById(R.id.spnMolde2)
        spnMolde3 = findViewById(R.id.spnMolde3)
        spnMolde4 = findViewById(R.id.spnMolde4)
        spnTipoMuestreoCilindros = findViewById(R.id.spnTipoMuestreoCilindros)
        spnTipoConcretoCilindros = findViewById(R.id.spnTipoConcretoCilindros)
        etFechaCompactacion=findViewById(R.id.etFechaCompactacion)
        etEdadCilindros=findViewById(R.id.etEdadCilindros)

        etObraCilindros=findViewById(R.id.etObraCilindros)
        etElementoColadoCilindros=findViewById(R.id.etElementoColadoCilindros)
        etUbicacionCilindros=findViewById(R.id.etUbicacionCilindros)
        etFCCilindros=findViewById(R.id.etFCCilindros)
        etVolumenCilindros=findViewById(R.id.etVolumenCilindros)
        etTMACilindros=findViewById(R.id.etTMACilindros)
        etConcreteraCilindros=findViewById(R.id.etConcreteraCilindros)
        etHOPropCilindros=findViewById(R.id.etHOPropCilindros)
        etAditivoCilindros=findViewById(R.id.etAditivoCilindros)
        etRemisionCilindros=findViewById(R.id.etRemisionCilindros)
        etReveniminetoObt1Cilindros=findViewById(R.id.etReveniminetoObt1Cilindros)
        etReveniminetoObt2Cilindros=findViewById(R.id.etReveniminetoObt2Cilindros)
        etTemperaturaCilindros=findViewById(R.id.etTemperaturaCilindros)
        etMolde1Cilindros=findViewById(R.id.etMolde1Cilindros)
        etMolde2Cilindros=findViewById(R.id.etMolde2Cilindros)
        etMolde3Cilindros=findViewById(R.id.etMolde3Cilindros)
        etMolde4Cilindros=findViewById(R.id.etMolde4Cilindros)

        etHoraSalidaCilindros=findViewById(R.id.etHoraSalidaCilindros)
        etHorallegadaCilindros=findViewById(R.id.etHorallegadaCilindros)
        etHoraMuestreoCilindros=findViewById(R.id.etHoraMuestreoCilindros)





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
        spnTipoConcretoCilindros.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
    }
    private fun cargarItemsTipoDeMuestreo(spnTipoConcretoCilindros:String){
        if (spnTipoConcretoCilindros=="Resistencia Normal")
        {etEdadCilindros.setText("28")}
        else{etEdadCilindros.setText("7")}
    }
    private fun FechaDeHoy() {
        val calendario = Calendar.getInstance()
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        etFechaCompactacion.setText(formatoFecha.format(calendario.time))
    }

    private fun cargarItemsEstadosCilindros() {
        val itemMuestreo = arrayOf("Bien", "Mal","---")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, itemMuestreo)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnMolde1.adapter = adapter
        spnMolde2.adapter = adapter
        spnMolde3.adapter = adapter
        spnMolde4.adapter = adapter

        val itemMuestreo1 = arrayOf(
            "Resistencia Normal",
            "Resistencia Rápida")
        val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, itemMuestreo1)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnTipoConcretoCilindros.adapter = adapter1
    }
    private fun cargarTipoDeMuestreo() {
        val itemMuestreo = arrayOf(
            "Olla de camión mezclador o agitador",
            "Mezcladora estacionarias (fijas y basculantes)",
            "Camión caja con o sin agitadores",
            "Camión de volteo","Pavimentadora",
            "Otro")
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