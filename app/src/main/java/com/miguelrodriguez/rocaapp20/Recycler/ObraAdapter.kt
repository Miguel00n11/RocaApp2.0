package com.miguelrodriguez.rocaapp20.Recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.miguelrodriguez.rocaapp20.R
import java.util.Locale

class ObraAdapter(
    private var listaObra: MutableList<ClaseObra>,
    private val onObraSelected: (Int) -> Unit,
    private val onItemDelete: (Int) -> Unit,
    private val onVerReporteCompactacion: (Int) -> Unit,
    private var mostrarBoton: Boolean = true
) : RecyclerView.Adapter<ObrasViewHolder>() {
    private var listaObraCompleta: MutableList<ClaseObra> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObrasViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_obras_compactaciones, parent, false)
        // Guarda la lista completa de obras
        listaObraCompleta.addAll(listaObra)
        return ObrasViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObrasViewHolder, position: Int) {

        holder.render(listaObra[position])
        holder.itemView.setOnClickListener { onObraSelected(position) }
        holder.bind(
            listaObra[position],
            onObraSelected,
            onItemDelete,
            onVerReporteCompactacion,
            mostrarBoton // Pasa la variable mostrarBoton al método bind
        )


    }

    override fun getItemCount(): Int {
        return listaObra.size
    }

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<ClaseObra>()
                val filterPattern = constraint.toString().toLowerCase(Locale.getDefault()).trim()

                if (filterPattern.isEmpty()) {
                    // Si el filtro está vacío, muestra la lista completa
                    filteredList.addAll(listaObraCompleta)
                } else {
                    // Filtra la lista de obras según el patrón de filtro
                    for (obra in listaObraCompleta) {
                        if (obra.Obra.toLowerCase(Locale.getDefault()).contains(filterPattern)) {
                            filteredList.add(obra)
                        }
                    }
                }

                val results = FilterResults()
                results.values = filteredList
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                listaObra.clear()
                listaObra.addAll(results?.values as MutableList<ClaseObra>)
                notifyDataSetChanged()
            }
        }
    }
    fun setMostrarBoton(mostrar: Boolean) {
        mostrarBoton = mostrar
        notifyDataSetChanged()
    }

}
