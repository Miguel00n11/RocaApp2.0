package com.miguelrodriguez.rocaapp20

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CapacitacionesActivity : AppCompatActivity() {

    private lateinit var listaExamenes: LinearLayout
    private val etiquetasEstado = mutableMapOf<String, TextView>()

    private data class Examen(
        val numero: String,
        val codigo: String,
        val pantalla: Class<out Activity>,
        // Nodo en Firebase donde la pantalla del examen guarda los resultados
        val nodoFirebase: String,
        // Versión del cuestionario que exige la pantalla del examen (null si no maneja versiones)
        val version: Int? = null
    )

    private val examenes = listOf(
        Examen("083", "NMX-C-083-ONNCCE-2014", ExamenNorma083Activity::class.java, "EXAMEN NORMA 083"),
        Examen("109", "NMX-C-109-ONNCCE-2013", ExamenNorma109Activity::class.java, "EXAMEN NORMA 109"),
        Examen("156", "NMX-C-156-ONNCCE-2010", ExamenNorma156Activity::class.java, "EXAMEN NORMA 156", version = 2),
        Examen("159", "NMX-C-159-ONNCCE-2016", ExamenNorma159Activity::class.java, "EXAMEN NORMA 159"),
        Examen("161", "NMX-C-161-ONNCCE-2013", ExamenNorma161Activity::class.java, "EXAMEN NORMA 161"),
        Examen("17025", "NMX-EC-17025-IMNC-2018", Examen17025GestionActivity::class.java, "EXAMEN 17025 GESTIÓN"),
        Examen("008", "LIC y NOM-008-SE-2021", ExamenLicNom008Activity::class.java, "EXAMEN LIC Y NOM-008-SE-2021")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capacitaciones)

        listaExamenes = findViewById(R.id.listaExamenes)

        val inflater = LayoutInflater.from(this)
        for (examen in examenes) {
            val tarjeta = inflater.inflate(R.layout.item_examen_capacitacion, listaExamenes, false)
            tarjeta.findViewById<TextView>(R.id.txtNumeroNorma).text = examen.numero
            tarjeta.findViewById<TextView>(R.id.txtCodigoNorma).text = examen.codigo
            etiquetasEstado[examen.nodoFirebase] = tarjeta.findViewById(R.id.txtEstadoExamen)
            tarjeta.setOnClickListener {
                startActivity(Intent(this, examen.pantalla))
            }
            listaExamenes.addView(tarjeta)
        }
    }

    // Se recarga al volver de un examen para reflejar el que se acaba de responder
    override fun onResume() {
        super.onResume()
        cargarEstadoExamenes()
    }

    private fun cargarEstadoExamenes() {
        // Mismo nombre y año con los que las pantallas de examen guardan los resultados
        val nombre = MainActivity.NombreUsuarioCompanion
            .takeIf { it != "NombreUsuario" && it.isNotBlank() }
            ?: "Usuario no identificado"
        val anioActual = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())

        FirebaseDatabase.getInstance().reference
            .child("Capacitaciones")
            .child("Examenes")
            .child(anioActual)
            .child(nombre)
            .get()
            .addOnSuccessListener { snapshot ->
                for (examen in examenes) {
                    val ultimoExamen = snapshot.child(examen.nodoFirebase).children.maxByOrNull { it.key ?: "0" }
                    mostrarEstado(examen, ultimoExamen)
                }
            }
            .addOnFailureListener {
                for (etiqueta in etiquetasEstado.values) {
                    pintarEtiqueta(etiqueta, "Sin conexión", "#4B5E6C")
                }
            }
    }

    private fun mostrarEstado(examen: Examen, ultimoExamen: DataSnapshot?) {
        val etiqueta = etiquetasEstado[examen.nodoFirebase] ?: return

        val versionGuardada = ultimoExamen?.child("version")?.getValue(Int::class.java)
        val respondido = ultimoExamen != null && (examen.version == null || versionGuardada == examen.version)
        if (!respondido) {
            pintarEtiqueta(etiqueta, "Pendiente", "#4B5E6C")
            return
        }

        val calificacion = (ultimoExamen?.child("calificacion")?.value as? Number)?.toDouble() ?: 0.0
        val fecha = ultimoExamen?.child("fecha")?.getValue(String::class.java)
        val textoFecha = if (fecha.isNullOrBlank()) "" else " · $fecha"
        val calificacionTexto = String.format(Locale.US, "%.1f", calificacion)

        if (calificacion >= 6.0) {
            pintarEtiqueta(etiqueta, "✓ Aprobado $calificacionTexto$textoFecha", "#2E7D32")
        } else {
            pintarEtiqueta(etiqueta, "✗ Reprobado $calificacionTexto$textoFecha", "#C62828")
        }
    }

    private fun pintarEtiqueta(etiqueta: TextView, texto: String, color: String) {
        etiqueta.text = texto
        etiqueta.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
    }
}
