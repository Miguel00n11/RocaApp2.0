package com.miguelrodriguez.rocaapp20

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase
import com.miguelrodriguez.rocaapp20.supervisiones.CatalogoSupervision
import com.miguelrodriguez.rocaapp20.supervisiones.RegistroSupervision

class FormularioSupervisionActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EVALUADO = "evaluado"
        const val EXTRA_NORMA = "norma"
        const val EXTRA_FECHA = "fecha"
        const val EXTRA_EVALUADOR = "evaluador"
        const val EXTRA_FIREBASE_RECORD_KEY = "firebase_key"   // null → nuevo registro
        const val EXTRA_RESULTADOS = "resultados_existentes"
        const val EXTRA_HALLAZGOS = "hallazgos_existentes"
        const val EXTRA_SOLO_LECTURA = "solo_lectura"
        const val EXTRA_REVISOY_AUTORIZO = "revisoy_autorizo"
    }

    private lateinit var evaluado: String
    private lateinit var norma: String
    private lateinit var fecha: String
    private lateinit var evaluador: String
    private var firebaseRecordKey: String? = null   // "evaluado/norma/pushKey" para edición
    private var soloLectura = false
    private var revisoyAutorizo = ""

    private val resultados = mutableListOf<String>()
    private val hallazgos = mutableListOf<String>()
    private val itemViews = mutableListOf<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_supervision)

        evaluado = intent.getStringExtra(EXTRA_EVALUADO) ?: ""
        norma = intent.getStringExtra(EXTRA_NORMA) ?: ""
        fecha = intent.getStringExtra(EXTRA_FECHA) ?: ""
        evaluador = intent.getStringExtra(EXTRA_EVALUADOR) ?: ""
        firebaseRecordKey = intent.getStringExtra(EXTRA_FIREBASE_RECORD_KEY)
        soloLectura = intent.getBooleanExtra(EXTRA_SOLO_LECTURA, false)
        revisoyAutorizo = intent.getStringExtra(EXTRA_REVISOY_AUTORIZO) ?: ""

        val modoEdicion = firebaseRecordKey != null && !soloLectura

        val normaData = CatalogoSupervision.porCodigo(norma)
        if (normaData == null) {
            Toast.makeText(this, "Norma no encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Cabecera
        findViewById<TextView>(R.id.txtTituloFormulario).text = "NMX-C-$norma"
        findViewById<TextView>(R.id.txtSubtituloFormulario).text =
            normaData.titulo.substringAfter("— ")
        findViewById<TextView>(R.id.txtEvaluadoFormulario).text = evaluado
        findViewById<TextView>(R.id.txtEvaluadorFormulario).text = evaluador
        findViewById<TextView>(R.id.txtFechaFormulario).text = fecha

        val btnGuardar = findViewById<Button>(R.id.btnGuardarSupervision)

        if (soloLectura) {
            btnGuardar.text = "Cerrar"
            btnGuardar.setOnClickListener { finish() }
            // Mostrar banner de autorización
            if (revisoyAutorizo.isNotBlank()) {
                val banner = findViewById<View>(R.id.bannerAutorizacion)
                val txtBanner = findViewById<TextView>(R.id.txtBannerAutorizacion)
                banner?.visibility = View.VISIBLE
                txtBanner?.text = "Revisó y autorizó: $revisoyAutorizo"
            }
        } else {
            btnGuardar.text = if (modoEdicion) "Actualizar supervisión" else "Guardar supervisión"
            btnGuardar.setOnClickListener { confirmarGuardar() }
        }

        findViewById<ImageButton>(R.id.btnAtrasFormulario).setOnClickListener { finish() }

        // Estado inicial: NA para todos
        val existingResultados = intent.getStringArrayListExtra(EXTRA_RESULTADOS)
        val existingHallazgos = intent.getStringArrayListExtra(EXTRA_HALLAZGOS)

        normaData.items.forEachIndexed { i, _ ->
            resultados.add(existingResultados?.getOrElse(i) { "NA" } ?: "NA")
            hallazgos.add(existingHallazgos?.getOrElse(i) { "" } ?: "")
        }

        inflarItems(normaData.items)
    }

    private fun inflarItems(items: List<CatalogoSupervision.ItemNorma>) {
        val contenedor = findViewById<LinearLayout>(R.id.listaItemsSupervision)
        val inflater = LayoutInflater.from(this)

        items.forEachIndexed { index, item ->
            val view = inflater.inflate(R.layout.item_supervision_check, contenedor, false)

            view.findViewById<TextView>(R.id.txtNumeroItem).text = (index + 1).toString()
            view.findViewById<TextView>(R.id.txtDescripcionItem).text = item.descripcion

            val btnS = view.findViewById<Button>(R.id.btnS)
            val btnNS = view.findViewById<Button>(R.id.btnNS)
            val btnNA = view.findViewById<Button>(R.id.btnNA)
            val btnComentar = view.findViewById<Button>(R.id.btnComentar)
            val tilHallazgos = view.findViewById<TextInputLayout>(R.id.tilHallazgos)
            val etHallazgos = view.findViewById<TextInputEditText>(R.id.etHallazgos)

            // Pre-fill
            val valorInicial = resultados[index]
            actualizarSeleccion(btnS, btnNS, btnNA, valorInicial)
            if (hallazgos[index].isNotBlank()) {
                tilHallazgos.visibility = View.VISIBLE
                etHallazgos.setText(hallazgos[index])
                btnComentar.text = "✕ Ocultar"
                btnComentar.setTextColor(Color.parseColor("#FF9800"))
            }

            if (soloLectura) {
                // Modo solo lectura: deshabilitar todos los controles interactivos
                btnS.isEnabled = false
                btnNS.isEnabled = false
                btnNA.isEnabled = false
                btnComentar.isEnabled = false
                etHallazgos.isEnabled = false
                etHallazgos.isFocusable = false
            } else {
                // S/NS/NA — solo cambia la selección, no afecta el campo de hallazgos
                btnS.setOnClickListener {
                    resultados[index] = "S"
                    actualizarSeleccion(btnS, btnNS, btnNA, "S")
                }
                btnNS.setOnClickListener {
                    resultados[index] = "NS"
                    actualizarSeleccion(btnS, btnNS, btnNA, "NS")
                }
                btnNA.setOnClickListener {
                    resultados[index] = "NA"
                    actualizarSeleccion(btnS, btnNS, btnNA, "NA")
                }

                // Comentar — alterna visibilidad del campo de hallazgos
                btnComentar.setOnClickListener {
                    if (tilHallazgos.visibility == View.GONE) {
                        tilHallazgos.visibility = View.VISIBLE
                        btnComentar.text = "✕ Ocultar"
                        btnComentar.setTextColor(Color.parseColor("#FF9800"))
                    } else {
                        tilHallazgos.visibility = View.GONE
                        btnComentar.text = "＋ Comentar"
                        btnComentar.setTextColor(Color.parseColor("#8FA3B5"))
                    }
                }
            }

            itemViews.add(view)
            contenedor.addView(view)
        }
    }

    private fun actualizarSeleccion(btnS: Button, btnNS: Button, btnNA: Button, sel: String) {
        fun estilizar(btn: Button, activo: Boolean) {
            btn.backgroundTintList = ColorStateList.valueOf(
                if (activo) Color.parseColor("#FF9800") else Color.parseColor("#334455")
            )
            btn.setTextColor(Color.WHITE)
        }
        estilizar(btnS, sel == "S")
        estilizar(btnNS, sel == "NS")
        estilizar(btnNA, sel == "NA")
    }

    private fun confirmarGuardar() {
        // Leer hallazgos actuales antes de guardar
        itemViews.forEachIndexed { index, view ->
            val et = view.findViewById<TextInputEditText>(R.id.etHallazgos)
            hallazgos[index] = et.text?.toString() ?: ""
        }

        val s = resultados.count { it == "S" }
        val ns = resultados.count { it == "NS" }
        val na = resultados.count { it == "NA" }
        val modoEdicion = firebaseRecordKey != null

        AlertDialog.Builder(this)
            .setTitle(if (modoEdicion) "Actualizar supervisión" else "Guardar supervisión")
            .setMessage("Resultados: $s S, $ns NS, $na NA\n\n¿Confirmar?")
            .setPositiveButton(if (modoEdicion) "Actualizar" else "Guardar") { _, _ -> guardarEnFirebase() }
            .setNegativeButton("Revisar", null)
            .show()
    }

    private fun guardarEnFirebase() {
        val progress = findViewById<ProgressBar>(R.id.progressGuardarSupervision)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarSupervision)
        progress.visibility = View.VISIBLE
        btnGuardar.isEnabled = false

        // En edición, el evaluador pasa a ser quien realizó los cambios
        val evaluadorFinal = if (firebaseRecordKey != null)
            MainActivity.NombreUsuarioCompanion
        else
            evaluador

        val registro = RegistroSupervision(
            norma = norma,
            fecha = fecha,
            evaluador = evaluadorFinal,
            evaluado = evaluado,
            resultados = resultados.toList(),
            hallazgos = hallazgos.toList()
        )

        val ref = if (firebaseRecordKey != null) {
            // Edición: actualizar el nodo existente
            FirebaseDatabase.getInstance().reference
                .child(SupervisionesActivity.NODO_SUPERVISIONES)
                .child(firebaseRecordKey!!)
        } else {
            // Nuevo registro: Supervisiones/{evaluado}/{norma}/{pushId}
            FirebaseDatabase.getInstance().reference
                .child(SupervisionesActivity.NODO_SUPERVISIONES)
                .child(evaluado)
                .child(norma)
                .push()
        }

        ref.setValue(registro)
            .addOnSuccessListener {
                progress.visibility = View.GONE
                Toast.makeText(this,
                    if (firebaseRecordKey != null) "Supervisión actualizada" else "Supervisión guardada",
                    Toast.LENGTH_SHORT).show()
                val intent = android.content.Intent(this, SupervisionesActivity::class.java)
                intent.flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                progress.visibility = View.GONE
                btnGuardar.isEnabled = true
                Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
