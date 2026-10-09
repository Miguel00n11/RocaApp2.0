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

class ExamenNorma109Activity : AppCompatActivity() {

    private lateinit var containerPreguntas: LinearLayout
    private lateinit var btnEnviar: Button
    private lateinit var txtNombreUsuario: TextView
    private lateinit var txtFechaActual: TextView
    private lateinit var txtCalificacionActual: TextView
    private lateinit var database: DatabaseReference
    private val gruposPreguntas = mutableMapOf<Int, RadioGroup>()
    private var esRevisionExamen = false

    private val nombreExamen = "EXAMEN NORMA 109"
    private val totalPreguntas = 8

    private val respuestasCorrectas = mapOf(
        1 to "Obtener planicidad y perpendicularidad en sus bases para su ensayo.",
        2 to "Entre 130 y 150 °C",
        3 to "Entre 20 y 80 s",
        4 to "300 kgf",
        5 to "Todos",
        6 to "Por lo menos 6",
        7 to "3-6 mm",
        8 to "5-8 mm"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_examen_norma_109)

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

        data class Pregunta(val texto: String, val opciones: List<String>)

        val preguntas = listOf(
            Pregunta("1.- ¿cuál es el objetivo de la norma?", listOf("Obtener Muestra representativa del concreto fresco.", "Obtener planicidad y perpendicularidad en sus bases para su ensayo.", "Determinar la consistencia de concreto hidráulico en estado fresco.")),
            Pregunta("2.- ¿A qué temperatura se debe calentar el compuesto de cabeceo?", listOf("Entre 130 y 150 °C", "Entre 100 y 150 °C", "Entre 160 y 180 °C", "Entre 120 y 130 °C")),
            Pregunta("3.- ¿En cuánto tiempo deben fallar los cubos de compuesto de cabeceo?", listOf("Entre 30 y 50 s", "Entre 20 y 70 s", "Entre 20 y 80 s", "Entre 120 y 130 s")),
            Pregunta("4.- ¿Cuál es la resistencia mínima del compuesto de cabeceo?", listOf("200 kgf", "150 kgf", "300 kgf", "350 kgf")),
            Pregunta("5.- ¿A cuántos cilindros se les debe verificar la adherencia?", listOf("Uno de cada 10", "Todos", "Uno sí y uno no", "Al núm. 5, 10 y 15")),
            Pregunta("6.- ¿Cuántos fragmentos debemos obtener para verificar los espesores de las capas de cabeceo?", listOf("Por lo menos 3", "Por lo menos 5", "Por lo menos 10", "Por lo menos 6")),
            Pregunta("7.- ¿De cuánto debe ser el espesor promedio de cada capa de cabeceo?", listOf("3-5 mm", "5-8 mm", "2-4 mm", "3-6 mm")),
            Pregunta("8.- ¿De cuánto debe ser el espesor individual en cualquier punto de oquedad de cualquier capa de cabeceo?", listOf("3-5 mm", "5-8 mm", "2-4 mm", "3-6 mm"))
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
            gruposPreguntas[preguntaIndex] = radioGroup

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

        var respuestasContestadas = 0
        var aciertos = 0

        for (numeroPregunta in 1..totalPreguntas) {
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
        val anioActual = CapacitacionesActivity.anioDelExamen(intent)
        val calificacion = (aciertos.toDouble() / totalPreguntas.toDouble() * 10.0)
        val calificacionFormateada = String.format(Locale.US, "%.2f", calificacion).toDouble()

        val examenId = System.currentTimeMillis().toString()
        val examenData = hashMapOf(
            "id" to examenId,
            "nombre" to nombre,
            "fecha" to fecha,
            "usuario" to MainActivity.NombreUsuarioCompanion,
            "examen" to nombreExamen,
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

    private fun cargarRespuestasGuardadas() {
        val nombre = txtNombreUsuario.text.toString().removePrefix("NOMBRE: ").trim()
        val anioActual = CapacitacionesActivity.anioDelExamen(intent)

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

                val respuestas = ultimoExamen.child("respuestas")
                if (!respuestas.exists()) return@addOnSuccessListener

                esRevisionExamen = true

                var aciertos = 0
                for (numeroPregunta in 1..totalPreguntas) {
                    val respuestaGuardada = respuestas.child(numeroPregunta.toString()).getValue(String::class.java)
                        ?: continue
                    val respuestaCorrecta = respuestasCorrectas[numeroPregunta]
                    if (respuestaGuardada == respuestaCorrecta) aciertos += 1

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
}
