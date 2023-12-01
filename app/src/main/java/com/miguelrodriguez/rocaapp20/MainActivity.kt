package com.miguelrodriguez.rocaapp20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    private lateinit var btnAcceder:Button
    private lateinit var NombreUsuario:EditText


   companion object{
       var NombreUsuarioCompanion="NombreUsuario"
   }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initComponent()

        initUI()


    }

    private fun initUI() {

        btnAcceder.setOnClickListener { abrirCalculo_Compactacion() }
    }

//    private fun abrirCalculo_Compactacion(NombreUsuario:String) {
//        val intent=Intent(this,Calculo_Compactacion::class.java )
//        intent.putExtra(NombreUsuarioCompanion,NombreUsuario)
//        startActivity(intent)
//    }

    private fun abrirCalculo_Compactacion() {
        val intent=Intent(this,SeleccionarActividad::class.java )
        NombreUsuarioCompanion=NombreUsuario.text.toString()
//        intent.putExtra(NombreUsuarioCompanion,NombreUsuario)
        startActivity(intent)
    }

    private fun initComponent() {
        btnAcceder=findViewById(R.id.btnAcceder)
        NombreUsuario=findViewById(R.id.etEmail)

    }
}