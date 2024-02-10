package com.miguelrodriguez.rocaapp20.cilindros

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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.miguelrodriguez.rocaapp20.MainActivity
import com.miguelrodriguez.rocaapp20.R
import com.miguelrodriguez.rocaapp20.Recycler.ClaseCala
import com.miguelrodriguez.rocaapp20.Recycler.ClaseObra
import com.miguelrodriguez.rocaapp20.Recycler.ClaseObraMecanica
import com.miguelrodriguez.rocaapp20.Recycler.ObraAdapter
import com.miguelrodriguez.rocaapp20.RegistroCompactaciones
import com.miguelrodriguez.rocaapp20.ReportesCompactaciones


class ReporteCilindros : AppCompatActivity() {

    private lateinit var dataReference: DatabaseReference
    private lateinit var dataReferenceCampo: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var personal: String

    companion object {
        lateinit var reporteSelecionado: ClaseObraCilindros
        var editar: Boolean = false
    }


    private lateinit var btnRegistroCilindros: Button
    private lateinit var rvObrasCilindros: RecyclerView
    private lateinit var ObraAdapter: ObraAdapterCilindros
    private lateinit var claseObra: ClaseObra
    private lateinit var listaObrasmutableListOf: MutableList<ClaseObraCilindros>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporte_cilindros)

        initComponent()
        initUI()
    }

    private fun initComponent() {
        rvObrasCilindros = findViewById(R.id.rvObrasCilindros)

        reporteSelecionado = ClaseObraCilindros(
            1, "estacion", "a","1", "1", 1,
            "1", "1", "1", 1.1, 1.1,"hola",1,1.1,"a","1",
            "1","1",1,1.1,1.1,1.1,1.1,1,1,1,1,"1","1",
            "1","1","1:1","1:1","1:1","a",
            "1","1","1","1","1","1","1","1","1"
            ,false,""
        )
        btnRegistroCilindros=findViewById(R.id.btnRegistroCilindros)


        listaObrasmutableListOf = mutableListOf(
        )

        listaObrasmutableListOf.clear()
        personal = MainActivity.NombreUsuarioCompanion



    }

    private fun initUI() {
        // Referencia a la base de datos de Firebase
        dataReference =FirebaseDatabase.getInstance().reference.child("Cilindros").child("Reportes").child(personal)
        dataReferenceCampo =FirebaseDatabase.getInstance().reference.child("Cilindros").child("Respaldo").child(personal)


        btnRegistroCilindros.setOnClickListener {
            val intent = Intent(this, RegistroCilindros::class.java)
            startActivity(intent)
        }

        ObraAdapter = ObraAdapterCilindros(listaObrasmutableListOf,
            onObraSelected = { position -> onItemSelected(position) },
            onItemDelete = { position -> onItemDelete(position) })

        rvObrasCilindros.layoutManager = LinearLayoutManager(this)
        rvObrasCilindros.adapter = ObraAdapter

        dataReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Limpia la lista actual
                listaObrasmutableListOf.clear()

                val personalDeseado =
                    "miguel" // Reemplaza con el nombre del personal que deseas filtrar

                for (snapshot in dataSnapshot.children) {
                    val validado=snapshot.child("validado").getValue(Boolean::class.java)
                    if (validado==true){
                        continue
                    }


                    val numeroReporteKey = snapshot.key // Obtiene el número de informe (1, 2, 3, 4)

                    // Accede a los datos específicos de cada informe
                    val obra1 = snapshot.child("obra").getValue(String::class.java)
                    val cliente = snapshot.child("cliente").getValue(String::class.java)
                    val fecha = snapshot.child("fecha").getValue(String::class.java)
                    val personal1 = snapshot.child("personal").getValue(String::class.java)
                    val numReporte = snapshot.child("numeroReporte").getValue(Int::class.java)

                    val tipoMuestreo = snapshot.child("tipoMuestreo").getValue(String::class.java)
                    val elementoColado = snapshot.child("elementoColado").getValue(String::class.java)
                    val ubicacion = snapshot.child("ubicacion").getValue(String::class.java)
                    val fc = snapshot.child("fc").getValue(Double::class.java)
                    val volumenTotal = snapshot.child("volumenTotal").getValue(Double::class.java)
                    val tipoResistencia = snapshot.child("tipoResistencia").getValue(String::class.java)
                    val edad = snapshot.child("edad").getValue(Int::class.java)
                    val tma = snapshot.child("tma").getValue(Double::class.java)
                    val concretera = snapshot.child("concretera").getValue(String::class.java)
                    val proporciones = snapshot.child("proporciones").getValue(String::class.java)
                    val aditivo = snapshot.child("aditivo").getValue(String::class.java)
                    val remision = snapshot.child("remision").getValue(String::class.java)

                    val muestra = snapshot.child("muestra").getValue(Int::class.java)
                    val revenimientoDis = snapshot.child("revenimientoDis").getValue(Double::class.java)
                    val revenimientoR1 = snapshot.child("revenimientoR1").getValue(Double::class.java)
                    val revenimientoR2 = snapshot.child("revenimientoR2").getValue(Double::class.java)
                    val temperatura = snapshot.child("temperatura").getValue(Double::class.java)
                    val molde1 = snapshot.child("molde1").getValue(Int::class.java)
                    val molde2 = snapshot.child("molde2").getValue(Int::class.java)
                    val molde3 = snapshot.child("molde3").getValue(Int::class.java)
                    val molde4 = snapshot.child("molde4").getValue(Int::class.java)
                    val estadoMolde1 = snapshot.child("estadoMolde1").getValue(String::class.java)
                    val estadoMolde2 = snapshot.child("estadoMolde2").getValue(String::class.java)
                    val estadoMolde3 = snapshot.child("estadoMolde3").getValue(String::class.java)
                    val estadoMolde4 = snapshot.child("estadoMolde4").getValue(String::class.java)
                    val horaSalida = snapshot.child("horaSalida").getValue(String::class.java)
                    val horaLLegada = snapshot.child("horaLLegada").getValue(String::class.java)
                    val horaMuestreo = snapshot.child("horaMuestreo").getValue(String::class.java)
                    val observaciones = snapshot.child("observaciones").getValue(String::class.java)

                    val carretilla=snapshot.child("carretilla").getValue(String::class.java)
                    val cono=snapshot.child("cono").getValue(String::class.java)
                    val varilla=snapshot.child("varilla").getValue(String::class.java)
                    val mazo=snapshot.child("mazo").getValue(String::class.java)
                    val termometro=snapshot.child("termometro").getValue(String::class.java)
                    val cucharon=snapshot.child("cucharon").getValue(String::class.java)
                    val placa=snapshot.child("placa").getValue(String::class.java)
                    val flexometro=snapshot.child("flexometro").getValue(String::class.java)
                    val enrasador=snapshot.child("enrasador").getValue(String::class.java)
//                    val validado=snapshot.child("validado").getValue(Boolean::class.java)

                    var llave = snapshot.child("llave").getValue(String::class.java)


                    // Verifica si el personal coincide con el personal deseado
                    if (personal1 == personal) {
                        // Crea un objeto ClaseObra y agrégalo a la lista solo si el personal coincide
                        val obra = ClaseObraCilindros(

                            numReporte!!,
                            obra1.toString(),
                            cliente.toString(),
                            fecha.toString(),
                            personal1.toString(),
                            numReporte.toInt(),
                            tipoMuestreo.toString(),

                            elementoColado.toString(),
                            ubicacion.toString(),
                            fc!!,
                            volumenTotal!!,
                            tipoResistencia.toString(),
                            edad!!,
                            tma!!,
                            concretera.toString(),
                            proporciones.toString(),
                            aditivo.toString(),
                            remision.toString(),

                            muestra!!,
                            revenimientoDis!!,
                            revenimientoR1!!,
                            revenimientoR2!!,
                            temperatura!!,
                            molde1!!,
                            molde2!!,
                            molde3!!,
                            molde4!!,
                            estadoMolde1.toString(),
                            estadoMolde2.toString(),
                            estadoMolde3.toString(),
                            estadoMolde4.toString(),
                            horaSalida.toString(),
                            horaLLegada.toString(),
                            horaMuestreo.toString(),
                            observaciones.toString(),

                            carretilla.toString(),
                            cono.toString(),
                            varilla.toString(),
                            mazo.toString(),
                            termometro.toString(),
                            cucharon.toString(),
                            placa.toString(),
                            flexometro.toString(),
                            enrasador.toString(),
                            validado!!,

                            llave.toString()




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
            ObraAdapter.notifyDataSetChanged()
        } else {
            Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
        }
    }
    private fun deleteReport(reportKey: String) {
        val reportReference = dataReference.child(reportKey)
        val reportReferenceCampo = dataReferenceCampo.child(reportKey)

        reportReference.removeValue()
        reportReferenceCampo.removeValue()
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
        val intent = Intent(this, RegistroCilindros::class.java)
//        intent.putExtra("ReporteSeleccionado",listaObrasmutableListOf[position])
        startActivity(intent)

    }
}