package com.miguelrodriguez.rocaapp20.Recycler

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.miguelrodriguez.rocaapp20.R

class ObrasViewHolder(view:View):RecyclerView.ViewHolder(view) {

    private val tvItemObrasCompactacion:TextView=view.findViewById(R.id.tvItemObrasCompactacion)
    private val tvReporteCompactacion:TextView=view.findViewById(R.id.tvReporteCompactacion)
    private val tvCapaCompactacion:TextView=view.findViewById(R.id.tvCapaCompactacion)
    private val tvFechaCompactacion:TextView=view.findViewById(R.id.tvFechaCompactacion)
    fun render(listaObras: ClaseObra) {

        tvItemObrasCompactacion.text=listaObras.Obra
        tvReporteCompactacion.text=listaObras.reporte
        tvCapaCompactacion.text=listaObras.capa
        tvFechaCompactacion.text=listaObras.fecha

    }
    fun bind(obra: ClaseObra, onCalaSelected: (Int) -> Unit, onItemDelete: (Int) -> Unit) {
        // Configura los elementos visuales con la información de la ClaseCala
        // ...

        // Configura el evento de clic en el elemento de lista (puedes mantenerlo si es necesario)
        itemView.setOnClickListener {
            onCalaSelected(adapterPosition)
        }
    }

}