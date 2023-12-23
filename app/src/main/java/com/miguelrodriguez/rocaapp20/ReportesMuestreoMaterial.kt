package com.miguelrodriguez.rocaapp20

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.miguelrodriguez.rocaapp20.Recycler.ClaseCala
import com.miguelrodriguez.rocaapp20.Recycler.ClaseEstratos
import com.miguelrodriguez.rocaapp20.Recycler.ClaseObra
import com.miguelrodriguez.rocaapp20.Recycler.ClaseObraMecanica
import java.util.Calendar

class ReportesMuestreoMaterial : AppCompatActivity() {
    private lateinit var btnRegistroMuestreoMaterial:Button
    private lateinit var etFechaMuestreoMecanica:Button
    private lateinit var listaObrasmutableListOf:MutableList<ClaseObraMecanica>
    private lateinit var listaEstratossmutableListOf: MutableList<ClaseEstratos>

    companion object {
        lateinit var reporteSelecionadoMuestroMaterial: ClaseObraMecanica
        var editarMuestreoMaterial: Boolean = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes_muestreo_material)


        InitComponet()
        InitUI()
    }

    private fun InitComponet() {
        listaEstratossmutableListOf= mutableListOf(ClaseEstratos(1,"h",1.0))
        reporteSelecionadoMuestroMaterial = ClaseObraMecanica(
            1, "estacion", "1", "1", "1",
            "1", "1", "1", "1", "1","ho111la",
            "tipo","estudio", listaEstratossmutableListOf
        )
        btnRegistroMuestreoMaterial=findViewById(R.id.btnRegistroMuestreoMaterial)
//        etFechaMuestreoMecanica=findViewById(R.id.etFechaMuestreoMecanica)
    }

    private fun InitUI() {
        btnRegistroMuestreoMaterial.setOnClickListener {
            val intent=Intent(this,RegistroMecanica::class.java)
            startActivity(intent)
        }
    }


}