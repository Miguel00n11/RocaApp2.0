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

class InstructivosActivity : AppCompatActivity() {

    private data class Instructivo(val clave: String, val codigoCompleto: String, val titulo: String)

    private val todosLosInstructivos = listOf(
        Instructivo("IT01", "IT01-PR14  Rev. 00", "Determinación del Revenimiento del Concreto Fresco"),
        Instructivo("IT02", "IT02-PR14  Rev. 00", "Elaboración de Especímenes de Concreto"),
        Instructivo("IT03", "IT03-PR14  Rev. 00", "Cabeceo de Cilindros de Concreto"),
        Instructivo("IT04", "IT04-PR14  Rev. 03", "Determinación de la Resistencia a Compresión"),
        Instructivo("IT05", "IT05-PR14  Rev. 00", "Operación de Máquina de Ensayo")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instructivos)

        findViewById<ImageButton>(R.id.btnAtrasInstructivos).setOnClickListener { finish() }

        cargarYMostrarInstructivos()
    }

    private fun cargarYMostrarInstructivos() {
        val lista = findViewById<LinearLayout>(R.id.listaInstructivos)
        val inflater = LayoutInflater.from(this)

        if (consultar_datos.esAdministrador) {
            mostrarInstructivos(todosLosInstructivos, lista, inflater)
            return
        }

        val correo = consultar_datos.usuarioApp
        if (correo.isNullOrBlank()) {
            mostrarInstructivos(todosLosInstructivos, lista, inflater)
            return
        }

        val clave = CatalogoPersonal.claveCorreo(correo)
        FirebaseDatabase.getInstance().reference
            .child(CatalogoPersonal.NODO_PERSONAL).child(clave)
            .child("acceso").child("instructivos")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val permitidos = snapshot.children
                        .filter { it.getValue(Boolean::class.java) == true }
                        .mapNotNull { it.key }.toSet()
                    val filtrados = if (permitidos.isEmpty()) todosLosInstructivos
                        else todosLosInstructivos.filter { it.clave in permitidos }
                    mostrarInstructivos(filtrados, lista, inflater)
                }
                override fun onCancelled(error: DatabaseError) {
                    mostrarInstructivos(todosLosInstructivos, lista, inflater)
                }
            })
    }

    private fun mostrarInstructivos(instructivos: List<Instructivo>, lista: LinearLayout, inflater: LayoutInflater) {
        for (instructivo in instructivos) {
            val tarjeta = inflater.inflate(R.layout.item_tema_aprendizaje, lista, false)
            tarjeta.findViewById<TextView>(R.id.txtNumeroTema).text = instructivo.clave
            tarjeta.findViewById<TextView>(R.id.txtCodigoTema).text = instructivo.codigoCompleto
            tarjeta.findViewById<TextView>(R.id.txtTituloTema).text = instructivo.titulo
            tarjeta.setOnClickListener { abrirDetalle(instructivo) }
            lista.addView(tarjeta)
            cargarTituloDesdeFirebase(instructivo, tarjeta)
        }
    }

    private fun cargarTituloDesdeFirebase(instructivo: Instructivo, tarjeta: android.view.View) {
        FirebaseDatabase.getInstance().reference
            .child("Instructivos").child("Material").child(instructivo.clave).child("titulo")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val titulo = snapshot.getValue(String::class.java) ?: return
                    tarjeta.findViewById<TextView>(R.id.txtTituloTema).text = titulo
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun abrirDetalle(instructivo: Instructivo) {
        startActivity(
            Intent(this, DetalleAprendizajeActivity::class.java)
                .putExtra(DetalleAprendizajeActivity.EXTRA_NUMERO, instructivo.clave)
                .putExtra(DetalleAprendizajeActivity.EXTRA_CODIGO, instructivo.codigoCompleto)
                .putExtra(DetalleAprendizajeActivity.EXTRA_BASE_PATH, "Instructivos/Material")
        )
    }
}
