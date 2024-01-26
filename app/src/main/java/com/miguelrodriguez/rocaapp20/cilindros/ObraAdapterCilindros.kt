package com.miguelrodriguez.rocaapp20.cilindros

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miguelrodriguez.rocaapp20.R
import com.miguelrodriguez.rocaapp20.cilindros.ClaseObraCilindros
import com.miguelrodriguez.rocaapp20.cilindros.ObraAdapterCilindros

class ObraAdapterCilindros(
    private val listaObra: MutableList<ClaseObraCilindros>,
    private val onObraSelected: (Int) -> Unit,
    private val onItemDelete: (Int) -> Unit
) : RecyclerView.Adapter<ObraCilindrosViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObraCilindrosViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_obras_compactaciones,parent,false)
        return ObraCilindrosViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObraCilindrosViewHolder, position: Int) {

        holder.render(listaObra[position])
        holder.itemView.setOnClickListener { onObraSelected(position) }
        holder.bind(listaObra[position], onObraSelected, onItemDelete)


    }

    override fun getItemCount(): Int {
        return listaObra.size
    }
}