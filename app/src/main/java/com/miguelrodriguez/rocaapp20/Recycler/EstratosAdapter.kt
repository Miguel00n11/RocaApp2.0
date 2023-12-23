package com.miguelrodriguez.rocaapp20.Recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miguelrodriguez.rocaapp20.R

class EstratosAdapter(
    private val ListaEstratos:MutableList<ClaseEstratos>,
    private val onEstratoSelected: (Int) -> Unit,
    private val onItemDelete: (Int) -> Unit
):RecyclerView.Adapter<EstratosViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EstratosViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_estratos_muestreo,parent,false)
        return EstratosViewHolder(view)
    }

    override fun onBindViewHolder(holder: EstratosViewHolder, position: Int) {
        holder.render(ListaEstratos[position])
        holder.itemView.setOnClickListener { onEstratoSelected(position) }
        holder.bind(ListaEstratos[position], onEstratoSelected, onItemDelete)

    }

    override fun getItemCount(): Int {
        return ListaEstratos.size
    }


}