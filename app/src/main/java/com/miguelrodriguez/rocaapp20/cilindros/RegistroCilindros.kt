package com.miguelrodriguez.rocaapp20.cilindros

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.miguelrodriguez.rocaapp20.R

class RegistroCilindros : AppCompatActivity() {

    private lateinit var spnMolde1: Spinner
    private lateinit var spnMolde2: Spinner
    private lateinit var spnMolde3: Spinner
    private lateinit var spnMolde4: Spinner
    private lateinit var spnTipoMuestreoCilindros: Spinner
    private lateinit var spnTipoConcretoCilindros: Spinner






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
}