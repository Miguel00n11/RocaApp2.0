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

class ExamenNorma159Activity : AppCompatActivity() {

    private lateinit var containerPreguntas: LinearLayout
    private lateinit var btnEnviar: Button
    private lateinit var txtNombreUsuario: TextView
    private lateinit var txtFechaActual: TextView
    private lateinit var txtCalificacionActual: TextView
    private lateinit var database: DatabaseReference
    private val gruposPreguntas = mutableMapOf<Int, RadioGroup>()
    private val spinnersTabla = linkedMapOf<String, Spinner>()
    private val indicadoresTabla = mutableMapOf<String, TextView>()
    private var esRevisionExamen = false

    private val nombreExamen = "EXAMEN NORMA 159"
    private val totalPreguntas = 11
    private val preguntaTabla = 5
    private val opcionPorDefecto = "Selecciona una opción"

    private val respuestasCorrectas = mapOf(
        1 to "Establecer los procedimientos para elaborar y curar, ya sea en obra o laboratorio, los especímenes de concreto utilizados para los ensayes que requieran.",
        2 to "Proceso mediante el cual en un ambiente de humedad y temperatura, y un tiempo determinado, se favorece la hidratación del elemento o de los materiales cementantes de la mezcla",
        3 to "9000",
        4 to "Horizontal y nivelada",
        6 to "54 y debe penetrar 1cm",
        7 to "Cerca de la estructura que representan",
        8 to "Después de 24h con una tolerancia de 20 a 48h",
        9 to "Después de 24h con una tolerancia de 24 a 48h",
        10 to "Registrar la temperatura ambiente",
        11 to "En posición vertical y de preferencia en sus moldes"
    )

    // Pregunta 5: tabla de métodos de compactación (clave Firebase -> fila, respuesta correcta)
    private val filasTabla = linkedMapOf(
        "5a" to Pair("Un concreto con revenimiento mayor de 80mm se debe:", "Varilla"),
        "5b" to Pair("Un concreto con revenimiento de 30 a 80mm se debe:", "Varilla y vibrar"),
        "5c" to Pair("Un concreto con revenimiento menor de 30mm se debe:", "Vibrar")
    )
    private val opcionesTabla = listOf("Varilla", "Varilla y vibrar", "Vibrar")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_examen_norma_159)

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
            Pregunta("1.- ¿cuál es el objetivo de la norma?", listOf("Determinar la resistencia a la compresión del concreto.", "Determinar la consistencia de concreto hidráulico en estado fresco.", "Establecer los procedimientos para elaborar y curar, ya sea en obra o laboratorio, los especímenes de concreto utilizados para los ensayes que requieran."), "radio"),
            Pregunta("2.- ¿qué se entiende por curado de acuerdo a la norma?", listOf("Empleo de productos químicos al concreto como el curacreto.", "Proceso mediante el cual en un ambiente de humedad y temperatura, y un tiempo determinado, se favorece la hidratación del elemento o de los materiales cementantes de la mezcla", "Humedecer los cilindros constantemente para evitar fisuras por la pérdida de agua."), "radio"),
            Pregunta("3.- si utilizas un vibrador de inmersión ¿cuántas vibraciones debe dar por minuto?", listOf("7000", "3500", "6500", "9000"), "radio"),
            Pregunta("4.- ¿cómo debe ser el lugar para el moldeo?", listOf("Como esté en la obra", "Superficie vertical", "Horizontal y nivelada", "Superficie húmeda"), "radio"),
            Pregunta("5.- completa la siguiente tabla de métodos de compactación", opcionesTabla, "tabla"),
            Pregunta("6.- ¿cuántas penetraciones se dan por capa a las vigas y cuánto debe penetrar la varilla en la capa inferior?", listOf("78 y debe penetrar 1cm", "54 y debe penetrar 1cm", "56 y debe penetrar 1cm", "75 y debe penetrar 1cm"), "radio"),
            Pregunta("7.- ¿cómo deben almacenarse los cilindros que se utilizan para determinar cuándo puede ponerse en servicio una estructura?", listOf("En el cuarto de curado", "En la pileta de curado", "Cerca de la estructura que representan", "Lejos de la estructura que representan"), "radio"),
            Pregunta("8.- ¿después de cuánto tiempo los cilindros deben retirarse de sus moldes y las tolerancias?", listOf("Después de 24h con una tolerancia de 24 a 48h", "Después de 20h con una tolerancia de 24 a 48h", "Después de 24h con una tolerancia de 20 a 48h", "Después de 24h con una tolerancia de 24 a 40h"), "radio"),
            Pregunta("9.- ¿después de cuánto tiempo las vigas deben retirarse de sus moldes y las tolerancias?", listOf("Después de 24h con una tolerancia de 24 a 48h", "Después de 20h con una tolerancia de 24 a 48h", "Después de 24h con una tolerancia de 20 a 48h", "Después de 24h con una tolerancia de 24 a 40h"), "radio"),
            Pregunta("10.- la norma menciona que los especímenes deben protegerse durante su curado inicial bajo condiciones que mantengan una temperatura de entre 16 y 27°C, lo anterior no siempre es posible. El subcomité de evaluación, ¿qué determinó al respecto?", listOf("No hacer nada", "Conseguir un equipo especial para curar los cilindros en la obra a la temperatura señalada", "Solo muestrear cuando el clima lo permita", "Registrar la temperatura ambiente"), "radio"),
            Pregunta("11.- ¿cómo deben transportarse los cilindros y las vigas?", listOf("En posición vertical y de preferencia en sus moldes", "Acostados sin sus moldes", "Estibados de 2 x 2", "En el asiento del copiloto"), "radio")
        )

        var preguntaIndex = 1
        for (pregunta in preguntas) {
            val titulo = TextView(this).apply {
                text = pregunta.texto
                textSize = 16f
                setPadding(0, 20, 0, 8)
            }
            containerPreguntas.addView(titulo)

            if (pregunta.tipo == "tabla") {
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
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf(opcionPorDefecto) + pregunta.opciones)
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

    private fun guardarExamen() {
        val respuestasSeleccionadas = linkedMapOf<String, String>()
        val respuestasCorrectasMap = linkedMapOf<String, String>()

        var respuestasContestadas = 0
        var aciertos = 0

        for (numeroPregunta in 1..totalPreguntas) {
            if (numeroPregunta == preguntaTabla) {
                // La tabla cuenta como una sola pregunta: es correcta solo si todas sus filas lo son
                var filasContestadas = 0
                var filasCorrectas = 0
                for ((clave, fila) in filasTabla) {
                    val seleccion = obtenerRespuestaDelSpinner(clave) ?: continue
                    filasContestadas += 1
                    respuestasSeleccionadas[clave] = seleccion
                    respuestasCorrectasMap[clave] = fila.second
                    if (seleccion == fila.second) filasCorrectas += 1
                }
                if (filasContestadas == filasTabla.size) {
                    respuestasContestadas += 1
                    if (filasCorrectas == filasTabla.size) aciertos += 1
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

    private fun obtenerRespuestaDelSpinner(clave: String): String? {
        val seleccion = spinnersTabla[clave]?.selectedItem?.toString()
        return if (seleccion.isNullOrEmpty() || seleccion == opcionPorDefecto) null else seleccion
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
                    if (numeroPregunta == preguntaTabla) {
                        mostrarRevisionTabla(respuestas)
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
                    val esCorrecta = if (numeroPregunta == preguntaTabla) {
                        filasTabla.all { (clave, fila) ->
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

    private fun mostrarRevisionTabla(respuestas: DataSnapshot) {
        for ((clave, fila) in filasTabla) {
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
