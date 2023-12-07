package com.miguelrodriguez.rocaapp20.Recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miguelrodriguez.rocaapp20.R

class CalasAdapter(
    private val listaCala:MutableList<ClaseCala>,
    private val onCalaSelected:(Int)->Unit,
    private val onItemDelete: (Int) -> Unit
):RecyclerView.Adapter<CalasViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalasViewHolder {
        val view=
            LayoutInflater.from(parent.context).inflate(R.layout.item_cala_compactacion,parent,false)
        return CalasViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalasViewHolder, position: Int) {
        holder.render(listaCala[position])
        holder.itemView.setOnClickListener {onCalaSelected(position) }
        holder.bind(listaCala[position], onCalaSelected, onItemDelete)
    }

    override fun getItemCount(): Int {
        return listaCala.size
    }
    fun removeItem(position: Int) {
        val removedItem = listaCala.removeAt(position)
        notifyItemRemoved(position)
        // Puedes agregar la lógica adicional aquí si es necesario
    }
}