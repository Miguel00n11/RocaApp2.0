package com.miguelrodriguez.rocaapp20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.miguelrodriguez.rocaapp20.cilindros.ReporteCilindros
import com.miguelrodriguez.rocaapp20.mecanicas.ReportesMuestreoMaterial
import com.miguelrodriguez.rocaapp20.morteros.ReportesMorteros
import com.miguelrodriguez.rocaapp20.vigas.ReportesVigas

class Seleccionar_actividad : AppCompatActivity() {

    private lateinit var btnIrReportesCompactacion:CardView
    private lateinit var btnItReportesMorteros:CardView
    private lateinit var btnIrReportesCilindros: CardView
    private lateinit var btnIrReportesVigas: CardView
    private lateinit var btnIrReportesMecanicas: CardView
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
        btnItReportesMorteros.setOnClickListener {
            Toast.makeText(this, "Aún no disponible", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
            val intent=Intent(this,ReportesMorteros::class.java)
            startActivity(intent)
        }
        btnIrReportesCilindros.setOnClickListener {
            val intent=Intent(this,ReporteCilindros::class.java)
            startActivity(intent)
        }
        btnIrReportesVigas.setOnClickListener {
            val intent=Intent(this,ReportesVigas::class.java)

            startActivity(intent)
        }
        btnIrReportesMecanicas.setOnClickListener {
            val intent=Intent(this,ReportesMuestreoMaterial::class.java)

            startActivity(intent)
        }


//        btnBorrar1.setOnClickListener { Toast.makeText(this, "hola mundo", Toast.LENGTH_SHORT).show() }
    }

    private fun InitComponet() {
        // Validar que el usuario esté correctamente autenticado
        if (MainActivity.NombreUsuarioCompanion == "NombreUsuario") {
            Toast.makeText(this, "Error: Usuario no autenticado. Por favor inicia sesión.", Toast.LENGTH_LONG).show()
            // Redirigir a login
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
            return
        }

        btnIrReportesCompactacion=findViewById(R.id.btnIrReportesCompactacion)
        btnItReportesMorteros=findViewById(R.id.btnItReportesMorteros)
        btnIrReportesCilindros=findViewById(R.id.btnIrReportesCilindros)
        btnIrReportesVigas=findViewById(R.id.btnIrReportesVigas)
        btnIrReportesMecanicas=findViewById(R.id.btnIrReportesMecanicas)
//        btnBorrar1=findViewById(R.id.prueba1)


    }
}