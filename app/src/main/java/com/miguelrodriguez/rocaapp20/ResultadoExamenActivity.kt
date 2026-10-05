package com.miguelrodriguez.rocaapp20

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultadoExamenActivity : AppCompatActivity() {

    private lateinit var txtNombreResultado: TextView
    private lateinit var txtFechaResultado: TextView
    private lateinit var txtTotalPreguntas: TextView
    private lateinit var txtAciertos: TextView
    private lateinit var txtCalificacion: TextView
    private lateinit var txtEstado: TextView
    private lateinit var btnVolverCapacitaciones: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado_examen)

        txtNombreResultado = findViewById(R.id.txtNombreResultado)
        txtFechaResultado = findViewById(R.id.txtFechaResultado)
        txtTotalPreguntas = findViewById(R.id.txtTotalPreguntas)
        txtAciertos = findViewById(R.id.txtAciertos)
        txtCalificacion = findViewById(R.id.txtCalificacion)
        txtEstado = findViewById(R.id.txtEstado)
        btnVolverCapacitaciones = findViewById(R.id.btnVolverCapacitaciones)

        val nombre = intent.getStringExtra("nombre") ?: "No disponible"
        val fecha = intent.getStringExtra("fecha") ?: "No disponible"
        val aciertos = intent.getIntExtra("aciertos", 0)
        val total = intent.getIntExtra("total", 16)
        val calificacion = intent.getDoubleExtra("calificacion", 0.0)
        val estado = if (calificacion >= 6.0) "APROBADO" else "REPROBADO"
        val colorEstado = if (calificacion >= 6.0) android.graphics.Color.GREEN else android.graphics.Color.RED

        txtNombreResultado.text = "NOMBRE: $nombre"
        txtFechaResultado.text = "FECHA: $fecha"
        txtTotalPreguntas.text = "PREGUNTAS: $total"
        txtAciertos.text = "ACIERTOS: $aciertos / $total"
        txtCalificacion.text = String.format("CALIFICACIÓN: %.2f / 10.0", calificacion)
        txtEstado.apply {
            text = "ESTADO: $estado"
            setTextColor(colorEstado)
            textSize = 20f
        }

        btnVolverCapacitaciones.setOnClickListener {
            finish()
        }
    }
}

