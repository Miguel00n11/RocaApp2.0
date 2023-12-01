package com.miguelrodriguez.rocaapp20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class SeleccionarActividad : AppCompatActivity() {

    private lateinit var btnRegistroCompactacion:Button
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
    }

    private fun initComponent() {
        btnRegistroCompactacion=findViewById(R.id.btnRegistroCompactacion)
    }
}