package com.miguelrodriguez.rocaapp20

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.database.FirebaseDatabase

class SelectorAprendizajeActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NUMERO = "numero"
        const val EXTRA_CODIGO = "codigo"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector_aprendizaje)

        val numero = intent.getStringExtra(EXTRA_NUMERO) ?: ""
        val codigo = intent.getStringExtra(EXTRA_CODIGO) ?: ""

        findViewById<ImageButton>(R.id.btnAtrasSelector).setOnClickListener { finish() }
        findViewById<TextView>(R.id.txtCodigoSelector).text = codigo

        val cardDoc = findViewById<CardView>(R.id.cardLeerDoc)
        val tvSubtitulo = findViewById<TextView>(R.id.txtSubtituloDoc)
        val tvBadge = findViewById<TextView>(R.id.tvBadgeDoc)

        if (ContenidoNormas.tieneContenido(numero)) {
            cardDoc.alpha = 1f
            cardDoc.isClickable = true
            cardDoc.setOnClickListener {
                abrirDocumentacion(numero, codigo, cardDoc, tvSubtitulo)
            }
        } else {
            cardDoc.alpha = 0.45f
            cardDoc.isClickable = false
            tvSubtitulo.text = "Próximamente"
            tvBadge.text = ""
        }

        val tieneJuego = numero in setOf("109", "17025", "008", "083", "148", "156", "159", "161")
        val cardJugar = findViewById<CardView>(R.id.cardJugar)

        if (tieneJuego) {
            cardJugar.alpha = 1f
            cardJugar.isClickable = true
            cardJugar.setOnClickListener {
                startActivity(
                    Intent(this, JuegoNormaActivity::class.java)
                        .putExtra(JuegoNormaActivity.EXTRA_NUMERO, numero)
                        .putExtra(JuegoNormaActivity.EXTRA_CODIGO, codigo)
                )
            }
        } else {
            cardJugar.alpha = 0.45f
            cardJugar.isClickable = false
        }
    }

    private fun abrirDocumentacion(
        numero: String,
        codigo: String,
        card: CardView,
        tvSubtitulo: TextView
    ) {
        card.isClickable = false
        tvSubtitulo.text = "Verificando contenido…"

        val db = FirebaseDatabase.getInstance().reference

        ContenidoNormas.subirSiAusente(numero, db) {
            runOnUiThread {
                card.isClickable = true
                tvSubtitulo.text = "Norma oficial completa"

                startActivity(
                    Intent(this, DetalleAprendizajeActivity::class.java)
                        .putExtra(DetalleAprendizajeActivity.EXTRA_NUMERO, numero)
                        .putExtra(DetalleAprendizajeActivity.EXTRA_CODIGO, codigo)
                )
            }
        }
    }
}
