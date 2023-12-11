package com.miguelrodriguez.rocaapp20

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Adapter
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.miguelrodriguez.rocaapp20.Recycler.ClaseCala
import com.miguelrodriguez.rocaapp20.Recycler.ClaseObra
import com.miguelrodriguez.rocaapp20.Recycler.ObraAdapter


class SeleccionarActividad : AppCompatActivity() {

    private lateinit var dataReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var btnRegistroCompactacion:Button
    private lateinit var rvObrasCompactacion:RecyclerView
    private lateinit var ObraAdapter:ObraAdapter
    private val listaObrasmutableListOf =
        mutableListOf(ClaseObra(1, "estacion"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccionar_actividad)



        initComponent()
        initUI()


    }

    private fun initUI() {
        btnRegistroCompactacion.setOnClickListener {
            val intent=Intent(this,RegistroCompactaciones::class.java)
            startActivity(intent)
        }

        ObraAdapter= ObraAdapter(listaObrasmutableListOf,
            onObraSelected = { position -> onItemSelected(position) })

        rvObrasCompactacion.layoutManager=LinearLayoutManager(this)
        rvObrasCompactacion.adapter=ObraAdapter
    }
    private fun onItemSelected(position: Int) {
//        Toast.makeText(this, position.toString(), Toast.LENGTH_SHORT).show()


    }
    private fun initComponent() {
        btnRegistroCompactacion=findViewById(R.id.btnRegistroCompactacion)
        rvObrasCompactacion=findViewById(R.id.rvObrasCompactacion)
    }
}