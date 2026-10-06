package com.miguelrodriguez.rocaapp20

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.content.Intent
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
    private val gruposPreguntas = mutableListOf<RadioGroup>()
    private var esRevisionExamen = false

    private val respuestasCorrectas = mapOf(
        1 to "Determinar la consistencia de concreto hidráulico en estado fresco.",
        2 to "Es la medida de consistencia del concreto fresco en términos de la disminución de altura",
        3 to "La primera a 7cm, la segunda a 15cm y la tercera a 30cm",
        4 to "25",
        5 to "2",
        6 to "Se desecha y se repite el procedimiento",
        7 to "20",
        8 to "35"
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
            Pregunta("2.- ¿Qué se entiende por revenimiento?", listOf("Es la medida de consistencia del concreto fresco en términos de la disminución de altura", "Es una medida de lo que desciende el cono de concreto.", "Es una prueba para determinar si el altero al concreto previo al colado."), "radio"),
            Pregunta("3.- ¿cuántas capas se llenan y a qué altura?", listOf("La primera a 6cm, la segunda a 12cm y la tercera a 28cm", "La primera a 7cm, la segunda a 15cm y la tercera a 30cm", "La primera a 8cm, la segunda a 15cm y la tercera a 30cm", "La primera a 10cm, la segunda a 15cm y la tercera a 30cm"), "radio"),
            Pregunta("4.- ¿cuántas penetraciones por capa se deben dar?", listOf("23", "30", "25", "41"), "radio"),
            Pregunta("5.- ¿cuánto debe penetrar la varilla en cada capa (cm)?", listOf("2", "4", "1"), "radio"),
            Pregunta("6.- ¿qué pasa si una porción del concreto se desliza o cae hacía un lado?", listOf("Se rechaza el concreto.", "se omite y se continúa con el procedimiento.", "Se desecha y se repite el procedimiento"), "radio"),
            Pregunta("7.- si en una obra, realizas dos revenimientos de la misma muestra de concreto, ¿cuál es la diferencia máxima en mm que puedes obtener entre la primera medida y la segunda?", listOf("20", "19", "35"), "radio"),
            Pregunta("8.- si en una obra, tú y un compañero realizan una prueba de revenimiento de un concreto que provenga de la misma muestra, ¿cuál es la diferencia máxima que pueden tener entre tú y tu compañero?", listOf("20", "19", "35"), "radio")
        )

        var preguntaIndex = 1
        for (pregunta in preguntas) {
            val titulo = TextView(this).apply {
                text = pregunta.texto
                textSize = 16f
                setPadding(0, 20, 0, 8)
            }
            containerPreguntas.addView(titulo)

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
            gruposPreguntas.add(radioGroup)

            preguntaIndex++
        }

        btnEnviar.setOnClickListener {
            guardarExamen()
        }

        cargarRespuestasGuardadas()
    }

    private fun guardarExamen() {
        val respuestasSeleccionadas = linkedMapOf<String, String>()
        val respuestasCorrectasMap = linkedMapOf<String, String>()
        val totalPreguntas = 8

        var respuestasContestadas = 0
        var aciertos = 0

        for (numeroPregunta in 1..totalPreguntas) {
            val clave = numeroPregunta.toString()
            val indexRadio = numeroPregunta - 1

            val seleccion: String? = if (indexRadio < gruposPreguntas.size) {
                obtenerRespuestaSeleccionada(gruposPreguntas[indexRadio])
            } else {
                null
            }

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
            "examen" to "EXAMEN NORMA 156",
            "calificacion" to calificacionFormateada,
            "respuestas" to respuestasSeleccionadas,
            "respuestasCorrectas" to respuestasCorrectasMap,
            "fechaRegistro" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )

        database.child("Capacitaciones")
            .child("Examenes")
            .child(anioActual)
            .child(nombre)
            .child("EXAMEN NORMA 156")
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

    private fun cargarRespuestasGuardadas() {
        val nombre = txtNombreUsuario.text.toString().removePrefix("NOMBRE: ").trim()
        val anioActual = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())

        database.child("Capacitaciones")
            .child("Examenes")
            .child(anioActual)
            .child(nombre)
            .child("EXAMEN NORMA 156")
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) return@addOnSuccessListener

                val ultimoExamen = snapshot.children.maxByOrNull { it.key ?: "0" }
                    ?: return@addOnSuccessListener

                val respuestas = ultimoExamen.child("respuestas")
                val respuestasCorrectasDB = ultimoExamen.child("respuestasCorrectas")

                if (!respuestas.exists()) return@addOnSuccessListener

                esRevisionExamen = true

                val totalPreguntas = 8
                for (numeroPregunta in 1..totalPreguntas) {
                    val respuestaGuardada = respuestas.child(numeroPregunta.toString()).getValue(String::class.java)
                    val respuestaCorrecta = if (respuestasCorrectasDB.exists()) {
                        respuestasCorrectasDB.child(numeroPregunta.toString()).getValue(String::class.java)
                    } else {
                        respuestasCorrectas[numeroPregunta]
                    }

                    if (respuestaGuardada == null) continue

                    val indexRadio = numeroPregunta - 1
                    if (indexRadio < gruposPreguntas.size) {
                        val radioGroup = gruposPreguntas[indexRadio]
                        for (i in 0 until radioGroup.childCount) {
                            val radioButton = radioGroup.getChildAt(i) as? RadioButton ?: continue
                            val esRespuestaSeleccionada = radioButton.text.toString() == respuestaGuardada
                            val esRespuestaCorrecta = radioButton.text.toString() == respuestaCorrecta

                            if (esRespuestaSeleccionada) {
                                radioGroup.check(radioButton.id)
                            }

                            // Mostrar indicadores visuales
                            if (esRespuestaCorrecta) {
                                // Respuesta correcta: mostrar ✓
                                radioButton.text = "${radioButton.text} ✓"
                                radioButton.setTextColor(android.graphics.Color.GREEN)
                            } else if (esRespuestaSeleccionada && respuestaGuardada != respuestaCorrecta) {
                                // Respuesta seleccionada pero incorrecta: mostrar ✗
                                radioButton.text = "${radioButton.text} ✗"
                                radioButton.setTextColor(android.graphics.Color.RED)
                            }

                            // Deshabilitar todos los RadioButtons en modo revisión
                            radioButton.isEnabled = false
                        }
                    }
                }

                // Recalcular la calificación con las respuestas correctas actualizadas
                var aciertos = 0
                var totalRespuestas = 0

                for (numeroPregunta in 1..totalPreguntas) {
                    val respuestaGuardada = respuestas.child(numeroPregunta.toString()).getValue(String::class.java)

                    if (respuestaGuardada != null) {
                        totalRespuestas += 1
                        val respuestaCorrecta = respuestasCorrectas[numeroPregunta]

                        if (respuestaGuardada == respuestaCorrecta) {
                            aciertos += 1
                        }
                    }
                }

                val calificacionRecalculada = if (totalRespuestas > 0) {
                    (aciertos.toDouble() / totalRespuestas.toDouble() * 10.0)
                } else {
                    0.0
                }
                val calificacionFormateada = String.format(Locale.US, "%.2f", calificacionRecalculada).toDouble()

                txtCalificacionActual.text = String.format(Locale.US, "CALIFICACIÓN: %.2f / 10.0", calificacionFormateada)

                // Actualizar la calificación en Firebase si cambió
                ultimoExamen.ref.child("calificacion").setValue(calificacionFormateada)

                // Cambiar texto del botón en modo revisión
                if (esRevisionExamen) {
                    btnEnviar.text = "Revisar examen"
                    btnEnviar.isEnabled = false
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "No se pudieron cargar respuestas previas: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

