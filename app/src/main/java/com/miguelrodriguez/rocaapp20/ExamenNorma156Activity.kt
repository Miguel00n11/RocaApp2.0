package com.miguelrodriguez.rocaapp20

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.content.Intent
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExamenNorma156Activity : AppCompatActivity() {

    private lateinit var containerPreguntas: LinearLayout
    private lateinit var btnEnviar: Button
    private lateinit var txtNombreUsuario: TextView
    private lateinit var txtFechaActual: TextView
    private lateinit var txtCalificacionActual: TextView
    private lateinit var database: DatabaseReference
    private val gruposPreguntas = mutableMapOf<Int, RadioGroup>()
    private val spinnersTabla = mutableMapOf<String, Spinner>()
    private val indicadoresTabla = mutableMapOf<String, TextView>()
    private var esRevisionExamen = false

    private val nombreExamen = "EXAMEN NORMA 156"
    // Versión del cuestionario: los exámenes guardados con otra versión no se revisan ni recalifican
    private val versionExamen = 2
    private val totalPreguntas = 10
    private val opcionPorDefecto = "Selecciona una opción"

    private val respuestasCorrectas = mapOf(
        1 to "Determinar la consistencia de concreto hidráulico en estado fresco.",
        2 to "Es la medida de consistencia del concreto fresco en términos de la disminución de altura",
        4 to "La primera a 7cm, la segunda a 15cm y la tercera a 30cm",
        5 to "25",
        6 to "2",
        7 to "Se desecha y se repite el procedimiento",
        9 to "20",
        10 to "35"
    )

    // Preguntas tipo tabla: cada fila se contesta con un Spinner (clave Firebase -> fila, respuesta correcta)
    private class Tabla(val filas: LinkedHashMap<String, Pair<String, String>>, val opciones: List<String>)

    private val tablas = mapOf(
        3 to Tabla(
            linkedMapOf(
                "3a" to Pair("Diámetro inferior (mm):", "200 ± 3"),
                "3b" to Pair("Diámetro superior (mm):", "100 ± 3"),
                "3c" to Pair("Altura (mm):", "300 ± 3")
            ),
            listOf("100 ± 3", "200 ± 3", "300 ± 3")
        ),
        8 to Tabla(
            linkedMapOf(
                "8a" to Pair("Revenimiento nominal menor de 50 mm, tolerancia (mm):", "15"),
                "8b" to Pair("Revenimiento nominal de 50 a 100 mm, tolerancia (mm):", "25"),
                "8c" to Pair("Revenimiento nominal mayor de 100 mm, tolerancia (mm):", "35")
            ),
            listOf("15", "25", "35")
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_examen_norma_156)

        database = FirebaseDatabase.getInstance().reference
        containerPreguntas = findViewById(R.id.containerPreguntas)
        btnEnviar = findViewById(R.id.btnEnviarExamen)
        txtNombreUsuario = findViewById(R.id.txtNombreUsuario)
        txtFechaActual = findViewById(R.id.txtFechaActual)
        txtCalificacionActual = findViewById(R.id.txtCalificacionActual)

        val nombreUsuario = MainActivity.NombreUsuarioCompanion
            .takeIf { it != "NombreUsuario" && it.isNotBlank() }
            ?: "Usuario no identificado"

        val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        txtNombreUsuario.text = "NOMBRE: $nombreUsuario"
        txtFechaActual.text = "FECHA: $fechaActual"

        data class Pregunta(val texto: String, val opciones: List<String>, val tipo: String)

        val preguntas = listOf(
            Pregunta("1.- ¿cuál es el objetivo de la norma?", listOf("Determinar la resistencia a la compresión del concreto.", "Determinar la consistencia de concreto hidráulico en estado fresco.", "Obtener Muestra representativa del concreto fresco."), "radio"),
            Pregunta("2.- ¿Qué se entiende por revenimiento?", listOf("Es la medida de consistencia del concreto fresco en términos de la disminución de altura", "Es una medida de lo que desciende el cono de concreto.", "Es una prueba para determinar si se alteró el concreto previo al colado."), "radio"),
            Pregunta("3.- ¿cuánto debe medir el cono en:", emptyList(), "tabla"),
            Pregunta("4.- ¿cuántas capas se llenan y a qué altura?", listOf("La primera a 6cm, la segunda a 12cm y la tercera a 28cm", "La primera a 7cm, la segunda a 15cm y la tercera a 30cm", "La primera a 8cm, la segunda a 15cm y la tercera a 30cm", "La primera a 10cm, la segunda a 15cm y la tercera a 30cm"), "radio"),
            Pregunta("5.- ¿cuántas penetraciones por capa se deben dar?", listOf("23", "30", "25", "41"), "radio"),
            Pregunta("6.- ¿cuánto debe penetrar la varilla en cada capa (cm)?", listOf("2", "4", "1"), "radio"),
            Pregunta("7.- ¿qué pasa si una porción del concreto se desliza o cae hacia un lado?", listOf("Se rechaza el concreto.", "Se omite y se continúa con el procedimiento.", "Se desecha y se repite el procedimiento"), "radio"),
            Pregunta("8.- completa la tabla de tolerancias", emptyList(), "tabla"),
            Pregunta("9.- si en una obra, realizas dos revenimientos de la misma muestra de concreto, ¿cuál es la diferencia máxima en mm que puedes obtener entre la primera medida y la segunda?", listOf("20", "19", "35"), "radio"),
            Pregunta("10.- si en una obra, tú y un compañero realizan una prueba de revenimiento de un concreto que provenga de la misma muestra, ¿cuál es la diferencia máxima que pueden tener entre tú y tu compañero?", listOf("20", "19", "35"), "radio")
        )

        var preguntaIndex = 1
        for (pregunta in preguntas) {
            val titulo = TextView(this).apply {
                text = pregunta.texto
                textSize = 16f
                setPadding(0, 20, 0, 8)
            }
            containerPreguntas.addView(titulo)

            val tabla = tablas[preguntaIndex]
            if (pregunta.tipo == "tabla" && tabla != null) {
                agregarTabla(tabla)
            } else {
                val radioGroup = RadioGroup(this).apply {
                    orientation = LinearLayout.VERTICAL
                }

                for (opcion in pregunta.opciones) {
                    val radio = RadioButton(this).apply {
                        text = opcion
                        textSize = 15f
                        setPadding(24, 8, 0, 8)
                    }
                    radioGroup.addView(radio)
                }

                containerPreguntas.addView(radioGroup)
                gruposPreguntas[preguntaIndex] = radioGroup
            }

            preguntaIndex++
        }

        btnEnviar.setOnClickListener {
            guardarExamen()
        }

        cargarRespuestasGuardadas()
    }

    private fun agregarTabla(tabla: Tabla) {
        for ((clave, fila) in tabla.filas) {
            val etiqueta = TextView(this).apply {
                text = fila.first
                textSize = 15f
                setPadding(24, 8, 0, 0)
            }
            containerPreguntas.addView(etiqueta)

            val spinner = Spinner(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(24, 8, 0, 8)
            }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf(opcionPorDefecto) + tabla.opciones)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            containerPreguntas.addView(spinner)
            spinnersTabla[clave] = spinner

            // Indicador ✓/✗ visible solo en modo revisión
            val indicador = TextView(this).apply {
                textSize = 14f
                setPadding(24, 0, 0, 8)
                visibility = View.GONE
            }
            containerPreguntas.addView(indicador)
            indicadoresTabla[clave] = indicador
        }
    }

    private fun guardarExamen() {
        val respuestasSeleccionadas = linkedMapOf<String, String>()
        val respuestasCorrectasMap = linkedMapOf<String, String>()

        var respuestasContestadas = 0
        var aciertos = 0

        for (numeroPregunta in 1..totalPreguntas) {
            val tabla = tablas[numeroPregunta]
            if (tabla != null) {
                // Cada tabla cuenta como una sola pregunta: es correcta solo si todas sus filas lo son
                var filasContestadas = 0
                var filasCorrectas = 0
                for ((clave, fila) in tabla.filas) {
                    val seleccion = obtenerRespuestaDelSpinner(clave) ?: continue
                    filasContestadas += 1
                    respuestasSeleccionadas[clave] = seleccion
                    respuestasCorrectasMap[clave] = fila.second
                    if (seleccion == fila.second) filasCorrectas += 1
                }
                if (filasContestadas == tabla.filas.size) {
                    respuestasContestadas += 1
                    if (filasCorrectas == tabla.filas.size) aciertos += 1
                }
                continue
            }

            val clave = numeroPregunta.toString()
            val seleccion = gruposPreguntas[numeroPregunta]?.let { obtenerRespuestaSeleccionada(it) }

            if (seleccion != null) {
                respuestasContestadas += 1
                respuestasSeleccionadas[clave] = seleccion
                respuestasCorrectasMap[clave] = respuestasCorrectas[numeroPregunta] ?: ""
                if (respuestasCorrectas[numeroPregunta] == seleccion) {
                    aciertos += 1
                }
            }
        }

        if (respuestasContestadas < totalPreguntas) {
            Toast.makeText(this, "Debes responder todas las preguntas antes de enviar.", Toast.LENGTH_SHORT).show()
            return
        }

        val nombre = txtNombreUsuario.text.toString().removePrefix("NOMBRE: ").trim()
        val fecha = txtFechaActual.text.toString().removePrefix("FECHA: ").trim()
        val anioActual = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        val calificacion = (aciertos.toDouble() / totalPreguntas.toDouble() * 10.0)
        val calificacionFormateada = String.format(Locale.US, "%.2f", calificacion).toDouble()

        val examenId = System.currentTimeMillis().toString()
        val examenData = hashMapOf(
            "id" to examenId,
            "nombre" to nombre,
            "fecha" to fecha,
            "usuario" to MainActivity.NombreUsuarioCompanion,
            "examen" to nombreExamen,
            "version" to versionExamen,
            "calificacion" to calificacionFormateada,
            "respuestas" to respuestasSeleccionadas,
            "respuestasCorrectas" to respuestasCorrectasMap,
            "fechaRegistro" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )

        database.child("Capacitaciones")
            .child("Examenes")
            .child(anioActual)
            .child(nombre)
            .child(nombreExamen)
            .child(examenId)
            .setValue(examenData)
            .addOnSuccessListener {
                Toast.makeText(this, "Examen guardado correctamente en Firebase", Toast.LENGTH_SHORT).show()

                val intentResultado = Intent(this, ResultadoExamenActivity::class.java)
                intentResultado.putExtra("nombre", nombre)
                intentResultado.putExtra("fecha", fecha)
                intentResultado.putExtra("aciertos", aciertos)
                intentResultado.putExtra("total", totalPreguntas)
                intentResultado.putExtra("calificacion", calificacionFormateada)
                startActivity(intentResultado)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "No se pudo guardar el examen: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun obtenerRespuestaSeleccionada(radioGroup: RadioGroup): String? {
        val idSeleccionado = radioGroup.checkedRadioButtonId
        if (idSeleccionado == View.NO_ID) return null
        val radioButton = radioGroup.findViewById<RadioButton>(idSeleccionado)
        return radioButton?.text?.toString()
    }

    private fun obtenerRespuestaDelSpinner(clave: String): String? {
        val seleccion = spinnersTabla[clave]?.selectedItem?.toString()
        return if (seleccion.isNullOrEmpty() || seleccion == opcionPorDefecto) null else seleccion
    }

    private fun cargarRespuestasGuardadas() {
        val nombre = txtNombreUsuario.text.toString().removePrefix("NOMBRE: ").trim()
        val anioActual = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())

        database.child("Capacitaciones")
            .child("Examenes")
            .child(anioActual)
            .child(nombre)
            .child(nombreExamen)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) return@addOnSuccessListener

                val ultimoExamen = snapshot.children.maxByOrNull { it.key ?: "0" }
                    ?: return@addOnSuccessListener

                // Un examen del cuestionario anterior no corresponde a estas preguntas: se permite contestar de nuevo
                val versionGuardada = ultimoExamen.child("version").getValue(Int::class.java)
                if (versionGuardada != versionExamen) return@addOnSuccessListener

                val respuestas = ultimoExamen.child("respuestas")
                if (!respuestas.exists()) return@addOnSuccessListener

                esRevisionExamen = true

                for (numeroPregunta in 1..totalPreguntas) {
                    val tabla = tablas[numeroPregunta]
                    if (tabla != null) {
                        mostrarRevisionTabla(tabla, respuestas)
                        continue
                    }

                    val respuestaGuardada = respuestas.child(numeroPregunta.toString()).getValue(String::class.java)
                        ?: continue
                    val respuestaCorrecta = respuestasCorrectas[numeroPregunta]
                    val radioGroup = gruposPreguntas[numeroPregunta] ?: continue

                    for (i in 0 until radioGroup.childCount) {
                        val radioButton = radioGroup.getChildAt(i) as? RadioButton ?: continue
                        val esRespuestaSeleccionada = radioButton.text.toString() == respuestaGuardada
                        val esRespuestaCorrecta = radioButton.text.toString() == respuestaCorrecta

                        if (esRespuestaSeleccionada) {
                            radioGroup.check(radioButton.id)
                        }

                        // Mostrar indicadores visuales
                        if (esRespuestaCorrecta) {
                            radioButton.text = "${radioButton.text} ✓"
                            radioButton.setTextColor(android.graphics.Color.GREEN)
                        } else if (esRespuestaSeleccionada) {
                            radioButton.text = "${radioButton.text} ✗"
                            radioButton.setTextColor(android.graphics.Color.RED)
                        }

                        // Deshabilitar todos los RadioButtons en modo revisión
                        radioButton.isEnabled = false
                    }
                }

                // Recalcular la calificación con las respuestas correctas actuales
                var aciertos = 0
                for (numeroPregunta in 1..totalPreguntas) {
                    val tabla = tablas[numeroPregunta]
                    val esCorrecta = if (tabla != null) {
                        tabla.filas.all { (clave, fila) ->
                            respuestas.child(clave).getValue(String::class.java) == fila.second
                        }
                    } else {
                        respuestas.child(numeroPregunta.toString()).getValue(String::class.java) ==
                            respuestasCorrectas[numeroPregunta]
                    }
                    if (esCorrecta) aciertos += 1
                }

                val calificacionRecalculada = aciertos.toDouble() / totalPreguntas.toDouble() * 10.0
                val calificacionFormateada = String.format(Locale.US, "%.2f", calificacionRecalculada).toDouble()

                txtCalificacionActual.text = String.format(Locale.US, "CALIFICACIÓN: %.2f / 10.0", calificacionFormateada)

                // Actualizar la calificación en Firebase si cambió
                ultimoExamen.ref.child("calificacion").setValue(calificacionFormateada)

                btnEnviar.text = "Revisar examen"
                btnEnviar.isEnabled = false
            }
            .addOnFailureListener {
                Toast.makeText(this, "No se pudieron cargar respuestas previas: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarRevisionTabla(tabla: Tabla, respuestas: DataSnapshot) {
        for ((clave, fila) in tabla.filas) {
            val spinner = spinnersTabla[clave] ?: continue
            val indicador = indicadoresTabla[clave] ?: continue
            val respuestaGuardada = respuestas.child(clave).getValue(String::class.java)

            @Suppress("UNCHECKED_CAST")
            val adapter = spinner.adapter as? ArrayAdapter<String>
            val posicion = respuestaGuardada?.let { adapter?.getPosition(it) } ?: -1
            if (posicion >= 0) spinner.setSelection(posicion)
            spinner.isEnabled = false

            if (respuestaGuardada == fila.second) {
                indicador.text = "✓ Correcto"
                indicador.setTextColor(android.graphics.Color.GREEN)
            } else {
                indicador.text = "✗ Respuesta correcta: ${fila.second}"
                indicador.setTextColor(android.graphics.Color.RED)
            }
            indicador.visibility = View.VISIBLE
        }
    }
}
