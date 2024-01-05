package com.miguelrodriguez.rocaapp20.Recycler

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.miguelrodriguez.rocaapp20.R

class EstratosViewHolder(view:View):RecyclerView.ViewHolder(view) {

    private val tvIdEstratos: TextView =view.findViewById(R.id.tvIdEstratos)
    private val tvNombreEstrato: TextView =view.findViewById(R.id.tvNombreEstrato)
    private val tvEspesorEstrato: TextView =view.findViewById(R.id.tvEspesorEstrato)
    private val btnEliminarEstrato: FloatingActionButton =
        view.findViewById(R.id.btnEliminarEstrato)

    fun render(listaEstratos: ClaseEstratos) {

        tvIdEstratos.text=listaEstratos.idEstrato.toString()
        tvNombreEstrato.text=listaEstratos.nombre
        tvEspesorEstrato.text=listaEstratos.espesor.toString()

    }
    fun bind(estrato: ClaseEstratos, onEstratosSelected: (Int) -> Unit, onItemDelete: (Int) -> Unit) {
        // Configura los elementos visuales con la información de la ClaseCala
        // ...

        // Configura el evento de clic en el elemento de lista (puedes mantenerlo si es necesario)
        itemView.setOnClickListener {
            onEstratosSelected(adapterPosition)
        }
        btnEliminarEstrato.setOnClickListener {
            onItemDelete(adapterPosition)
        }
//        itemView.findViewById<FloatingActionButton>(R.id.btnEliminarEstrato)
//            .setOnClickListener {
//                showDeleteConfirmationDialog(itemView.context) {
//                    // Llama a onItemDelete solo si el usuario hace clic en "Sí"
//                    if (it) {
//                        onItemDelete(position)
//                    }
//                }
//
//            }
    }
//    private fun showDeleteConfirmationDialog(context: Context, onConfirmation: (Boolean) -> Unit) {
//        val builder = AlertDialog.Builder(context)
//        builder.setTitle("Confirmar Eliminación")
//            .setMessage("¿Estás seguro de que deseas eliminar este elemento?")
//            .setPositiveButton("Sí") { _, _ ->
//                // Llama al manejador con true cuando el usuario hace clic en "Sí"
//                onConfirmation(true)
//            }
//            .setNegativeButton("No") { _, _ ->
//                // Llama al manejador con false cuando el usuario hace clic en "No"
//                onConfirmation(false)
//            }
//            .show()
//
//
//
//
//    }
}