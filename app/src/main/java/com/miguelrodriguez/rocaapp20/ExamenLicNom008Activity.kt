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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExamenLicNom008Activity : AppCompatActivity() {

    private lateinit var containerPreguntas: LinearLayout
    private lateinit var btnEnviar: Button
    private lateinit var txtNombreUsuario: TextView
    private lateinit var txtFechaActual: TextView
    private lateinit var txtCalificacionActual: TextView
    private lateinit var database: DatabaseReference
    private val gruposPreguntas = mutableListOf<RadioGroup>()
    private val spinnersPreguntas = mutableMapOf<Int, Spinner>()
    private var esRevisionExamen = false

    private val respuestasCorrectas = mapOf(
        1 to "La Secretaría de Economía",
        2 to "Acreditación",
        3 to "Evaluación de la conformidad",
        4 to "La norma oficial es de carácter obligatorio y las normas mexicanas son voluntarias.",
        5 to "Cierto",
        6 to "Falso",
        7 to "Falso",
        8 to "Falso",
        9 to "Cierto",
        10 to "Cierto"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_examen_lic_nom008)

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
            Pregunta("1.- ¿A qué secretaría le corresponde aplicar la ley de infraestructura de la calidad?", listOf("La Secretaría de Trabajo y Previsión Social", "La Secretaría de Economía", "La Secretaría de Agricultura", "La secretaría de acreditación"), "radio"),
            Pregunta("2.- Acto por el cual una entidad de acreditación reconoce la competencia técnica y confiabilidad de los resultados, es la definición de:", listOf("Certificación", "Calibración", "Evaluación de la conformidad", "Acreditación"), "radio"),
            Pregunta("3.- Permite demostrar el cumplimiento con las Normas Oficiales Mexicanas, Estándares, Normas Internacionales ahí referidos o de otras disposiciones legales. Comprende, entre otros, los procedimientos de muestreo, prueba, inspección, evaluación y certificación:", listOf("Certificación", "Calibración", "Evaluación de la conformidad", "Acreditación"), "radio"),
            Pregunta("4.- ¿Cuál es la diferencia entre una norma Oficial mexicana y una norma mexicana?", listOf("La norma oficial es de carácter obligatorio y las normas mexicanas son voluntarias.", "Las normas mexicanas son de carácter obligatorio y las normas oficiales son voluntarias.", "No existe diferencia."), "radio"),
            Pregunta("5.- Los símbolos que deriven de nombres propios se podrán expresar con mayúsculas (Cierto o Falso)", listOf("Cierto", "Falso"), "radio"),
            Pregunta("6.- Se debe colocar un punto después del símbolo de la unidad (Cierto o Falso)", listOf("Cierto", "Falso"), "radio"),
            Pregunta("7.- Los símbolos de las unidades se pueden pluralizar (ejemplo: mts, kgs, segs) (Cierto o Falso)", listOf("Cierto", "Falso"), "radio"),
            Pregunta("8.- Se pueden utilizar varias líneas inclinadas (ejemplo m/s/h ó m*kg/s/A) (Cierto o Falso)", listOf("Cierto", "Falso"), "radio"),
            Pregunta("9.- Los símbolos de los prefijos deben ser impresos en caracteres romanos (rectos), sin espacio entre el símbolo del prefijo y el símbolo de la unidad (Cierto o Falso)", listOf("Cierto", "Falso"), "radio"),
            Pregunta("10.- Se puede usar puntos o comas, siempre y cuando sean constantes (Cierto o Falso)", listOf("Cierto", "Falso"), "radio")
        )

        var preguntaIndex = 1
        for (pregunta in preguntas) {
            val titulo = TextView(this).apply {
                text = pregunta.texto
                textSize = 16f
                setPadding(0, 20, 0, 8)
            }
            containerPreguntas.addView(titulo)

            if (pregunta.tipo == "spinner") {
                val spinner = Spinner(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    setPadding(24, 8, 0, 8)
                }

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, pregunta.opciones)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter

                containerPreguntas.addView(spinner)
                spinnersPreguntas[preguntaIndex] = spinner
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
                gruposPreguntas.add(radioGroup)
            }

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
        val totalPreguntas = 10

        var respuestasContestadas = 0
        var aciertos = 0

        for (numeroPregunta in 1..totalPreguntas) {
            val clave = numeroPregunta.toString()
            val seleccion: String? = if (spinnersPreguntas.containsKey(numeroPregunta)) {
                obtenerRespuestaDelSpinner(numeroPregunta)
            } else {
                val indexRadio = numeroPregunta - 1
                if (indexRadio < gruposPreguntas.size) {
                    obtenerRespuestaSeleccionada(gruposPreguntas[indexRadio])
                } else {
                    null
                }
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
            "examen" to "EXAMEN LIC Y NOM-008-SE-2021",
            "calificacion" to calificacionFormateada,
            "respuestas" to respuestasSeleccionadas,
            "respuestasCorrectas" to respuestasCorrectasMap,
            "fechaRegistro" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )

        database.child("Capacitaciones")
            .child("Examenes")
            .child(anioActual)
            .child(nombre)
            .child("EXAMEN LIC Y NOM-008-SE-2021")
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

    private fun obtenerRespuestaDelSpinner(numeroPregunta: Int): String? {
        val spinner = spinnersPreguntas[numeroPregunta]
        val seleccion = spinner?.selectedItem?.toString()
        return if (seleccion.isNullOrEmpty() || seleccion == "Selecciona una opción") null else seleccion
    }

    private fun cargarRespuestasGuardadas() {
        val nombre = txtNombreUsuario.text.toString().removePrefix("NOMBRE: ").trim()
        val anioActual = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())

        database.child("Capacitaciones")
            .child("Examenes")
            .child(anioActual)
            .child(nombre)
            .child("EXAMEN LIC Y NOM-008-SE-2021")
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) return@addOnSuccessListener

                val ultimoExamen = snapshot.children.maxByOrNull { it.key ?: "0" }
                    ?: return@addOnSuccessListener

                val respuestas = ultimoExamen.child("respuestas")
                val respuestasCorrectasDB = ultimoExamen.child("respuestasCorrectas")

                if (!respuestas.exists()) return@addOnSuccessListener

                esRevisionExamen = true

                val totalPreguntas = 10
                for (numeroPregunta in 1..totalPreguntas) {
                    val respuestaGuardada = respuestas.child(numeroPregunta.toString()).getValue(String::class.java)
                    val respuestaCorrecta = if (respuestasCorrectasDB.exists()) {
                        respuestasCorrectasDB.child(numeroPregunta.toString()).getValue(String::class.java)
                    } else {
                        respuestasCorrectas[numeroPregunta]?.toString()
                    }

                    if (respuestaGuardada == null) continue

                    if (spinnersPreguntas.containsKey(numeroPregunta)) {
                        // Manejar Spinner
                        val spinner = spinnersPreguntas[numeroPregunta]
                        val adapter = spinner?.adapter as? ArrayAdapter<String>
                        val posicion = adapter?.getPosition(respuestaGuardada) ?: -1
                        if (posicion >= 0) {
                            spinner?.setSelection(posicion)
                        }
                        spinner?.isEnabled = false
                    } else {
                        // Manejar RadioGroup
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

