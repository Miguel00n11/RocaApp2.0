package com.miguelrodriguez.rocaapp20.Recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miguelrodriguez.rocaapp20.R

class ObraAdapter(
    private val listaObra: MutableList<ClaseObra>,
    private val onObraSelected: (Int) -> Unit,
    private val onItemDelete: (Int) -> Unit,
    private val onVerReporteCompactacion: (Int) -> Unit
) : RecyclerView.Adapter<ObrasViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObrasViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_obras_compactaciones,parent,false)
        return ObrasViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObrasViewHolder, position: Int) {

        holder.render(listaObra[position])
        holder.itemView.setOnClickListener { onObraSelected(position) }
        holder.bind(listaObra[position], onObraSelected, onItemDelete,onVerReporteCompactacion)


    }

    override fun getItemCount(): Int {
        return listaObra.size
    }
}