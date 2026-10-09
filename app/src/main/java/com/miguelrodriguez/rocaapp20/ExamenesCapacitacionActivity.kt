package com.miguelrodriguez.rocaapp20

import android.app.Activity
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

class ExamenesCapacitacionActivity : AppCompatActivity() {

    data class NormaExamen(
        val numero: String,
        val codigo: String,
        val nodoFirebase: String,
        val pantalla: Class<out Activity>,
        val version: Int? = null
    )

    companion object {
        val NORMAS = listOf(
            NormaExamen("083", "NMX-C-083-ONNCCE-2014", "EXAMEN NORMA 083", ExamenNorma083Activity::class.java),
            NormaExamen("109", "NMX-C-109-ONNCCE-2013", "EXAMEN NORMA 109", ExamenNorma109Activity::class.java),
            NormaExamen("156", "NMX-C-156-ONNCCE-2010", "EXAMEN NORMA 156", ExamenNorma156Activity::class.java, version = 2),
            NormaExamen("159", "NMX-C-159-ONNCCE-2016", "EXAMEN NORMA 159", ExamenNorma159Activity::class.java),
            NormaExamen("161", "NMX-C-161-ONNCCE-2013", "EXAMEN NORMA 161", ExamenNorma161Activity::class.java),
            NormaExamen("17025", "NMX-EC-17025-IMNC-2018", "EXAMEN 17025 GESTIÓN", Examen17025GestionActivity::class.java),
            NormaExamen("008", "LIC y NOM-008-SE-2021", "EXAMEN LIC Y NOM-008-SE-2021", ExamenLicNom008Activity::class.java)
        )

        const val EXTRA_NORMA_INDEX = "normaIndex"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_examenes_capacitacion)

        findViewById<ImageButton>(R.id.btnAtrasExamenes).setOnClickListener { finish() }

        cargarYMostrarNormas()
    }

    private fun cargarYMostrarNormas() {
        val lista = findViewById<LinearLayout>(R.id.listaNormas)
        val inflater = LayoutInflater.from(this)

        if (consultar_datos.esAdministrador) {
            mostrarNormas(NORMAS.mapIndexed { i, n -> i to n }, lista, inflater)
            return
        }

        val correo = consultar_datos.usuarioApp
        if (correo.isNullOrBlank()) {
            mostrarNormas(NORMAS.mapIndexed { i, n -> i to n }, lista, inflater)
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
                    val filtradas = if (permitidas.isEmpty()) NORMAS.mapIndexed { i, n -> i to n }
                        else NORMAS.mapIndexed { i, n -> i to n }.filter { (_, n) -> n.numero in permitidas }
                    mostrarNormas(filtradas, lista, inflater)
                }
                override fun onCancelled(error: DatabaseError) {
                    mostrarNormas(NORMAS.mapIndexed { i, n -> i to n }, lista, inflater)
                }
            })
    }

    private fun mostrarNormas(
        normas: List<Pair<Int, NormaExamen>>,
        lista: LinearLayout,
        inflater: LayoutInflater
    ) {
        for ((originalIndex, norma) in normas) {
            val tarjeta = inflater.inflate(R.layout.item_examen_capacitacion, lista, false)
            tarjeta.findViewById<TextView>(R.id.txtNumeroNorma).text = norma.numero
            tarjeta.findViewById<TextView>(R.id.txtCodigoNorma).text = norma.codigo
            tarjeta.findViewById<TextView>(R.id.txtEstadoExamen).visibility = android.view.View.GONE
            tarjeta.setOnClickListener {
                startActivity(
                    Intent(this, ExamenNormaDetalleActivity::class.java)
                        .putExtra(EXTRA_NORMA_INDEX, originalIndex)
                )
            }
            lista.addView(tarjeta)
        }
    }
}
