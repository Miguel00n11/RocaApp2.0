package com.miguelrodriguez.rocaapp20

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.miguelrodriguez.rocaapp20.acceso.consultar_datos
import com.miguelrodriguez.rocaapp20.supervisiones.RegistroSupervision



class SupervisionesActivity : AppCompatActivity() {

    companion object {
        const val NODO_SUPERVISIONES = "Supervisiones"
        private val NORMAS_DISPONIBLES = listOf("083", "109", "148", "156", "159", "161")
        private val PUESTOS_SUPERVISORES = setOf(
            "Responsable técnico",
            "Responsable de laboratorio central",
            "Responsable de calidad"
        )
    }

    private fun puedeCrearSupervisiones(): Boolean {
        val puesto = consultar_datos.puestoUsuario ?: return false
        return PUESTOS_SUPERVISORES.any { it.equals(puesto.trim(), ignoreCase = true) }
    }

    // Path completa del registro para edición: "evaluado/norma/recordKey"
    private data class RegistroConRuta(
        val ruta: String,              // "evaluado/norma/recordKey"
        val registro: RegistroSupervision
    )

    private val todasLasSupervisiones = mutableListOf<RegistroConRuta>()
    private var filtroAnio: String? = null
    private var filtroNorma: String? = null

    // Vista frontal del item actualmente abierto por deslizamiento
    private var itemDesplazadoActual: View? = null

    private val chipBtnsAnio = mutableListOf<Button>()
    private val chipBtnsNorma = mutableListOf<Button>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supervisiones)

        findViewById<ImageButton>(R.id.btnAtrasSupervisiones).setOnClickListener { finish() }

        // Solo supervisores pueden agregar nuevas supervisiones
        val btnAgregar = findViewById<View>(R.id.btnAgregarSupervision)
        if (puedeCrearSupervisiones()) {
            btnAgregar.visibility = View.VISIBLE
            btnAgregar.setOnClickListener {
                startActivity(Intent(this, NuevaSupervisionActivity::class.java))
            }
        } else {
            btnAgregar.visibility = View.GONE
        }

        configurarChipsNorma()
        cargarSupervisiones()
    }

    override fun onResume() {
        super.onResume()
        cargarSupervisiones()
    }

    // ── Chips ──────────────────────────────────────────────────────────────

    private fun configurarChipsAnio(anios: List<String>) {
        val contenedor = findViewById<LinearLayout>(R.id.chipsAnio)
        contenedor.removeAllViews()
        chipBtnsAnio.clear()

        val opciones = listOf("Todos") + anios.sortedDescending()
        opciones.forEach { anio ->
            val btn = crearChip(anio)
            btn.setOnClickListener {
                filtroAnio = if (anio == "Todos") null else anio
                actualizarChips(chipBtnsAnio, btn)
                aplicarFiltros()
            }
            chipBtnsAnio.add(btn)
            contenedor.addView(btn)
        }
        chipBtnsAnio.firstOrNull()?.let { actualizarChips(chipBtnsAnio, it) }
    }

    private fun configurarChipsNorma() {
        val contenedor = findViewById<LinearLayout>(R.id.chipsNorma)
        contenedor.removeAllViews()
        chipBtnsNorma.clear()

        val opciones = listOf("Todas") + NORMAS_DISPONIBLES
        opciones.forEach { norma ->
            val btn = crearChip(if (norma == "Todas") "Todas" else "NMX-C-$norma")
            btn.tag = norma
            btn.setOnClickListener {
                filtroNorma = if (norma == "Todas") null else norma
                actualizarChips(chipBtnsNorma, btn)
                aplicarFiltros()
            }
            chipBtnsNorma.add(btn)
            contenedor.addView(btn)
        }
        chipBtnsNorma.firstOrNull()?.let { actualizarChips(chipBtnsNorma, it) }
    }

    private fun crearChip(texto: String): Button {
        val btn = Button(this)
        val dp = resources.displayMetrics.density
        btn.text = texto
        btn.textSize = 11f
        btn.setTextColor(Color.WHITE)
        btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#334455"))
        btn.setPadding((12 * dp).toInt(), 0, (12 * dp).toInt(), 0)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, (32 * dp).toInt()
        )
        params.marginEnd = (6 * dp).toInt()
        btn.layoutParams = params
        btn.minWidth = 0
        btn.minimumWidth = 0
        return btn
    }

    private fun actualizarChips(chips: List<Button>, seleccionado: Button) {
        chips.forEach { btn ->
            btn.backgroundTintList = ColorStateList.valueOf(
                if (btn == seleccionado) Color.parseColor("#FF9800")
                else Color.parseColor("#334455")
            )
        }
    }

    // ── Carga Firebase ─────────────────────────────────────────────────────

    private fun cargarSupervisiones() {
        val progress = findViewById<ProgressBar>(R.id.progressSupervisiones)
        val txtSin = findViewById<TextView>(R.id.txtSinSupervisiones)
        val lista = findViewById<LinearLayout>(R.id.listaSupervisiones)

        progress.visibility = View.VISIBLE
        txtSin.visibility = View.GONE
        lista.removeAllViews()

        val nombreActual = MainActivity.NombreUsuarioCompanion
        val esAdmin = consultar_datos.esAdministrador

        // Admin y supervisores leen todo; el resto solo ve las suyas (donde es evaluado)
        val ref = if (esAdmin || puedeCrearSupervisiones())
            FirebaseDatabase.getInstance().reference.child(NODO_SUPERVISIONES)
        else
            FirebaseDatabase.getInstance().reference.child(NODO_SUPERVISIONES).child(nombreActual)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progress.visibility = View.GONE
                todasLasSupervisiones.clear()

                if (esAdmin || puedeCrearSupervisiones()) {
                    // Estructura: Supervisiones/{evaluado}/{norma}/{key}
                    for (evaluadoSnap in snapshot.children) {
                        for (normaSnap in evaluadoSnap.children) {
                            for (recordSnap in normaSnap.children) {
                                try {
                                    val reg = recordSnap.getValue(RegistroSupervision::class.java)
                                        ?: continue
                                    val ruta = "${evaluadoSnap.key}/${normaSnap.key}/${recordSnap.key}"
                                    todasLasSupervisiones.add(RegistroConRuta(ruta, reg))
                                } catch (_: Exception) { /* registro en formato antiguo, ignorar */ }
                            }
                        }
                    }
                } else {
                    // Snapshot = {norma}/{key} (ya filtrado por evaluado)
                    for (normaSnap in snapshot.children) {
                        for (recordSnap in normaSnap.children) {
                            try {
                                val reg = recordSnap.getValue(RegistroSupervision::class.java)
                                    ?: continue
                                val ruta = "${nombreActual}/${normaSnap.key}/${recordSnap.key}"
                                todasLasSupervisiones.add(RegistroConRuta(ruta, reg))
                            } catch (_: Exception) {}
                        }
                    }
                }

                // Más reciente primero (push keys son cronológicas)
                todasLasSupervisiones.sortByDescending { it.ruta.substringAfterLast("/") }

                val anios = todasLasSupervisiones
                    .mapNotNull { it.registro.fecha.takeLastWhile { c -> c.isDigit() }.takeIf { it.length == 4 } }
                    .distinct()
                configurarChipsAnio(anios)

                aplicarFiltros()
            }

            override fun onCancelled(error: DatabaseError) {
                progress.visibility = View.GONE
                val txtSin = findViewById<TextView>(R.id.txtSinSupervisiones)
                txtSin.text = "Error al cargar supervisiones"
                txtSin.visibility = View.VISIBLE
            }
        })
    }

    // ── Filtros y lista ────────────────────────────────────────────────────

    private fun aplicarFiltros() {
        val txtSin = findViewById<TextView>(R.id.txtSinSupervisiones)
        val lista = findViewById<LinearLayout>(R.id.listaSupervisiones)
        lista.removeAllViews()

        val filtradas = todasLasSupervisiones.filter { (_, reg) ->
            val anioReg = reg.fecha.takeLastWhile { it.isDigit() }.takeIf { it.length == 4 }
            val anioOk = filtroAnio == null || anioReg == filtroAnio
            val normaOk = filtroNorma == null || reg.norma == filtroNorma
            anioOk && normaOk
        }

        if (filtradas.isEmpty()) {
            txtSin.text = if (filtroAnio != null || filtroNorma != null)
                "Sin resultados para el filtro seleccionado"
            else
                "Sin supervisiones registradas"
            txtSin.visibility = View.VISIBLE
            return
        }
        txtSin.visibility = View.GONE

        val inflater = LayoutInflater.from(this)
        val puedeAcciones = puedeCrearSupervisiones() || consultar_datos.esAdministrador
        for ((ruta, reg) in filtradas) {
            val item = inflater.inflate(R.layout.item_supervision_registro, lista, false)
            val capaContenido = item.findViewById<View>(R.id.capaContenido)
            val btnEditar = item.findViewById<View>(R.id.btnEditarSupervision)
            val btnEliminar = item.findViewById<View>(R.id.btnEliminarSupervision)

            val autorizada = reg.revisoyAutorizo.isNotBlank()

            item.findViewById<TextView>(R.id.txtNormaRegistro).text = "NMX-C-${reg.norma}"
            item.findViewById<TextView>(R.id.txtFechaRegistro).text = reg.fecha
            item.findViewById<TextView>(R.id.txtEvaluadoRegistro).text = reg.evaluado
            item.findViewById<TextView>(R.id.txtEvaluadorRegistro).text = "Por: ${reg.evaluador}"

            // Badges de estado
            item.findViewById<TextView>(R.id.badgeAutorizado).visibility =
                if (autorizada) View.VISIBLE else View.GONE
            item.findViewById<TextView>(R.id.badgePendiente).visibility =
                if (!autorizada) View.VISIBLE else View.GONE

            val s = reg.resultados.count { it == "S" }
            val ns = reg.resultados.count { it == "NS" }
            val na = reg.resultados.count { it == "NA" }
            item.findViewById<TextView>(R.id.txtResumenS).text = "S: $s"
            item.findViewById<TextView>(R.id.txtResumenNS).text = "NS: $ns"
            item.findViewById<TextView>(R.id.txtResumenNA).text = "NA: $na"

            // Intent base para abrir el formulario
            fun abrirFormulario(soloLectura: Boolean) {
                val intent = Intent(this, FormularioSupervisionActivity::class.java).apply {
                    putExtra(FormularioSupervisionActivity.EXTRA_EVALUADO, reg.evaluado)
                    putExtra(FormularioSupervisionActivity.EXTRA_NORMA, reg.norma)
                    putExtra(FormularioSupervisionActivity.EXTRA_FECHA, reg.fecha)
                    putExtra(FormularioSupervisionActivity.EXTRA_EVALUADOR, reg.evaluador)
                    putExtra(FormularioSupervisionActivity.EXTRA_FIREBASE_RECORD_KEY, ruta)
                    putExtra(FormularioSupervisionActivity.EXTRA_SOLO_LECTURA, soloLectura)
                    putExtra(FormularioSupervisionActivity.EXTRA_REVISOY_AUTORIZO, reg.revisoyAutorizo)
                    putStringArrayListExtra(FormularioSupervisionActivity.EXTRA_RESULTADOS,
                        ArrayList(reg.resultados))
                    putStringArrayListExtra(FormularioSupervisionActivity.EXTRA_HALLAZGOS,
                        ArrayList(reg.hallazgos))
                }
                startActivity(intent)
            }

            if (puedeAcciones) {
                // Si está autorizada: botón "Ver" (azul); si no: "Editar" (naranja)
                if (autorizada) {
                    btnEditar.setBackgroundColor(Color.parseColor("#1565C0"))
                    btnEditar.findViewById<TextView>(R.id.txtIconoAccionEditar).text = "👁"
                    btnEditar.findViewById<TextView>(R.id.txtLabelAccionEditar).text = "Ver"
                }

                btnEditar.setOnClickListener {
                    cerrarDeslizamiento(capaContenido)
                    itemDesplazadoActual = null
                    abrirFormulario(soloLectura = autorizada)
                }

                // Eliminar solo disponible cuando NO está autorizada
                if (autorizada) {
                    btnEliminar.visibility = View.GONE
                } else {
                    btnEliminar.visibility = View.VISIBLE
                    btnEliminar.setOnClickListener {
                        cerrarDeslizamiento(capaContenido)
                        itemDesplazadoActual = null
                        androidx.appcompat.app.AlertDialog.Builder(this)
                            .setTitle("Eliminar supervisión")
                            .setMessage("¿Eliminar la supervisión de ${reg.evaluado} (NMX-C-${reg.norma}, ${reg.fecha})?\n\nEsta acción no se puede deshacer.")
                            .setPositiveButton("Eliminar") { _, _ ->
                                eliminarSupervision(ruta, item, lista)
                            }
                            .setNegativeButton("Cancelar", null)
                            .show()
                    }
                }

                configurarDeslizamiento(capaContenido)
            } else {
                // Sin permisos de acción: tap abre en solo lectura
                capaContenido.setOnClickListener { abrirFormulario(soloLectura = true) }
            }

            lista.addView(item)
        }
    }

    // ── Swipe ──────────────────────────────────────────────────────────────

    private val anchoAccionesPx: Int by lazy {
        (180 * resources.displayMetrics.density).toInt()
    }

    @android.annotation.SuppressLint("ClickableViewAccessibility")
    private fun configurarDeslizamiento(capaContenido: View) {
        var startX = 0f
        var startY = 0f
        var startTranslation = 0f
        var arrastrando = false
        var direccionDefinida = false

        capaContenido.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.rawX
                    startY = event.rawY
                    startTranslation = capaContenido.translationX
                    arrastrando = false
                    direccionDefinida = false
                    true  // CRÍTICO: capturar el stream de eventos
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = event.rawX - startX
                    val deltaY = event.rawY - startY
                    if (!direccionDefinida && (Math.abs(deltaX) > 10 || Math.abs(deltaY) > 10)) {
                        direccionDefinida = true
                        arrastrando = Math.abs(deltaX) > Math.abs(deltaY)
                        if (arrastrando) {
                            // Cerrar otro item abierto
                            val otro = itemDesplazadoActual
                            if (otro != null && otro != capaContenido) {
                                cerrarDeslizamiento(otro)
                                itemDesplazadoActual = null
                            }
                            // Impedir que el ScrollView robe los eventos
                            (capaContenido.parent as? android.view.ViewGroup)
                                ?.requestDisallowInterceptTouchEvent(true)
                        }
                    }
                    if (arrastrando) {
                        val nueva = (startTranslation + deltaX).coerceIn(-anchoAccionesPx.toFloat(), 0f)
                        capaContenido.translationX = nueva
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    (capaContenido.parent as? android.view.ViewGroup)
                        ?.requestDisallowInterceptTouchEvent(false)
                    if (arrastrando) {
                        val deltaX = event.rawX - startX
                        val mitad = -anchoAccionesPx / 2f
                        if (capaContenido.translationX < mitad) {
                            abrirDeslizamiento(capaContenido)
                        } else {
                            cerrarDeslizamiento(capaContenido)
                            if (itemDesplazadoActual == capaContenido) itemDesplazadoActual = null
                        }
                    } else {
                        // Tap: si está abierto, cerrarlo
                        if (startTranslation < -5f) {
                            cerrarDeslizamiento(capaContenido)
                            itemDesplazadoActual = null
                        }
                    }
                    true
                }
                MotionEvent.ACTION_CANCEL -> {
                    (capaContenido.parent as? android.view.ViewGroup)
                        ?.requestDisallowInterceptTouchEvent(false)
                    if (capaContenido.translationX < -anchoAccionesPx / 2f) {
                        abrirDeslizamiento(capaContenido)
                    } else {
                        cerrarDeslizamiento(capaContenido)
                        if (itemDesplazadoActual == capaContenido) itemDesplazadoActual = null
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun abrirDeslizamiento(vista: View) {
        ObjectAnimator.ofFloat(vista, "translationX", vista.translationX, -anchoAccionesPx.toFloat())
            .apply { duration = 220 }.start()
        itemDesplazadoActual = vista
    }

    private fun cerrarDeslizamiento(vista: View) {
        ObjectAnimator.ofFloat(vista, "translationX", vista.translationX, 0f)
            .apply { duration = 200 }.start()
    }

    // ── Eliminar ────────────────────────────────────────────────────────────

    private fun eliminarSupervision(ruta: String, itemView: View, lista: LinearLayout) {
        FirebaseDatabase.getInstance().reference
            .child(NODO_SUPERVISIONES)
            .child(ruta)
            .removeValue()
            .addOnSuccessListener {
                todasLasSupervisiones.removeAll { it.ruta == ruta }
                lista.removeView(itemView)
                val txtSin = findViewById<TextView>(R.id.txtSinSupervisiones)
                if (lista.childCount == 0) {
                    txtSin.text = if (filtroAnio != null || filtroNorma != null)
                        "Sin resultados para el filtro seleccionado"
                    else
                        "Sin supervisiones registradas"
                    txtSin.visibility = View.VISIBLE
                }
                android.widget.Toast.makeText(this, "Supervisión eliminada", android.widget.Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                android.widget.Toast.makeText(this, "Error al eliminar: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            }
    }
}
