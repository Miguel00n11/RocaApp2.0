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
        "EXAMEN 156",
        "EXAMEN 17025 GESTIÓN",
        "EXAMEN DE LA 083",
        "EXAMEN DE LA 109",
        "EXAMEN DE LA 159",
        "EXAMEN DE LA 161",
        "EXAMEN DE LA 191",
        "EXAMEN LIC Y NOM-008-SE-2021"
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
            } else if (examenSeleccionado == "EXAMEN LIC Y NOM-008-SE-2021") {
                val intent = Intent(this, ExamenLicNom008Activity::class.java)
                startActivity(intent)
            } else if (examenSeleccionado == "EXAMEN 156") {
                val intent = Intent(this, ExamenNorma156Activity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Seleccionaste: $examenSeleccionado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
