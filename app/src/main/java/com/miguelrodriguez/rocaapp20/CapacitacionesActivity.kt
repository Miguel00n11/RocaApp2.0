package com.miguelrodriguez.rocaapp20

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CapacitacionesActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ANIO = "anioExamen"

        fun anioActual(): String = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())

        fun anioDelExamen(intent: Intent): String = intent.getStringExtra(EXTRA_ANIO) ?: anioActual()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capacitaciones)

        findViewById<ImageButton>(R.id.btnAtrasCapacitaciones).setOnClickListener { finish() }

        findViewById<CardView>(R.id.cardAprendizaje).setOnClickListener {
            startActivity(Intent(this, SelectorAprendizajeActivity::class.java))
        }

        findViewById<CardView>(R.id.cardInstructivos).setOnClickListener {
            startActivity(Intent(this, InstructivosActivity::class.java))
        }

        findViewById<CardView>(R.id.cardExamenes).setOnClickListener {
            startActivity(Intent(this, ExamenesCapacitacionActivity::class.java))
        }
    }
}
