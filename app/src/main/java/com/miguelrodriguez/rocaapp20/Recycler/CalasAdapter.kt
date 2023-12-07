package com.miguelrodriguez.rocaapp20.Recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miguelrodriguez.rocaapp20.R

interface OnItemSwipeListener {
    fun onItemSwiped(position: Int, removedItem: ClaseCala)
}

class CalasAdapter(
    private val listaCala: MutableList<ClaseCala>,
    private val onCalaSelected: (Int) -> Unit,
    private val onItemSwipeListener: OnItemSwipeListener
) : RecyclerView.Adapter<CalasViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalasViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_cala_compactacion, parent, false)
        return CalasViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalasViewHolder, position: Int) {
        holder.render(listaCala[position])
        holder.itemView.setOnClickListener { onCalaSelected(position) }
    }

    override fun getItemCount(): Int {
        return listaCala.size
    }


    fun removeItem(position: Int) {
        // Elimina el elemento de la lista y notifica el cambio
        val removedItem = listaCala.removeAt(position)
        notifyItemRemoved(position)

        // Llama al método de eliminación
        onItemSwipeListener.onItemSwiped(position, removedItem)
    }
}