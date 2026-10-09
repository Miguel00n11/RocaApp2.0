package com.miguelrodriguez.rocaapp20

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.miguelrodriguez.rocaapp20.acceso.CatalogoPersonal
import com.miguelrodriguez.rocaapp20.acceso.consultar_datos
import com.miguelrodriguez.rocaapp20.supervisiones.CatalogoSupervision
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NuevaSupervisionActivity : AppCompatActivity() {

    private data class PersonalItem(val nombre: String, val correo: String, val puesto: String)

    private val personalData = mutableListOf<PersonalItem>()
    private var normasDisponibles = CatalogoSupervision.NORMAS.toList()

    private lateinit var spinnerEvaluado: Spinner
    private lateinit var spinnerNorma: Spinner
    private lateinit var etFecha: TextInputEditText
    private lateinit var progressNorma: ProgressBar
    private lateinit var txtNormaHint: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nueva_supervision)

        spinnerEvaluado = findViewById(R.id.spinnerEvaluado)
        spinnerNorma = findViewById(R.id.spinnerNorma)
        etFecha = findViewById(R.id.etFechaSupervision)
        progressNorma = findViewById(R.id.progressNorma)
        txtNormaHint = findViewById(R.id.txtNormaHint)

        findViewById<ImageButton>(R.id.btnAtrasNuevaSupervision).setOnClickListener { finish() }

        val puesto = consultar_datos.puestoUsuario ?: ""
        val supervisores = setOf("Responsable técnico", "Responsable de laboratorio central", "Responsable de calidad")
        if (!supervisores.any { it.equals(puesto.trim(), ignoreCase = true) }) {
            android.widget.Toast.makeText(this, "No tienes permiso para crear supervisiones", android.widget.Toast.LENGTH_LONG).show()
            finish()
            return
        }

        configurarFecha()
        cargarPersonal()

        findViewById<android.widget.Button>(R.id.btnComenzarSupervision).setOnClickListener {
            comenzarSupervision()
        }
    }

    private fun configurarFecha() {
        val cal = Calendar.getInstance()
        val fmt = SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX"))
        etFecha.setText(fmt.format(cal.time))

        etFecha.setOnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                cal.set(year, month, day)
                etFecha.setText(fmt.format(cal.time))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun cargarPersonal() {
        val progressPersonal = findViewById<ProgressBar>(R.id.progressPersonal)
        progressPersonal.visibility = View.VISIBLE

        FirebaseDatabase.getInstance().reference.child(CatalogoPersonal.NODO_PERSONAL)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    progressPersonal.visibility = View.GONE
                    personalData.clear()
                    for (child in snapshot.children) {
                        val activo = child.child("activo").getValue(Boolean::class.java) != false
                        val nombre = child.child("nombre").getValue(String::class.java) ?: continue
                        val correo = child.child("correo").getValue(String::class.java) ?: continue
                        val puesto = child.child("puesto").getValue(String::class.java) ?: ""
                        if (activo) personalData.add(PersonalItem(nombre, correo, puesto))
                    }
                    personalData.sortBy { it.nombre }

                    val nombres = personalData.map { it.nombre }
                    val adapter = ArrayAdapter(this@NuevaSupervisionActivity,
                        R.layout.spinner_item_dark, nombres)
                    adapter.setDropDownViewResource(R.layout.spinner_item_dark)
                    spinnerEvaluado.adapter = adapter

                    spinnerEvaluado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                            cargarNormasAutorizadas(personalData[pos].correo)
                        }
                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    progressPersonal.visibility = View.GONE
                    Toast.makeText(this@NuevaSupervisionActivity,
                        "No se pudo cargar el personal", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun cargarNormasAutorizadas(correo: String) {
        val puesto = personalData.find { it.correo == correo }?.puesto ?: ""
        progressNorma.visibility = View.VISIBLE
        txtNormaHint.visibility = View.GONE
        spinnerNorma.visibility = View.INVISIBLE

        val clave = CatalogoPersonal.claveCorreo(correo)
        FirebaseDatabase.getInstance().reference
            .child(CatalogoPersonal.NODO_PERSONAL)
            .child(clave)
            .child("acceso")
            .child("capacitaciones")
            .get()
            .addOnSuccessListener { snap ->
                progressNorma.visibility = View.GONE

                val autorizadas = snap.children
                    .filter { it.getValue(Boolean::class.java) == true }
                    .mapNotNull { it.key }.toSet()

                // 148 solo aplica al Laboratorista central
                val es148 = puesto.equals("Laboratorista central", ignoreCase = true)
                normasDisponibles = CatalogoSupervision.NORMAS.filter { norma ->
                    (norma.codigo == "148" && es148) || norma.codigo in autorizadas
                }

                if (normasDisponibles.isEmpty()) {
                    txtNormaHint.text = "Este personal no tiene normas autorizadas"
                    txtNormaHint.visibility = View.VISIBLE
                    spinnerNorma.visibility = View.INVISIBLE
                } else {
                    txtNormaHint.visibility = View.GONE
                    spinnerNorma.visibility = View.VISIBLE
                    actualizarSpinnerNorma()
                }
            }
            .addOnFailureListener {
                progressNorma.visibility = View.GONE
                // Si no hay datos de acceso, mostrar todas las normas (excepto 148 salvo laboratorista)
                val es148fallback = puesto.equals("Laboratorista central", ignoreCase = true)
                normasDisponibles = CatalogoSupervision.NORMAS.filter { it.codigo != "148" || es148fallback }
                spinnerNorma.visibility = View.VISIBLE
                actualizarSpinnerNorma()
            }
    }

    private fun actualizarSpinnerNorma() {
        val opciones = normasDisponibles.map { norma ->
            val titulo = norma.titulo.substringAfter("— ")
            val tituloCorto = if (titulo.length > 42) titulo.take(42) + "…" else titulo
            "NMX-C-${norma.codigo} — $tituloCorto"
        }
        val adapter = ArrayAdapter(this, R.layout.spinner_item_dark, opciones)
        adapter.setDropDownViewResource(R.layout.spinner_item_dark)
        spinnerNorma.adapter = adapter
    }

    private fun comenzarSupervision() {
        if (personalData.isEmpty()) {
            Toast.makeText(this, "Espera a que cargue el personal", Toast.LENGTH_SHORT).show()
            return
        }
        if (normasDisponibles.isEmpty()) {
            Toast.makeText(this, "El personal seleccionado no tiene normas autorizadas", Toast.LENGTH_SHORT).show()
            return
        }

        val pos = spinnerEvaluado.selectedItemPosition
        if (pos < 0 || pos >= personalData.size) return
        val evaluado = personalData[pos].nombre

        val normaPos = spinnerNorma.selectedItemPosition
        if (normaPos < 0 || normaPos >= normasDisponibles.size) return
        val codigoNorma = normasDisponibles[normaPos].codigo

        val fecha = etFecha.text.toString().trim()
        if (fecha.isBlank()) {
            Toast.makeText(this, "Selecciona la fecha", Toast.LENGTH_SHORT).show()
            return
        }

        val evaluador = MainActivity.NombreUsuarioCompanion

        val intent = Intent(this, FormularioSupervisionActivity::class.java).apply {
            putExtra(FormularioSupervisionActivity.EXTRA_EVALUADO, evaluado)
            putExtra(FormularioSupervisionActivity.EXTRA_NORMA, codigoNorma)
            putExtra(FormularioSupervisionActivity.EXTRA_FECHA, fecha)
            putExtra(FormularioSupervisionActivity.EXTRA_EVALUADOR, evaluador)
        }
        startActivity(intent)
    }
}
