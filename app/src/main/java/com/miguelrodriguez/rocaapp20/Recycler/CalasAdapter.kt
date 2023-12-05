package com.miguelrodriguez.rocaapp20.Recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miguelrodriguez.rocaapp20.R

class CalasAdapter(private val listaCala:List<ClaseCala>):RecyclerView.Adapter<CalasViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalasViewHolder {
        val view=
            LayoutInflater.from(parent.context).inflate(R.layout.item_cala_compactacion,parent,false)
        return CalasViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalasViewHolder, position: Int) {
        holder.render(listaCala[position])
//        holder.itemView.setOnClickListener { }
    }

    override fun getItemCount(): Int {
        return listaCala.size
    }
}