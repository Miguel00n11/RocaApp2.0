package com.miguelrodriguez.rocaapp20.cilindros

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miguelrodriguez.rocaapp20.R
import com.miguelrodriguez.rocaapp20.Recycler.ClaseObra
import com.miguelrodriguez.rocaapp20.cilindros.ClaseObraCilindros
import com.miguelrodriguez.rocaapp20.cilindros.ObraAdapterCilindros

class ObraAdapterCilindros(
    private var listaObra: MutableList<ClaseObraCilindros>,
    private val onObraSelected: (Int) -> Unit,
    private val onItemDelete: (Int) -> Unit,
    private val onVerReporteCilindros: (Int) -> Unit,
    private var mostrarBoton: Boolean
) : RecyclerView.Adapter<ObraCilindrosViewHolder>() {
    private var listaObraCompleta: MutableList<ClaseObraCilindros> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObraCilindrosViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_obras_compactaciones, parent, false)
        // Guarda la lista completa de obras
        listaObraCompleta.addAll(listaObra)

        return ObraCilindrosViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObraCilindrosViewHolder, position: Int) {

        holder.render(listaObra[position])
        holder.itemView.setOnClickListener { onObraSelected(position) }
        holder.bind(
            listaObra[position],
            onObraSelected,
            onItemDelete,
            onVerReporteCilindros,
            mostrarBoton )// Pasa la variable mostrarBoton al método bind)


    }

    override fun getItemCount(): Int {
        return listaObra.size
    }
    fun setMostrarBoton(mostrar: Boolean) {
        mostrarBoton = mostrar
        notifyDataSetChanged()
    }
}