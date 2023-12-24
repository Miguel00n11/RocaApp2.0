package com.miguelrodriguez.rocaapp20.Recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miguelrodriguez.rocaapp20.R

class ObraMecanicaAdapter(

    private val listaObraMecanica: MutableList<ClaseObraMecanica>,
    private val onObraSelected: (Int) -> Unit,
    private val onItemDelete: (Int) -> Unit
):RecyclerView.Adapter<ObrasMecanicaViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObrasMecanicaViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_obras_mecanicas,parent,false)
        return ObrasMecanicaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObrasMecanicaViewHolder, position: Int) {
        holder.render(listaObraMecanica[position])
        holder.itemView.setOnClickListener { onObraSelected(position) }
        holder.bind(listaObraMecanica[position], onObraSelected, onItemDelete)
    }

    override fun getItemCount(): Int {
        return listaObraMecanica.size
    }
}