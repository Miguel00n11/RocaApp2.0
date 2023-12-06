package com.miguelrodriguez.rocaapp20.Recycler

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.miguelrodriguez.rocaapp20.R

class CalasViewHolder(view:View):RecyclerView.ViewHolder (view) {

    private val tvIdCalaCompactacion:TextView=view.findViewById(R.id.tvIdCalaCompactacion)
    private val tvEstacionCalaCompactacion:TextView=view.findViewById(R.id.tvEstacionCalaCompactacion)
    private val tvProfundidadCalaCompactacion:TextView=view.findViewById(R.id.tvProfundidadCalaCompactacion)
    private val tvMVSLCalaCompactacion:TextView=view.findViewById(R.id.tvMVSLCalaCompactacion)
    private val tvHumedadCalaCompactacion:TextView=view.findViewById(R.id.tvHumedadCalaCompactacion)
    private val tvPorcentajeCalaCompactacion:TextView=view.findViewById(R.id.tvPorcentajeCalaCompactacion)
    fun render(listaCalas: ClaseCala) {

        tvIdCalaCompactacion.text=listaCalas.id.toString()
        tvEstacionCalaCompactacion.text=listaCalas.Estacion
        tvProfundidadCalaCompactacion.text=listaCalas.Profundidad.toString()
        tvMVSLCalaCompactacion.text=listaCalas.MVSL.toString()
        tvHumedadCalaCompactacion.text=listaCalas.Humedad.toString()
        tvPorcentajeCalaCompactacion.text=listaCalas.Porcentaje.toString()+"%"




    }

}