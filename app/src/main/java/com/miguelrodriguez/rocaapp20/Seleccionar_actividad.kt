package com.miguelrodriguez.rocaapp20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.miguelrodriguez.rocaapp20.cilindros.ReporteCilindros

class Seleccionar_actividad : AppCompatActivity() {

    private lateinit var btnIrReportesCompactacion:Button
    private lateinit var btnItReportesMecanicas:Button
    private lateinit var btnIrReportesCilindros: Button
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
        btnItReportesMecanicas.setOnClickListener {
            val intent=Intent(this,ReportesMuestreoMaterial::class.java)
            startActivity(intent)
        }
        btnIrReportesCilindros.setOnClickListener {
            val intent=Intent(this,ReporteCilindros::class.java)
            startActivity(intent)
        }
    }

    private fun InitComponet() {
        if (MainActivity.NombreUsuarioCompanion=="NombreUsuario"){
            onBackPressed()
        }
        btnIrReportesCompactacion=findViewById(R.id.btnIrReportesCompactacion)
        btnItReportesMecanicas=findViewById(R.id.btnItReportesMecanicas)
        btnIrReportesCilindros=findViewById(R.id.btnIrReportesCilindros)

    }
}