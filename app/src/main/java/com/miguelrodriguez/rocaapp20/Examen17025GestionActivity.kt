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

class Examen17025GestionActivity : AppCompatActivity() {

    private lateinit var containerPreguntas: LinearLayout
    private lateinit var btnEnviar: Button
    private lateinit var txtNombreUsuario: TextView
    private lateinit var txtFechaActual: TextView
    private lateinit var txtCalificacionActual: TextView
    private lateinit var database: DatabaseReference
    private val gruposPreguntas = mutableListOf<RadioGroup>()
    private var esRevisionExamen = false

    private val respuestasCorrectas = mapOf(
        1 to "EMA",
        2 to "Se aplica una no conformidad.",
        3 to "Cada tres meses",
        4 to "estar implementados",
        5 to "En el manual de la calidad",
        6 to "Para asegurar que los requisitos del cliente se encuentren definidos y documentados",
        7 to "Inspeccionar el producto para verificar que cumpla con lo solicitado",
        8 to "10 días hábiles",
        9 to "En cualquier procedimiento del laboratorio",
        10 to "Investigar para determinar la causa y evaluar el impacto",
        11 to "Acciones preventivas",
        12 to "Acciones correctivas",
        13 to "4 años",
        14 to "Cada 12 meses",
        15 to "Cada año",
        16 to "Identificar la causa raíz y aplicar acciones correctivas"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_examen_17025_gestion)

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

        val preguntas = listOf(
            "1.- En México, ¿quién acredita a los laboratorios de ensayo o calibración?" to listOf("INACAL", "EMA", "ICA", "ONNCCE"),
            "2.- Menciona que pasa si no se cumple un requisito de la norma" to listOf("Se aplica una no conformidad.", "Se notifica al cliente.", "Se capacita nuevamente al personal."),
            "3.- ¿Cada cuánto tiempo se deben supervisar las actividades de ensayo en campo?" to listOf("Cada 15 días", "Cada mes", "Cada tercer día", "Cada tres meses"),
            "4.- ¿Los procedimientos requeridos como tales en la norma deben estar documentados, ser entendidos, estar implementados, todas las anteriores?" to listOf("estar documentados", "ser entendidos", "estar implementados", "todas las anteriores"),
            "5.- ¿Qué es la política de la calidad?" to listOf("Cuadros por todo el laboratorio", "En los procedimientos documentados", "En el manual de la calidad", "En ninguna de las anteriores"),
            "6.- Para qué nos sirve el procedimiento de revisión de los pedidos, ofertas y contratos." to listOf("Para asegurar que el laboratorio ofrece el menor costo", "Para asegurar que los requisitos del cliente se encuentren definidos y documentados", "Para asegurar que no hay competencia", "Ninguna de las anteriores"),
            "7.- Al adquirir un producto, que acciones debemos tomar:" to listOf("Inspeccionar el producto para verificar que cumpla con lo solicitado", "Verificar la garantía de calidad", "Revisar errores en la factura", "Verificar el precio"),
            "8.- En cuanto tiempo debemos atender la queja de un cliente" to listOf("10 días naturales", "10 días hábiles", "10 semanas", "10 meses"),
            "9.- En donde se puede presentar un servicio no conforme" to listOf("En cualquier procedimiento del laboratorio", "En las auditorías", "En el rendimiento y Ensaye de concreto"),
            "10.- ¿Qué es lo primero que debemos hacer en una acción correctiva?" to listOf("Notificar al director", "Notificar al cliente", "Investigar para determinar la causa y evaluar el impacto"),
            "11.- Si se detecta una posible no conformidad que aplica" to listOf("Acciones correctivas", "Acciones de mejora", "Acciones preventivas"),
            "12.- Si se detecta una no conformidad que aplica" to listOf("Acciones correctivas", "Acciones de mejora", "Acciones preventivas"),
            "13.- ¿Durante cuánto tiempo se deben resguardar los registros?" to listOf("5 meses", "4 años", "4 semestres", "5 años"),
            "14.- ¿Cada cuánto tiempo se debe realizar una auditoría interna?" to listOf("Cada 12 meses", "Una vez al año", "Cada 6 meses"),
            "15.- ¿Cada cuánto tiempo se debe realizar una revisión por la dirección?" to listOf("Cada 3 meses", "Cada año", "Cada 8 meses"),
            "16.- ¿Qué debemos hacer si en una auditoría interna se detectan no conformidades?" to listOf("No pasa nada, se quedan las cosas igual", "Buscar un culpable y corregirlo", "Identificar la causa raíz y aplicar acciones correctivas", "Realizar otra auditoría interna")
        )

        for ((pregunta, opciones) in preguntas) {
            val titulo = TextView(this).apply {
                text = pregunta
                textSize = 16f
                setPadding(0, 20, 0, 8)
            }
            containerPreguntas.addView(titulo)

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
            gruposPreguntas.add(radioGroup)
        }

        btnEnviar.setOnClickListener {
            guardarExamen()
        }

        cargarRespuestasGuardadas()
    }

    private fun guardarExamen() {
        val respuestasSeleccionadas = linkedMapOf<String, String>()
        val respuestasCorrectasMap = linkedMapOf<String, String>()
        val preguntas = mutableListOf<Triple<Int, String, RadioGroup>>()

        for (i in 0 until containerPreguntas.childCount) {
            val child = containerPreguntas.getChildAt(i)
            if (child is RadioGroup) {
                val numeroPregunta = (i + 1) / 2
                preguntas.add(Triple(numeroPregunta, numeroPregunta.toString(), child))
            }
        }

        var respuestasContestadas = 0
        var aciertos = 0

        for ((numeroPregunta, clave, radioGroup) in preguntas) {
            val seleccion = obtenerRespuestaSeleccionada(radioGroup)
            if (seleccion != null) {
                respuestasContestadas += 1
                respuestasSeleccionadas[clave] = seleccion
                respuestasCorrectasMap[clave] = respuestasCorrectas[numeroPregunta] ?: ""
                if (respuestasCorrectas[numeroPregunta] == seleccion) {
                    aciertos += 1
                }
            }
        }

        if (respuestasContestadas < preguntas.size) {
            Toast.makeText(this, "Debes responder todas las preguntas antes de enviar.", Toast.LENGTH_SHORT).show()
            return
        }

        val nombre = txtNombreUsuario.text.toString().removePrefix("NOMBRE: ").trim()
        val fecha = txtFechaActual.text.toString().removePrefix("FECHA: ").trim()
        val anioActual = CapacitacionesActivity.anioDelExamen(intent)
        val calificacion = (aciertos.toDouble() / preguntas.size.toDouble() * 10.0)
        val calificacionFormateada = String.format(Locale.US, "%.2f", calificacion).toDouble()

        val examenId = System.currentTimeMillis().toString()
        val examenData = hashMapOf(
            "id" to examenId,
            "nombre" to nombre,
            "fecha" to fecha,
            "usuario" to MainActivity.NombreUsuarioCompanion,
            "examen" to "EXAMEN 17025 GESTIÓN",
            "calificacion" to calificacionFormateada,
            "respuestas" to respuestasSeleccionadas,
            "respuestasCorrectas" to respuestasCorrectasMap,
            "fechaRegistro" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )

        database.child("Capacitaciones")
            .child("Examenes")
            .child(anioActual)
            .child(nombre)
            .child("EXAMEN 17025 GESTIÓN")
            .child(examenId)
            .setValue(examenData)
            .addOnSuccessListener {
                Toast.makeText(this, "Examen guardado correctamente en Firebase", Toast.LENGTH_SHORT).show()

                val intentResultado = Intent(this, ResultadoExamenActivity::class.java)
                intentResultado.putExtra("nombre", nombre)
                intentResultado.putExtra("fecha", fecha)
                intentResultado.putExtra("aciertos", aciertos)
                intentResultado.putExtra("total", preguntas.size)
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
            .child("EXAMEN 17025 GESTIÓN")
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) return@addOnSuccessListener

                val ultimoExamen = snapshot.children.maxByOrNull { it.key ?: "0" }
                    ?: return@addOnSuccessListener

                val respuestas = ultimoExamen.child("respuestas")
                val respuestasCorrectasDB = ultimoExamen.child("respuestasCorrectas")

                if (!respuestas.exists()) return@addOnSuccessListener
                
                esRevisionExamen = true

                for ((indice, radioGroup) in gruposPreguntas.withIndex()) {
                    val numeroPregunta = indice + 1
                    val respuestaGuardada = respuestas.child(numeroPregunta.toString()).getValue(String::class.java)
                    val respuestaCorrecta = if (respuestasCorrectasDB.exists()) {
                        respuestasCorrectasDB.child(numeroPregunta.toString()).getValue(String::class.java)
                    } else {
                        respuestasCorrectas[numeroPregunta]?.toString()
                    }
                    
                    if (respuestaGuardada == null) continue

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

                // Recalcular la calificación con las respuestas correctas actualizadas
                var aciertos = 0
                var totalPreguntas = 0

                for ((indice, radioGroup) in gruposPreguntas.withIndex()) {
                    val numeroPregunta = indice + 1
                    val respuestaGuardada = respuestas.child(numeroPregunta.toString()).getValue(String::class.java)

                    if (respuestaGuardada != null) {
                        totalPreguntas += 1
                        val respuestaCorrecta = respuestasCorrectas[numeroPregunta]

                        if (respuestaGuardada == respuestaCorrecta) {
                            aciertos += 1
                        }
                    }
                }

                val calificacionRecalculada = if (totalPreguntas > 0) {
                    (aciertos.toDouble() / totalPreguntas.toDouble() * 10.0)
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
