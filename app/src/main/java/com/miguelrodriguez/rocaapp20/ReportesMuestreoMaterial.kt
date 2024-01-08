package com.miguelrodriguez.rocaapp20

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.miguelrodriguez.rocaapp20.Recycler.CalasAdapter
import com.miguelrodriguez.rocaapp20.Recycler.ClaseCala
import com.miguelrodriguez.rocaapp20.Recycler.ClaseEstratos
import com.miguelrodriguez.rocaapp20.Recycler.ClaseObra
import com.miguelrodriguez.rocaapp20.Recycler.ClaseObraMecanica
import com.miguelrodriguez.rocaapp20.Recycler.EstratosAdapter
import com.miguelrodriguez.rocaapp20.Recycler.ObraAdapter
import com.miguelrodriguez.rocaapp20.Recycler.ObraMecanicaAdapter
import java.util.Calendar
import kotlin.math.log
import com.google.firebase.storage.StorageReference

class ReportesMuestreoMaterial : AppCompatActivity() {
    private lateinit var dataReference: DatabaseReference
    private lateinit var btnRegistroMuestreoMaterial: Button
    private lateinit var etFechaMuestreoMecanica: Button
    private lateinit var listaObrasmutableListOf: MutableList<ClaseObraMecanica>
    private lateinit var listaEstratossmutableListOf: MutableList<ClaseEstratos>
    private lateinit var listaImagenesmutableListOf: MutableList<String>

    private lateinit var storage:FirebaseStorage
    private lateinit var personal: String
    private lateinit var reporteSelecionado: ClaseObraMecanica


    companion object {
        lateinit var reporteSelecionadoMuestroMaterial: ClaseObraMecanica
        var editarMuestreoMaterial: Boolean = false
    }

    private lateinit var btnRegistroCompactacion: Button
    private lateinit var rvObrasCompactacion: RecyclerView
    private lateinit var ObraAdapter: ObraMecanicaAdapter
    private lateinit var claseObra: ClaseObra

    private lateinit var estratosAdapter: EstratosAdapter
    private lateinit var rvEstratos: RecyclerView

    private lateinit var etObra: EditText
    private lateinit var etFecha: EditText
    private lateinit var etCapa: EditText
    private lateinit var etTramo: EditText
    private lateinit var etSubTramo: EditText
    private lateinit var procedencia: EditText
    private lateinit var lugarMuestreo: EditText
    private lateinit var estacion: EditText
    private lateinit var tipoMuestreo: Spinner
    private lateinit var estudioMuestreo: Spinner
    private lateinit var llave: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes_muestreo_material)





        InitComponet()
        InitUI()
    }


    private fun InitComponet() {
        listaEstratossmutableListOf = mutableListOf(ClaseEstratos(1, "1", 1.0))
        listaImagenesmutableListOf = mutableListOf("a1")
        listaObrasmutableListOf = mutableListOf()
        listaObrasmutableListOf.clear()
        personal = MainActivity.NombreUsuarioCompanion

        reporteSelecionadoMuestroMaterial = ClaseObraMecanica(
            1, "estacion", "1", "1", "1",
            "1", "1", "1", "1", "1", "ho111la",
            "tipo", "estudio","latitud","longitud", listaEstratossmutableListOf, listaImagenesmutableListOf
        )

//        listaEstratossmutableListOf = mutableListOf(ClaseEstratos(1, "h", 1.0))
        btnRegistroMuestreoMaterial = findViewById(R.id.btnRegistroMuestreoMaterial)
        rvObrasCompactacion = findViewById(R.id.rvObrasMecanicas)
    }

    private fun cargarObraSeleccionada(reporteSelecionado: ClaseObraMecanica) {

        etObra.setText(reporteSelecionado.Obra)
        etFecha.setText(reporteSelecionado.fecha)
        etCapa.setText(reporteSelecionado.capa)
        etTramo.setText(reporteSelecionado.tramo)
        etSubTramo.setText(reporteSelecionado.subtramo)
        procedencia.setText(reporteSelecionado.procedencia)
        lugarMuestreo.setText(reporteSelecionado.lugarMuestreo)
        estacion.setText(reporteSelecionado.estacion)
//        tipoMuestreo.setSelection(reporteSelecionado.tipoMuestreo)
//        estudioMuestreo.setSelection(reporteSelecionado.estudioMuestreo)
        llave = reporteSelecionado.llave

        listaEstratossmutableListOf = reporteSelecionado.listaEstratos

        estratosAdapter =
            EstratosAdapter(reporteSelecionado.listaEstratos,
                onEstratoSelected = { position -> onItemSelected(position) },
                onItemDelete = { position -> onItemDelete(position) })
        rvEstratos.layoutManager = LinearLayoutManager(this)
        rvEstratos.adapter = estratosAdapter

    }

    private fun InitUI() {
        btnRegistroMuestreoMaterial.setOnClickListener {
            val intent = Intent(this, RegistroMecanica::class.java)
            startActivity(intent)
        }

        ObraAdapter = ObraMecanicaAdapter(listaObrasmutableListOf,
            onObraSelected = { position -> onItemSelected(position) },
            onItemDelete = { position -> onItemDelete(position) })


        rvObrasCompactacion.layoutManager = LinearLayoutManager(this)
        rvObrasCompactacion.adapter = ObraAdapter


        storage= FirebaseStorage.getInstance()
        dataReference =
            FirebaseDatabase.getInstance().reference.child("ReportesMecanicas").child(personal)

        dataReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Limpia la lista actual
                listaObrasmutableListOf.clear()

                val personalDeseado =
                    "miguel" // Reemplaza con el nombre del personal que deseas filtrar

                for (snapshot in dataSnapshot.children) {
                    val numeroReporteKey = snapshot.key // Obtiene el número de informe (1, 2, 3, 4)

                    // Accede a los datos específicos de cada informe
                    val obra1 = snapshot.child("obra").getValue(String::class.java)
                    val capa = snapshot.child("capa").getValue(String::class.java)
                    val estacion = snapshot.child("estacion").getValue(String::class.java)
                    val fecha = snapshot.child("fecha").getValue(String::class.java)
                    val estudioMuestreo =
                        snapshot.child("estudioMuestreo").getValue(String::class.java)
                    val tipoMuestreo = snapshot.child("tipoMuestreo").getValue(String::class.java)
                    var llave = snapshot.child("llave").getValue(String::class.java)
//                    val listaCalas = snapshot.child("listaCalas").getValue(MutableList<ClaseCala>::class.java)

                    val listaEstratosSnapshot = snapshot.child("listaEstratos")
                    val listaEstratos: MutableList<ClaseEstratos> = mutableListOf()
                    val listaImagenes: MutableList<String> = mutableListOf()

                    for (snapshot in listaEstratosSnapshot.children) {

                        val idEstrato = snapshot.child("idEstrato").getValue(Int::class.java)
                        val espesor = snapshot.child("espesor").getValue(Double::class.java)
                        val nombre = snapshot.child("nombre").getValue(String::class.java)

//                        // Asegúrate de ajustar los nombres de los campos según tu modelo ClaseCala
//                        val estacion = calaSnapshot.child("estacion").getValue(String::class.java)
//                        val humedad = calaSnapshot.child("humedad").getValue(Double::class.java)
//                        val estrato = calaSnapshot.child("cala").getValue(Int::class.java)
//                        val mvsl = calaSnapshot.child("mvsl").getValue(Double::class.java)
//                        val porcentaje =
//                            calaSnapshot.child("porcentaje").getValue(Double::class.java)
//                        val prof =
//                            calaSnapshot.child("prof").getValue(Double::class.java)
                        // Crea un objeto ClaseCala y agrégalo a la lista
                        val Estrato = ClaseEstratos(
                            idEstrato!!,
                            nombre!!,
                            espesor!!
                        )
                        listaEstratos.add(Estrato)
                    }


                    val procedencia = snapshot.child("procedencia").getValue(String::class.java)
                    val numReporte = snapshot.child("numeroReporte").getValue(Int::class.java)
                    val personal1 = snapshot.child("personal").getValue(String::class.java)
                    val subTramo = snapshot.child("subTramo").getValue(String::class.java)
                    val tramo = snapshot.child("tramo").getValue(String::class.java)
                    val lugarMuestreo = snapshot.child("lugarMuestreo").getValue(String::class.java)
                    val latitud = snapshot.child("latitud").getValue(String::class.java)
                    val longitud = snapshot.child("longitud").getValue(String::class.java)

                    // Verifica si el personal coincide con el personal deseado
                    if (personal1 == personal) {
                        // Crea un objeto ClaseObra y agrégalo a la lista solo si el personal coincide
                        val obra = ClaseObraMecanica(
                            numReporte!!,
                            obra1.toString(),
                            numReporte.toString(),
                            capa.toString(),
                            fecha.toString(),
                            tramo.toString(),
                            subTramo.toString(),
                            procedencia.toString(),
                            lugarMuestreo.toString(),
                            estacion.toString(),
                            llave.toString(),
                            tipoMuestreo.toString(),
                            estudioMuestreo.toString(),
                            latitud.toString(),
                            longitud.toString(),
                            listaEstratos,
                            listaImagenes


                        ) // Asegúrate de ajustar los parámetros según tu clase
                        listaObrasmutableListOf.add(obra)
                    }
                }

                // Notifica al adaptador que los datos han cambiado
                ObraAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar error de base de datos, si es necesario
            }

        })


    }

    private fun onItemSelected(position: Int) {
//        Toast.makeText(this, position.toString(), Toast.LENGTH_SHORT).show()

//        listaCalasmutableListOf[position], position


        editarMuestreoMaterial = true
        reporteSelecionadoMuestroMaterial = listaObrasmutableListOf[position]
        val intent = Intent(this, RegistroMecanica::class.java)
//        intent.putExtra("ReporteSeleccionado",listaObrasmutableListOf[position])
        startActivity(intent)

    }

    private fun onItemDelete(position: Int) {
        if (isNetworkAvailable()) {
            val reportKey =
                listaObrasmutableListOf[position].llave // Utiliza la clave única del informe

            // Elimina el informe de la base de datos Firebase
            deleteReport(reportKey)

            // Elimina el informe de la lista local
            listaObrasmutableListOf.removeAt(position)

            // Notifica al adaptador que los datos han cambiado
            updateTask()
        } else {
            Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ?: false
    }

    private fun deleteReport(reportKey: String) {
        val reportReference = dataReference.child(reportKey)

//        val storage = FirebaseStorage.getInstance()
//        val storage = FirebaseStorage.getInstance().reference.child(reportKey)

        val storageRef: StorageReference = storage.reference.child(reportKey)


        println(reportReference.toString())
        println(storageRef.toString())



        storageRef.delete()

            .addOnSuccessListener {
                Toast.makeText(this, "Informe eliminado exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error al eliminar el informe: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        reportReference.removeValue()
    }

    private fun updateTask() {
        ObraAdapter.notifyDataSetChanged()
    }


}