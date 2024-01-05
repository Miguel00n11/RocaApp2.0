package com.miguelrodriguez.rocaapp20.cilindros

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.miguelrodriguez.rocaapp20.MainActivity
import com.miguelrodriguez.rocaapp20.R
import com.miguelrodriguez.rocaapp20.Recycler.ClaseCala
import com.miguelrodriguez.rocaapp20.Recycler.ClaseObra
import com.miguelrodriguez.rocaapp20.Recycler.ObraAdapter
import com.miguelrodriguez.rocaapp20.ReportesCompactaciones


class ReporteCilindros : AppCompatActivity() {

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporte_cilindros)

        initComponent()
        initUI()
    }

    private fun initComponent() {

        listacalasmutableListOf = mutableListOf(ClaseCala(1, "1", 1.1, 1.1, 1.1, 1.1))

        listaObrasmutableListOf = mutableListOf(
        )

        listaObrasmutableListOf.clear()
        personal = MainActivity.NombreUsuarioCompanion



        ReportesCompactaciones.reporteSelecionado = ClaseObra(
            1, "estacion", "1", "1", "1",
            "1", "1", "1", "1", "1","hola", listacalasmutableListOf
        )
    }

    private fun initUI() {
        TODO("Not yet implemented")
    }
}