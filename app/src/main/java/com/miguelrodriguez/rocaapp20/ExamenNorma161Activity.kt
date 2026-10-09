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

class ExamenNorma161Activity : AppCompatActivity() {

    private lateinit var containerPreguntas: LinearLayout
    private lateinit var btnEnviar: Button
    private lateinit var txtNombreUsuario: TextView
    private lateinit var txtFechaActual: TextView
    private lateinit var txtCalificacionActual: TextView
    private lateinit var database: DatabaseReference
    private val gruposPreguntas = mutableMapOf<Int, RadioGroup>()
    private var esRevisionExamen = false

    private val nombreExamen = "EXAMEN NORMA 161"
    private val totalPreguntas = 10

    // Opciones compartidas por las preguntas 2 a 6 (métodos de muestreo según el equipo)
    private val opcionTodosLosMetodos = "La que aplique de todos los métodos especificados en la norma"
    private val opcionMitadDescarga = "A la mitad de la descarga"
    private val opcionEsperar7Min = "Esperar 7 min a que el camión mezcle, después, se realiza el despunte de 10 litros y después se toma la muestra"
    private val opcionDespuntar10L = "Despuntar de 10 L. Se realiza el revenimiento. Se muestrea entre el 15% y 85%"
    private val opcionCincoPuntos = "Al término de la descarga por lo menos en 5 puntos diferentes, posteriormente, se tiene que remezclar."
    private val opcionesMuestreo = listOf(opcionTodosLosMetodos, opcionMitadDescarga, opcionEsperar7Min, opcionDespuntar10L, opcionCincoPuntos)

    private val respuestasCorrectas = mapOf(
        1 to "Obtener una muestra representativa técnica del concreto para su ensayo y cumplir los requisitos de calidad",
        2 to opcionMitadDescarga,
        3 to opcionCincoPuntos,
        4 to opcionEsperar7Min,
        5 to opcionDespuntar10L,
        6 to opcionTodosLosMetodos,
        7 to "Lo suficiente para las pruebas",
        8 to "Para asegurar su uniformidad",
        9 to "15 min",
        10 to "2.5 min"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_examen_norma_161)

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
            Pregunta("1.- ¿cuál es el objetivo de la norma?", listOf("Obtener una muestra representativa técnica del concreto para su ensayo y cumplir los requisitos de calidad", "Determinar la consistencia de concreto hidráulico en estado fresco.", "Establecer los procedimientos para elaborar y curar, ya sea en obra o laboratorio, los especímenes de concreto utilizados para los ensayes que requieran.")),
            Pregunta("2.- ¿cómo se obtiene la muestra de una mezcladora estacionaria (fija y basculante)?", opcionesMuestreo),
            Pregunta("3.- ¿cómo se obtiene la muestra de una pavimentadora?", opcionesMuestreo),
            Pregunta("4.- ¿cómo se obtiene la muestra de una olla de camión mezclador o agitador en planta premezcladora?", opcionesMuestreo),
            Pregunta("5.- ¿cómo se obtiene la muestra de una olla de camión mezclador o agitador en obra?", opcionesMuestreo),
            Pregunta("6.- ¿cómo se obtiene la muestra de camiones caja, con o sin agitadores, de volteo u otros tipos?", opcionesMuestreo),
            Pregunta("7.- ¿cuánta cantidad se debe muestrear?", listOf("10 kg", "Toda la carretilla", "Lo suficiente para las pruebas", "Dos carretillas")),
            Pregunta("8.- ¿para qué se debe remezclar la muestra?", listOf("Para segregar el concreto", "Para asegurar su uniformidad", "Para verificar su consistencia", "Para cumplir con la norma de compresión")),
            Pregunta("9.- ¿en cuánto tiempo se debe terminar de tomar la muestra?", listOf("10 min", "20 min", "15 min", "5 min")),
            Pregunta("10.- ¿en cuánto tiempo se debe realizar la prueba de revenimiento?", listOf("10 min", "20 min", "15 min", "2.5 min"))
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
