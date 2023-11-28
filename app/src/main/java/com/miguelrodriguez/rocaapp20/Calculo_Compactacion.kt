package com.miguelrodriguez.rocaapp20

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.miguelrodriguez.rocaapp20.MainActivity.Companion.NombreUsuarioCompanion
import android.widget.TextView
import androidx.core.widget.addTextChangedListener

class Calculo_Compactacion : AppCompatActivity() {

    private lateinit var tvUsuarioCalculoCompactacion: TextView
    private lateinit var btnRegresar: Button
    private lateinit var tvMSVM:TextView
    private lateinit var etMSVM:EditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculo_compactacion)


        initComponet()
        initUI()





    }

    private fun actualizarTexto() {
        var mvsm = etMSVM.text.toString().toDouble()
        tvMSVM.text= (mvsm*1.55).toString()
    }

    private fun initComponet() {



        btnRegresar=findViewById(R.id.btnRegresarCompactacion)
        tvUsuarioCalculoCompactacion = findViewById(R.id.tvUsuarioCalculoCompactacion)
        tvMSVM=findViewById(R.id.tvMVSM)
        etMSVM=findViewById(R.id.etMVSM)


        tvUsuarioCalculoCompactacion.text ="Bienvenido, " + NombreUsuarioCompanion.toString()
    }

    private fun initUI() {



        btnRegresar.setOnClickListener { onBackPressed() }
        etMSVM.addTextChangedListener { actualizarTexto() }
    }
}