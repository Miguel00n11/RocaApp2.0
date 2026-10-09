package com.miguelrodriguez.rocaapp20

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class SelectorAprendizajeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector_aprendizaje)

        findViewById<ImageButton>(R.id.btnAtrasSelector).setOnClickListener { finish() }

        // Leer documentación → lista de normas en modo doc
        findViewById<CardView>(R.id.cardLeerDoc).setOnClickListener {
            startActivity(
                Intent(this, AprendizajeActivity::class.java)
                    .putExtra(AprendizajeActivity.EXTRA_MODO, AprendizajeActivity.MODO_DOC)
            )
        }

        // Juego de memoria → directo, cubre todas las normas
        findViewById<CardView>(R.id.cardMemoria).setOnClickListener {
            startActivity(Intent(this, JuegoMemoriaActivity::class.java))
        }

        // Juego trivia → lista de normas en modo trivia
        findViewById<CardView>(R.id.cardJugar).setOnClickListener {
            startActivity(
                Intent(this, AprendizajeActivity::class.java)
                    .putExtra(AprendizajeActivity.EXTRA_MODO, AprendizajeActivity.MODO_TRIVIA)
            )
        }
    }
}
