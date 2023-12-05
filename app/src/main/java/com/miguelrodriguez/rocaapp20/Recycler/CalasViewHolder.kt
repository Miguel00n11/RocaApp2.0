package com.miguelrodriguez.rocaapp20.Recycler

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.miguelrodriguez.rocaapp20.R

class CalasViewHolder(view:View):RecyclerView.ViewHolder (view) {

    private val tvIdCalaCompactacion:TextView=view.findViewById(R.id.tvIdCalaCompactacion)
    fun render(listaCalas: ClaseCala) {

        tvIdCalaCompactacion.text=listaCalas.Estacion



    }

}