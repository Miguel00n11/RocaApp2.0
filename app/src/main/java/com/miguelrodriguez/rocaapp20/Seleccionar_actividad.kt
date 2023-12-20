package com.miguelrodriguez.rocaapp20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Seleccionar_actividad : AppCompatActivity() {

    private lateinit var btnIrReportesCompactacion:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccionar_actividad)
        InitComponet()
        InitUI()
    }

    private fun InitUI() {
        btnIrReportesCompactacion.setOnClickListener {
            val intent=Intent(this,ReportesCompactaciones::class.java)
            startActivity(intent)

        }
    }

    private fun InitComponet() {
        btnIrReportesCompactacion=findViewById(R.id.btnIrReportesCompactacion)
    }
}