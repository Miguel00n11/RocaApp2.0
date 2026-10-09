package com.miguelrodriguez.rocaapp20

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.miguelrodriguez.rocaapp20.acceso.consultar_datos
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Permite al administrador eliminar exámenes registrados por error.
// Estructura en Firebase: Capacitaciones/Examenes/{año}/{nombre}/{examen}/{id}
class ExamenesPersonalActivity : AppCompatActivity() {

    companion object {
        // Nombre de la persona a mostrar al abrir la pantalla (opcional)
        const val EXTRA_NOMBRE = "nombrePersona"

        private const val TODAS_LAS_PERSONAS = "Todas las personas"
        private const val TODOS_LOS_ANIOS = "Todos"
        private const val TODOS_LOS_EXAMENES = "Todos los exámenes"
    }

    private lateinit var etPersona: MaterialAutoCompleteTextView
    private lateinit var etAnio: MaterialAutoCompleteTextView
    private lateinit var etTipoExamen: MaterialAutoCompleteTextView
    private lateinit var progreso: ProgressBar
    private lateinit var txtResumen: TextView
    private lateinit var listaExamenes: LinearLayout
    private lateinit var nodoCapacitaciones: DatabaseReference

    private data class ExamenRegistrado(
        val anio: String,
        val nombre: String,
        val examen: String,
        val id: String,
        val fecha: String,
        val calificacion: Double?,
        val fechaRegistro: String,
        val datos: Any?,
        // Ruta real del registro dentro de Capacitaciones (los exámenes antiguos no siguen la estructura actual)
        val ruta: String
    )

    // Número y código de norma de cada nodo de examen (los mismos que muestra Capacitaciones)
    private val normasPorExamen = linkedMapOf(
        "EXAMEN NORMA 083" to Pair("083", "NMX-C-083-ONNCCE-2014"),
        "EXAMEN NORMA 109" to Pair("109", "NMX-C-109-ONNCCE-2013"),
        "EXAMEN NORMA 156" to Pair("156", "NMX-C-156-ONNCCE-2010"),
        "EXAMEN NORMA 159" to Pair("159", "NMX-C-159-ONNCCE-2016"),
        "EXAMEN NORMA 161" to Pair("161", "NMX-C-161-ONNCCE-2013"),
        "EXAMEN 17025 GESTIÓN" to Pair("17025", "NMX-EC-17025-IMNC-2018"),
        "EXAMEN LIC Y NOM-008-SE-2021" to Pair("008", "LIC y NOM-008-SE-2021")
    )

    private var todosLosExamenes = listOf<ExamenRegistrado>()
    private var filtroPersona = TODAS_LAS_PERSONAS
    private var filtroAnio = TODOS_LOS_ANIOS
    private var filtroExamen = TODOS_LOS_EXAMENES

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!consultar_datos.esAdministrador) {
            Toast.makeText(this, "Solo un administrador puede eliminar exámenes.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setContentView(R.layout.activity_examenes_personal)

        nodoCapacitaciones = FirebaseDatabase.getInstance().reference.child("Capacitaciones")
        etPersona = findViewById(R.id.etPersonaExamenes)
        etAnio = findViewById(R.id.etAnioExamenes)
        etTipoExamen = findViewById(R.id.etTipoExamen)
        progreso = findViewById(R.id.progresoExamenes)
        txtResumen = findViewById(R.id.txtResumenExamenes)
        listaExamenes = findViewById(R.id.listaExamenesPersona)

        intent.getStringExtra(EXTRA_NOMBRE)?.let { filtroPersona = it }

        etPersona.setOnItemClickListener { _, _, _, _ ->
            filtroPersona = etPersona.text.toString()
            mostrarExamenesFiltrados()
        }
        etAnio.setOnItemClickListener { _, _, _, _ ->
            filtroAnio = etAnio.text.toString()
            mostrarExamenesFiltrados()
        }
        etTipoExamen.setOnItemClickListener { _, _, _, _ ->
            filtroExamen = etTipoExamen.text.toString()
            mostrarExamenesFiltrados()
        }

        cargarExamenes()
    }

    private fun cargarExamenes() {
        progreso.visibility = View.VISIBLE
        nodoCapacitaciones.child("Examenes").get()
            .addOnSuccessListener { snapshot ->
                progreso.visibility = View.GONE
                val examenes = mutableListOf<ExamenRegistrado>()
                recolectarExamenes(snapshot, emptyList(), examenes)
                todosLosExamenes = examenes
                actualizarOpcionesDeFiltros()
                mostrarExamenesFiltrados()
            }
            .addOnFailureListener {
                progreso.visibility = View.GONE
                txtResumen.text = "No se pudieron cargar los exámenes: ${it.message}"
            }
    }

    // Recorre el árbol buscando registros de examen (los que tienen calificación o respuestas).
    // La estructura actual es {año}/{nombre}/{examen}/{id}, pero versiones anteriores de la app
    // guardaron exámenes con otra forma (p. ej. "17025Gestion"); de esos se toman los datos del propio registro.
    private fun recolectarExamenes(nodo: DataSnapshot, ruta: List<String>, destino: MutableList<ExamenRegistrado>) {
        val esRegistro = nodo.hasChild("calificacion") || nodo.hasChild("respuestas")
        if (!esRegistro) {
            for (hijo in nodo.children) {
                recolectarExamenes(hijo, ruta + (hijo.key ?: continue), destino)
            }
            return
        }

        val fecha = nodo.child("fecha").getValue(String::class.java) ?: ""
        val fechaRegistro = nodo.child("fechaRegistro").getValue(String::class.java) ?: ""
        val estructuraActual = ruta.size == 4 && ruta[0].matches(Regex("\\d{4}"))

        val anio = when {
            estructuraActual -> ruta[0]
            fechaRegistro.matches(Regex("\\d{4}-.*")) -> fechaRegistro.take(4)
            fecha.matches(Regex(".*\\d{4}")) -> fecha.takeLast(4)
            else -> "Sin año"
        }
        val nombre = nodo.child("nombre").getValue(String::class.java)?.takeIf { it.isNotBlank() }
            ?: if (estructuraActual) ruta[1] else "Sin nombre"
        val examen = nodo.child("examen").getValue(String::class.java)?.takeIf { it.isNotBlank() }
            ?: if (estructuraActual) ruta[2] else ruta.firstOrNull { !it.matches(Regex("\\d{4}")) } ?: "Examen"

        destino += ExamenRegistrado(
            anio = anio,
            nombre = nombre,
            examen = normalizarExamen(examen),
            id = ruta.lastOrNull() ?: "",
            fecha = fecha,
            calificacion = (nodo.child("calificacion").value as? Number)?.toDouble(),
            fechaRegistro = fechaRegistro,
            datos = nodo.value,
            ruta = (listOf("Examenes") + ruta).joinToString("/")
        )
    }

    // Agrupa variantes antiguas del nombre de un examen con el nombre actual (p. ej. "17025Gestion")
    private fun normalizarExamen(examen: String): String {
        if (examen in normasPorExamen) return examen
        val numero = Regex("\\d{3,5}").find(examen)?.value ?: return examen
        return normasPorExamen.entries.firstOrNull { it.value.first == numero }?.key ?: examen
    }

    private fun opcionesFiltro(campo: MaterialAutoCompleteTextView, opciones: List<String>) {
        campo.setAdapter(ArrayAdapter(this, R.layout.item_opcion_filtro, opciones))
        campo.setDropDownBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_dropdown_filtro))
    }

    // Las opciones de cada filtro salen de los exámenes que existen en Firebase
    private fun actualizarOpcionesDeFiltros() {
        val personas = todosLosExamenes.map { it.nombre }.distinct().sorted()
        val anios = todosLosExamenes.map { it.anio }.distinct().sortedDescending()
        val examenes = todosLosExamenes.map { it.examen }.distinct()
            .sortedBy { normasPorExamen.keys.indexOf(it).let { i -> if (i < 0) Int.MAX_VALUE else i } }
            .map { nombreExamen(it) }

        // Si una opción elegida ya no existe (p. ej. se eliminó su último examen), se vuelve a "Todos"
        if (filtroPersona !in personas) filtroPersona = TODAS_LAS_PERSONAS
        if (filtroAnio !in anios) filtroAnio = TODOS_LOS_ANIOS
        if (filtroExamen !in examenes) filtroExamen = TODOS_LOS_EXAMENES

        opcionesFiltro(etPersona, listOf(TODAS_LAS_PERSONAS) + personas)
        opcionesFiltro(etAnio, listOf(TODOS_LOS_ANIOS) + anios)
        opcionesFiltro(etTipoExamen, listOf(TODOS_LOS_EXAMENES) + examenes)

        etPersona.setText(filtroPersona, false)
        etAnio.setText(filtroAnio, false)
        etTipoExamen.setText(filtroExamen, false)
    }

    private fun mostrarExamenesFiltrados() {
        listaExamenes.removeAllViews()

        if (todosLosExamenes.isEmpty()) {
            txtResumen.text = "Aún no hay exámenes registrados."
            return
        }

        val todasLasPersonas = filtroPersona == TODAS_LAS_PERSONAS
        // Más recientes primero
        val examenes = todosLosExamenes
            .filter { todasLasPersonas || it.nombre == filtroPersona }
            .filter { filtroAnio == TODOS_LOS_ANIOS || it.anio == filtroAnio }
            .filter { filtroExamen == TODOS_LOS_EXAMENES || nombreExamen(it.examen) == filtroExamen }
            .sortedByDescending { it.id }

        txtResumen.text = when (examenes.size) {
            0 -> "No hay exámenes con estos filtros."
            1 -> "1 examen encontrado"
            else -> "${examenes.size} exámenes encontrados"
        }

        val inflater = LayoutInflater.from(this)
        for (examen in examenes) {
            val tarjeta = inflater.inflate(R.layout.item_examen_registrado, listaExamenes, false)

            tarjeta.findViewById<TextView>(R.id.txtNumeroExamenRegistrado).text =
                normasPorExamen[examen.examen]?.first ?: "?"
            tarjeta.findViewById<TextView>(R.id.txtCodigoExamenRegistrado).text = nombreExamen(examen.examen)
            tarjeta.findViewById<TextView>(R.id.txtFechaExamenRegistrado).text = descripcionFecha(examen)

            // Con varias personas en la lista hace falta saber de quién es cada examen
            tarjeta.findViewById<TextView>(R.id.txtPersonaExamenRegistrado).apply {
                text = examen.nombre
                visibility = if (todasLasPersonas) View.VISIBLE else View.GONE
            }

            val etiqueta = tarjeta.findViewById<TextView>(R.id.txtCalificacionExamenRegistrado)
            val calificacion = examen.calificacion
            if (calificacion == null) {
                etiqueta.text = "Sin calificación"
                etiqueta.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4B5E6C"))
            } else {
                val aprobado = calificacion >= 6.0
                etiqueta.text = String.format(Locale.US, "%s %.1f", if (aprobado) "✓ Aprobado" else "✗ Reprobado", calificacion)
                etiqueta.backgroundTintList = ColorStateList.valueOf(
                    Color.parseColor(if (aprobado) "#2E7D32" else "#C62828")
                )
            }

            tarjeta.findViewById<ImageButton>(R.id.btnEliminarExamen).setOnClickListener {
                confirmarEliminacion(examen)
            }
            listaExamenes.addView(tarjeta)
        }
    }

    private fun nombreExamen(nodo: String): String = normasPorExamen[nodo]?.second ?: nodo

    private fun descripcionFecha(examen: ExamenRegistrado): String {
        val hora = examen.fechaRegistro.substringAfter(" ", "").take(5)
        val fecha = examen.fecha.ifBlank { examen.fechaRegistro.substringBefore(" ") }
        return if (hora.isNotBlank()) "$fecha · $hora h" else fecha
    }

    private fun confirmarEliminacion(examen: ExamenRegistrado) {
        val calificacion = examen.calificacion?.let { String.format(Locale.US, "%.2f", it) } ?: "—"

        AlertDialog.Builder(this)
            .setTitle("¿Eliminar este examen?")
            .setMessage(
                "Persona: ${examen.nombre}\n" +
                    "Examen: ${nombreExamen(examen.examen)}\n" +
                    "Fecha: ${descripcionFecha(examen)}\n" +
                    "Calificación: $calificacion\n\n" +
                    "Dejará de contar como respondido. Si la persona tiene un intento anterior, ese será el que se muestre."
            )
            .setPositiveButton("Eliminar") { _, _ -> eliminarExamen(examen) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Mueve el examen a ExamenesEliminados en una sola operación: nunca queda borrado sin respaldo
    private fun eliminarExamen(examen: ExamenRegistrado) {
        val respaldo = hashMapOf(
            "anio" to examen.anio,
            "nombre" to examen.nombre,
            "examen" to examen.examen,
            "id" to examen.id,
            "datos" to examen.datos,
            "eliminadoPor" to MainActivity.NombreUsuarioCompanion,
            "fechaEliminacion" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )
        val cambios = hashMapOf<String, Any?>(
            "ExamenesEliminados/${examen.id}" to respaldo,
            examen.ruta to null
        )

        progreso.visibility = View.VISIBLE
        nodoCapacitaciones.updateChildren(cambios)
            .addOnSuccessListener {
                Toast.makeText(this, "Examen eliminado", Toast.LENGTH_SHORT).show()
                cargarExamenes()
            }
            .addOnFailureListener {
                progreso.visibility = View.GONE
                Toast.makeText(this, "No se pudo eliminar: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }
}
