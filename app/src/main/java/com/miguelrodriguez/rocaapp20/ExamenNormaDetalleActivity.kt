package com.miguelrodriguez.rocaapp20

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.miguelrodriguez.rocaapp20.acceso.consultar_datos
import java.util.Locale

class ExamenNormaDetalleActivity : AppCompatActivity() {

    companion object {
        const val TODOS = "Todos"
    }

    private lateinit var norma: ExamenesCapacitacionActivity.NormaExamen
    private lateinit var etAnio: MaterialAutoCompleteTextView
    private lateinit var listaResultados: LinearLayout
    private lateinit var btnTomar: Button

    private var anioSeleccionado = CapacitacionesActivity.anioActual()
    private var todosLosAnios: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_examen_norma_detalle)

        val index = intent.getIntExtra(ExamenesCapacitacionActivity.EXTRA_NORMA_INDEX, 0)
        norma = ExamenesCapacitacionActivity.NORMAS[index]

        findViewById<ImageButton>(R.id.btnAtrasNormaDetalle).setOnClickListener { finish() }
        findViewById<TextView>(R.id.txtCodigoNormaDetalle).text = norma.codigo

        etAnio = findViewById(R.id.etAnioNormaDetalle)
        listaResultados = findViewById(R.id.listaResultados)
        btnTomar = findViewById(R.id.btnTomarExamen)

        savedInstanceState?.getString(CapacitacionesActivity.EXTRA_ANIO)?.let { anioSeleccionado = it }
        etAnio.setText(anioSeleccionado, false)
        etAnio.setDropDownBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_dropdown_filtro))
        etAnio.setOnItemClickListener { _, _, _, _ ->
            anioSeleccionado = etAnio.text.toString()
            cargarEstado()
        }

        btnTomar.setOnClickListener { abrirExamen(anioSeleccionado) }

        cargarAniosDisponibles()
    }

    override fun onResume() {
        super.onResume()
        cargarEstado()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CapacitacionesActivity.EXTRA_ANIO, anioSeleccionado)
    }

    private fun cargarAniosDisponibles() {
        val actual = CapacitacionesActivity.anioActual().toInt()
        val base = listOf(actual.toString(), (actual - 1).toString(), (actual - 2).toString())
        mostrarOpciones(base)

        FirebaseDatabase.getInstance().reference
            .child("Capacitaciones").child("Examenes").get()
            .addOnSuccessListener { snapshot ->
                val conExamenes = snapshot.children
                    .mapNotNull { it.key }
                    .filter { it.matches(Regex("\\d{4}")) }
                todosLosAnios = (base + conExamenes).distinct().sortedDescending()
                mostrarOpciones(todosLosAnios)
            }
    }

    private fun mostrarOpciones(anios: List<String>) {
        val opciones = listOf(TODOS) + (anios + anioSeleccionado)
            .filter { it != TODOS }
            .distinct()
            .sortedDescending()
        etAnio.setAdapter(ArrayAdapter(this, R.layout.item_opcion_filtro, opciones))
    }

    private fun cargarEstado() {
        listaResultados.removeAllViews()
        btnTomar.visibility = View.GONE

        val nombre = MainActivity.NombreUsuarioCompanion
            .takeIf { it != "NombreUsuario" && it.isNotBlank() }
            ?: "Usuario no identificado"

        if (anioSeleccionado == TODOS) {
            cargarTodosLosAnios(nombre)
        } else {
            cargarAnioEspecifico(nombre, anioSeleccionado)
        }
    }

    private fun cargarTodosLosAnios(nombre: String) {
        mostrarCargando()
        FirebaseDatabase.getInstance().reference
            .child("Capacitaciones").child("Examenes").get()
            .addOnSuccessListener { snapshot ->
                listaResultados.removeAllViews()
                val aniosConDatos = snapshot.children
                    .mapNotNull { it.key }
                    .filter { it.matches(Regex("\\d{4}")) }
                    .sortedDescending()

                if (aniosConDatos.isEmpty()) {
                    mostrarSinRegistros()
                    return@addOnSuccessListener
                }

                for (anio in aniosConDatos) {
                    val nodoNorma = snapshot.child(anio).child(nombre).child(norma.nodoFirebase)
                    val ultimo = nodoNorma.children.maxByOrNull { it.key ?: "0" }
                    agregarTarjetaAnio(anio, ultimo)
                }
            }
            .addOnFailureListener { listaResultados.removeAllViews(); mostrarSinRegistros() }
    }

    private fun cargarAnioEspecifico(nombre: String, anio: String) {
        mostrarCargando()
        FirebaseDatabase.getInstance().reference
            .child("Capacitaciones").child("Examenes")
            .child(anio).child(nombre).child(norma.nodoFirebase).get()
            .addOnSuccessListener { snapshot ->
                listaResultados.removeAllViews()
                val ultimo = snapshot.children.maxByOrNull { it.key ?: "0" }
                agregarTarjetaAnio(anio, ultimo)
                btnTomar.visibility = View.VISIBLE
                btnTomar.text = textoBoton(ultimo)
            }
            .addOnFailureListener {
                listaResultados.removeAllViews()
                mostrarSinRegistros()
                btnTomar.visibility = View.VISIBLE
                btnTomar.text = "Tomar examen"
            }
    }

    private fun agregarTarjetaAnio(anio: String, ultimo: DataSnapshot?) {
        val versionGuardada = ultimo?.child("version")?.getValue(Int::class.java)
        val esActual = anio == CapacitacionesActivity.anioActual()
        val respondido = ultimo != null &&
            (norma.version == null || versionGuardada == norma.version || !esActual)

        val card = CardView(this).apply {
            radius = dpToPx(12).toFloat()
            cardElevation = dpToPx(3).toFloat()
            setCardBackgroundColor(Color.parseColor("#1E2D3D"))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = dpToPx(12)
            layoutParams = params
        }

        val inner = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(16), dpToPx(14), dpToPx(16), dpToPx(14))
        }

        // Encabezado: año + badge estado
        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        val tvAnio = TextView(this).apply {
            text = anio
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        fila.addView(tvAnio)

        val badge = TextView(this).apply {
            val (label, color) = when {
                !respondido -> Pair("Pendiente", "#4B5E6C")
                else -> {
                    val cal = (ultimo?.child("calificacion")?.value as? Number)?.toDouble() ?: 0.0
                    val candado = if (esActual || consultar_datos.esAdministrador) "" else "🔒 "
                    if (cal >= 6.0) Pair("${candado}✓ Aprobado", "#2E7D32")
                    else Pair("${candado}✗ Reprobado", "#C62828")
                }
            }
            text = label
            textSize = 12f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
            background = ContextCompat.getDrawable(context, R.drawable.bg_estado_examen)
            backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
            setPadding(dpToPx(10), dpToPx(4), dpToPx(10), dpToPx(4))
        }
        fila.addView(badge)
        inner.addView(fila)

        if (respondido) {
            val cal = (ultimo?.child("calificacion")?.value as? Number)?.toDouble() ?: 0.0
            val fecha = ultimo?.child("fecha")?.getValue(String::class.java)

            val tvCal = TextView(this).apply {
                text = String.format(Locale.US, "%.1f / 10.0", cal)
                textSize = 26f
                setTypeface(null, Typeface.BOLD)
                setTextColor(if (cal >= 6.0) Color.parseColor("#4CAF50") else Color.parseColor("#EF5350"))
                val p = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                p.topMargin = dpToPx(8)
                layoutParams = p
            }
            inner.addView(tvCal)

            if (!fecha.isNullOrBlank()) {
                val tvFecha = TextView(this).apply {
                    text = fecha
                    textSize = 13f
                    setTextColor(Color.parseColor("#8FA3B5"))
                    val p = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    p.topMargin = dpToPx(2)
                    layoutParams = p
                }
                inner.addView(tvFecha)
            }
        }

        card.addView(inner)

        // Tap en tarjeta para ir al examen de ese año (solo si es "Todos")
        if (anioSeleccionado == TODOS) {
            card.isClickable = true
            card.isFocusable = true
            val tv = android.util.TypedValue()
            theme.resolveAttribute(android.R.attr.selectableItemBackground, tv, true)
            card.foreground = ContextCompat.getDrawable(this, tv.resourceId)
            card.setOnClickListener { abrirExamen(anio) }
        }

        listaResultados.addView(card)
    }

    private fun mostrarCargando() {
        val tv = TextView(this).apply {
            text = "Cargando…"
            textSize = 14f
            setTextColor(Color.parseColor("#8FA3B5"))
            gravity = android.view.Gravity.CENTER
            val p = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            p.topMargin = dpToPx(24)
            layoutParams = p
        }
        listaResultados.addView(tv)
    }

    private fun mostrarSinRegistros() {
        val tv = TextView(this).apply {
            text = "Sin registros para este año"
            textSize = 14f
            setTextColor(Color.parseColor("#8FA3B5"))
            gravity = android.view.Gravity.CENTER
            val p = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            p.topMargin = dpToPx(24)
            layoutParams = p
        }
        listaResultados.addView(tv)
    }

    private fun textoBoton(ultimo: DataSnapshot?): String {
        val esActual = anioSeleccionado == CapacitacionesActivity.anioActual()
        val versionGuardada = ultimo?.child("version")?.getValue(Int::class.java)
        val respondido = ultimo != null &&
            (norma.version == null || versionGuardada == norma.version || !esActual)
        return when {
            !respondido -> "Tomar examen"
            esActual || consultar_datos.esAdministrador -> "Ver / Repetir examen"
            else -> "Ver calificación"
        }
    }

    private fun abrirExamen(anio: String) {
        val esActual = anio == CapacitacionesActivity.anioActual()
        if (esActual || consultar_datos.esAdministrador) {
            startActivity(Intent(this, norma.pantalla).putExtra(CapacitacionesActivity.EXTRA_ANIO, anio))
            return
        }
        // Año pasado: solo mostrar calificación
        FirebaseDatabase.getInstance().reference
            .child("Capacitaciones").child("Examenes")
            .child(anio)
            .child(MainActivity.NombreUsuarioCompanion)
            .child(norma.nodoFirebase).get()
            .addOnSuccessListener { snapshot ->
                val ultimo = snapshot.children.maxByOrNull { it.key ?: "0" }
                val versionGuardada = ultimo?.child("version")?.getValue(Int::class.java)
                val respondido = ultimo != null &&
                    (norma.version == null || versionGuardada == norma.version || !esActual)

                if (!respondido) {
                    startActivity(Intent(this, norma.pantalla).putExtra(CapacitacionesActivity.EXTRA_ANIO, anio))
                    return@addOnSuccessListener
                }

                val cal = (ultimo?.child("calificacion")?.value as? Number)?.toDouble() ?: 0.0
                val fecha = ultimo?.child("fecha")?.getValue(String::class.java)
                val fechaTxt = if (fecha.isNullOrBlank()) "" else "Fecha: $fecha\n"
                AlertDialog.Builder(this)
                    .setTitle(norma.codigo)
                    .setMessage(
                        "Año: $anio\n$fechaTxt" +
                            String.format(Locale.US, "Calificación: %.2f / 10.0\n", cal) +
                            "Resultado: ${if (cal >= 6.0) "APROBADO" else "REPROBADO"}\n\n" +
                            "Las respuestas solo pueden consultarse durante el año en curso."
                    )
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
    }

    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()
}
