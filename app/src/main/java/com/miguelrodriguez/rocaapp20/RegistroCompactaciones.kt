package com.miguelrodriguez.rocaapp20

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import java.util.Calendar

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.miguelrodriguez.rocaapp20.Recycler.CalasAdapter
import com.miguelrodriguez.rocaapp20.Recycler.ClaseCala
import com.miguelrodriguez.rocaapp20.Recycler.ClaseObra
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

class RegistroCompactaciones : AppCompatActivity() {


    private var listaCalasmutableListOf =
        mutableListOf(ClaseCala(1, "estacion", 1.0, 1.0, 1.0, 1.0))
    private var listaCalasOriginal: MutableList<ClaseCala> = mutableListOf()

    private lateinit var dataReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var etObra: EditText
    private lateinit var etFecha: EditText

    private lateinit var etCapa: EditText
    private lateinit var etTramo: EditText
    private lateinit var etSubTramo: EditText
    private lateinit var etcompactacionProyecto: EditText
    private lateinit var etMVSM: EditText
    private lateinit var etHumedad: EditText
    private lateinit var llave: String

    private lateinit var CalasAdapter: CalasAdapter
    private lateinit var rvCalas: RecyclerView


    private lateinit var tvNumeroReporteCompactacion: TextView

    private lateinit var btnCancelar: Button
    private lateinit var btnGuardar: Button
    private lateinit var btnVerCalendarioCompactaciones: Button

    private lateinit var fbNuevaCalaCompactacion: FloatingActionButton

    //    controladores del item Cala
    private lateinit var tvIdCalaCompactacion: TextView
    private lateinit var tvEstacionCalaCompactacion: TextView
    private lateinit var tvProfundidadCalaCompactacion: TextView
    private lateinit var tvMVSLCalaCompactacion: TextView
    private lateinit var tvHumedadCalaCompactacion: TextView
    private lateinit var tvPorcentajeCalaCompactacion: TextView

//    private lateinit var btnEliminarCalaCompactaciones: FloatingActionButton


    private lateinit var personal: String
    private lateinit var reporteSelecionado: ClaseObra
    private var editar: Boolean = false

    private lateinit var calaNueva: ClaseCala

    var reporteAGuardar: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_compactaciones)

        listaCalasmutableListOf.clear()

        reporteSelecionado = ReportesCompactaciones.reporteSelecionado

        editar = ReportesCompactaciones.editar




        initComponet()
        if (editar == true) {
            cargarObraSeleccionada(reporteSelecionado)
        }

        initUI()

        personal = MainActivity.NombreUsuarioCompanion
        ConsultarUltimoRegistro()

//        obtenerElReporteAGuardar()

//        dataReference=FirebaseDatabase.getInstance().reference
        llave=dataReference.push().key.toString()

        listaCalasOriginal.addAll(listaCalasmutableListOf)

        listaCalasOriginal.forEachIndexed { index, elemento ->
            // Puedes realizar alguna lógica para determinar la nueva numeración
            val nuevaNumeracion = index  // Sumar 1 para empezar desde 1, si es necesario

            // Reemplazar la numeración en cada objeto
            elemento.cala = nuevaNumeracion
        }

    }


    private fun updateTask() {
        CalasAdapter.notifyDataSetChanged()
    }

    private fun saveLocally(
        obra: String,
        fecha: String,
        personal: String,
        numeroReporte: Int,
        listaCalas: List<ClaseCala>,
        capa: String,
        Tramo: String,
        subTramo: String,
        compactacion: Double,
        msvm: Double,
        humedad: Double,
        llave: String


    ) {
        // Obtener una lista existente de registros locales o crear una nueva
        val registrosLocales = getLocalRecords()

        // Agregar el nuevo registro a la lista
        val nuevoRegistro = Registro(
            obra,
            fecha,
            personal,
            numeroReporte,
            capa,
            Tramo,
            subTramo,
            compactacion,
            msvm,
            humedad,
            llave,
            listaCalas
        )
        registrosLocales.add(nuevoRegistro)

        // Guardar la lista actualizada localmente
        saveLocalRecords(registrosLocales)
    }


    private fun getLocalRecords(): MutableList<Registro> {
        val registrosJson = sharedPreferences.getString("registros", "[]")
        return Gson().fromJson(registrosJson, object : TypeToken<MutableList<Registro>>() {}.type)
            ?: mutableListOf()
    }

    private fun saveLocalRecords(registros: List<Registro>) {

        val registrosJson = Gson().toJson(registros)
        sharedPreferences.edit().putString("registros", registrosJson).apply()
    }

    private fun syncDataWithFirebase(
        numeroReporte: Int,
        listaCalas: List<ClaseCala>,
        accion: Boolean
    ) {
        // Verificar si hay conexión a Internet
        // Puedes usar una biblioteca como Connectivity Manager para esto

        // Obtener registros locales
        val registrosLocales = getLocalRecords()

        // Sincronizar cada registro con Firebase Realtime Database
        for (registro in registrosLocales) {

            // Generar una nueva clave única para cada registro
//            val nuevaClave = dataReference.push().key
//            llave=nuevaClave!!



            val reportesReferencia = dataReference.child("Reportes").child(personal)

            reportesReferencia.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

//
                    val nuevoNumeroReporte = snapshot.childrenCount.toInt()
                    val totalReportes = tvNumeroReporteCompactacion.text

                    if (accion == true) {
                        // Guardar el registro en Firebase Realtime Database
                        dataReference.child("Reportes").child(personal)
                            .child(reporteSelecionado.llave)
                            .setValue(registro)
                        onBackPressed()
                    } else {
                        // Guardar el registro en Firebase Realtime Database
                        registro.llave=llave
                        dataReference.child("Reportes").child(personal)
                            .child(llave)
                            .setValue(registro)

                    }
                    if (accion == true) {
                        // Guardar el registro en Firebase Realtime Database
                        dataReference.child("Respaldo").child(personal)
                            .child(reporteSelecionado.llave)
                            .setValue(registro)
                        onBackPressed()
                    } else {
                        // Guardar el registro en Firebase Realtime Database
                        registro.llave=llave
                        dataReference.child("Respaldo").child(personal)
                            .child(llave)
                            .setValue(registro)

                    }


                }

                override fun onCancelled(error: DatabaseError) {


                }
            })



            // Guardar el registro en Firebase Realtime Database
//            dataReference.child("Reportes").child(nuevaClave!!).setValue(registro)


            // Eliminar el registro local después de la sincronización
            registrosLocales.remove(registro)
        }

        // Limpiar registros locales después de la sincronización
        saveLocalRecords(registrosLocales)
    }

    data class Registro(
        val obra: String,
        val fecha: String,
        val personal: String,

        val numeroReporte: Int,
        val capa: String,
        val Tramo: String,
        val subTramo: String,
        val compactacion: Double,
        val mvsm: Double,
        val humedad: Double,
        var llave: String,
        val listaCalas: List<ClaseCala>
    )


    private fun ConsultarUltimoRegistro() {

// Obtén la referencia de la base de datos
        dataReference = FirebaseDatabase.getInstance().reference
        sharedPreferences = getPreferences(Context.MODE_PRIVATE)

        // Agrega un listener para contar el total de registros
        dataReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Obtiene el número total de registros
                if (editar == true) {
                    tvNumeroReporteCompactacion.text = reporteSelecionado.reporte
                } else {
                    tvNumeroReporteCompactacion.text =
                        (dataSnapshot.child("Reportes").child(personal).childrenCount).toString()
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Error al leer la base de datos: ${databaseError.message}")
            }


        })

    }

//    private fun obtenerElReporteAGuardar() {
//        // Obtén la referencia de la base de datos
//        dataReference = FirebaseDatabase.getInstance().reference
//        sharedPreferences = getPreferences(Context.MODE_PRIVATE)
//
//
//        // Agrega un listener para contar el total de registros
//        dataReference.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // Obtiene el número total de registros
//                val reporteAGuardar1 = (dataSnapshot.child(personal).childrenCount + 1).toInt()
//
//                // Muestra el Toast con el valor obtenido
//                Toast.makeText(
//                    this@RegistroCompactaciones,
//                    reporteAGuardar1.toString(),
//                    Toast.LENGTH_SHORT
//                ).show()
//
//                // Puedes hacer más cosas con reporteAGuardar1 aquí, si es necesario
//
//
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                println("Error al leer la base de datos: ${databaseError.message}")
//            }
//        })
//    }

    override fun onBackPressed() {
        // Aquí puedes realizar acciones específicas cuando se presiona el botón de retroceso
        // Por ejemplo, puedes mostrar un cuadro de diálogo de confirmación o realizar alguna operación antes de cerrar la actividad
        // Puedes agregar tu lógica aquí o llamar al método super.onBackPressed() para cerrar la actividad sin ninguna acción adicional.
        if (editar==true){restaurarDatosOriginales()}
        ReportesCompactaciones.editar = false
        super.onBackPressed()
    }

    private fun initUI() {


        btnCancelar.setOnClickListener {

            restaurarDatosOriginales()
            ReportesCompactaciones.editar = false


            onBackPressed()

        }
        btnGuardar.setOnClickListener {

            mostrarDialogo()


        }
        fbNuevaCalaCompactacion.setOnClickListener { showDialog() }

        CalasAdapter =
            CalasAdapter(listaCalasmutableListOf,
                onCalaSelected = { position -> onItemSelected(position) },
                onItemDelete = { position -> onItemDelete(position) })
        rvCalas.layoutManager = LinearLayoutManager(this)
        rvCalas.adapter = CalasAdapter

    }

    private fun onItemSelected(position: Int) {
//        Toast.makeText(this, position.toString(), Toast.LENGTH_SHORT).show()

        showDialog(listaCalasmutableListOf[position], position)

    }

    private fun onItemDelete(position: Int) {

        // Crea un objeto AlertDialog66
        val builder = AlertDialog.Builder(this)

        // Configura el título y el mensaje del cuadro de diálogo
        builder.setTitle("Confirmación")
        builder.setMessage("¿Deseas eliminar esta cala?")

        // Configura el botón positivo (sí)
        builder.setPositiveButton("Sí") { dialog, which ->

            listaCalasmutableListOf.removeAt(position)
            listaCalasmutableListOf.forEachIndexed { index, elemento ->
                // Puedes realizar alguna lógica para determinar la nueva numeración
                val nuevaNumeracion = index  // Sumar 1 para empezar desde 1, si es necesario

                // Reemplazar la numeración en cada objeto
                elemento.cala = nuevaNumeracion
            }
            updateTask()

        }

        // Configura el botón negativo (no)
        builder.setNegativeButton("No") { dialog, which ->
            return@setNegativeButton
            // Código a ejecutar si el usuario hace clic en No
        }

        // Muestra el cuadro de diálogo
        builder.show()



    }


    private fun showDialog(calaSeleccionada: ClaseCala, indice: Int) {


        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_nueva_cala_compactacion)

        val btnGuardarCalaCompactacion: Button =
            dialog.findViewById(R.id.btnGuardarCalaCompactacion)

        val etEstacionCalaCompactacion: EditText =
            dialog.findViewById(R.id.etEstacionCalaCompactacion)
        val etProfCalaCompactacion: EditText = dialog.findViewById(R.id.etProfCalaCompactacion)
        val etMVSLCalaCompactacion: EditText = dialog.findViewById(R.id.etMVSLCalaCompactacion)
        val etHumedadLugarCalaCopactacion: EditText =
            dialog.findViewById(R.id.etHumedadLugarCalaCopactacion)

        try {
            if (etMVSM.text.toString().toDouble()<=0||etMVSM.text.toString().isEmpty()){
                Toast.makeText(this, "La MVSM debe ser maayor que 0. ", Toast.LENGTH_SHORT).show()
                return
            }
        }catch (e: NumberFormatException) {
            Toast.makeText(this, "llenar correctamente los campos", Toast.LENGTH_SHORT).show()
            return
        }

        etEstacionCalaCompactacion.setText(calaSeleccionada.Estacion)
        etProfCalaCompactacion.setText(calaSeleccionada.prof.toString())
        etMVSLCalaCompactacion.setText(calaSeleccionada.MVSL.toString())
        etHumedadLugarCalaCopactacion.setText(calaSeleccionada.Humedad.toString())

        btnGuardarCalaCompactacion.setOnClickListener {

            try {
                val estacion = etEstacionCalaCompactacion.text.toString()
                val profundidad = etProfCalaCompactacion.text.toString().toDouble()
                val MSVL = etMVSLCalaCompactacion.text.toString().toDouble()
                val humedad = etHumedadLugarCalaCopactacion.text.toString().toDouble()
                val porcentajeCompactacion =
                    (MSVL / etMVSM.text.toString().toDouble() * 100.0 * 100).roundToInt() / 100.0


//            if (estacion.isEmpty()) {
//                dialog.hide()
//
//                return@setOnClickListener
//
//            }

                calaNueva = ClaseCala(
                    indice,
                    estacion,
                    profundidad,
                    MSVL,
                    humedad,
                    porcentajeCompactacion
                )
                listaCalasmutableListOf.set(indice, calaNueva)


                updateTask()

                dialog.hide()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "llenar correctamente los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            catch (e: IllegalArgumentException) {
                Toast.makeText(this, "La MVSM debe ser diferente a 0.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


        }

        dialog.show()

    }


    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_nueva_cala_compactacion)

        val btnGuardarCalaCompactacion: Button =
            dialog.findViewById(R.id.btnGuardarCalaCompactacion)

        val etEstacionCalaCompactacion: EditText =
            dialog.findViewById(R.id.etEstacionCalaCompactacion)
        val etProfCalaCompactacion: EditText = dialog.findViewById(R.id.etProfCalaCompactacion)
        val etMVSLCalaCompactacion: EditText = dialog.findViewById(R.id.etMVSLCalaCompactacion)
        val etHumedadLugarCalaCopactacion: EditText =
            dialog.findViewById(R.id.etHumedadLugarCalaCopactacion)

        try {
            if (etMVSM.text.toString().toDouble()<=0||etMVSM.text.toString().isEmpty()){
                Toast.makeText(this, "La MVSM debe ser maayor que 0. ", Toast.LENGTH_SHORT).show()
                return
            }
        }catch (e: NumberFormatException) {
            Toast.makeText(this, "llenar correctamente los campos", Toast.LENGTH_SHORT).show()
            return
        }


        btnGuardarCalaCompactacion.setOnClickListener {

            try {
                if (etMVSLCalaCompactacion.text.toString().toDouble() == null) {
                    return@setOnClickListener
                }
                val estacion = etEstacionCalaCompactacion.text.toString()
                val profundidad = etProfCalaCompactacion.text.toString().toDouble()
                val MSVL = etMVSLCalaCompactacion.text.toString().toDouble()
                val humedad = etHumedadLugarCalaCopactacion.text.toString().toDouble()
                val porcentajeCompactacion =
                    (MSVL / etMVSM.text.toString().toDouble() * 100.0 * 100).roundToInt() / 100.0

//            if (estacion.isEmpty()) {
//                dialog.hide()
//
//                return@setOnClickListener
//
//            }


                calaNueva = ClaseCala(
                    listaCalasmutableListOf.count(),
                    estacion,
                    profundidad,
                    MSVL,
                    humedad,
                    porcentajeCompactacion
                )
                listaCalasmutableListOf.add(calaNueva)

                llave=dataReference.push().key.toString()

                updateTask()

                dialog.hide()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "llenar correctamente los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            catch (e: IllegalArgumentException) {
                Toast.makeText(this, "La MVSM debe ser diferente a 0.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

        }

        dialog.show()

    }

    fun mostrarCalendario(view: View) {
        val calendario = Calendar.getInstance()
        val año = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH)
        val dia = calendario.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this, { _, year, month, dayOfMonth ->
                // Formatear la fecha seleccionada con dos dígitos para el día
                val fechaSeleccionada = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                etFecha.setText(fechaSeleccionada)
            }, año, mes, dia
        )

        datePickerDialog.show()
    }

    //    private fun GuardarCompactacion(
//        obra: String, fecha: String, capa: String, tramo: String, personal: String
//    ) {
//        // Obtén la referencia de la base de datos
//        dataReference = FirebaseDatabase.getInstance().reference
//
//        // Crear un objeto con los datos que deseas almacenar
//        val data = HashMap<String, Any>()
//        data["Tramo"] = tramo
//        data["Fecha"] = fecha
//        data["Capa"] = capa
//
//        // Generar un nuevo nodo único en la base de datos
//        val newChildRef =
//            dataReference.child(personal).child(tvNumeroReporteCompactacion.text.toString())
//        newChildRef.setValue(data)
//
//    }
    private fun cargarObraSeleccionada(reporteSelecionado: ClaseObra) {

        etObra.setText(reporteSelecionado.Obra)
        etFecha.setText(reporteSelecionado.fecha)
        etCapa.setText(reporteSelecionado.capa)
        etTramo.setText(reporteSelecionado.tramo)
        etSubTramo.setText(reporteSelecionado.subtramo)
        etcompactacionProyecto.setText(reporteSelecionado.compactacion)
        etMVSM.setText(reporteSelecionado.mvsm)
        etHumedad.setText(reporteSelecionado.humedad)
        llave=reporteSelecionado.llave

        listaCalasmutableListOf = reporteSelecionado.listaCalas

        CalasAdapter =
            CalasAdapter(reporteSelecionado.listaCalas,
                onCalaSelected = { position -> onItemSelected(position) },
                onItemDelete = { position -> onItemDelete(position) })
        rvCalas.layoutManager = LinearLayoutManager(this)
        rvCalas.adapter = CalasAdapter

//        Toast.makeText(this, reporteSelecionado.listaCalas.count(), Toast.LENGTH_SHORT).show()
    }

    private fun initComponet() {
        //REPORTE DE COMPACTACION
        etObra = findViewById(R.id.etObraCompactacion)
        etFecha = findViewById(R.id.etFechaCompactacion)

        FechaDeHoy()

        //DATOS DE LA PRUEBA
        etCapa = findViewById(R.id.etCapaCompactacion)
        etTramo = findViewById(R.id.etTramoCompactacion)
        etSubTramo = findViewById(R.id.etSubTramo)
        etcompactacionProyecto = findViewById(R.id.etcompactacionProyecto)
        etMVSM = findViewById(R.id.etMVSM)
        etHumedad = findViewById(R.id.etHumedad)
        tvNumeroReporteCompactacion = findViewById(R.id.tvNumeroReporteCompactacion)


        //REGISTRO DE CALAS
        rvCalas = findViewById(R.id.rvCalasCompactaciones)

        //BOTONES
        btnCancelar = findViewById(R.id.btnCancelarRegistroCompactacion)
        btnGuardar = findViewById(R.id.btnGuardarRegistroCompactacion)
        btnVerCalendarioCompactaciones = findViewById(R.id.btnVerCalendarioCompactaciones)
        fbNuevaCalaCompactacion = findViewById(R.id.fbNuevaCalaCompactacion)


    }


    private fun FechaDeHoy() {
        val calendario = Calendar.getInstance()
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        etFecha.setText(formatoFecha.format(calendario.time))
    }

    private fun mostrarDialogo() {
        // Crea un objeto AlertDialog66
        val builder = AlertDialog.Builder(this)

        // Configura el título y el mensaje del cuadro de diálogo
        builder.setTitle("Confirmación")
        builder.setMessage("¿Deseas guardar este reporte?")

        // Configura el botón positivo (sí)
        builder.setPositiveButton("Sí") { dialog, which ->

//            if (etMVSM.text.toString()==""||etMVSM.text.toString().toDouble()==0.0){
//                Toast.makeText(this, "Colocar MVSM", Toast.LENGTH_SHORT).show()
//                return@setPositiveButton}

            try {


                var obra: String = etObra.text.toString()
                var fecha: String = etFecha.text.toString()
                var numeroReporte: Int = tvNumeroReporteCompactacion.text.toString().toInt()
                var capa: String = etCapa.text.toString()
                var Tramo: String = etTramo.text.toString()
                var subTramo: String = etSubTramo.text.toString()
                var compactacion: Double = etcompactacionProyecto.text.toString().toDouble()
                var msvm: Double = etMVSM.text.toString().toDouble()
                var llave=reporteSelecionado.llave
                var humedad: Double = etHumedad.text.toString().toDouble()


                // Agregar un nuevo registro localmente
                saveLocally(
                    obra,
                    fecha,
                    personal,
                    numeroReporte,
                    listaCalasmutableListOf,
                    capa,
                    Tramo,
                    subTramo,
                    compactacion,
                    msvm,
                    humedad,
                    llave
                )


                // Sincronizar los datos cuando hay conexión a Internet
                syncDataWithFirebase(numeroReporte, listaCalasmutableListOf, editar)

                ConsultarUltimoRegistro()
                listaCalasmutableListOf.clear()
                updateTask()
                llave=dataReference.push().key.toString()
                Toast.makeText(this, "Reporte guardado correctamente.", Toast.LENGTH_LONG).show()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "llenar correctamente los campos", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
        }

        // Configura el botón negativo (no)
        builder.setNegativeButton("No") { dialog, which ->
            return@setNegativeButton
            // Código a ejecutar si el usuario hace clic en No
        }

        // Muestra el cuadro de diálogo
        builder.show()
    }
    private fun restaurarDatosOriginales() {
        listaCalasmutableListOf.clear()
        listaCalasmutableListOf.addAll(listaCalasOriginal)
        updateTask()
    }

}