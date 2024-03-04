package com.miguelrodriguez.rocaapp20.vigas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miguelrodriguez.rocaapp20.R
import com.miguelrodriguez.rocaapp20.vigas.ClaseObraVigas
import com.miguelrodriguez.rocaapp20.vigas.ObraVigasViewHolder

class ObraAdapterVigas (
    private var listaObra: MutableList<ClaseObraVigas>,
    private val onObraSelected: (Int) -> Unit,
    private val onItemDelete: (Int) -> Unit,
    private val onVerReporteVigas: (Int) -> Unit,
    private var mostrarBoton: Boolean
) : RecyclerView.Adapter<ObraVigasViewHolder>() {
    private var listaObraCompleta: MutableList<ClaseObraVigas> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObraVigasViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_obras_compactaciones, parent, false)
        // Guarda la lista completa de obras
        listaObraCompleta.addAll(listaObra)

        return ObraVigasViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObraVigasViewHolder, position: Int) {

        holder.render(listaObra[position])
        holder.itemView.setOnClickListener { onObraSelected(position) }
        holder.bind(
            listaObra[position],
            onObraSelected,
            onItemDelete,
            onVerReporteVigas,
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