package com.miguelrodriguez.rocaapp20.mecanicas

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import com.miguelrodriguez.rocaapp20.Recycler.ClaseEstratos
import com.miguelrodriguez.rocaapp20.Recycler.ClaseObra
import com.miguelrodriguez.rocaapp20.Recycler.EstratosAdapter
import com.google.firebase.storage.StorageReference
import com.miguelrodriguez.rocaapp20.MainActivity
import com.miguelrodriguez.rocaapp20.R

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
    private lateinit var rvObrasMecanicas: RecyclerView
    private lateinit var ObraAdapter: ObraMecanicaAdapter
    private lateinit var claseObra: ClaseObra

    private lateinit var estratosAdapter: EstratosAdapter
    private lateinit var rvEstratos: RecyclerView

    private lateinit var etCliente: EditText
    private lateinit var etObra: EditText
    private lateinit var etlocalizacion: EditText
    private lateinit var etAtencion: EditText
    private lateinit var etFecha: EditText
//    private lateinit var tipoMuestreo: Spinner

    private lateinit var etSondeoNumMuestreoMecanica: EditText
    private lateinit var etUbicacionMuestreoMecanica: EditText
    private lateinit var etNAFMuestreoMecanica: EditText
    private lateinit var etProfunMuestreoMecanica: EditText
    private lateinit var etProfunNAFMuestreoMecanica: EditText
    private lateinit var etHoraMuestreoMecanica: EditText







    private lateinit var llave: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes_muestreo_material)

        InitComponet()
        InitUI()
    }


    private fun InitComponet() {
        listaEstratossmutableListOf = mutableListOf(ClaseEstratos(
            1,
            "1",
            1.0,
            1.0,
            1.0,
            "1.0",
            "1.0"
            ))
        listaImagenesmutableListOf = mutableListOf("a1")
        listaObrasmutableListOf = mutableListOf()
        listaObrasmutableListOf.clear()
        personal = MainActivity.NombreUsuarioCompanion

        reporteSelecionadoMuestroMaterial = ClaseObraMecanica(
            1,
            "obra",
            "cliente",
            "localizacion",
            "atencion",
            "fecha",
            "sondeo_num",
            "ubicacion",
             false,
            "profundidad_muestreo",
            "profundidad_naf",
            "hora",
            "llave",
            "latitud",
            "longitud",
            listaEstratossmutableListOf,
            listaImagenesmutableListOf
        )

//        etObra = findViewById(R.id.etObraMuestreoMecanica)
//        etCliente = findViewById(R.id.etClienteMuestreoMecanica)
//        etlocalizacion = findViewById(R.id.etLocalizacionMuestreoMecanica)
//        etAtencion = findViewById(R.id.etAtencionMuestreoMecanica)
//        etFecha = findViewById(R.id.etFechaMuestreoMecanica)
//        etSondeoNumMuestreoMecanica = findViewById(R.id.etSondeoNumMuestreoMecanica)
//        etUbicacionMuestreoMecanica = findViewById(R.id.etUbicacionMuestreoMecanica)
//        etNAFMuestreoMecanica = findViewById(R.id.etNAFMuestreoMecanica)
//        etProfunMuestreoMecanica = findViewById(R.id.etProfundidadMuestreoMecanica)
//        etProfunNAFMuestreoMecanica = findViewById(R.id.etProfundidadNAFMuestreoMecanica)
//        rvEstratos = findViewById(R.id.rvMuestreoEstratos)

//        listaEstratossmutableListOf = mutableListOf(ClaseEstratos(1, "h", 1.0))
        btnRegistroMuestreoMaterial = findViewById(R.id.btnRegistroMuestreoMaterial)
        rvObrasMecanicas = findViewById(R.id.rvObrasMecanicas)
    }

    private fun cargarObraSeleccionada(reporteSelecionado: ClaseObraMecanica) {

        etObra.setText(reporteSelecionado.Obra)
        etCliente.setText(reporteSelecionado.cliente)
        etAtencion.setText(reporteSelecionado.atencion)
        etlocalizacion.setText(reporteSelecionado.localizacion)
        etFecha.setText(reporteSelecionado.fecha)
        etSondeoNumMuestreoMecanica.setText(reporteSelecionado.sondeo_num)
        etUbicacionMuestreoMecanica.setText(reporteSelecionado.ubicacion)
        etNAFMuestreoMecanica.setText(reporteSelecionado.naf.toString())

        etProfunMuestreoMecanica.setText(reporteSelecionado.profundidad_muestreo)
        etProfunNAFMuestreoMecanica.setText(reporteSelecionado.profundidad_naf)


//
//        etSubTramo.setText(reporteSelecionado.subtramo)
//        procedencia.setText(reporteSelecionado.procedencia)
//        lugarMuestreo.setText(reporteSelecionado.lugarMuestreo)
//        estacion.setText(reporteSelecionado.estacion)
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


        rvObrasMecanicas.layoutManager = LinearLayoutManager(this)
        rvObrasMecanicas.adapter = ObraAdapter


        storage= FirebaseStorage.getInstance()
        dataReference =
            FirebaseDatabase.getInstance().reference.child("Mecanicas").child("ReportesMecanicas").child(personal)

        dataReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Limpia la lista actual
                listaObrasmutableListOf.clear()

                for (snapshot in dataSnapshot.children) {
                    val numeroReporteKey = snapshot.key // Obtiene el número de informe (1, 2, 3, 4)

                    // Accede a los datos específicos de cada informe
                    val obra1 = snapshot.child("obra").getValue(String::class.java)
                    val cliente = snapshot.child("cliente").getValue(String::class.java)
                    val localizacion = snapshot.child("localizacion").getValue(String::class.java)
                    val atencion = snapshot.child("atencion").getValue(String::class.java)
                    val fecha = snapshot.child("fecha").getValue(String::class.java)
//                    val estudioMuestreo =snapshot.child("estudioMuestreo").getValue(String::class.java)
//                    val tipoMuestreo = snapshot.child("tipoMuestreo").getValue(String::class.java)

                    val sondeo_num = snapshot.child("sondeo_num").getValue(String::class.java)
                    val ubicacion = snapshot.child("ubicacion").getValue(String::class.java)
                    val naf = snapshot.child("naf").getValue(Boolean::class.java)
                    val profundidad_muestreo =snapshot.child("profundidad_muestreo").getValue(String::class.java)
                    val profundidad_naf =snapshot.child("profundidad_naf").getValue(String::class.java)
                    val hora = snapshot.child("hora").getValue(String::class.java)

                    var llave = snapshot.child("llave").getValue(String::class.java)
//                    val listaCalas = snapshot.child("listaCalas").getValue(MutableList<ClaseCala>::class.java)

                    val listaEstratosSnapshot = snapshot.child("listaEstratos")
                    val listaEstratos: MutableList<ClaseEstratos> = mutableListOf()
                    val listaImagenes: MutableList<String> = mutableListOf()

                    for (snapshot in listaEstratosSnapshot.children) {

                        val idEstrato = snapshot.child("idEstrato").getValue(Int::class.java)
                        val tipo_muestreo = snapshot.child("tipo_muestreo").getValue(String::class.java)
                        val profundidad_inicio = snapshot.child("profundidad_inicio").getValue(Double::class.java)
                        val profundidad_final = snapshot.child("profundidad_final").getValue(Double::class.java)
                        val profundidad_muestreo= snapshot.child("profundidad_muestreo").getValue(Double::class.java)
                        val clasificacion_visual = snapshot.child("clasificacion_visual").getValue(String::class.java)
                        val observaciones = snapshot.child("observaciones").getValue(String::class.java)
//                        val nombre = snapshot.child("nombre").getValue(String::class.java)

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
                            tipo_muestreo!!,
                            profundidad_inicio!!,
                            profundidad_final!!,
                            profundidad_muestreo!!,
                            clasificacion_visual!!,
                            observaciones!!,
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
                            cliente.toString(),
                            localizacion.toString(),
                            atencion.toString(),
                            fecha.toString(),
                            sondeo_num.toString(),
                            ubicacion.toString(),
                            naf!!,
                            profundidad_muestreo.toString(),
                            profundidad_naf.toString(),
                            hora.toString(),
                            llave.toString(),
//                            estudioMuestreo.toString(),
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
        val reportReference = FirebaseDatabase.getInstance().reference.child("Mecanicas").child("ReportesMecanicas").child(personal).child(reportKey)
        val reportReference1= FirebaseDatabase.getInstance().reference.child("Mecanicas").child("RespaldoMecanicas").child(personal).child(reportKey)
        val reportReference2= FirebaseDatabase.getInstance().reference.child("ImagenesMecanicas").child(personal).child(reportKey)

//        val storage = FirebaseStorage.getInstance()
//        val storage = FirebaseStorage.getInstance().reference.child(reportKey)

        val storageRef: StorageReference = storage.reference.child(reportKey)


        println(reportReference.toString())
        println(storageRef.toString())



        // Eliminar imágenes asociadas en Firebase Storage
        val imageRef =
            storage.reference.child(reportKey) // Suponiendo que las imágenes están en una carpeta con el nombre del `reportKey`

        imageRef.listAll().addOnSuccessListener { listResult ->
            // Recorre cada archivo en el directorio y elimínalo
            for (fileRef in listResult.items) {
                fileRef.delete().addOnSuccessListener {
                    Log.d("FirebaseStorage", "Imagen eliminada: ${fileRef.name}")
                }.addOnFailureListener { e ->
                    Log.e(
                        "FirebaseStorage",
                        "Error al eliminar imagen ${fileRef.name}: ${e.message}"
                    )
                }
            }
        }.addOnFailureListener { e ->
            Log.e("FirebaseStorage", "Error al listar archivos para eliminar: ${e.message}")
        }


        reportReference.removeValue().addOnSuccessListener {
            Toast.makeText(
                this,
                "Reporte eliminado existosamente.",
                Toast.LENGTH_SHORT
            ).show() }

        reportReference1.removeValue().addOnSuccessListener {}

        reportReference2.removeValue().addOnSuccessListener {}

    }

    private fun updateTask() {
        ObraAdapter.notifyDataSetChanged()
    }


}