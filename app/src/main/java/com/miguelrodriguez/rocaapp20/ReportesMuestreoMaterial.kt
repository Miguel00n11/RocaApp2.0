package com.miguelrodriguez.rocaapp20

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import java.util.Calendar

class ReportesMuestreoMaterial : AppCompatActivity() {
    private lateinit var btnRegistroMuestreoMaterial:Button
    private lateinit var etFechaMuestreoMecanica:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes_muestreo_material)


        InitComponet()
        InitUI()
    }

    private fun InitComponet() {
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