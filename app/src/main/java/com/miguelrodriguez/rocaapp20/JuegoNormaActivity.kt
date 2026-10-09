package com.miguelrodriguez.rocaapp20

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class JuegoNormaActivity : AppCompatActivity() {

    data class Pregunta(
        val texto: String,
        val opciones: List<String>,
        val correcta: Int,
        val explicacion: String,
        val categoria: String
    )

    private val colorCategoria = mapOf(
        "TOLERANCIAS" to "#1565C0",
        "PROCEDIMIENTO" to "#2E7D32",
        "EQUIPO" to "#6A1B9A",
        "SEGURIDAD" to "#B71C1C",
        "IMPARCIALIDAD" to "#4A148C",
        "PERSONAL" to "#01579B",
        "REGISTROS" to "#1B5E20",
        "INFORMES" to "#E65100",
        "UNIDADES BASE" to "#0D47A1",
        "PREFIJOS" to "#006064",
        "CONVERSIÓN" to "#1A237E",
        "SÍMBOLOS" to "#880E4F",
        "CÁLCULO" to "#004D40",
        "CONDICIONES" to "#1A237E",
        "TANQUE" to "#0277BD",
        "OPERACIÓN" to "#558B2F",
        "RESULTADOS" to "#6A1B9A",
        "MOLDES" to "#37474F",
        "COMPACTACIÓN" to "#BF360C",
        "CURADO" to "#00695C",
        "MUESTREO" to "#283593",
        "TIEMPOS" to "#AD1457"
    )

    companion object {
        const val EXTRA_NUMERO = "numero"
        const val EXTRA_CODIGO = "codigo"
    }

    private var preguntas: List<Pregunta> = emptyList()
    private var indice = 0
    private var puntuacion = 0
    private var respondido = false

    private lateinit var tvProgreso: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvCategoria: TextView
    private lateinit var tvPregunta: TextView
    private lateinit var tvPuntuacion: TextView
    private lateinit var botones: List<Button>
    private lateinit var cardFeedback: CardView
    private lateinit var tvFeedback: TextView
    private lateinit var tvExplicacion: TextView
    private lateinit var btnSiguiente: Button
    private lateinit var layoutJuego: View
    private lateinit var layoutResultado: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juego_norma)

        val numero = intent.getStringExtra(EXTRA_NUMERO) ?: ""
        val codigo = intent.getStringExtra(EXTRA_CODIGO) ?: ""

        findViewById<ImageButton>(R.id.btnAtrasJuego).setOnClickListener { finish() }
        findViewById<TextView>(R.id.txtCodigoJuego).text = codigo

        tvProgreso = findViewById(R.id.tvProgresoJuego)
        progressBar = findViewById(R.id.progressJuego)
        tvCategoria = findViewById(R.id.tvCategoriaJuego)
        tvPregunta = findViewById(R.id.tvPreguntaJuego)
        tvPuntuacion = findViewById(R.id.tvPuntuacionJuego)
        cardFeedback = findViewById(R.id.cardFeedbackJuego)
        tvFeedback = findViewById(R.id.tvFeedbackJuego)
        tvExplicacion = findViewById(R.id.tvExplicacionJuego)
        btnSiguiente = findViewById(R.id.btnSiguienteJuego)
        layoutJuego = findViewById(R.id.scrollJuego)
        layoutResultado = findViewById(R.id.layoutResultadoJuego)

        botones = listOf(
            findViewById(R.id.btnOpcionA),
            findViewById(R.id.btnOpcionB),
            findViewById(R.id.btnOpcionC),
            findViewById(R.id.btnOpcionD)
        )

        btnSiguiente.setOnClickListener { avanzar() }

        preguntas = obtenerPreguntas(numero).shuffled().take(10)

        if (preguntas.isEmpty()) {
            mostrarProximamente()
        } else {
            mostrarPregunta()
        }
    }

    private fun mostrarPregunta() {
        if (indice >= preguntas.size) { mostrarResultado(); return }

        respondido = false
        cardFeedback.visibility = View.GONE
        btnSiguiente.visibility = View.GONE

        val p = preguntas[indice]

        tvProgreso.text = "Pregunta ${indice + 1} de ${preguntas.size}"
        progressBar.max = preguntas.size
        progressBar.progress = indice + 1

        val color = colorCategoria[p.categoria] ?: "#1565C0"
        tvCategoria.text = p.categoria
        tvCategoria.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))

        tvPregunta.text = p.texto
        tvPuntuacion.text = "$puntuacion pts"

        botones.forEachIndexed { i, btn ->
            val letra = ('A' + i).toString()
            btn.text = "$letra)  ${p.opciones[i]}"
            btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1E2D3D"))
            btn.setTextColor(Color.WHITE)
            btn.isEnabled = true
            btn.setOnClickListener { responder(i) }
        }
    }

    private fun responder(seleccion: Int) {
        if (respondido) return
        respondido = true

        val p = preguntas[indice]
        val correcto = seleccion == p.correcta

        botones.forEach { it.isEnabled = false }

        botones.forEachIndexed { i, btn ->
            when {
                i == p.correcta -> btn.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#2E7D32"))
                i == seleccion && !correcto -> btn.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#C62828"))
            }
        }

        if (correcto) {
            puntuacion += 10
            tvPuntuacion.text = "$puntuacion pts"
        }

        tvFeedback.text = if (correcto) "✓ ¡Correcto!" else "✗ Incorrecto"
        tvFeedback.setTextColor(if (correcto) Color.parseColor("#4CAF50") else Color.parseColor("#EF5350"))
        tvExplicacion.text = p.explicacion
        cardFeedback.visibility = View.VISIBLE
        btnSiguiente.visibility = View.VISIBLE
        btnSiguiente.text = if (indice < preguntas.size - 1) "Siguiente →" else "Ver resultado"
    }

    private fun avanzar() {
        indice++
        mostrarPregunta()
    }

    private fun mostrarResultado() {
        layoutJuego.visibility = View.GONE
        layoutResultado.visibility = View.VISIBLE

        val total = preguntas.size
        val correctas = puntuacion / 10
        val pct = correctas * 100 / total

        val estrellas = when {
            pct >= 80 -> 3
            pct >= 60 -> 2
            pct >= 40 -> 1
            else -> 0
        }

        findViewById<TextView>(R.id.tvEstrellasResultado).text =
            "★".repeat(estrellas) + "☆".repeat(3 - estrellas)
        findViewById<TextView>(R.id.tvPuntajeFinal).text =
            "$puntuacion de ${total * 10} puntos  ($correctas/$total correctas)"
        findViewById<TextView>(R.id.tvMensajeResultado).text = when (estrellas) {
            3 -> "¡Excelente! Dominas la norma."
            2 -> "¡Bien! Sigue practicando."
            1 -> "Buen intento. Repasa el material."
            else -> "Sigue estudiando la norma."
        }

        findViewById<Button>(R.id.btnReintentarJuego).setOnClickListener {
            indice = 0
            puntuacion = 0
            preguntas = obtenerPreguntas(intent.getStringExtra(EXTRA_NUMERO) ?: "").shuffled().take(10)
            layoutJuego.visibility = View.VISIBLE
            layoutResultado.visibility = View.GONE
            mostrarPregunta()
        }
        findViewById<Button>(R.id.btnSalirJuego).setOnClickListener { finish() }
    }

    private fun mostrarProximamente() {
        layoutJuego.visibility = View.GONE
        layoutResultado.visibility = View.VISIBLE
        layoutResultado.gravity = android.view.Gravity.CENTER
        val tv = TextView(this).apply {
            text = "🎮 Juego próximamente\n\nEsta norma aún no tiene preguntas disponibles."
            textSize = 16f
            setTextColor(Color.parseColor("#8FA3B5"))
            gravity = android.view.Gravity.CENTER
            lineHeight = (textSize * 1.6f).toInt()
        }
        layoutResultado.removeAllViews()
        layoutResultado.addView(tv)
        val btnSalir = Button(this).apply {
            text = "Salir"
            backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2C3E50"))
            setTextColor(Color.WHITE)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.topMargin = dpToPx(24)
            layoutParams = params
            setOnClickListener { finish() }
        }
        layoutResultado.addView(btnSalir)
    }

    private fun obtenerPreguntas(numero: String) = when (numero) {
        "109" -> preguntas109()
        "17025" -> preguntas17025()
        "008" -> preguntas008()
        "083" -> preguntas083()
        "148" -> preguntas148()
        "156" -> preguntas156()
        "159" -> preguntas159()
        "161" -> preguntas161()
        else -> emptyList()
    }

    private fun preguntas109() = listOf(
        Pregunta(
            "¿Cuál es la tolerancia de planicidad permitida en las bases de los especímenes?",
            listOf("± 0.10 mm en 100 mm", "± 0.05 mm en 150 mm", "± 0.25 mm en 150 mm", "± 0.05 mm en 100 mm"),
            1,
            "La norma exige una tolerancia de ± 0.05 mm en una longitud de 150 mm. Si las bases no cumplen esta condición, deben cortarse, pulirse o cabecearse.",
            "TOLERANCIAS"
        ),
        Pregunta(
            "¿A qué temperatura se debe calentar el compuesto de azufre para su empleo en el cabeceo?",
            listOf("100°C ± 10°C", "120°C ± 10°C", "140°C ± 10°C", "160°C ± 10°C"),
            2,
            "El compuesto se calienta a 140°C ± 10°C (413 K ± 10 K). Por encima de este rango puede degradarse o encenderse.",
            "TOLERANCIAS"
        ),
        Pregunta(
            "¿Cuál es el punto de ignición del azufre?",
            listOf("150°C", "180°C", "200°C", "227°C"),
            3,
            "El punto de ignición del azufre es 227°C. Calentar con flama directa es peligroso porque el mortero puede encenderse por sobrecalentamiento.",
            "SEGURIDAD"
        ),
        Pregunta(
            "¿Cuándo se realiza el cabeceo con pasta de cemento en especímenes recién moldeados?",
            listOf("Inmediatamente después del moldeado", "Entre 2 h y 4 h después del moldeado", "Entre 6 h y 8 h después del moldeado", "24 horas después del moldeado"),
            1,
            "El cabeceo con pasta de cemento se realiza entre 2 y 4 horas después del moldeado. Antes se retira el agua de sangrado.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Cuántos días mínimo deben curar las capas de pasta de cemento antes de ensayar?",
            listOf("24 horas", "3 días", "7 días", "28 días"),
            2,
            "Las capas de cemento puro requieren mínimo 7 días para desarrollar resistencia aceptable. No se debe ensayar antes de este plazo.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Cuántos minutos después de aplicar la pasta de cemento se procede a enrasar con la placa cabeceadora?",
            listOf("10 minutos", "20 minutos", "30 minutos", "60 minutos"),
            2,
            "Se aplica la pasta de cemento y 30 minutos después se enrasa con la placa cabeceadora (humedecida).",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "Para concreto con resistencia de 30 MPa, ¿cuál es el espesor PROMEDIO del cabeceo?",
            listOf("3 mm", "5 mm", "6 mm", "8 mm"),
            2,
            "Para resistencias de 3.5 a 50 MPa, el espesor promedio de cada capa de cabeceo es 6 mm. El espesor máximo en cualquier punto de oquedad es 8 mm.",
            "TOLERANCIAS"
        ),
        Pregunta(
            "Para concreto con resistencia mayor a 50 MPa, ¿cuál es el espesor PROMEDIO del cabeceo?",
            listOf("3 mm", "6 mm", "8 mm", "10 mm"),
            0,
            "Para concreto de más de 50 MPa, el espesor promedio del cabeceo es 3 mm. El espesor máximo en cualquier punto es 5 mm.",
            "TOLERANCIAS"
        ),
        Pregunta(
            "¿Cuál es el espesor mínimo del plato metálico para cabecear cilindros de 15 cm de diámetro?",
            listOf("6 mm", "8 mm", "11 mm", "15 mm"),
            2,
            "El espesor de la placa base del plato para cilindros de 15 cm no debe ser menor de 11 mm, para evitar contracciones y fracturas al enfriarse.",
            "EQUIPO"
        ),
        Pregunta(
            "¿En qué proporción de especímenes se verifica el espesor del cabeceo después de los ensayos?",
            listOf("En todos los especímenes", "En 1 de cada 5", "En 1 de cada 10", "En 1 de cada 20"),
            2,
            "Después de la operación diaria de ensayo, se verifica el espesor del cabeceo en uno de cada 10 especímenes.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Qué se debe hacer si el cabeceo no cumple con la planicidad requerida?",
            listOf("Continuar el ensayo con ese espécimen", "Reducir la carga de ensayo", "Remover y volver a elaborar el cabeceo", "Agregar más compuesto encima"),
            2,
            "Si el cabeceo falla en los requisitos de planicidad o tiene áreas sin adherencia, se debe remover y rehacer completamente.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Cuál es la relación agua/cemento de la pasta para cabeceo?",
            listOf("0.10 a 0.20", "0.25 a 0.35", "0.40 a 0.50", "0.50 a 0.60"),
            1,
            "La pasta de cemento puro para cabeceo debe tener una relación agua/cemento de consistencia normal, aproximadamente entre 0.25 y 0.35.",
            "TOLERANCIAS"
        ),
        Pregunta(
            "¿De qué tamaño son los cubos de mortero para verificar la resistencia del compuesto de cabeceo?",
            listOf("25 mm por lado", "50 mm por lado", "75 mm por lado", "100 mm por lado"),
            1,
            "Se preparan 3 especímenes cúbicos de 50 mm ± 1 mm por lado para verificar la resistencia del compuesto antes de su uso.",
            "EQUIPO"
        ),
        Pregunta(
            "¿A qué temperatura se cuelan los cubos de azufre para verificar su resistencia?",
            listOf("80°C a 100°C", "100°C a 120°C", "130°C a 150°C", "160°C a 180°C"),
            2,
            "Los cubos se cuelan con el compuesto fundido a una temperatura entre 130°C y 150°C (403 K a 423 K).",
            "TOLERANCIAS"
        ),
        Pregunta(
            "¿En cuánto tiempo debe fallar el cubo de compuesto durante el ensayo de compresión?",
            listOf("Entre 5 s y 20 s", "Entre 20 s y 80 s", "Entre 80 s y 120 s", "Más de 120 s"),
            1,
            "La velocidad de carga debe aplicarse de modo que el espécimen falle entre 20 s y 80 s. Si falla antes o después, indica que la velocidad no es correcta.",
            "PROCEDIMIENTO"
        ),
        // ── Preguntas adicionales ──
        Pregunta(
            "¿Cuál es la desviación máxima de perpendicularidad permitida en un espécimen cabeceado?",
            listOf("0.1°", "0.3°", "0.5°", "1.0°"),
            2,
            "La escuadra metálica de 90° no debe apartarse más de 0.5° (aproximadamente 3 mm en 300 mm de altura del espécimen).",
            "TOLERANCIAS"
        ),
        Pregunta(
            "¿Cuánto mayor debe ser el diámetro del plato metálico respecto al espécimen?",
            listOf("Al menos 1 mm mayor", "Al menos 3 mm mayor", "Al menos 5 mm mayor", "Al menos 10 mm mayor"),
            2,
            "El diámetro del plato debe ser al menos 5 mm mayor que el del espécimen por cabecear.",
            "EQUIPO"
        ),
        Pregunta(
            "¿Cuántas lecturas mínimo se toman al verificar la planicidad de las bases?",
            listOf("1 lectura", "2 lecturas", "3 lecturas", "5 lecturas"),
            2,
            "Durante el procedimiento de cabeceo se toman mínimo 3 lecturas en diámetros diferentes para verificar la planicidad.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Qué se aplica sobre el plato de cabeceo antes de verter el compuesto de azufre?",
            listOf("Agua", "Aceite mineral", "Pasta de cemento", "Grasa mineral"),
            1,
            "Antes de vaciar cada capa, se aceita ligeramente el plato de cabeceo para evitar adherencia con el plato.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Cuál es el espesor mínimo de placas de acrílico o material pétreo para cabeceo con pasta?",
            listOf("6 mm", "10 mm", "12 mm", "15 mm"),
            2,
            "Las placas de acrílico, plástico, granito u otro material pétreo pulido deben tener mínimo 12 mm de espesor.",
            "EQUIPO"
        ),
        Pregunta(
            "¿Dónde se debe realizar el cabeceo de especímenes?",
            listOf("En cualquier lugar del laboratorio", "En un sitio cubierto sin cambios bruscos de clima", "Al aire libre para mejor ventilación", "En cuarto a temperatura controlada de 20°C"),
            1,
            "El cabeceo debe realizarse en un sitio cubierto sin cambios bruscos de clima para evitar que el compuesto se enfríe de forma desigual.",
            "SEGURIDAD"
        ),
        Pregunta(
            "¿Cuándo puede ensayarse un espécimen sin necesidad de cabeceo?",
            listOf("Nunca, siempre requiere cabeceo", "Cuando su base cumple con planicidad y perpendicularidad requeridas", "Solo cuando tiene más de 28 días de curado", "Solo en especímenes extraídos (núcleos)"),
            1,
            "Cuando la base del espécimen cumple con la planicidad y perpendicularidad requerida, puede ensayarse sin cabeceo, corte o pulido.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Qué problema genera el cabeceo con pasta de cemento en especímenes de concreto seco?",
            listOf("La pasta se calienta demasiado", "El concreto seco absorbe agua de la pasta y produce capas de adherencia no satisfactorias", "La pasta no endurece a temperatura ambiente", "La pasta es incompatible con el concreto seco"),
            1,
            "Los especímenes de concreto seco absorben agua de la mezcla de pasta de cemento puro, produciendo capas de adherencia no satisfactorias.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Con qué herramienta se verifica la perpendicularidad de un espécimen cabeceado?",
            listOf("Con un nivel de burbuja", "Con un calibrador vernier", "Con una escuadra metálica de 90°", "Con una regla graduada"),
            2,
            "Para verificar la perpendicularidad se usa una escuadra metálica de albañil a 90°, que tenga una muesca que libre las orillas del cabeceo.",
            "EQUIPO"
        ),
        Pregunta(
            "¿Cuál es la tolerancia de planicidad de la superficie del asiento del plato metálico?",
            listOf("0.01 mm en 150 mm", "0.05 mm en 150 mm", "0.10 mm en 150 mm", "0.25 mm en 150 mm"),
            1,
            "La planicidad de la superficie del asiento del plato no debe diferir en más de 0.05 mm en 150 mm.",
            "TOLERANCIAS"
        ),
        Pregunta(
            "Para concreto de 30 MPa, ¿cuál es la resistencia mínima del compuesto para cabeceo?",
            listOf("20 MPa", "30 MPa", "35 MPa", "50 MPa"),
            2,
            "Para concreto entre 3.5 y 50 MPa, la resistencia mínima del compuesto es 35 MPa o la del concreto, cualquiera que sea mayor. Para 30 MPa, 35 MPa es el mínimo.",
            "TOLERANCIAS"
        ),
        Pregunta(
            "¿Qué se debe eliminar de las bases del espécimen antes de cabecear?",
            listOf("El agua superficial únicamente", "Cualquier depósito de cera, material aceitoso o exceso de agua", "El lechado de cemento superficial", "Las marcas de identificación"),
            1,
            "Se debe eliminar cualquier depósito de cera, material aceitoso o exceso de agua que interfiera con la adherencia de la capa de cabeceo.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Cuántos compartimentos tienen los moldes para cubos de mortero de azufre?",
            listOf("1 compartimento", "2 compartimentos", "3 compartimentos", "4 compartimentos"),
            2,
            "Los moldes para verificar resistencia del compuesto tienen tres compartimentos cúbicos de 50 mm por lado, con placa base y cubierta perforada.",
            "EQUIPO"
        ),
        Pregunta(
            "¿Qué debe hacerse con los especímenes curados en húmedo tras el cabeceo?",
            listOf("Dejarlos secar al aire libre durante 24 h", "Mantenerlos en condiciones húmedas hasta el ensayo", "Almacenarlos a temperatura de refrigeración", "Exponerlos al sol para acelerar el curado"),
            1,
            "Los especímenes curados por vía húmeda deben mantenerse en condiciones húmedas durante el tiempo entre el terminado del cabeceo y el momento del ensayo.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Cuál es el espesor mínimo de la placa cabeceadora metálica para especímenes prismáticos?",
            listOf("6 mm", "8 mm", "11 mm", "15 mm"),
            2,
            "La placa cabeceadora para especímenes prismáticos debe ser metálica con mínimo 11 mm de espesor, con dos fronteras fijas y dos desmontables.",
            "EQUIPO"
        )
    )

    private fun preguntas17025() = listOf(
        Pregunta(
            "¿Cuál es el objetivo principal de la NMX-EC-17025?",
            listOf("Regular el comercio de equipos de laboratorio", "Establecer requisitos para la competencia, imparcialidad e implementación de laboratorios", "Definir métodos de ensayo para concreto", "Certificar al personal técnico de laboratorio"),
            1,
            "La norma establece requisitos generales para la competencia, imparcialidad e implementación consistente de laboratorios de ensayo y calibración.",
            "INFORMES"
        ),
        Pregunta(
            "¿A cuántos laboratorios aplica la NMX-EC-17025?",
            listOf("Solo a laboratorios con más de 10 empleados", "Solo a laboratorios acreditados por la EMA", "A todos, independientemente del número de personal o extensión de actividades", "Solo a laboratorios de primera parte"),
            2,
            "La norma es aplicable a todos los laboratorios, sin importar cuánto personal tengan ni cuán extensas sean sus actividades de ensayo.",
            "INFORMES"
        ),
        Pregunta(
            "¿Qué es un riesgo a la imparcialidad de un laboratorio?",
            listOf("No tener suficiente equipo de seguridad", "Realizar ensayos también del material que el laboratorio fabrica o vende", "No contar con personal certificado", "Tener instalaciones pequeñas"),
            1,
            "Un laboratorio que fabrica o vende el material que ensaya tiene un conflicto de interés que compromete su imparcialidad.",
            "IMPARCIALIDAD"
        ),
        Pregunta(
            "¿Cuándo puede un laboratorio revelar información confidencial de un cliente?",
            listOf("Siempre que lo solicite otro cliente", "Nunca, la información es siempre confidencial", "Cuando lo requiere la ley o una autoridad competente", "Cuando el laboratorio lo considere necesario"),
            2,
            "La información del cliente solo puede revelarse cuando la ley lo requiere o una autoridad competente lo exige; en ese caso se debe notificar al cliente.",
            "IMPARCIALIDAD"
        ),
        Pregunta(
            "¿Qué debe demostrar el personal técnico para ser autorizado a realizar ensayos?",
            listOf("Solo tener título universitario", "Competencia: educación, calificaciones, entrenamiento, habilidades y experiencia", "Tener mínimo 5 años de experiencia", "Solo aprobar un examen escrito"),
            1,
            "La competencia se demuestra mediante educación, calificaciones, entrenamiento, habilidades técnicas y experiencia documentada.",
            "PERSONAL"
        ),
        Pregunta(
            "¿Qué información debe contener la etiqueta del estado de calibración de un equipo?",
            listOf("Solo el número de serie", "Solo la fecha de última calibración", "Fecha de calibración y próxima fecha de calibración", "Nombre del técnico que calibró"),
            2,
            "Cada equipo debe llevar etiqueta con la fecha de calibración y la fecha de la próxima calibración para controlar el estado de vigencia.",
            "EQUIPOS"
        ),
        Pregunta(
            "¿Qué acción inmediata tomar cuando un equipo falla durante el ensayo?",
            listOf("Continuar el ensayo y reportar la falla al final del día", "Retirarlo de servicio, identificarlo como fuera de servicio y evaluar ensayos previos", "Repararlo de inmediato sin documentar", "Reemplazarlo sin notificar al cliente"),
            1,
            "Al detectar una falla en equipo se debe retirarlo de servicio, etiquetarlo como 'Fuera de servicio', evaluar el impacto en ensayos previos y notificar al cliente si corresponde.",
            "EQUIPOS"
        ),
        Pregunta(
            "¿Cuál es la temperatura de curado estándar exigida en el cuarto húmedo según 17025 aplicado a concreto?",
            listOf("20°C ± 2°C", "23°C ± 2°C", "25°C ± 2°C", "28°C ± 2°C"),
            1,
            "Para curado estándar de especímenes de concreto, el cuarto húmedo debe mantenerse a 23°C ± 2°C con humedad relativa ≥ 95%.",
            "PERSONAL"
        ),
        Pregunta(
            "¿Cómo se corrige un error en un registro técnico en papel?",
            listOf("Borrar el error con corrector líquido", "Tachar con una sola línea, escribir el valor correcto y firmar con fecha", "Destruir el registro y elaborar uno nuevo", "Escribir encima con bolígrafo de otro color"),
            1,
            "Los registros técnicos deben corregirse tachando con una línea simple (sin borrar), anotando el valor correcto al lado y firmando con fecha para mantener la trazabilidad.",
            "REGISTROS"
        ),
        Pregunta(
            "¿Qué es la trazabilidad metrológica?",
            listOf("La capacidad de medir con alta precisión", "La propiedad de un resultado relacionado con una referencia mediante cadena ininterrumpida de calibraciones", "El registro de todas las mediciones históricas", "La verificación anual de los equipos"),
            1,
            "La trazabilidad metrológica es la propiedad de un resultado mediante la cual se relaciona con una referencia (el SI) a través de una cadena ininterrumpida y documentada de calibraciones.",
            "EQUIPOS"
        ),
        Pregunta(
            "¿Qué organismo proporciona trazabilidad directa al SI en México?",
            listOf("EMA (Entidad Mexicana de Acreditación)", "CENAM (Centro Nacional de Metrología)", "IMSS (Instituto Mexicano del Seguro Social)", "UNAM"),
            1,
            "El CENAM es el laboratorio nacional de metrología de México y el organismo que proporciona trazabilidad directa al SI en el país.",
            "EQUIPOS"
        ),
        Pregunta(
            "¿Qué diferencia hay entre verificación y validación de métodos?",
            listOf("Son sinónimos en la norma", "Verificación: métodos normalizados existentes; Validación: métodos no estándar o desarrollados internamente", "Validación: métodos normalizados; Verificación: métodos propios", "Ninguna diferencia práctica"),
            1,
            "La verificación confirma que el laboratorio puede aplicar correctamente un método normalizado. La validación confirma que un método no estándar es apto para el uso pretendido.",
            "PERSONAL"
        ),
        Pregunta(
            "¿Cuál es la prioridad máxima en la selección de métodos de ensayo?",
            listOf("Métodos desarrollados internamente por el laboratorio", "Métodos de fabricantes de equipos", "Métodos de normas internacionales (ISO, ASTM, EN)", "Métodos de publicaciones científicas"),
            2,
            "Los métodos de normas internacionales (ISO, ASTM, EN) tienen la máxima prioridad. Después vienen normas nacionales, organizaciones técnicas reconocidas, literatura científica y finalmente métodos internos.",
            "PERSONAL"
        ),
        Pregunta(
            "¿Con qué frecuencia mínima se deben realizar las revisiones por la dirección?",
            listOf("Cada semana", "Cada mes", "Al menos una vez al año", "Solo cuando haya quejas de clientes"),
            2,
            "La alta dirección del laboratorio debe revisar el sistema de gestión al menos una vez al año para asegurar su idoneidad, adecuación y efectividad.",
            "INFORMES"
        ),
        Pregunta(
            "¿Qué tipo de componente de incertidumbre es la repetibilidad de una balanza?",
            listOf("Tipo B", "Tipo C", "Tipo A", "Tipo D"),
            2,
            "Los componentes de incertidumbre Tipo A se evalúan por métodos estadísticos, como la repetibilidad (varianza de mediciones repetidas).",
            "REGISTROS"
        ),
        Pregunta(
            "¿Qué tipo de componente de incertidumbre es el certificado de calibración de una balanza?",
            listOf("Tipo A", "Tipo C", "Tipo B", "Tipo D"),
            2,
            "Los componentes Tipo B se evalúan por métodos distintos al estadístico, como certificados de calibración, especificaciones del fabricante o datos de referencia.",
            "REGISTROS"
        ),
        Pregunta(
            "¿Qué debe incluir el informe de ensayo al reportar conformidad con una especificación?",
            listOf("Solo el resultado numérico", "La regla de decisión aplicada y consideración de la incertidumbre", "Solo indicar 'CUMPLE' o 'NO CUMPLE'", "La firma del cliente"),
            1,
            "Al declarar conformidad con especificaciones, el informe debe indicar la regla de decisión utilizada y cómo se consideró la incertidumbre de medición en esa declaración.",
            "INFORMES"
        ),
        Pregunta(
            "¿Qué información mínima debe registrarse al recibir una muestra en el laboratorio?",
            listOf("Solo la fecha de recepción", "Fecha, identificación del cliente, descripción y condición del ítem", "Solo el número de muestras", "Solo el nombre del técnico que la recibió"),
            1,
            "Al recibir una muestra se debe registrar la fecha de recepción, identificación del cliente, descripción completa y la condición del ítem al momento de la recepción.",
            "REGISTROS"
        ),
        Pregunta(
            "¿Cuándo debe notificarse al cliente sobre su muestra recibida con daños?",
            listOf("Al momento de emitir el informe", "Nunca, se ensaya igual", "Inmediatamente, al momento de la recepción", "Después de 48 horas"),
            2,
            "Cualquier condición anormal o daño al recibir una muestra debe registrarse y notificarse al cliente de inmediato, antes de iniciar el ensayo.",
            "REGISTROS"
        ),
        Pregunta(
            "¿Cómo se modifica un informe de ensayo ya emitido?",
            listOf("Editando el informe original", "Emitiendo un nuevo informe que referencia al original que reemplaza", "Tachando los datos incorrectos del informe original", "No pueden modificarse los informes emitidos"),
            1,
            "La modificación de un informe se hace emitiendo uno nuevo con identificación única, que debe hacer referencia al informe original que reemplaza y documentar el motivo del cambio.",
            "INFORMES"
        ),
        Pregunta(
            "¿Cuál es la Opción B del sistema de gestión en la NMX-EC-17025?",
            listOf("Implementar los requisitos de gestión propios de la norma 17025", "Cumplir con todos los requisitos de ISO 9001 en las actividades del laboratorio", "Contratar a una empresa auditora externa", "Registrarse ante la EMA voluntariamente"),
            1,
            "La Opción B permite implementar ISO 9001 como sistema de gestión del laboratorio, en lugar de los requisitos de gestión propios de la sección 8 de la norma 17025.",
            "INFORMES"
        ),
        Pregunta(
            "¿Cuál debe ser la humedad relativa mínima en el cuarto de curado de concreto?",
            listOf("70%", "80%", "90%", "95%"),
            3,
            "El cuarto de curado húmedo debe mantenerse con humedad relativa mínima del 95% para garantizar el curado continuo de los especímenes.",
            "PERSONAL"
        ),
        Pregunta(
            "¿Qué documento establece la política y objetivos de calidad del laboratorio?",
            listOf("El organigrama del laboratorio", "El manual de calidad", "El plan de calibración", "El listado de métodos acreditados"),
            1,
            "El manual de calidad documenta la política de calidad, los objetivos y el sistema de gestión del laboratorio, incluyendo su estructura organizacional.",
            "INFORMES"
        ),
        Pregunta(
            "¿Qué sucede si se detecta trabajo no conforme en el laboratorio?",
            listOf("Se continúa el trabajo y se documenta al final", "Se detiene, se retiene, se notifica al cliente y se evalúa el impacto", "Se destruyen los especímenes involucrados", "Se espera a la próxima auditoría para reportarlo"),
            1,
            "Al detectar trabajo no conforme se debe detener inmediatamente, retener el trabajo, notificar al cliente y evaluar qué tanto impacta en resultados previos o futuros.",
            "REGISTROS"
        ),
        Pregunta(
            "¿Con qué frecuencia planificada deben realizarse las auditorías internas?",
            listOf("Solo cuando hay una queja de cliente", "A intervalos planificados y regulares definidos por el laboratorio", "Solo antes de cada acreditación", "Cada 5 años"),
            1,
            "Las auditorías internas deben planificarse a intervalos regulares que el propio laboratorio define en su programa de auditorías.",
            "INFORMES"
        ),
        Pregunta(
            "¿Qué debe hacer el laboratorio si un método solicitado por el cliente es inapropiado?",
            listOf("Aplicarlo de todas formas", "Informar al cliente y proponer una alternativa más adecuada", "Rechazar el trabajo sin explicación", "Cobrar más por el trabajo de adecuación"),
            1,
            "Antes de aceptar un trabajo, el laboratorio revisa la solicitud. Si el método solicitado es inapropiado, debe informarlo al cliente y proponer una alternativa adecuada.",
            "PERSONAL"
        ),
        Pregunta(
            "¿Qué contenido mínimo debe tener el informe de ensayo respecto al método?",
            listOf("Solo el nombre del ensayo", "La identificación específica del método utilizado (clave de la norma)", "Solo indicar 'método estándar'", "El año de publicación de la norma"),
            1,
            "El informe debe identificar claramente el método utilizado, incluyendo la clave de la norma (ejemplo: NMX-C-083, ASTM C39).",
            "INFORMES"
        ),
        Pregunta(
            "¿Qué información sobre el personal debe registrar el laboratorio?",
            listOf("Solo el nombre y cargo", "Competencia, autorizaciones para actividades específicas y firmas autorizadas", "Solo las certificaciones externas obtenidas", "Solo los años de experiencia"),
            1,
            "El laboratorio debe mantener registros de competencia de cada miembro del personal, sus autorizaciones específicas para actividades y sus firmas autorizadas para validar informes.",
            "PERSONAL"
        ),
        Pregunta(
            "¿Qué proceso se usa para tratar quejas de clientes?",
            listOf("Se ignoran a menos que sean formales por escrito", "Un proceso documentado para recibirlas, investigarlas y resolver", "Solo las quejas sobre resultados son atendidas", "Se transfieren al área comercial sin seguimiento técnico"),
            1,
            "El laboratorio debe tener un proceso documentado para recibir, evaluar, investigar y resolver quejas de clientes de manera sistemática.",
            "INFORMES"
        ),
        Pregunta(
            "¿Cuántos registros de calibración mínimo debe mantener el laboratorio para cada equipo?",
            listOf("Solo el más reciente", "Los últimos 2", "Todos los registros históricos de calibración del equipo", "Solo los del año en curso"),
            2,
            "El laboratorio debe mantener todos los registros históricos de calibración de cada equipo para demostrar la trazabilidad y detectar tendencias de deriva.",
            "EQUIPOS"
        ),
        Pregunta(
            "¿Cuándo se requiere participar en ensayos de aptitud (proficiency testing)?",
            listOf("Nunca es obligatorio", "Solo cuando la acreditación está en riesgo", "Cuando la trazabilidad directa al SI no es posible, como medio para demostrar equivalencia", "Solo en laboratorios de calibración, no de ensayo"),
            2,
            "Cuando la trazabilidad directa al SI no puede lograrse, el laboratorio puede demostrar equivalencia de resultados participando en programas de ensayos de aptitud interlaboratorios.",
            "EQUIPOS"
        )
    )

    private fun preguntas008() = listOf(
        Pregunta(
            "¿Cuántas unidades base tiene el Sistema Internacional de Unidades (SI)?",
            listOf("5", "6", "7", "8"),
            2,
            "El SI está construido sobre 7 unidades base independientes: metro, kilogramo, segundo, ampere, kelvin, mol y candela.",
            "UNIDADES BASE"
        ),
        Pregunta(
            "¿Cuál es la unidad SI de fuerza?",
            listOf("Kilogramo-fuerza (kgf)", "Newton (N)", "Pascal (Pa)", "Joule (J)"),
            1,
            "El newton (N) es la unidad SI de fuerza. Equivale a kg·m/s². 1 kgf = 9.80665 N ≈ 9.81 N.",
            "UNIDADES BASE"
        ),
        Pregunta(
            "¿Cuántos kgf/cm² equivale 1 MPa?",
            listOf("1.0 kgf/cm²", "9.81 kgf/cm²", "10.197 kgf/cm²", "100 kgf/cm²"),
            2,
            "1 MPa = 10.197 kgf/cm². Esta conversión es fundamental en laboratorios de concreto en México, donde históricamente se reporta en kgf/cm².",
            "CONVERSIÓN"
        ),
        Pregunta(
            "¿Cuál es la unidad SI de presión y esfuerzo?",
            listOf("Newton (N)", "Bar", "Pascal (Pa)", "Kilogramo-fuerza por cm² (kgf/cm²)"),
            2,
            "El pascal (Pa = N/m²) es la unidad SI de presión y esfuerzo. En concreto se usa el megapascal (MPa = 1 N/mm²).",
            "UNIDADES BASE"
        ),
        Pregunta(
            "¿Cómo se convierte una temperatura de Celsius a Kelvin?",
            listOf("K = °C - 273.15", "K = °C × 1.8 + 32", "K = °C + 273.15", "K = °C / 273.15"),
            2,
            "La relación es K = °C + 273.15. Ejemplo: 23°C = 296.15 K. El cero absoluto es 0 K = -273.15°C.",
            "CONVERSIÓN"
        ),
        Pregunta(
            "¿Cuántos milímetros equivale 1 pulgada?",
            listOf("22.4 mm", "24 mm", "25.4 mm", "30 mm"),
            2,
            "1 pulgada (in) = 25.4 mm exactamente. Por lo tanto, 1 pie = 12 × 25.4 = 304.8 mm.",
            "CONVERSIÓN"
        ),
        Pregunta(
            "¿Cómo se escribe correctamente el símbolo del megapascal?",
            listOf("mpa", "Mpa", "MPa", "MPA"),
            2,
            "MPa: la M (mega) es mayúscula porque es el prefijo; P (pascal) en mayúscula porque Pascal es un apellido propio; a en minúscula. Incorrecto: 'Mpa'.",
            "SÍMBOLOS"
        ),
        Pregunta(
            "¿Los símbolos de unidades se pluralizan?",
            listOf("Sí, se agrega 's' al final", "Sí, cuando el valor es mayor a 1", "No, los símbolos nunca se pluralizan", "Solo los de tiempo"),
            2,
            "Los símbolos de unidades nunca se pluralizan. Correcto: '5 kg', '10 m'. Incorrecto: '5 kgs', '10 ms'.",
            "SÍMBOLOS"
        ),
        Pregunta(
            "¿Qué separador decimal debe usarse en documentos técnicos en México según el SI?",
            listOf("Coma (,)", "Punto (.)", "Barra (/)", "Espacio"),
            1,
            "El SI adopta el punto (.) como separador decimal en México, aunque en algunos países hispanohablantes se usa la coma. Ejemplo: 22.5 MPa.",
            "SÍMBOLOS"
        ),
        Pregunta(
            "¿Se debe dejar espacio entre el número y el símbolo de la unidad?",
            listOf("No, deben ir juntos siempre", "Sí, siempre un espacio (excepción: grados y porcentaje)", "Solo cuando la unidad tiene más de una letra", "Depende de la unidad"),
            1,
            "Siempre debe dejarse un espacio entre el número y el símbolo (25 kg, no 25kg). La excepción aceptada es el símbolo de temperatura (25°C) y ángulo (45°).",
            "SÍMBOLOS"
        ),
        Pregunta(
            "¿Qué prefijo del SI representa 10⁶?",
            listOf("kilo (k)", "hecto (h)", "Mega (M)", "Giga (G)"),
            2,
            "Mega (M) = 10⁶. Por eso 1 MPa = 10⁶ Pa y 1 MHz = 10⁶ Hz. La 'M' del prefijo Mega va en mayúscula.",
            "PREFIJOS"
        ),
        Pregunta(
            "¿Qué prefijo del SI representa 10³?",
            listOf("mega (M)", "hecto (h)", "kilo (k)", "deca (da)"),
            2,
            "kilo (k) = 10³. Por eso 1 km = 10³ m = 1 000 m y 1 kN = 1 000 N. La 'k' del prefijo kilo va en minúscula.",
            "PREFIJOS"
        ),
        Pregunta(
            "¿Cuántos litros equivalen a 1 metro cúbico?",
            listOf("100 L", "500 L", "1 000 L", "10 000 L"),
            2,
            "1 m³ = 1 000 litros = 1 000 dm³. En dosificación de concreto, se trabaja en litros por metro cúbico de mezcla.",
            "CONVERSIÓN"
        ),
        Pregunta(
            "¿Cuál es la unidad base del SI para temperatura?",
            listOf("Grado Celsius (°C)", "Grado Fahrenheit (°F)", "Kelvin (K)", "Rankine (R)"),
            2,
            "El kelvin (K) es la unidad base del SI para temperatura termodinámica. El grado Celsius es una unidad derivada (°C = K - 273.15).",
            "UNIDADES BASE"
        ),
        Pregunta(
            "¿Cuántos Newton equivale 1 kgf?",
            listOf("1 N", "4.44 N", "9.81 N", "98.1 N"),
            2,
            "1 kgf = 9.80665 N ≈ 9.81 N. Esto se debe a que el kgf se define como la fuerza ejercida por 1 kg de masa en la gravedad estándar (g = 9.80665 m/s²).",
            "CONVERSIÓN"
        ),
        Pregunta(
            "¿Cuál es la unidad correcta para reportar resistencia del concreto en documentos técnicos?",
            listOf("kgf/cm²", "lb/in² (psi)", "MPa", "kPa"),
            2,
            "El MPa (megapascal) es la unidad SI correcta. Aunque en México sigue usándose kgf/cm² por costumbre, los documentos técnicos oficiales deben usar MPa.",
            "UNIDADES BASE"
        ),
        Pregunta(
            "¿Cuántos kN equivale 1 tonelada-fuerza (tf)?",
            listOf("1 kN", "9.81 kN", "10 kN", "100 kN"),
            1,
            "1 tonelada-fuerza (tf) = 1 000 kgf = 9.80665 kN ≈ 9.81 kN. Las máquinas de compresión de laboratorio suelen leer en kN.",
            "CONVERSIÓN"
        ),
        Pregunta(
            "¿Cuántos kg equivale 1 libra (lb)?",
            listOf("0.227 kg", "0.454 kg", "0.500 kg", "1.00 kg"),
            1,
            "1 libra (lb) = 0.453592 kg ≈ 0.454 kg. Inversamente, 1 kg = 2.20462 lb.",
            "CONVERSIÓN"
        ),
        Pregunta(
            "¿Cuántos mm equivale 1 pie?",
            listOf("254 mm", "300 mm", "304.8 mm", "360 mm"),
            2,
            "1 pie = 12 pulgadas = 12 × 25.4 mm = 304.8 mm. Los moldes de concreto de 6×12 pulgadas equivalen a 152.4×304.8 mm.",
            "CONVERSIÓN"
        ),
        Pregunta(
            "¿Cuál es la fórmula correcta para calcular resistencia en MPa, dado F en kN y área en mm²?",
            listOf("f = F / A", "f = F × 1000 / A", "f = F / (A × 1000)", "f = F × A / 1000"),
            1,
            "f (MPa) = F (kN) × 1 000 / A (mm²). Como 1 kN = 1 000 N y 1 MPa = 1 N/mm², se multiplica por 1 000 para convertir kN a N.",
            "CONVERSIÓN"
        ),
        Pregunta(
            "¿Cuántos Pa equivale 1 bar?",
            listOf("1 000 Pa", "10 000 Pa", "100 000 Pa", "1 000 000 Pa"),
            2,
            "1 bar = 100 000 Pa = 100 kPa = 0.1 MPa. El bar es una unidad aceptada fuera del SI muy usada en manómetros.",
            "CONVERSIÓN"
        ),
        Pregunta(
            "¿Cuántos prefijos del SI se pueden usar simultáneamente en una unidad?",
            listOf("Dos o más, según sea necesario", "Solo uno por unidad", "Ilimitados", "Depende de la magnitud"),
            1,
            "Solo puede usarse UN prefijo a la vez. No se escribe 'mm μg'; se usa 'ng'. Excepción histórica: el kilogramo ya lleva 'kilo', sus múltiplos usan 'g' como base (Mg, no kkg).",
            "PREFIJOS"
        ),
        Pregunta(
            "¿Qué prefijo representa 10⁻³?",
            listOf("micro (μ)", "centi (c)", "mili (m)", "deci (d)"),
            2,
            "mili (m) = 10⁻³. Por ejemplo: 1 mm = 10⁻³ m. No confundir con la 'm' minúscula del metro.",
            "PREFIJOS"
        ),
        Pregunta(
            "¿Cuántos cm² equivale 1 m²?",
            listOf("100 cm²", "1 000 cm²", "10 000 cm²", "100 000 cm²"),
            2,
            "1 m² = (100 cm)² = 10 000 cm² = 1 000 000 mm². Las áreas cuadráticas se convierten elevando al cuadrado el factor lineal.",
            "CONVERSIÓN"
        ),
        Pregunta(
            "¿Cuál es el error en la expresión '25kg de agregado'?",
            listOf("La cantidad es incorrecta", "Falta el espacio entre el número y el símbolo", "El símbolo 'kg' está mal escrito", "No hay error"),
            1,
            "Correcto: '25 kg de agregado'. Siempre debe haber un espacio entre el número y el símbolo de la unidad.",
            "SÍMBOLOS"
        ),
        Pregunta(
            "¿Cuál es la equivalencia de f'c = 250 kgf/cm² en MPa?",
            listOf("20.0 MPa", "24.5 MPa", "25.0 MPa", "30.0 MPa"),
            1,
            "250 kgf/cm² × 0.098066 MPa/(kgf/cm²) = 24.52 MPa ≈ 24.5 MPa. O bien: 250 / 10.197 = 24.52 MPa.",
            "CONVERSIÓN"
        ),
        Pregunta(
            "¿Cuál es la unidad correcta para expresar temperatura de curado en informes técnicos?",
            listOf("Solo kelvin (K)", "Solo grados Fahrenheit (°F)", "Grados Celsius (°C) o Kelvin (K)", "Grados Rankine (R)"),
            2,
            "En laboratorios de concreto se usa °C para temperatura práctica. El kelvin se usa en cálculos termodinámicos. Ambas son válidas en documentos técnicos.",
            "UNIDADES BASE"
        ),
        Pregunta(
            "¿Cuántos kPa equivale 1 MPa?",
            listOf("10 kPa", "100 kPa", "1 000 kPa", "10 000 kPa"),
            2,
            "1 MPa = 10⁶ Pa = 10³ kPa = 1 000 kPa. El kPa se usa para presiones menores, como la presión de agua.",
            "PREFIJOS"
        ),
        Pregunta(
            "¿Cuál es la unidad base del SI para masa?",
            listOf("Gramo (g)", "Kilogramo (kg)", "Libra (lb)", "Tonelada (t)"),
            1,
            "El kilogramo (kg) es la única unidad base del SI que ya lleva un prefijo en su nombre. El gramo (g) es una unidad derivada: 1 g = 10⁻³ kg.",
            "UNIDADES BASE"
        ),
        Pregunta(
            "¿Es correcto escribir '5 kilogramos por m²'?",
            listOf("Sí, es correcto", "No, se deben usar solo símbolos o solo nombres, no mezclarlos", "Sí, cuando el numerador es nombre y denominador es símbolo", "Solo si la unidad es poco conocida"),
            1,
            "No se deben mezclar nombres escritos con símbolos. Correcto: '5 kg/m²' o '5 kilogramos por metro cuadrado'. Incorrecto: '5 kilogramos por m²'.",
            "SÍMBOLOS"
        ),
        Pregunta(
            "¿Cuántos mm² equivale 1 cm²?",
            listOf("10 mm²", "100 mm²", "1 000 mm²", "10 000 mm²"),
            1,
            "1 cm² = (10 mm)² = 100 mm². Esto es importante para el cálculo de área de cilindros: un cilindro de 150 mm de diámetro tiene área = π(75)² = 17 671 mm².",
            "CONVERSIÓN"
        )
    )

    private fun preguntas083() = listOf(
        Pregunta(
            "¿Cuál es la tolerancia de planitud permitida en la superficie del asiento esférico de la máquina?",
            listOf("±0.10 mm en 150 mm", "±0.05 mm en 150 mm", "±0.25 mm en 150 mm", "±0.05 mm en 100 mm"),
            1,
            "La superficie del asiento esférico no debe diferir más de 0.05 mm en una longitud de 150 mm.",
            "EQUIPO"
        ),
        Pregunta(
            "¿Cuál es el diámetro mínimo de la esfera del asiento respecto al espécimen?",
            listOf("Al menos 50% del diámetro del espécimen", "Al menos 65% del diámetro del espécimen", "Al menos 75% del diámetro del espécimen", "Al menos 90% del diámetro del espécimen"),
            2,
            "El diámetro de la esfera debe ser al menos 75% del diámetro del espécimen a ensayar.",
            "EQUIPO"
        ),
        Pregunta(
            "¿Cuántos grados debe poder girar libremente la porción móvil del asiento esférico?",
            listOf("Al menos 4° en cualquier dirección", "Al menos 2° en cualquier dirección", "Al menos 6° en cualquier dirección", "Al menos 1° en cualquier dirección"),
            0,
            "La porción móvil debe girar libremente al menos 4° en cualquier dirección para asegurar distribución uniforme de la carga.",
            "EQUIPO"
        ),
        Pregunta(
            "¿Cuál es el espesor mínimo del bloque inferior de la máquina de compresión?",
            listOf("15 mm", "18 mm", "20 mm", "22.5 mm"),
            3,
            "El bloque rígido inferior debe tener un espesor mínimo de 22.5 mm.",
            "EQUIPO"
        ),
        Pregunta(
            "¿Cuál es la capacidad mínima de la escala graduada del indicador de carga?",
            listOf("≥ 100 mm", "≥ 200 mm", "≥ 310 mm", "≥ 500 mm"),
            2,
            "La escala graduada del indicador de carga debe tener una capacidad de al menos 310 mm.",
            "EQUIPO"
        ),
        Pregunta(
            "¿Cuál es el error máximo de exactitud tolerado en la máquina de compresión?",
            listOf("±1% de la carga aplicada", "±3% de la carga aplicada", "±5% de la carga aplicada", "±10% de la carga aplicada"),
            1,
            "La norma permite un error de exactitud máximo de ±3% de la carga aplicada en el rango de uso.",
            "EQUIPO"
        ),
        Pregunta(
            "¿Cuál es la división mínima de la escala graduada de la máquina?",
            listOf("0.5 mm", "1 mm", "2 mm", "5 mm"),
            1,
            "La división mínima de la escala del indicador de carga debe ser 1 mm.",
            "EQUIPO"
        ),
        Pregunta(
            "¿Con qué frecuencia mínima debe calibrarse la máquina de compresión?",
            listOf("Cada año o cada 40,000 ensayos (lo que ocurra primero)", "Cada 2 años sin excepción", "Cada 20,000 ensayos únicamente", "Solo antes de poner en operación"),
            0,
            "La calibración debe realizarse al menos una vez al año O cada 40,000 ensayos, lo que ocurra primero.",
            "TOLERANCIAS"
        ),
        Pregunta(
            "¿Cuál es la desviación máxima de perpendicularidad de las bases del espécimen?",
            listOf("1.0° (6 mm en 300 mm)", "0.3° (1.5 mm en 300 mm)", "0.5° (3 mm en 300 mm)", "0.2° (1 mm en 300 mm)"),
            2,
            "Las bases no deben apartarse de la perpendicular en más de 0.5°, equivalente a 3 mm en 300 mm de altura.",
            "TOLERANCIAS"
        ),
        Pregunta(
            "¿Cuánto pueden medir máximo las irregularidades superficiales de las bases del espécimen?",
            listOf("0.10 mm", "0.25 mm", "0.02 mm", "0.05 mm"),
            3,
            "Las irregularidades superficiales de las bases no deben exceder 0.05 mm para asegurar distribución uniforme de la carga.",
            "TOLERANCIAS"
        ),
        Pregunta(
            "¿Cuál es la tolerancia de edad para ensayar a 28 días?",
            listOf("±6 h", "±20 h", "±12 h", "±48 h"),
            1,
            "Los especímenes a los 28 días deben ensayarse con una tolerancia de ±20 h respecto a la edad nominal.",
            "TOLERANCIAS"
        ),
        Pregunta(
            "¿Cuál es la tolerancia de edad para ensayar a 24 horas?",
            listOf("±0.5 h", "±1 h", "±2 h", "±3 h"),
            0,
            "Los especímenes a las 24 horas deben ensayarse con una tolerancia muy estrecha de ±0.5 h.",
            "TOLERANCIAS"
        ),
        Pregunta(
            "¿Cuál es la tolerancia de edad para ensayar a 7 días?",
            listOf("±2 h", "±12 h", "±6 h", "±24 h"),
            2,
            "Los especímenes a los 7 días tienen una tolerancia de ±6 h.",
            "TOLERANCIAS"
        ),
        Pregunta(
            "¿Cuál es el factor de corrección de resistencia para un espécimen con h/d = 1.00?",
            listOf("0.80", "0.85", "0.91", "1.00"),
            2,
            "Cuando h/d = 1.00 la resistencia medida debe multiplicarse por el factor 0.91 según la Tabla 1 de la norma.",
            "TOLERANCIAS"
        ),
        Pregunta(
            "¿Qué factor de corrección se aplica cuando h/d = 2.00?",
            listOf("0.91", "0.95", "0.98", "1.00"),
            3,
            "Cuando h/d = 2.00 (esbeltez estándar) el factor de corrección es 1.00; no se aplica ninguna corrección.",
            "TOLERANCIAS"
        ),
        Pregunta(
            "¿Cuál es la velocidad de aplicación de carga requerida?",
            listOf("0.25 MPa/s ± 0.05 MPa/s", "0.50 MPa/s ± 0.10 MPa/s", "0.10 MPa/s ± 0.02 MPa/s", "1.0 MPa/s ± 0.2 MPa/s"),
            0,
            "La carga debe aplicarse de forma CONTINUA a una velocidad de 0.25 MPa/s ± 0.05 MPa/s, sin impacto ni pérdida de carga.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "Para cilindros de 15 cm de diámetro, ¿cuál es el rango de velocidad de carga en kN/s?",
            listOf("1.5 a 3.0 kN/s", "3.5 a 5.3 kN/s", "5.0 a 8.0 kN/s", "2.0 a 4.0 kN/s"),
            1,
            "Para cilindros de 15 cm de diámetro la velocidad mínima es 3.5 kN/s y la máxima es 5.3 kN/s.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Cómo se coloca un cubo de concreto en la máquina para el ensayo?",
            listOf("Sobre la cara que fue la parte superior en el molde", "Sobre la cara que estuvo en contacto con las paredes del molde", "En cualquier orientación, no importa", "Sobre la cara inferior del molde"),
            1,
            "Los cubos se colocan sobre las caras que estuvieron en contacto con las paredes del molde, nunca sobre la cara superior o inferior.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿En qué espécimen se continúa la carga para observar el tipo de falla completo?",
            listOf("En todos los especímenes", "En 1 de cada 5 especímenes", "En todos los especímenes del lote", "En 1 de cada 10 especímenes"),
            3,
            "En 1 de cada 10 especímenes se continúa la carga hasta observar claramente el tipo de falla.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Cuántas dimensiones se miden en el espécimen antes del ensayo?",
            listOf("2 medidas del diámetro y 1 de altura", "2 medidas perpendiculares del diámetro y 2 alturas opuestas", "1 medida del diámetro y 1 de altura", "3 medidas del diámetro y 3 alturas"),
            1,
            "Se toman 2 medidas perpendiculares a la altura media del espécimen y 2 medidas de altura en posiciones opuestas.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Con qué aproximación se miden las dimensiones del espécimen?",
            listOf("0.1 mm", "0.5 mm", "1 mm", "5 mm"),
            2,
            "Las dimensiones del espécimen (diámetro y altura) se miden con una aproximación de 1 mm.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Qué se debe hacer con un espécimen cuya relación h/d es menor que 1:1?",
            listOf("Corregir la resistencia con factor de Tabla 1", "Ensayar sin corrección", "No ensayar; está fuera de rango", "Triplicar el resultado"),
            2,
            "La norma prohíbe ensayar especímenes con h/d < 1:1. Deben descartarse o reportarse como no conformes.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Cuál es la fórmula para calcular la resistencia a la compresión?",
            listOf("fc = F × A", "fc = A / F", "fc = F / A", "fc = F + A"),
            2,
            "La resistencia se calcula como fc = F / A, donde F es la carga máxima en N y A es el área de la sección transversal en mm².",
            "CÁLCULO"
        ),
        Pregunta(
            "¿Con qué aproximación se reporta el resultado de resistencia?",
            listOf("10 kPa (0.1 kg/cm²)", "50 kPa (0.5 kg/cm²)", "100 kPa (1 kg/cm²)", "500 kPa (5 kg/cm²)"),
            2,
            "El resultado se reporta con una aproximación de 100 kPa, equivalente a 1 kg/cm².",
            "CÁLCULO"
        ),
        Pregunta(
            "¿Cuántos especímenes mínimo deben promediarse para reportar la resistencia?",
            listOf("1 espécimen", "2 especímenes", "3 especímenes", "5 especímenes"),
            1,
            "El resultado final es el promedio de al menos 2 especímenes de la misma muestra y edad.",
            "CÁLCULO"
        ),
        Pregunta(
            "¿Cuál es la repetibilidad del método (un operador, mismo laboratorio)?",
            listOf("2.9%", "5.0%", "7.0%", "1.5%"),
            0,
            "La repetibilidad (coeficiente de variación de un solo operador) es 2.9% para cilindros.",
            "CÁLCULO"
        ),
        Pregunta(
            "¿Cuál es la reproducibilidad del método (distintos laboratorios)?",
            listOf("2.9%", "3.5%", "4.0%", "5.0%"),
            3,
            "La reproducibilidad (variación entre laboratorios) es 5.0% para cilindros.",
            "CÁLCULO"
        ),
        Pregunta(
            "¿Qué tipo de falla en cilindros es la más deseable e indica ensayo correcto?",
            listOf("Tipo 1 (doble cono en ambos extremos)", "Tipo 3 (fractura columnar)", "Tipo 4 (diagonal sin grietas)", "Tipo 5 (fracturas en lados)"),
            0,
            "La falla Tipo 1 con conos bien formados en ambos extremos es la más deseable e indica una distribución uniforme de la carga.",
            "CÁLCULO"
        ),
        Pregunta(
            "¿Qué tipo de falla muestra fracturas verticales columnares sin conos formados?",
            listOf("Tipo 1", "Tipo 2", "Tipo 3", "Tipo 4"),
            2,
            "La falla Tipo 3 muestra fracturas verticales columnares sin formación de conos; puede indicar velocidad de carga inadecuada.",
            "CÁLCULO"
        ),
        Pregunta(
            "¿Dentro de qué porcentaje del radio debe estar el centro de la esfera del asiento?",
            listOf("±2% del radio", "±5% del radio", "±10% del radio", "±15% del radio"),
            1,
            "El centro de la esfera debe estar dentro del ±5% del radio del bloque de carga superior.",
            "EQUIPO"
        ),
        Pregunta(
            "Si las bases del espécimen no cumplen planitud, ¿qué normas permiten corregirlo?",
            listOf("NMX-C-159 o NMX-C-083", "NMX-C-083 o NMX-C-148", "NMX-C-109 (cabeceo) o NMX-C-469 (neopreno)", "NMX-C-161 o NMX-C-156"),
            2,
            "Si las bases no cumplen planitud se deben cabecear según NMX-C-109 o usar almohadillas de neopreno según NMX-C-469.",
            "TOLERANCIAS"
        )
    )

    private fun preguntas148() = listOf(
        Pregunta(
            "¿Cuál es la temperatura requerida en el cuarto húmedo para curado de concreto?",
            listOf("296 K ± 2 K (23°C ± 2°C)", "293 K ± 2 K (20°C ± 2°C)", "300 K ± 2 K (27°C ± 2°C)", "290 K ± 2 K (17°C ± 2°C)"),
            0,
            "La temperatura debe mantenerse a 296 K ± 2 K, equivalente a 23°C ± 2°C, tanto en cuartos/gabinetes como en el agua del tanque.",
            "CONDICIONES"
        ),
        Pregunta(
            "¿Cuál es la humedad relativa mínima requerida en el cuarto o gabinete húmedo?",
            listOf("70%", "80%", "95%", "100%"),
            2,
            "La humedad relativa mínima es del 95%. Las superficies expuestas de los especímenes deben verse con brillo acuoso.",
            "CONDICIONES"
        ),
        Pregunta(
            "¿Cómo deben verse las superficies expuestas de los especímenes para confirmar humedad suficiente?",
            listOf("Levemente húmedas al tacto", "Con brillo acuoso (capa de agua visible)", "Completamente secas", "Con gotas de agua grandes"),
            1,
            "Las superficies deben verse con brillo acuoso, lo que indica una capa continua de agua sobre el espécimen.",
            "CONDICIONES"
        ),
        Pregunta(
            "¿A qué temperatura debe mantenerse el agua en el tanque de almacenamiento?",
            listOf("290 K ± 2 K", "294 K ± 2 K", "296 K ± 2 K (23°C ± 2°C)", "300 K ± 2 K"),
            2,
            "El agua del tanque también debe estar a 296 K ± 2 K (23°C ± 2°C), igual que cuartos y gabinetes.",
            "CONDICIONES"
        ),
        Pregunta(
            "¿Qué diferencia hay entre cuarto húmedo y gabinete húmedo?",
            listOf("El cuarto tiene temperatura más alta", "El cuarto permite transitar en su interior; el gabinete no", "El gabinete usa agua corriente", "No hay diferencia práctica"),
            1,
            "El cuarto húmedo es una habitación donde se puede transitar. El gabinete es más pequeño y no permite transitar en su interior.",
            "CONDICIONES"
        ),
        Pregunta(
            "¿Cuántas lecturas de temperatura y humedad se deben tomar diariamente en un laboratorio?",
            listOf("1 lectura fija", "3 lecturas aleatorias", "5 lecturas por hora", "2 lecturas fijas"),
            1,
            "En laboratorio se deben tomar 3 lecturas aleatorias durante el día laboral.",
            "OPERACIÓN"
        ),
        Pregunta(
            "¿Cuántas lecturas mínimo se toman en obra durante el día?",
            listOf("1 lectura", "2 lecturas", "3 lecturas", "5 lecturas"),
            1,
            "En obra (condiciones de campo) se requieren mínimo 2 lecturas durante el día.",
            "OPERACIÓN"
        ),
        Pregunta(
            "Si el termómetro no está fijo en el cuarto, ¿cuántos minutos mínimo antes de leer debe introducirse?",
            listOf("5 minutos", "10 minutos", "15 minutos", "30 minutos"),
            2,
            "El termómetro debe introducirse al menos 15 minutos antes de tomar la lectura para alcanzar equilibrio térmico con el ambiente interior.",
            "OPERACIÓN"
        ),
        Pregunta(
            "¿Cuál es la precisión mínima requerida para el termómetro?",
            listOf("0.1 K (0.1°C)", "0.5 K (0.5°C)", "1 K (1°C)", "2 K (2°C)"),
            2,
            "El termómetro debe tener una precisión mínima de 1 K (equivalente a 1°C).",
            "EQUIPO"
        ),
        Pregunta(
            "¿Cuál es la precisión mínima requerida para el higrómetro o psicrómetro?",
            listOf("2 K y 5% de HR", "1 K (1°C) y 1% de HR", "0.5 K y 2% de HR", "3 K y 3% de HR"),
            1,
            "El higrómetro o psicrómetro debe tener una precisión mínima de 1 K en temperatura Y 1% en humedad relativa.",
            "EQUIPO"
        ),
        Pregunta(
            "¿Cuál es la separación mínima entre especímenes dentro del tanque de almacenamiento?",
            listOf("0.5 cm", "1 cm", "2 cm", "5 cm"),
            1,
            "Los especímenes deben estar separados entre sí un mínimo de 1 cm para permitir la circulación del agua.",
            "TANQUE"
        ),
        Pregunta(
            "¿Cuál es la separación mínima de los especímenes respecto a las paredes del tanque?",
            listOf("1 cm", "2 cm", "3 cm", "5 cm"),
            2,
            "Los especímenes deben estar a un mínimo de 3 cm de las paredes del tanque.",
            "TANQUE"
        ),
        Pregunta(
            "¿Cuántos centímetros de agua debe haber sobre la superficie libre de los especímenes?",
            listOf("Mínimo 1 cm", "Mínimo 2 cm", "Mínimo 5 cm", "Mínimo 10 cm"),
            1,
            "El tirante de agua debe ser de mínimo 2 cm sobre la superficie libre de los especímenes.",
            "TANQUE"
        ),
        Pregunta(
            "¿A qué distancia mínima del espécimen debe estar el elemento calefactor del tanque?",
            listOf("5 cm", "10 cm", "15 cm", "20 cm"),
            1,
            "El elemento calefactor debe estar alejado mínimo 10 cm de los especímenes para evitar calentamiento localizado.",
            "TANQUE"
        ),
        Pregunta(
            "¿Cuántos gramos de Ca(OH)₂ por litro debe contener el agua del tanque?",
            listOf("Mínimo 1.0 g/L", "Mínimo 2.0 g/L", "Mínimo 3.0 g/L", "Mínimo 5.0 g/L"),
            2,
            "El agua del tanque debe ser agua saturada de cal con al menos 3.0 g de Ca(OH)₂ por litro (3 kg/m³).",
            "TANQUE"
        ),
        Pregunta(
            "¿Con qué frecuencia máxima se debe mezclar el agua del tanque?",
            listOf("Cada semana", "Cada 15 días", "Intervalos no mayores de 1 mes", "Cada 6 meses"),
            2,
            "El agua del tanque debe mezclarse o agitarse a intervalos no mayores de 1 mes para mantener la concentración homogénea.",
            "TANQUE"
        ),
        Pregunta(
            "¿Con qué frecuencia máxima debe realizarse la limpieza completa del tanque?",
            listOf("Cada 3 meses", "Cada 6 meses", "Cada 12 meses", "Cada 2 años"),
            2,
            "La limpieza completa del tanque debe realizarse como máximo cada 12 meses.",
            "TANQUE"
        ),
        Pregunta(
            "¿Qué tipo de agua NO debe usarse en el tanque de almacenamiento?",
            listOf("Agua saturada de cal", "Agua potable con Ca(OH)₂", "Agua corriente continua o agua desmineralizada", "Agua de pozo con cal"),
            2,
            "No debe usarse flujo continuo de agua corriente ni agua desmineralizada porque lixivian el calcio del concreto.",
            "TANQUE"
        ),
        Pregunta(
            "¿Por qué se usa agua saturada de Ca(OH)₂ en lugar de agua pura?",
            listOf("Por ser más barata", "Para prevenir la lixiviación del calcio del cemento hidratado", "Por ser más fácil de conseguir", "Porque mejora la resistencia del concreto"),
            1,
            "El agua saturada de cal evita que el agua extraiga el Ca(OH)₂ del concreto, lo que alteraría la resistencia real.",
            "TANQUE"
        ),
        Pregunta(
            "¿Cuál de las siguientes es la definición correcta de cuarto húmedo?",
            listOf("Recipiente o pileta con agua controlada", "Gabinete pequeño no transitable", "Habitación cerrada con control de temperatura y humedad donde se puede transitar", "Cámara de vacío para curado acelerado"),
            2,
            "El cuarto húmedo es una habitación cerrada de dimensiones suficientes para transitar en su interior, con control de temperatura y humedad.",
            "CONDICIONES"
        ),
        Pregunta(
            "¿Qué tipo de instrumento es aceptable para medir la humedad relativa según la norma?",
            listOf("Solo termómetros de mercurio", "Higrómetro o psicrómetro con precisión de 1 K y 1% HR", "Cualquier sensor de temperatura", "Solo sensores digitales con certificado"),
            1,
            "La norma acepta higrómetro o psicrómetro con precisión mínima de 1 K en temperatura y 1% en humedad relativa.",
            "EQUIPO"
        ),
        Pregunta(
            "¿Qué se debe registrar en cada lectura diaria?",
            listOf("Solo la temperatura", "Solo la humedad relativa", "Temperatura y humedad relativa", "Temperatura, humedad y nivel del agua"),
            2,
            "En cada lectura se deben registrar tanto la temperatura como la humedad relativa, con hora y fecha.",
            "OPERACIÓN"
        ),
        Pregunta(
            "¿Qué se debe hacer al detectar una temperatura fuera del rango de 296 K ± 2 K?",
            listOf("Ignorarlo si es por poco tiempo", "Registrar el valor y aplicar acción correctiva en el sistema de clima", "Sacar los especímenes del cuarto inmediatamente", "Agregar más hielo al sistema"),
            1,
            "Al detectar una temperatura fuera de rango se debe registrar el evento y aplicar las acciones correctivas al sistema de calefacción/enfriamiento.",
            "OPERACIÓN"
        ),
        Pregunta(
            "¿A qué tipos de especímenes aplica esta norma?",
            listOf("Solo especímenes de concreto hidráulico", "Especímenes de pasta, mortero o concreto", "Solo cilindros de 15 cm de diámetro", "Solo cubos de concreto"),
            1,
            "La norma NMX-C-148 aplica a gabinetes, cuartos y tanques para conservar especímenes de pasta, mortero O concreto.",
            "CONDICIONES"
        ),
        Pregunta(
            "¿Qué concentración de Ca(OH)₂ equivale a 3.0 g/L en términos de kg/m³?",
            listOf("0.3 kg/m³", "1.0 kg/m³", "3.0 kg/m³", "30 kg/m³"),
            2,
            "3.0 g/L equivale exactamente a 3.0 kg/m³ (3 gramos por cada 1000 mL = 3 kg por cada 1000 L = 1 m³).",
            "TANQUE"
        ),
        Pregunta(
            "¿Cuál es el rango permitido de temperatura en el cuarto húmedo?",
            listOf("Entre 15°C y 30°C", "Entre 20°C y 30°C", "Entre 21°C y 25°C", "Entre 18°C y 28°C"),
            2,
            "El rango permitido es 296 K ± 2 K, que equivale a entre 21°C y 25°C (entre 294 K y 298 K).",
            "CONDICIONES"
        ),
        Pregunta(
            "¿Cómo se repone el agua perdida por evaporación en el tanque?",
            listOf("Con agua potable pura", "Con agua destilada fría", "Con agua previamente saturada con Ca(OH)₂", "Con agua de lluvia filtrada"),
            2,
            "Al reponer agua evaporada se debe agregar agua previamente saturada con Ca(OH)₂ para mantener la concentración requerida.",
            "OPERACIÓN"
        ),
        Pregunta(
            "¿Cuál es la diferencia principal entre gabinete y tanque de almacenamiento?",
            listOf("El gabinete usa agua; el tanque usa aire húmedo", "El gabinete controla humedad del aire; el tanque sumerge especímenes en agua", "No hay diferencia entre ellos", "El tanque controla humedad del aire; el gabinete usa agua"),
            1,
            "El gabinete húmedo controla la humedad del aire alrededor de los especímenes. El tanque los sumerge directamente en agua saturada de cal.",
            "CONDICIONES"
        ),
        Pregunta(
            "¿Qué pasa si los especímenes en el cuarto húmedo no presentan brillo acuoso?",
            listOf("Es normal si la temperatura es correcta", "La humedad relativa puede ser insuficiente; revisar el sistema de humidificación", "Indica que el concreto ya está completamente curado", "Solo significa que la superficie absorbió el agua"),
            1,
            "Si no hay brillo acuoso es señal de que la humedad relativa puede estar por debajo del 95% mínimo requerido.",
            "CONDICIONES"
        ),
        Pregunta(
            "¿Cuántas lecturas en total se deben tomar en obra durante un día laboral de 8 horas?",
            listOf("1 lectura", "Al menos 2 lecturas", "Al menos 5 lecturas", "Al menos 8 lecturas"),
            1,
            "En obra se requieren mínimo 2 lecturas durante el día laboral para verificar las condiciones de curado.",
            "OPERACIÓN"
        ),
        Pregunta(
            "¿Cuál de las siguientes medidas NO es un requisito para el tanque de almacenamiento?",
            listOf("Elementos calefactores a ≥10 cm de los especímenes", "Tirante de agua mínimo 2 cm sobre especímenes", "Aireación continua con burbujeo de aire", "Separación de especímenes entre sí de ≥1 cm"),
            2,
            "La norma no requiere aireación con burbujeo. Los demás requisitos (calefactor, tirante de agua, separación) sí están establecidos.",
            "TANQUE"
        )
    )

    private fun preguntas156() = listOf(
        Pregunta(
            "¿Cuál es el diámetro de la base mayor (inferior) del cono de Abrams?",
            listOf("10 cm", "20 cm", "30 cm", "15 cm"),
            1,
            "La base mayor (inferior) del molde tiene un diámetro de 20 cm.",
            "EQUIPO"
        ),
        Pregunta(
            "¿Cuál es la altura del cono de Abrams?",
            listOf("20 cm", "25 cm", "30 cm", "35 cm"),
            2,
            "La altura del molde cónico es de 30 cm.",
            "EQUIPO"
        ),
        Pregunta(
            "¿Cuál es el diámetro de la base menor (superior) del cono de Abrams?",
            listOf("10 cm", "15 cm", "20 cm", "5 cm"),
            0,
            "La base menor (superior) del molde tiene un diámetro de 10 cm.",
            "EQUIPO"
        ),
        Pregunta(
            "¿Cuál es la tolerancia dimensional del molde en todas sus medidas?",
            listOf("±1 mm", "±2 mm", "±5 mm", "±3 mm"),
            3,
            "La norma establece una tolerancia de ±3 mm en todas las dimensiones del molde (bases y altura).",
            "EQUIPO"
        ),
        Pregunta(
            "¿Cuál es el diámetro de la varilla de compactación?",
            listOf("10 mm", "12 mm", "16 mm", "20 mm"),
            2,
            "La varilla de compactación debe tener 16 mm de diámetro.",
            "EQUIPO"
        ),
        Pregunta(
            "¿Cuál es la longitud aproximada de la varilla de compactación?",
            listOf("300 mm", "450 mm", "600 mm", "750 mm"),
            2,
            "La longitud aproximada de la varilla es de 600 mm.",
            "EQUIPO"
        ),
        Pregunta(
            "¿Cómo son los extremos de la varilla de compactación?",
            listOf("Planos y cuadrados", "Cónicos (en punta)", "Semiesféricos (redondeados)", "Dentados"),
            2,
            "Los extremos de la varilla deben ser semiesféricos (redondeados) para no dañar el concreto durante la compactación.",
            "EQUIPO"
        ),
        Pregunta(
            "¿Cuántas asas tiene el cono de Abrams para facilitar el levantamiento?",
            listOf("1 asa", "2 asas", "3 asas", "4 asas"),
            1,
            "El molde debe tener 2 asas para facilitar el levantamiento recto y uniforme.",
            "EQUIPO"
        ),
        Pregunta(
            "¿Cuántas capas se utilizan para llenar el cono durante el ensayo?",
            listOf("1 capa", "2 capas", "3 capas", "4 capas"),
            2,
            "El llenado se realiza en 3 capas aproximadamente iguales en volumen.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Cuántas penetraciones se dan con la varilla en cada capa?",
            listOf("10 penetraciones", "15 penetraciones", "20 penetraciones", "25 penetraciones"),
            3,
            "Se aplican 25 penetraciones por capa con la varilla de compactación.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Hasta qué altura aproximada se llena la primera capa?",
            listOf("3 cm", "5 cm", "7 cm", "10 cm"),
            2,
            "La primera capa se llena hasta aproximadamente 7 cm de altura.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Hasta qué altura aproximada se llena la segunda capa?",
            listOf("10 cm", "12 cm", "15 cm", "20 cm"),
            2,
            "La segunda capa se llena hasta aproximadamente 15 cm de altura (la mitad del molde).",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Qué profundidad penetra la varilla en la capa anterior en la 2ª y 3ª capas?",
            listOf("1 cm", "2 cm", "5 cm", "10 cm"),
            1,
            "En la 2ª y 3ª capas la varilla penetra aproximadamente 2 cm dentro de la capa anterior para garantizar la compactación entre capas.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿En cuántos segundos debe levantarse el molde al retirar?",
            listOf("2 s ± 1 s", "3 s ± 1 s", "5 s ± 2 s", "10 s ± 3 s"),
            2,
            "El molde debe levantarse en 5 s ± 2 s en dirección vertical, sin movimiento lateral ni torsional.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Cuál es el tiempo máximo desde el inicio del llenado hasta el levantamiento del molde?",
            listOf("1.5 min", "2.0 min", "2.5 min", "5.0 min"),
            2,
            "El tiempo total desde el inicio del llenado hasta el retiro del molde no debe exceder 2.5 minutos.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Cómo debe realizarse el movimiento al levantar el molde?",
            listOf("Con movimiento circular suave", "Vertical, sin movimiento lateral ni torsional", "Con ligera inclinación hacia el operador", "Rápido con movimiento de vaivén"),
            1,
            "El molde debe levantarse estrictamente en dirección vertical, sin ningún movimiento lateral ni torsional.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Cuándo se descarta el resultado y se repite con nueva porción?",
            listOf("Siempre que el revenimiento sea mayor de 15 cm", "Cuando parte del concreto se desliza o cae a un lado al retirar el molde", "Si el tiempo de llenado fue mayor de 2 min", "Cuando la temperatura supera 25°C"),
            1,
            "Si una porción del concreto se desliza o cae a un lado al retirar el molde, se descarta el resultado y se repite con nueva porción.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Qué indican 2 fallas consecutivas (concreto que cae) durante el ensayo?",
            listOf("Que el operador no tiene experiencia", "Que el concreto es muy resistente", "Que el concreto probablemente carece de plasticidad; el ensayo no es aplicable", "Que el molde está defectuoso"),
            2,
            "Dos fallas consecutivas indican que el concreto no tiene suficiente plasticidad para este ensayo. El método no es aplicable a ese concreto.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Cómo se realizan las primeras penetraciones en cada capa?",
            listOf("Todas en el centro con varilla vertical", "Aproximadamente la mitad cerca del perímetro con varilla inclinada, luego en espiral hacia el centro", "Todas en espiral desde el centro hacia afuera", "Aleatoriamente por toda la superficie"),
            1,
            "Aproximadamente la mitad de las penetraciones se realizan cerca del perímetro con la varilla inclinada; el resto en espiral hacia el centro con varilla vertical.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Cuál es la tolerancia para revenimiento nominal menor de 50 mm?",
            listOf("±5 mm", "±10 mm", "±15 mm", "±20 mm"),
            2,
            "Para revenimiento nominal menor de 50 mm la tolerancia es ±15 mm.",
            "TOLERANCIAS"
        ),
        Pregunta(
            "¿Cuál es la tolerancia para revenimiento nominal entre 50 mm y 100 mm?",
            listOf("±15 mm", "±20 mm", "±25 mm", "±35 mm"),
            2,
            "Para revenimiento nominal de 50 a 100 mm la tolerancia es ±25 mm.",
            "TOLERANCIAS"
        ),
        Pregunta(
            "¿Cuál es la tolerancia para revenimiento nominal mayor de 100 mm?",
            listOf("±25 mm", "±30 mm", "±35 mm", "±50 mm"),
            2,
            "Para revenimiento nominal mayor de 100 mm la tolerancia es ±35 mm.",
            "TOLERANCIAS"
        ),
        Pregunta(
            "¿Cuál es la desviación estándar máxima para un solo operador?",
            listOf("5 mm", "7 mm", "10 mm", "12.5 mm"),
            1,
            "La desviación estándar máxima para un operador (mismo laboratorio) es de 7 mm.",
            "TOLERANCIAS"
        ),
        Pregunta(
            "¿Cuál es la diferencia máxima permisible entre dos mediciones del mismo operador (d2s)?",
            listOf("10 mm", "15 mm", "20 mm", "25 mm"),
            2,
            "Dos mediciones del mismo operador no deben diferir en más de 20 mm (valor d2s).",
            "TOLERANCIAS"
        ),
        Pregunta(
            "¿Cuál es la diferencia máxima permisible entre operadores distintos (d2s)?",
            listOf("20 mm", "25 mm", "30 mm", "35 mm"),
            3,
            "Entre operadores de distintos laboratorios la diferencia máxima es de 35 mm (valor d2s).",
            "TOLERANCIAS"
        ),
        Pregunta(
            "¿Cuál es la desviación estándar máxima para varios operadores distintos?",
            listOf("7 mm", "10 mm", "12.5 mm", "15 mm"),
            2,
            "La desviación estándar máxima para varios operadores es de 12.5 mm.",
            "TOLERANCIAS"
        ),
        Pregunta(
            "¿Con qué aproximación se reporta el revenimiento?",
            listOf("0.1 cm", "0.5 cm", "1 cm", "5 mm"),
            2,
            "El revenimiento se reporta con una aproximación de 1 cm.",
            "RESULTADOS"
        ),
        Pregunta(
            "¿Cuál es el rango de revenimientos al que aplica esta norma?",
            listOf("0 cm a 10 cm", "2 cm a 20 cm", "5 cm a 25 cm", "0 cm a 30 cm"),
            1,
            "La norma aplica a concreto con revenimientos entre 2 cm y 20 cm. Fuera de este rango el ensayo no es confiable.",
            "RESULTADOS"
        ),
        Pregunta(
            "¿Cuál es el tamaño máximo nominal (TMN) máximo del agregado para aplicar esta norma?",
            listOf("25 mm", "38 mm", "50 mm", "75 mm"),
            2,
            "La norma aplica solo cuando el TMN del agregado es menor de 50 mm.",
            "RESULTADOS"
        ),
        Pregunta(
            "¿Qué se mide para obtener el valor del revenimiento?",
            listOf("La altura total del concreto después del desmoldeo", "La diferencia de alturas entre el borde del molde y el centro de la superficie superior del concreto", "El peso del concreto derramado al retirar el molde", "El diámetro de la base del cono de concreto"),
            1,
            "El revenimiento es la diferencia de altura entre la parte superior del molde y el centro de la superficie superior del concreto después de retirar el molde.",
            "RESULTADOS"
        ),
        Pregunta(
            "¿Con qué tipo de superficies se debe realizar el ensayo?",
            listOf("En cualquier superficie disponible", "Sobre una superficie horizontal, plana, rígida, húmeda y no absorbente", "Solo sobre plataformas de madera", "Sobre la cama de grava de la obra"),
            1,
            "El molde debe colocarse sobre una superficie horizontal, plana, rígida, húmeda y no absorbente para asegurar resultados válidos.",
            "RESULTADOS"
        )
    )

    private fun preguntas159() = listOf(
        Pregunta(
            "¿Cuál es la dimensión mínima del molde respecto al Tamaño Máximo Nominal (TMN) del agregado?",
            listOf("Mínimo 1 × TMN", "Mínimo 2 × TMN", "Mínimo 3 × TMN", "Mínimo 5 × TMN"),
            2,
            "La dimensión menor del molde debe ser al menos 3 veces el Tamaño Máximo Nominal del agregado grueso.",
            "MOLDES"
        ),
        Pregunta(
            "¿Con qué material se revisten los moldes antes de usar?",
            listOf("Aceite mineral u otro material no reactivo", "Agua jabonosa", "Grasa animal", "Cemento diluido"),
            0,
            "Los moldes deben revestirse con aceite mineral u otro material no reactivo antes de cada uso para facilitar el descimbrado.",
            "MOLDES"
        ),
        Pregunta(
            "¿Cuánto puede diferir un diámetro del molde cilíndrico respecto al promedio de los diámetros?",
            listOf("No más del 0.5%", "No más del 1%", "No más del 2%", "No más del 5%"),
            2,
            "Ningún diámetro del molde cilíndrico puede diferir del promedio de los diámetros en más del 2%.",
            "MOLDES"
        ),
        Pregunta(
            "¿Cuáles son las dimensiones de la sección transversal de la viga estándar?",
            listOf("100 × 100 mm", "150 × 150 mm", "200 × 200 mm", "300 × 150 mm"),
            1,
            "La viga estándar tiene una sección transversal de 150 × 150 mm.",
            "MOLDES"
        ),
        Pregunta(
            "¿Cuál es la longitud mínima del molde prismático (viga)?",
            listOf("≥ 3 × peralte", "≥ 50 mm + 2 × peralte", "≥ 50 mm + 3 × peralte", "≥ 100 mm + 2 × peralte"),
            2,
            "La longitud del molde prismático debe ser igual o mayor a 50 mm más tres veces el peralte.",
            "MOLDES"
        ),
        Pregunta(
            "¿Cuál es el diámetro mínimo permitido para moldes cilíndricos?",
            listOf("3 cm", "5 cm", "7 cm", "10 cm"),
            1,
            "El diámetro mínimo del molde cilíndrico es 5 cm, con una longitud mínima de 10 cm.",
            "MOLDES"
        ),
        Pregunta(
            "¿Cuál es la tolerancia de perpendicularidad en moldes cúbicos?",
            listOf("Desviación máxima 0.1°", "Desviación máxima 0.5°", "Desviación máxima 1.0°", "Desviación máxima 2.0°"),
            1,
            "Los lados adyacentes de los moldes cúbicos deben ser perpendiculares entre sí con una desviación máxima de 0.5°.",
            "MOLDES"
        ),
        Pregunta(
            "¿Cuál es la variación máxima en dimensiones de moldes cúbicos respecto a la nominal?",
            listOf("0.5% de la dimensión nominal", "1% de la dimensión nominal", "2% de la dimensión nominal", "5% de la dimensión nominal"),
            1,
            "La variación máxima en las dimensiones de los moldes cúbicos es del 1% respecto a la dimensión nominal.",
            "MOLDES"
        ),
        Pregunta(
            "¿Con qué revenimiento se debe usar exclusivamente varillado para compactar?",
            listOf("Revenimiento > 3 cm", "Revenimiento > 5 cm", "Revenimiento > 8 cm", "Revenimiento > 10 cm"),
            2,
            "Cuando el revenimiento es mayor de 8 cm se debe usar ÚNICAMENTE el método de varillado.",
            "COMPACTACIÓN"
        ),
        Pregunta(
            "¿Con qué revenimiento se debe usar exclusivamente vibrado para compactar?",
            listOf("Revenimiento < 1 cm", "Revenimiento < 2 cm", "Revenimiento < 3 cm", "Revenimiento < 5 cm"),
            2,
            "Cuando el revenimiento es menor de 3 cm se debe usar ÚNICAMENTE el método de vibrado.",
            "COMPACTACIÓN"
        ),
        Pregunta(
            "¿Cuándo pueden usarse indistintamente varillado O vibrado?",
            listOf("Revenimiento entre 1 y 5 cm", "Revenimiento entre 3 y 8 cm", "Revenimiento entre 5 y 10 cm", "Siempre que el operador lo decida"),
            1,
            "Cuando el revenimiento está entre 3 y 8 cm pueden usarse cualquiera de los dos métodos: varillado o vibrado.",
            "COMPACTACIÓN"
        ),
        Pregunta(
            "¿Cuántas penetraciones por capa se aplican en un cilindro de 150 mm de diámetro?",
            listOf("10 penetraciones", "15 penetraciones", "25 penetraciones", "50 penetraciones"),
            2,
            "Para cilindros de 150 mm de diámetro se aplican 25 penetraciones por capa usando la varilla larga de 16 mm.",
            "COMPACTACIÓN"
        ),
        Pregunta(
            "¿Cuántas penetraciones por capa se aplican en un cilindro de 200 mm de diámetro?",
            listOf("25 penetraciones", "35 penetraciones", "50 penetraciones", "75 penetraciones"),
            2,
            "Para cilindros de 200 mm de diámetro se aplican 50 penetraciones por capa.",
            "COMPACTACIÓN"
        ),
        Pregunta(
            "¿Cuántas capas se usan para compactar un cilindro de 150 mm de diámetro?",
            listOf("1 capa", "2 capas", "3 capas", "4 capas"),
            2,
            "Los cilindros de 150 mm de diámetro se llenan y compactan en 3 capas.",
            "COMPACTACIÓN"
        ),
        Pregunta(
            "¿Cuántas capas se usan en cilindros de 75 a 100 mm de diámetro?",
            listOf("1 capa", "2 capas", "3 capas", "4 capas"),
            1,
            "Los cilindros pequeños de 75 a 100 mm de diámetro se compactan en 2 capas.",
            "COMPACTACIÓN"
        ),
        Pregunta(
            "¿Qué varilla se usa en cilindros de 75-100 mm de diámetro?",
            listOf("Varilla de 16 mm (larga)", "Varilla de 12 mm", "Varilla corta de 10 mm", "Varilla de 20 mm"),
            2,
            "En cilindros de 75-100 mm se usa la varilla CORTA de 10 mm de diámetro.",
            "COMPACTACIÓN"
        ),
        Pregunta(
            "¿Cuánto debe penetrar la varilla dentro de la capa anterior durante el varillado?",
            listOf("5 mm", "10 mm", "20 mm", "50 mm"),
            2,
            "Al varillas cada capa, la varilla debe penetrar aproximadamente 20 mm dentro de la capa inferior.",
            "COMPACTACIÓN"
        ),
        Pregunta(
            "¿Cuántas inserciones del vibrador se hacen por capa al usar vibrado interno?",
            listOf("1 inserción", "2 inserciones", "3 inserciones", "5 inserciones"),
            2,
            "Se realizan 3 inserciones del vibrador por capa cuando se usa vibración interna en cilindros.",
            "COMPACTACIÓN"
        ),
        Pregunta(
            "¿Cuál es la frecuencia mínima del vibrador de inmersión?",
            listOf("3,600 vibraciones/min", "6,000 vibraciones/min", "9,000 vibraciones/min", "12,000 vibraciones/min"),
            2,
            "El vibrador de inmersión debe tener una frecuencia mínima de 9,000 vibraciones por minuto.",
            "COMPACTACIÓN"
        ),
        Pregunta(
            "¿En qué tipo de cilindros NO se debe usar vibración interna?",
            listOf("Cilindros de 200 mm de diámetro", "Cilindros de 150 mm de diámetro", "Cilindros de 10 cm (100 mm) o menor de diámetro", "Cilindros con revenimiento > 8 cm"),
            2,
            "La norma prohíbe el uso de vibración interna en cilindros de 10 cm o menor de diámetro porque el cabezal del vibrador podría tocar las paredes.",
            "COMPACTACIÓN"
        ),
        Pregunta(
            "¿Cuándo se debe cubrir el espécimen después de terminado?",
            listOf("A las 2 horas de terminado", "Inmediatamente después de terminado", "Al día siguiente del moldeado", "Solo si hay viento o sol directo"),
            1,
            "Los especímenes deben cubrirse INMEDIATAMENTE después de terminados con placa no absorbente o membrana plástica impermeable.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Cuál es el tiempo mínimo para descimbrar (retirar el molde)?",
            listOf("6 horas", "12 horas", "20 horas", "48 horas"),
            2,
            "El molde no debe retirarse antes de 20 horas después de la elaboración del espécimen.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Cuál es el tiempo máximo para descimbrar?",
            listOf("24 horas", "36 horas", "48 horas", "72 horas"),
            2,
            "El molde debe retirarse antes de las 48 horas de elaborado el espécimen.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Cuál es el rango de temperatura preferido en el laboratorio durante la fabricación?",
            listOf("293-298 K (20-25°C)", "296-303 K (23-30°C)", "288-293 K (15-20°C)", "300-305 K (27-32°C)"),
            0,
            "La temperatura del laboratorio durante la fabricación debe ser preferiblemente entre 293 K y 298 K (20°C a 25°C).",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Cuándo puede transportarse el espécimen desde el sitio de elaboración?",
            listOf("En cualquier momento después de cubierto", "No antes de 6 horas", "No antes de 20 horas de fabricado", "Solo después de 7 días"),
            2,
            "Los especímenes no deben transportarse antes de 20 horas después de su fabricación.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Cuál es la temperatura de curado húmedo estándar para cilindros y cubos?",
            listOf("293 K ± 2 K (20°C ± 2°C)", "296 K ± 2 K (23°C ± 2°C)", "300 K ± 2 K (27°C ± 2°C)", "303 K ± 2 K (30°C ± 2°C)"),
            1,
            "La temperatura de curado estándar es 296 K ± 2 K, equivalente a 23°C ± 2°C.",
            "CURADO"
        ),
        Pregunta(
            "¿Qué se debe hacer inmediatamente después de descimbrar los cilindros?",
            listOf("Dejarlos al aire libre para que sequen", "Trasladarlos inmediatamente a curado húmedo a 23°C ± 2°C", "Sumergirlos en agua potable", "Llevarlos al ensayo de compresión"),
            1,
            "Inmediatamente después del descimbrado los cilindros deben colocarse en curado húmedo a 296 K ± 2 K (23°C ± 2°C).",
            "CURADO"
        ),
        Pregunta(
            "¿Cuánto tiempo mínimo antes del ensayo deben estar las vigas en agua saturada de Ca(OH)₂?",
            listOf("6 horas", "12 horas", "20 horas", "48 horas"),
            2,
            "Las vigas prismáticas deben estar en agua saturada de Ca(OH)₂ a 23°C mínimo 20 horas antes del ensayo de flexión.",
            "CURADO"
        ),
        Pregunta(
            "¿Qué debe prevenirse desde el retiro del curado hasta el inicio del ensayo en vigas?",
            listOf("La carbonatación superficial", "El secado de la viga", "La absorción de agua adicional", "La expansión térmica"),
            1,
            "Desde que la viga se retira del curado hasta el inicio del ensayo de flexión se debe prevenir activamente el secado, ya que la resistencia a flexión es muy sensible a la humedad.",
            "CURADO"
        ),
        Pregunta(
            "¿En qué tipo de agua se curan las vigas prismáticas para ensayos de aceptación?",
            listOf("Agua potable fría", "Agua destilada", "Agua saturada de hidróxido de calcio (Ca(OH)₂) a 23°C ± 2°C", "Agua de mar filtrada"),
            2,
            "Las vigas se curan en agua saturada de hidróxido de calcio (Ca(OH)₂) a 23°C ± 2°C para las pruebas de aceptación.",
            "CURADO"
        ),
        Pregunta(
            "¿Cuál es la relación longitud/diámetro requerida para moldes cilíndricos?",
            listOf("Longitud = 1 × diámetro", "Longitud = 1.5 × diámetro", "Longitud = 2 × diámetro", "Longitud = 3 × diámetro"),
            2,
            "La longitud del molde cilíndrico debe ser igual a 2 veces su diámetro (eje del cilindro en posición vertical).",
            "MOLDES"
        )
    )

    private fun preguntas161() = listOf(
        Pregunta(
            "¿Cuál es la capacidad mínima del recipiente de muestreo?",
            listOf("5 litros", "10 litros", "15 litros", "20 litros"),
            2,
            "El recipiente de muestreo (cubeta, charola o carretilla) debe tener una capacidad mínima de 15 litros.",
            "EQUIPO"
        ),
        Pregunta(
            "¿Cuáles son los requisitos del recipiente de muestreo?",
            listOf("Solo que sea de gran capacidad", "Impermeable y no reactivo con el concreto", "De plástico transparente únicamente", "Con tapa hermética y escala graduada"),
            1,
            "El recipiente debe ser impermeable y no reactivo con el concreto; puede ser cubeta, charola o carretilla.",
            "EQUIPO"
        ),
        Pregunta(
            "¿Para qué sirve el cucharón de muestreo?",
            listOf("Para medir la temperatura del concreto", "Para interceptar el flujo de descarga sin pérdida de material", "Para mezclar los componentes del concreto", "Para medir el volumen de la muestra"),
            1,
            "El cucharón tiene la forma y capacidad adecuadas para interceptar el flujo de descarga del concreto sin perder material.",
            "EQUIPO"
        ),
        Pregunta(
            "¿Cuándo se considera válido tomar la muestra?",
            listOf("En cualquier momento durante la descarga", "Solo cuando el camión ha descargado al menos 50%", "Cuando todos los componentes han sido agregados y la mezcla es homogénea", "Solo cuando el proveedor lo autoriza"),
            2,
            "La muestra solo debe tomarse cuando todos los componentes han sido incorporados y la mezcla es homogénea.",
            "MUESTREO"
        ),
        Pregunta(
            "¿Cuántos litros mínimo se despuntan antes de muestrear en un camión mezclador?",
            listOf("5 litros", "10 litros", "20 litros", "50 litros"),
            1,
            "Se deben descartar (despuntar) un mínimo estimado de 10 litros antes de tomar la muestra en camión mezclador.",
            "MUESTREO"
        ),
        Pregunta(
            "¿Cómo se toma la muestra en una mezcladora estacionaria?",
            listOf("Del inicio de la descarga únicamente", "Interceptando el flujo completo aproximadamente a la mitad de la descarga", "Del final de la descarga únicamente", "Tomando pequeñas porciones de toda la descarga"),
            1,
            "En mezcladora estacionaria se debe interceptar el flujo COMPLETO de la descarga aproximadamente a la mitad de esta.",
            "MUESTREO"
        ),
        Pregunta(
            "¿En cuántos puntos mínimo se toma la muestra en pavimentadoras?",
            listOf("2 puntos", "3 puntos", "5 puntos", "10 puntos"),
            2,
            "En pavimentadoras la muestra debe tomarse en mínimo 5 puntos diferentes a lo largo del tramo de descarga.",
            "MUESTREO"
        ),
        Pregunta(
            "¿Qué se hace con las porciones tomadas en diferentes puntos de la pavimentadora?",
            listOf("Se analizan por separado", "Se mezclan en un recipiente de remezclado formando una sola muestra compuesta", "Se toma la de mayor revenimiento", "Se descarta la primera y última"),
            1,
            "Todas las porciones de los distintos puntos se integran en un único recipiente de remezclado para formar la muestra compuesta representativa.",
            "MUESTREO"
        ),
        Pregunta(
            "¿Entre qué porcentajes de la descarga total del camión se toma la muestra de aceptación en obra?",
            listOf("Entre 0% y 50%", "Entre 10% y 90%", "Entre 15% y 85%", "Entre 25% y 75%"),
            2,
            "La muestra para ensayos de aceptación debe tomarse entre el 15% y el 85% de la descarga total del camión.",
            "MUESTREO"
        ),
        Pregunta(
            "¿Cómo se controla la velocidad de descarga del camión mezclador?",
            listOf("Por la abertura de la compuerta de descarga", "Por el número de revoluciones de la olla mezcladora", "Por la velocidad de avance del camión", "Por el tiempo de descarga"),
            1,
            "La velocidad de descarga se controla por el número de revoluciones de la olla; NO por la abertura de la compuerta de descarga.",
            "MUESTREO"
        ),
        Pregunta(
            "¿Cuántos minutos mínimo debe esperar el camión mezclador antes de muestrear en planta?",
            listOf("3 minutos", "5 minutos", "7 minutos", "10 minutos"),
            2,
            "El camión debe esperar 7 minutos a la velocidad de mezclado especificada para que el concreto se homogenice antes de muestrear en planta.",
            "TIEMPOS"
        ),
        Pregunta(
            "¿Cómo se intercepta el flujo del camión en planta para el muestreo?",
            listOf("Tomando el primer litro de descarga", "Interceptando totalmente el flujo de la descarga del canal con el recipiente", "Solo tomando la parte central del flujo", "Con el cucharón a intervalos regulares"),
            1,
            "El recipiente debe interceptar TOTALMENTE el flujo de la descarga a través del canal de descarga del camión.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Qué debe hacerse con la muestra antes de realizar cualquier ensayo?",
            listOf("Dejarla reposar 5 minutos", "Remezclarla con el cucharón para asegurar homogeneidad", "Tamizarla para quitar agregado grueso", "Agregarle agua para facilitar el ensayo"),
            1,
            "Antes de cada ensayo se debe remezclar la muestra con el cucharón para asegurar que sea homogénea.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿De qué debe protegerse la muestra una vez obtenida?",
            listOf("Del polvo del sitio de trabajo únicamente", "Sol, viento, lluvia y fuentes de evaporación o contaminación", "Solo de la lluvia y la contaminación", "Del contacto con cualquier persona"),
            1,
            "La muestra debe protegerse inmediatamente del sol, viento, lluvia y toda fuente de evaporación o contaminación.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Cuántas etapas tiene el procedimiento de muestreo en camión mezclador en obra?",
            listOf("1 etapa", "2 etapas (verificación y muestreo para ensayos)", "3 etapas", "4 etapas"),
            1,
            "En obra el muestreo tiene 2 etapas: primero verificación/aceptación y luego muestreo representativo para ensayos.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Cuál es el tiempo máximo permitido entre la primera y la última porción de la muestra compuesta?",
            listOf("5 minutos", "10 minutos", "15 minutos", "30 minutos"),
            2,
            "El intervalo entre la primera y la última porción de la muestra no debe exceder 15 minutos.",
            "TIEMPOS"
        ),
        Pregunta(
            "¿Por qué el camión mezclador espera 7 minutos antes del muestreo en planta?",
            listOf("Para enfriar el motor del camión", "Para que el concreto alcance la temperatura de diseño", "Para asegurar que todos los componentes estén bien mezclados y homogéneos", "Para cumplir con el tiempo de transporte mínimo"),
            2,
            "Los 7 minutos a velocidad de mezclado permiten que todos los componentes se incorporen completamente y la mezcla sea homogénea.",
            "TIEMPOS"
        ),
        Pregunta(
            "¿Qué es el despunte en el muestreo de concreto?",
            listOf("Poner un punto de referencia en el camión", "Desechar los primeros litros de la descarga antes de tomar la muestra", "Calcular el volumen del camión", "Verificar la presión de la olla mezcladora"),
            1,
            "El despunte consiste en desechar un mínimo estimado de 10 litros de los primeros que salen del camión antes de tomar la muestra representativa.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Por qué se realiza el despunte en el muestreo?",
            listOf("Para reducir el costo del ensayo", "Porque los primeros litros pueden tener una relación agua/cemento diferente al resto", "Para llenar el recipiente de muestreo más fácilmente", "Para cumplir con el tiempo de espera de 7 minutos"),
            1,
            "Los primeros litros de la descarga pueden tener composición diferente (relación a/c variable). El despunte asegura que la muestra sea representativa del cuerpo central.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿A qué tipos de equipos de producción de concreto aplica la norma NMX-C-161?",
            listOf("Solo a camiones mezcladores", "Solo a plantas fijas", "A mezcladoras estacionarias, pavimentadoras, camiones mezcladores, agitadores, volteo u otro tipo", "Solo a equipos con capacidad mayor de 6 m³"),
            2,
            "La norma aplica a todo tipo de equipos: mezcladoras estacionarias, pavimentadoras, camiones mezcladores, agitadores, volteo u otro tipo.",
            "MUESTREO"
        ),
        Pregunta(
            "¿Qué debe verificarse al tomar la primera muestra en camión en obra antes de la aceptación?",
            listOf("El número de serie del camión", "La homogeneidad del concreto y la decisión de aceptar o rechazar", "La temperatura del motor del camión", "El tiempo de tránsito desde la planta"),
            1,
            "Al inicio de la descarga en obra se verifica la homogeneidad del concreto para decidir si se acepta el camión.",
            "PROCEDIMIENTO"
        ),
        Pregunta(
            "¿Qué datos obligatorios debe incluir el informe de muestreo?",
            listOf("Solo el revenimiento obtenido", "Revenimiento, TMN, resistencia de proyecto, ubicación, hora de muestreo y si fue cribado", "Solo fecha, hora y nombre del operador", "Solo la identificación del camión y la planta"),
            1,
            "El informe debe incluir: revenimiento, TMN del agregado, resistencia de proyecto, ubicación en obra, hora de muestreo y si se cribó la muestra.",
            "MUESTREO"
        ),
        Pregunta(
            "¿En qué condición debe estar el recipiente antes de usarlo para el muestreo?",
            listOf("Seco y sin humedad", "Limpio y húmedo", "Untado con aceite mineral", "Previamente calentado al sol"),
            1,
            "El recipiente de muestreo debe estar limpio y húmedo antes de usarlo para no alterar la relación agua/cemento de la muestra.",
            "EQUIPO"
        ),
        Pregunta(
            "¿Qué sucede si el camión mezclador es rechazado en la etapa de verificación en obra?",
            listOf("Se toma la muestra de igual manera", "No se continúa la descarga y se registra el motivo de rechazo", "Se agregan aditivos para corregir el concreto en campo", "Se regresa al camión para un segundo mezclado"),
            1,
            "Si el camión es rechazado en la etapa de verificación no se continúa la descarga y se debe registrar el motivo.",
            "MUESTREO"
        ),
        Pregunta(
            "¿Cuándo se registran temperatura, masa unitaria o contenido de aire en el informe?",
            listOf("Siempre, son obligatorios", "Solo cuando el proveedor lo pide", "Cuando se solicitan expresamente en los requisitos del ensayo", "Nunca, no son parte del muestreo"),
            2,
            "Temperatura, masa unitaria y contenido de aire son datos opcionales que se registran cuando se solicitan.",
            "MUESTREO"
        ),
        Pregunta(
            "¿Cuántos minutos puede transcurrir como máximo entre el inicio del muestreo de una mezcladora y el final?",
            listOf("5 minutos", "10 minutos", "15 minutos", "20 minutos"),
            2,
            "El intervalo entre primera y última porción de la muestra compuesta no debe exceder 15 minutos para que sea representativa del mismo lote.",
            "TIEMPOS"
        ),
        Pregunta(
            "¿Qué se indica en el informe cuando la muestra fue cribada?",
            listOf("La cantidad de material cribado", "La designación de la malla utilizada para el cribado", "El peso del material retenido", "El diámetro del agregado eliminado"),
            1,
            "Cuando se criba la muestra se debe indicar en el informe la designación de la malla utilizada.",
            "MUESTREO"
        ),
        Pregunta(
            "¿Por qué es importante que la muestra sea representativa del lote?",
            listOf("Para simplificar el trabajo de laboratorio", "Porque los ensayos de revenimiento, resistencia y temperatura se basan en esa muestra", "Para cumplir solo con requisitos legales", "Para reducir el tiempo de ensayo"),
            1,
            "Todos los ensayos de control (revenimiento, resistencia, temperatura, etc.) se realizan sobre la misma muestra; si no es representativa, los resultados son inválidos.",
            "MUESTREO"
        ),
        Pregunta(
            "¿En qué parte de la descarga del camión NO se debe tomar la muestra de aceptación?",
            listOf("En la parte central (50%) de la descarga", "En los primeros 15% y los últimos 15% de la descarga", "En cualquier punto entre el 20% y el 80%", "En el punto exacto de la mitad de la descarga"),
            1,
            "Se excluyen los primeros 15% y los últimos 15% de la descarga (fuera del rango 15%-85%) por no ser representativos.",
            "MUESTREO"
        )
    )

    private fun dpToPx(dp: Int) = (dp * resources.displayMetrics.density).toInt()
}
