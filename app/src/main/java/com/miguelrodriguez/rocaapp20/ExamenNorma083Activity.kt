package com.miguelrodriguez.rocaapp20

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.content.Intent
import android.widget.ImageView
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

class ExamenNorma083Activity : AppCompatActivity() {

    private lateinit var containerPreguntas: LinearLayout
    private lateinit var btnEnviar: Button
    private lateinit var txtNombreUsuario: TextView
    private lateinit var txtFechaActual: TextView
    private lateinit var txtCalificacionActual: TextView
    private lateinit var database: DatabaseReference
    // RadioGroups por clave de Firebase ("1".."8" y "10a".."10c" para los tipos de falla)
    private val gruposPreguntas = mutableMapOf<String, RadioGroup>()
    private val spinnersTabla = mutableMapOf<String, Spinner>()
    private val indicadoresTabla = mutableMapOf<String, TextView>()
    private var esRevisionExamen = false

    private val nombreExamen = "EXAMEN NORMA 083"
    private val totalPreguntas = 10
    private val preguntaTabla = 9
    private val preguntaFallas = 10
    private val opcionPorDefecto = "Selecciona una opción"

    private val respuestasCorrectas = mapOf(
        1 to "Determinar la resistencia a la compresión del concreto.",
        2 to "0.05 mm",
        3 to "3%",
        4 to "Cada año o cada 40 000 ensayes",
        5 to "Cualquiera de las anteriores",
        6 to "2000 kg en 5 s",
        7 to "1 cada 10 especímenes",
        8 to "28 días, promediando 2 especímenes"
    )

    // Pregunta 9: tabla de tolerancias (clave Firebase -> fila, respuesta correcta)
    private val filasTabla = linkedMapOf(
        "9a" to Pair("Edad de prueba 24 h, tolerancia permisible (h):", "0.5"),
        "9b" to Pair("Edad de prueba 3 días, tolerancia permisible (h):", "2"),
        "9c" to Pair("Edad de prueba 7 días, tolerancia permisible (h):", "6"),
        "9d" to Pair("Edad de prueba 14 días, tolerancia permisible (h):", "12"),
        "9e" to Pair("Edad de prueba 28 días, tolerancia permisible (h):", "20")
    )
    private val opcionesTabla = listOf("0.5", "2", "6", "12", "20")

    // Pregunta 10: tipos de falla (clave Firebase -> dibujo, respuesta correcta)
    private val opcionFallaLados = "Fracturas en los lados de la parte inferior o superior, comúnmente con tapas no adheridas"
    private val opcionFallaColumnar = "Fracturas verticales en columna de extremo a extremo, conos no bien formados"
    private val opcionFallaConos = "Conos formados en ambos extremos, a menos de 2.5 cm de las grietas hasta las tapas"
    private val opcionesFallas = listOf(opcionFallaLados, opcionFallaColumnar, opcionFallaConos)

    private val subpreguntasFallas = linkedMapOf(
        "10a" to Pair(R.drawable.falla_cilindro_conos, opcionFallaConos),
        "10b" to Pair(R.drawable.falla_cilindro_columnar, opcionFallaColumnar),
        "10c" to Pair(R.drawable.falla_cilindro_lados, opcionFallaLados)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_examen_norma_083)

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
            Pregunta("2.- ¿de cuánto debe ser la planicidad de los bloques de apoyo?", listOf("0.05 mm", "3 mm", "0.025 mm", "1 mm"), "radio"),
            Pregunta("3.- ¿cuál es el error máximo permitido para la máquina?", listOf("1%", "3%", "2%", "5%"), "radio"),
            Pregunta("4.- ¿cada cuándo se debe calibrar la máquina?", listOf("Cada año o cada 40 000 ensayes", "Cada dos años o cada 40 000 ensayes", "Cada año o cada 30 000 ensayes", "Cada año o cada 45 000 ensayes"), "radio"),
            Pregunta("5.- ¿qué métodos podemos usar para cabeceo?", listOf("Compuesto de azufre", "Casquetes no adheridos de neopreno", "Cualquiera de las anteriores"), "radio"),
            Pregunta("6.- ¿A cuánto equivale aproximadamente la velocidad de aplicación de carga referida en la norma?", listOf("400 kgf en 5 s", "11 t en 5 s", "2000 kg en 5 s", "1000 kg en 5 s"), "radio"),
            Pregunta("7.- ¿cuántos cilindros se llevan a la falla?", listOf("3 cada 10 especímenes", "1 cada 10 especímenes", "Ninguno"), "radio"),
            Pregunta("8.- a resistencia normal, ¿cuál es la edad de prueba y cuántos cilindros deben promediarse?", listOf("28 días, promediando 3 especímenes", "28 días, promediando 2 especímenes", "14 días, sin promediar especímenes"), "radio"),
            Pregunta("9.- completa la siguiente tabla de tolerancias:", opcionesTabla, "tabla"),
            Pregunta("10.- describe los siguientes tipos de falla", opcionesFallas, "fallas")
        )

        var preguntaIndex = 1
        for (pregunta in preguntas) {
            val titulo = TextView(this).apply {
                text = pregunta.texto
                textSize = 16f
                setPadding(0, 20, 0, 8)
            }
            containerPreguntas.addView(titulo)

            when (pregunta.tipo) {
                "tabla" -> agregarTabla(pregunta.opciones)
                "fallas" -> {
                    for ((clave, falla) in subpreguntasFallas) {
                        val imagen = ImageView(this).apply {
                            setImageResource(falla.first)
                            adjustViewBounds = true
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                (160 * resources.displayMetrics.density).toInt()
                            )
                            setPadding(24, 16, 0, 8)
                            contentDescription = "Tipo de falla del cilindro"
                        }
                        containerPreguntas.addView(imagen)
                        agregarRadioGroup(clave, pregunta.opciones)
                    }
                }
                else -> agregarRadioGroup(preguntaIndex.toString(), pregunta.opciones)
            }

            preguntaIndex++
        }

        btnEnviar.setOnClickListener {
            guardarExamen()
        }

        cargarRespuestasGuardadas()
    }

    private fun agregarRadioGroup(clave: String, opciones: List<String>) {
        val radioGroup = RadioGroup(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        for (opcion in opciones) {
            val radio = RadioButton(this).apply {
                text = opcion
                textSize = 15f
                setPadding(24, 8, 0, 8)
            }
            radioGroup.addView(radio)
        }

        containerPreguntas.addView(radioGroup)
        gruposPreguntas[clave] = radioGroup
    }

    private fun agregarTabla(opciones: List<String>) {
        for ((clave, fila) in filasTabla) {
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
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf(opcionPorDefecto) + opciones)
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

    // Respuestas correctas por clave de Firebase de las preguntas con varias partes
    private fun partesDePregunta(numeroPregunta: Int): Map<String, String>? = when (numeroPregunta) {
        preguntaTabla -> filasTabla.mapValues { it.value.second }
        preguntaFallas -> subpreguntasFallas.mapValues { it.value.second }
        else -> null
    }

    private fun guardarExamen() {
        val respuestasSeleccionadas = linkedMapOf<String, String>()
        val respuestasCorrectasMap = linkedMapOf<String, String>()

        var respuestasContestadas = 0
        var aciertos = 0

        for (numeroPregunta in 1..totalPreguntas) {
            val partes = partesDePregunta(numeroPregunta)
            if (partes != null) {
                // Las preguntas con varias partes cuentan como una sola: es correcta solo si todas sus partes lo son
                var partesContestadas = 0
                var partesCorrectas = 0
                for ((clave, correcta) in partes) {
                    val seleccion = obtenerRespuesta(clave) ?: continue
                    partesContestadas += 1
                    respuestasSeleccionadas[clave] = seleccion
                    respuestasCorrectasMap[clave] = correcta
                    if (seleccion == correcta) partesCorrectas += 1
                }
                if (partesContestadas == partes.size) {
                    respuestasContestadas += 1
                    if (partesCorrectas == partes.size) aciertos += 1
                }
                continue
            }

            val clave = numeroPregunta.toString()
            val seleccion = obtenerRespuesta(clave)

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

    private fun obtenerRespuesta(clave: String): String? {
        spinnersTabla[clave]?.let { spinner ->
            val seleccion = spinner.selectedItem?.toString()
            return if (seleccion.isNullOrEmpty() || seleccion == opcionPorDefecto) null else seleccion
        }
        val radioGroup = gruposPreguntas[clave] ?: return null
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

                for (numeroPregunta in 1..totalPreguntas) {
                    val partes = partesDePregunta(numeroPregunta)
                        ?: mapOf(numeroPregunta.toString() to (respuestasCorrectas[numeroPregunta] ?: ""))

                    for ((clave, correcta) in partes) {
                        val respuestaGuardada = respuestas.child(clave).getValue(String::class.java)
                        if (spinnersTabla.containsKey(clave)) {
                            mostrarRevisionSpinner(clave, respuestaGuardada, correcta)
                        } else {
                            mostrarRevisionRadio(clave, respuestaGuardada, correcta)
                        }
                    }
                }

                // Recalcular la calificación con las respuestas correctas actuales
                val aciertos = (1..totalPreguntas).count { numeroPregunta ->
                    esPreguntaCorrecta(numeroPregunta, respuestas)
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

    private fun esPreguntaCorrecta(numeroPregunta: Int, respuestas: DataSnapshot): Boolean {
        val partes = partesDePregunta(numeroPregunta)
            ?: return respuestas.child(numeroPregunta.toString()).getValue(String::class.java) ==
                respuestasCorrectas[numeroPregunta]
        return partes.all { (clave, correcta) ->
            respuestas.child(clave).getValue(String::class.java) == correcta
        }
    }

    private fun mostrarRevisionRadio(clave: String, respuestaGuardada: String?, respuestaCorrecta: String) {
        val radioGroup = gruposPreguntas[clave] ?: return
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

    private fun mostrarRevisionSpinner(clave: String, respuestaGuardada: String?, respuestaCorrecta: String) {
        val spinner = spinnersTabla[clave] ?: return
        val indicador = indicadoresTabla[clave] ?: return

        @Suppress("UNCHECKED_CAST")
        val adapter = spinner.adapter as? ArrayAdapter<String>
        val posicion = respuestaGuardada?.let { adapter?.getPosition(it) } ?: -1
        if (posicion >= 0) spinner.setSelection(posicion)
        spinner.isEnabled = false

        if (respuestaGuardada == respuestaCorrecta) {
            indicador.text = "✓ Correcto"
            indicador.setTextColor(android.graphics.Color.GREEN)
        } else {
            indicador.text = "✗ Respuesta correcta: $respuestaCorrecta"
            indicador.setTextColor(android.graphics.Color.RED)
        }
        indicador.visibility = View.VISIBLE
    }
}
