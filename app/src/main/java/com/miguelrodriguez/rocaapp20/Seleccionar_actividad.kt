package com.miguelrodriguez.rocaapp20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.miguelrodriguez.rocaapp20.cilindros.ReporteCilindros
import com.miguelrodriguez.rocaapp20.vigas.ReportesVigas

class Seleccionar_actividad : AppCompatActivity() {

    private lateinit var btnIrReportesCompactacion:CardView
    private lateinit var btnItReportesMecanicas:CardView
    private lateinit var btnIrReportesCilindros: CardView
    private lateinit var btnIrReportesVigas: CardView
    private lateinit var btnBorrar1: CardView
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
            Toast.makeText(this, "Aún no disponible", Toast.LENGTH_SHORT).show()
        }
        btnIrReportesCilindros.setOnClickListener {
            val intent=Intent(this,ReporteCilindros::class.java)
            startActivity(intent)
        }
        btnIrReportesVigas.setOnClickListener {
            val intent=Intent(this,ReportesVigas::class.java)

            startActivity(intent)
        }
//        btnBorrar1.setOnClickListener { Toast.makeText(this, "hola mundo", Toast.LENGTH_SHORT).show() }
    }

    private fun InitComponet() {
        if (MainActivity.NombreUsuarioCompanion=="NombreUsuario"){
            onBackPressed()
        }
        btnIrReportesCompactacion=findViewById(R.id.btnIrReportesCompactacion)
        btnItReportesMecanicas=findViewById(R.id.btnItReportesMecanicas)
        btnIrReportesCilindros=findViewById(R.id.btnIrReportesCilindros)
        btnIrReportesVigas=findViewById(R.id.btnIrReportesVigas)
//        btnBorrar1=findViewById(R.id.prueba1)


    }
}