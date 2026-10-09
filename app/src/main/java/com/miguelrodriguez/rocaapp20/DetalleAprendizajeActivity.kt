package com.miguelrodriguez.rocaapp20

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetalleAprendizajeActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NUMERO = "numero"
        const val EXTRA_CODIGO = "codigo"
        const val EXTRA_BASE_PATH = "firebaseBasePath"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_aprendizaje)

        val numero = intent.getStringExtra(EXTRA_NUMERO) ?: ""
        val codigo = intent.getStringExtra(EXTRA_CODIGO) ?: ""

        findViewById<ImageButton>(R.id.btnAtrasDetalle).setOnClickListener { finish() }
        findViewById<TextView>(R.id.txtCodigoDetalle).text = codigo

        cargarMaterial(numero)
    }

    private fun cargarMaterial(numero: String) {
        val basePath = intent.getStringExtra(EXTRA_BASE_PATH) ?: "Capacitaciones/Material"
        var ref = FirebaseDatabase.getInstance().reference
        for (segment in basePath.split("/")) ref = ref.child(segment)
        ref.child(numero)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    findViewById<View>(R.id.layoutCargando).visibility = View.GONE

                    if (!snapshot.exists()) {
                        findViewById<View>(R.id.layoutSinContenido).visibility = View.VISIBLE
                        return
                    }

                    val titulo = snapshot.child("titulo").getValue(String::class.java) ?: ""
                    findViewById<TextView>(R.id.txtTituloDetalle).text = titulo

                    val secciones = snapshot.child("secciones")
                    if (!secciones.exists()) {
                        // Compatibilidad: si solo hay un campo "contenido" plano
                        val contenido = snapshot.child("contenido").getValue(String::class.java)
                        if (!contenido.isNullOrBlank()) {
                            mostrarSeccionSimple("Contenido", contenido)
                            mostrarContenido()
                        } else {
                            findViewById<View>(R.id.layoutSinContenido).visibility = View.VISIBLE
                        }
                        return
                    }

                    for (seccion in secciones.children) {
                        val tituloSec = seccion.child("titulo").getValue(String::class.java) ?: ""
                        val contenidoSec = seccion.child("contenido").getValue(String::class.java) ?: ""
                        mostrarSeccion(tituloSec, contenidoSec)
                    }
                    mostrarContenido()
                }

                override fun onCancelled(error: DatabaseError) {
                    findViewById<View>(R.id.layoutCargando).visibility = View.GONE
                    findViewById<View>(R.id.layoutSinContenido).visibility = View.VISIBLE
                }
            })
    }

    private fun mostrarContenido() {
        findViewById<ScrollView>(R.id.scrollContenido).visibility = View.VISIBLE
    }

    private fun mostrarSeccion(titulo: String, contenido: String) {
        val layout = findViewById<LinearLayout>(R.id.layoutSecciones)

        if (titulo.isNotBlank()) {
            val tvTitulo = TextView(this).apply {
                text = titulo
                textSize = 16f
                setTypeface(null, Typeface.BOLD)
                setTextColor(ContextCompat.getColor(context, R.color.btn_Background_Acceder))
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.topMargin = dpToPx(20)
                params.bottomMargin = dpToPx(6)
                layoutParams = params
            }
            layout.addView(tvTitulo)

            val divisor = View(this).apply {
                setBackgroundColor(ContextCompat.getColor(context, R.color.btn_Background_Acceder))
                alpha = 0.4f
                val params = LinearLayout.LayoutParams(dpToPx(40), dpToPx(2))
                params.bottomMargin = dpToPx(10)
                layoutParams = params
            }
            layout.addView(divisor)
        }

        val tvContenido = TextView(this).apply {
            text = contenido
            textSize = 14f
            setTextColor(0xFFDDE4EA.toInt())
            lineHeight = (textSize * 1.7f).toInt()
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams = params
        }
        layout.addView(tvContenido)
    }

    private fun mostrarSeccionSimple(titulo: String, contenido: String) = mostrarSeccion(titulo, contenido)

    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()
}
