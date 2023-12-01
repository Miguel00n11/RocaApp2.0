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
    private lateinit var tvMVSM:TextView

    private lateinit var etMVSM:EditText
    private lateinit var etPesoMaterial:EditText
    private lateinit var etPesoArena:EditText
    private lateinit var etMVA:EditText
    private lateinit var etwHumedad:EditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculo_compactacion)


        initComponet()
        initUI()





    }

    private fun initComponet() {



        btnRegresar=findViewById(R.id.btnRegresarCompactacion)
        tvUsuarioCalculoCompactacion = findViewById(R.id.tvUsuarioCalculoCompactacion)
        tvMVSM=findViewById(R.id.tvMVSM)

        etMVSM=findViewById(R.id.etMVSM)
        etMVA=findViewById(R.id.etMVA)
        etPesoMaterial=findViewById(R.id.etPesoMaterial)
        etPesoArena=findViewById(R.id.etPesoArena)


        tvUsuarioCalculoCompactacion.text ="Bienvenido, " + NombreUsuarioCompanion.toString()
    }

    private fun initUI() {



        btnRegresar.setOnClickListener { onBackPressed() }
        etMVSM.addTextChangedListener { CacularCala() }
        etPesoMaterial.addTextChangedListener { CacularCala() }
        etPesoArena.addTextChangedListener { CacularCala() }

    }

    private fun CacularCala() {


        try {
            var etMSVM:Double = etMVSM.text.toString().toDouble()
            var etPesoArena:Double = etPesoArena.text.toString().toDouble()
            var etMVA:Double = etMVA.text.toString().toDouble()
            var etMVSM:Double = etMVSM.text.toString().toDouble()
            var etPesoMaterial = etPesoMaterial.text.toString().toDouble()

            val VolVarena=etPesoArena/etMVA
            val MVSM=etMVSM

//            tvMSVM.text=

        } finally {
            return
        }


    }
}