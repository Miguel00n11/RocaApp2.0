package com.miguelrodriguez.rocaapp20

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.miguelrodriguez.rocaapp20.acceso.CatalogoPersonal
import com.miguelrodriguez.rocaapp20.acceso.consultar_datos

class AprendizajeActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MODO = "modo_aprendizaje"
        const val MODO_DOC    = "doc"
        const val MODO_TRIVIA = "trivia"
    }

    private lateinit var modo: String

    private data class Tema(
        val numero: String,
        val codigo: String,
        val tituloDefault: String
    )

    private val todosLosTemas = listOf(
        Tema("083", "NMX-C-083-ONNCCE-2014", "Determinación de la resistencia a la compresión"),
        Tema("109", "NMX-C-109-ONNCCE-2013", "Cabeceo de especímenes de concreto"),
        Tema("156", "NMX-C-156-ONNCCE-2010", "Determinación del revenimiento en concreto fresco"),
        Tema("159", "NMX-C-159-ONNCCE-2016", "Elaboración y curado de especímenes de concreto"),
        Tema("161", "NMX-C-161-ONNCCE-2013", "Concreto fresco - Muestreo"),
        Tema("17025", "NMX-EC-17025-IMNC-2018", "Requisitos generales de laboratorios de ensayo"),
        Tema("008", "LIC y NOM-008-SE-2021", "Sistema General de Unidades de Medida")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aprendizaje)

        modo = intent.getStringExtra(EXTRA_MODO) ?: MODO_DOC

        val (titulo, subtitulo) = when (modo) {
            MODO_TRIVIA -> "Juego trivia" to "Elige la norma que quieres practicar"
            else        -> "Leer documentación" to "Elige la norma que quieres consultar"
        }
        findViewById<TextView>(R.id.txtTituloAprendizaje).text = titulo
        findViewById<TextView>(R.id.txtSubtituloAprendizaje).text = subtitulo

        findViewById<ImageButton>(R.id.btnAtrasAprendizaje).setOnClickListener { finish() }

        cargarYMostrarTemas()
    }

    private fun cargarYMostrarTemas() {
        val lista = findViewById<LinearLayout>(R.id.listaTemas)
        val inflater = LayoutInflater.from(this)

        if (consultar_datos.esAdministrador) {
            mostrarTemas(todosLosTemas, lista, inflater)
            return
        }

        val correo = consultar_datos.usuarioApp
        if (correo.isNullOrBlank()) {
            mostrarTemas(todosLosTemas, lista, inflater)
            return
        }

        val clave = CatalogoPersonal.claveCorreo(correo)
        FirebaseDatabase.getInstance().reference
            .child(CatalogoPersonal.NODO_PERSONAL).child(clave)
            .child("acceso").child("capacitaciones")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val permitidas = snapshot.children
                        .filter { it.getValue(Boolean::class.java) == true }
                        .mapNotNull { it.key }.toSet()
                    val filtrados = if (permitidas.isEmpty()) todosLosTemas
                        else todosLosTemas.filter { it.numero in permitidas }
                    mostrarTemas(filtrados, lista, inflater)
                }
                override fun onCancelled(error: DatabaseError) {
                    mostrarTemas(todosLosTemas, lista, inflater)
                }
            })
    }

    private fun mostrarTemas(temas: List<Tema>, lista: LinearLayout, inflater: LayoutInflater) {
        for (tema in temas) {
            val tarjeta = inflater.inflate(R.layout.item_tema_aprendizaje, lista, false)
            tarjeta.findViewById<TextView>(R.id.txtNumeroTema).text = tema.numero
            tarjeta.findViewById<TextView>(R.id.txtCodigoTema).text = tema.codigo
            tarjeta.findViewById<TextView>(R.id.txtTituloTema).text = tema.tituloDefault
            tarjeta.setOnClickListener { abrirDetalle(tema) }
            lista.addView(tarjeta)
            cargarTituloDesdeFirebase(tema, tarjeta)
        }
    }

    private fun cargarTituloDesdeFirebase(tema: Tema, tarjeta: android.view.View) {
        FirebaseDatabase.getInstance().reference
            .child("Capacitaciones").child("Material").child(tema.numero).child("titulo")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val titulo = snapshot.getValue(String::class.java) ?: return
                    tarjeta.findViewById<TextView>(R.id.txtTituloTema).text = titulo
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun abrirDetalle(tema: Tema) {
        if (modo == MODO_TRIVIA) {
            startActivity(Intent(this, JuegoNormaActivity::class.java).apply {
                putExtra(JuegoNormaActivity.EXTRA_NUMERO, tema.numero)
                putExtra(JuegoNormaActivity.EXTRA_CODIGO, tema.codigo)
            })
        } else {
            startActivity(Intent(this, DetalleAprendizajeActivity::class.java).apply {
                putExtra(DetalleAprendizajeActivity.EXTRA_NUMERO, tema.numero)
                putExtra(DetalleAprendizajeActivity.EXTRA_CODIGO, tema.codigo)
            })
        }
    }
}
