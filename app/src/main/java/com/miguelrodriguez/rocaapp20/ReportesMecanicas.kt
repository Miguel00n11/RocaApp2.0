package com.miguelrodriguez.rocaapp20

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ReportesMecanicas : AppCompatActivity() {
    private lateinit var btnItReportesMecanicas:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes_mecanicas)


        InitComponet()
        InitUI()
    }

    private fun InitComponet() {
//        btnItReportesMecanicas=findViewById(R.id.btnItReportesMecanicas)
    }

    private fun InitUI() {
//        btnItReportesMecanicas.setOnClickListener {  }
    }

}