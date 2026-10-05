package com.miguelrodriguez.rocaapp20

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CapacitacionesActivity : AppCompatActivity() {

    private lateinit var listaExamenes: ListView

    private val examenes = arrayListOf(
        "EXAMEN 156 hugo",
        "EXAMEN 156",
        "EXAMEN 159 hugo",
        "EXAMEN 17025 GESTIÓN",
        "EXAMEN DE LA 083",
        "EXAMEN DE LA 109",
        "EXAMEN DE LA 159",
        "EXAMEN DE LA 161",
        "EXAMEN DE LA 191",
        "EXAMEN NOM 008 SE"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capacitaciones)

        listaExamenes = findViewById(R.id.listaExamenes)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            examenes
        )

        listaExamenes.adapter = adapter

        listaExamenes.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val examenSeleccionado = examenes[position]

            if (examenSeleccionado == "EXAMEN 17025 GESTIÓN") {
                val intent = Intent(this, Examen17025GestionActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Seleccionaste: $examenSeleccionado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
