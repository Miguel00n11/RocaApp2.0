package com.miguelrodriguez.rocaapp20

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.miguelrodriguez.rocaapp20.MainActivity.Companion.NombreUsuarioCompanion
import android.widget.TextView

class Calculo_Compactacion : AppCompatActivity() {

    private lateinit var tvUsuarioCalculoCompactacion:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculo_compactacion)

        tvUsuarioCalculoCompactacion=findViewById(R.id.tvUsuarioCalculoCompactacion)
        val NombreUsuarioCompanion1:String=intent.extras!!.getString(NombreUsuarioCompanion).toString()


        tvUsuarioCalculoCompactacion.text=NombreUsuarioCompanion1


    }
}