package com.miguelrodriguez.rocaapp20.Recycler

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.miguelrodriguez.rocaapp20.R

class ObrasViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val tvItemObrasCompactacion: TextView = view.findViewById(R.id.tvItemObrasCompactacion)
    private val tvReporteCompactacion: TextView = view.findViewById(R.id.tvReporteCompactacion)
    private val tvCapaCompactacion: TextView = view.findViewById(R.id.tvCapaCompactacion)
    private val tvFechaCompactacion: TextView = view.findViewById(R.id.tvFechaCompactacion)
    private val btnEliminar: FloatingActionButton =
        view.findViewById(R.id.btnEliminarReporteCompactacion)
    private val btnVerReporteCompactacion: FloatingActionButton =
        view.findViewById(R.id.btnVerReporteCompactacion)

    fun render(listaObras: ClaseObra) {

        tvItemObrasCompactacion.text = listaObras.Obra
        tvReporteCompactacion.text = listaObras.reporte
        tvCapaCompactacion.text = listaObras.capa
        tvFechaCompactacion.text = listaObras.fecha


    }

    fun bind(
        obra: ClaseObra,
        onCalaSelected: (Int) -> Unit,
        onItemDelete: (Int) -> Unit,
        onIntemVerReporteCompactacion: (Int) -> Unit,
        mostrarBotones: Boolean
    ) {
        // Configura los elementos visuales con la información de la ClaseCala
        // ...

        // Configura el evento de clic en el elemento de lista (puedes mantenerlo si es necesario)
        itemView.setOnClickListener {
            onCalaSelected(adapterPosition)
        }
        btnEliminar.setOnClickListener {
            onItemDelete(adapterPosition)
        }
        btnVerReporteCompactacion.setOnClickListener {
            onIntemVerReporteCompactacion(adapterPosition)
        }
        itemView.findViewById<FloatingActionButton>(R.id.btnEliminarReporteCompactacion)
            .setOnClickListener {
                showDeleteConfirmationDialog(itemView.context) {
                    // Llama a onItemDelete solo si el usuario hace clic en "Sí"
                    if (it) {
                        onItemDelete(position)
                    }
                }

            }
        if (!mostrarBotones) {
            btnEliminar.visibility = View.VISIBLE
            btnVerReporteCompactacion.visibility = View.VISIBLE
        } else {
            btnEliminar.visibility = View.GONE
            btnVerReporteCompactacion.visibility = View.GONE


        }
        println(mostrarBotones.toString())
//        btnEliminar.visibility = if (mostrarBotones) View.VISIBLE else View.GONE
//        btnVerReporteCompactacion.visibility = if (mostrarBotones) View.VISIBLE else View.GONE

    }



    private fun showDeleteConfirmationDialog(context: Context, onConfirmation: (Boolean) -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar este elemento?")
            .setPositiveButton("Sí") { _, _ ->
                // Llama al manejador con true cuando el usuario hace clic en "Sí"
                onConfirmation(true)
            }
            .setNegativeButton("No") { _, _ ->
                // Llama al manejador con false cuando el usuario hace clic en "No"
                onConfirmation(false)
            }
            .show()
    }
}