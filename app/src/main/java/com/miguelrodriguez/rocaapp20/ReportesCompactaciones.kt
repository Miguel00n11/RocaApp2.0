package com.miguelrodriguez.rocaapp20

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.miguelrodriguez.rocaapp20.Recycler.ClaseObra
import com.miguelrodriguez.rocaapp20.Recycler.ObraAdapter
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.miguelrodriguez.rocaapp20.Recycler.ClaseCala


class ReportesCompactaciones : AppCompatActivity() {

    private lateinit var dataReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var personal: String
    private lateinit var cala1: MutableList<ClaseCala>

    companion object {
        lateinit var reporteSelecionado: ClaseObra
        var editar: Boolean = false
    }


    private lateinit var btnRegistroCompactacion: Button
    private lateinit var rvObrasCompactacion: RecyclerView
    private lateinit var ObraAdapter: ObraAdapter
    private lateinit var claseObra: ClaseObra
    private lateinit var listaObrasmutableListOf: MutableList<ClaseObra>
    private lateinit var listacalasmutableListOf: MutableList<ClaseCala>

//    private val listaObrasmutableListOf =
//        mutableListOf(ClaseObra(1, "estacion","1","1","1",
//            "1","1","1","1","1",))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes_compactaciones)

        // Inicializa Firebase
        FirebaseApp.initializeApp(this)
//        // Opcional: Configura la persistencia si es necesario
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        listacalasmutableListOf = mutableListOf(ClaseCala(1, "1", 1.1, 1.1, 1.1, 1.1))

        listaObrasmutableListOf = mutableListOf(
        )

        listaObrasmutableListOf.clear()
        personal = MainActivity.NombreUsuarioCompanion



        reporteSelecionado = ClaseObra(
            1, "estacion", "1", "1", "1",
            "1", "1", "1", "1", "1","hola", listacalasmutableListOf
        )

        initComponent()
        initUI()


    }


//    private fun onItemDelete(position: Int) {
//
//
//        val reportNumber = listaObrasmutableListOf[position].reporte.toInt()
//        deleteReport(reportNumber)
//        listaObrasmutableListOf.removeAt(position)
////        listaObrasmutableListOf.forEachIndexed { index, elemento ->
////            // Puedes realizar alguna lógica para determinar la nueva numeración
////            val nuevaNumeracion = index  // Sumar 1 para empezar desde 1, si es necesario
////
////            // Reemplazar la numeración en cada objeto
////            elemento.reporte = nuevaNumeracion.toString()
////        }
//        updateTask()
//    }

    private fun updateTask() {
        ObraAdapter.notifyDataSetChanged()
    }

    private fun initUI() {
        // Referencia a la base de datos de Firebase
        dataReference = FirebaseDatabase.getInstance().reference.child("Reportes").child(personal)

        btnRegistroCompactacion.setOnClickListener {
            val intent = Intent(this, RegistroCompactaciones::class.java)
            startActivity(intent)
        }

        ObraAdapter = ObraAdapter(listaObrasmutableListOf,
            onObraSelected = { position -> onItemSelected(position) },
            onItemDelete = { position -> onItemDelete(position) })



        rvObrasCompactacion.layoutManager = LinearLayoutManager(this)
        rvObrasCompactacion.adapter = ObraAdapter

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
                    val compactacion = snapshot.child("compactacion").getValue(Int::class.java)
                    val fecha = snapshot.child("fecha").getValue(String::class.java)
                    val humedad = snapshot.child("humedad").getValue(Int::class.java)
                    var llave = snapshot.child("llave").getValue(String::class.java)
//                    val listaCalas = snapshot.child("listaCalas").getValue(MutableList<ClaseCala>::class.java)

                    val listaCalasSnapshot = snapshot.child("listaCalas")
                    val listaCalas: MutableList<ClaseCala> = mutableListOf()

                    for (calaSnapshot in listaCalasSnapshot.children) {
                        // Asegúrate de ajustar los nombres de los campos según tu modelo ClaseCala
                        val estacion = calaSnapshot.child("estacion").getValue(String::class.java)
                        val humedad = calaSnapshot.child("humedad").getValue(Double::class.java)
                        val cala = calaSnapshot.child("cala").getValue(Int::class.java)
                        val mvsl = calaSnapshot.child("mvsl").getValue(Double::class.java)
                        val porcentaje =
                            calaSnapshot.child("porcentaje").getValue(Double::class.java)
                        val prof =
                            calaSnapshot.child("prof").getValue(Double::class.java)
                        // Crea un objeto ClaseCala y agrégalo a la lista
                        val cala1 = ClaseCala(
                            cala!!,
                            estacion!!,
                            prof!!,
                            mvsl!!,
                            humedad!!,
                            porcentaje!!
                        )
                        listaCalas.add(cala1)
                    }


                    val mvsm = snapshot.child("mvsm").getValue(Int::class.java)
                    val numReporte = snapshot.child("numeroReporte").getValue(Int::class.java)
                    val personal1 = snapshot.child("personal").getValue(String::class.java)
                    val subTramo = snapshot.child("subTramo").getValue(String::class.java)
                    val tramo = snapshot.child("tramo").getValue(String::class.java)

                    // Verifica si el personal coincide con el personal deseado
                    if (personal1 == personal) {
                        // Crea un objeto ClaseObra y agrégalo a la lista solo si el personal coincide
                        val obra = ClaseObra(
                            numReporte!!,
                            obra1.toString(),
                            numReporte.toString(),
                            capa.toString(),
                            fecha.toString(),
                            tramo.toString(),
                            subTramo.toString(),
                            compactacion.toString(),
                            mvsm.toString(),
                            humedad.toString(),
                            llave.toString(),

                            listaCalas


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

    private fun onItemDelete(position: Int) {
        if (isNetworkAvailable()) {
            val reportKey = listaObrasmutableListOf[position].llave // Utiliza la clave única del informe

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
    private fun deleteReport(reportKey: String) {
        val reportReference = dataReference.child(reportKey)

        reportReference.removeValue()
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
    }
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ?: false
    }



    private fun onItemSelected(position: Int) {
//        Toast.makeText(this, position.toString(), Toast.LENGTH_SHORT).show()

//        listaCalasmutableListOf[position], position


        editar = true
        reporteSelecionado = listaObrasmutableListOf[position]
        val intent = Intent(this, RegistroCompactaciones::class.java)
//        intent.putExtra("ReporteSeleccionado",listaObrasmutableListOf[position])
        startActivity(intent)

    }

    private fun initComponent() {
        btnRegistroCompactacion = findViewById(R.id.btnRegistroCompactacion)
        rvObrasCompactacion = findViewById(R.id.rvObrasCompactacion)
    }

    private fun mostrarDialogo() {
        // Crea un objeto AlertDialog66
        val builder = AlertDialog.Builder(this)

        // Configura el título y el mensaje del cuadro de diálogo
        builder.setTitle("Confirmación")
        builder.setMessage("¿Deseas guardar este reporte?")

        // Configura el botón positivo (sí)
        builder.setPositiveButton("Sí") { dialog, which ->


        }

        // Configura el botón negativo (no)
        builder.setNegativeButton("No") { dialog, which ->
            return@setNegativeButton
            // Código a ejecutar si el usuario hace clic en No
        }

        // Muestra el cuadro de diálogo
        builder.show()
    }
}