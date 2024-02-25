package com.miguelrodriguez.rocaapp20.cilindros

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.miguelrodriguez.rocaapp20.R

class ObraCilindrosViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val tvItemObrasCompactacion: TextView = view.findViewById(R.id.tvItemObrasCompactacion)
    private val tvReporteCompactacion: TextView = view.findViewById(R.id.tvReporteCompactacion)
    private val tvCapaCompactacion: TextView = view.findViewById(R.id.tvCapaCompactacion)
    private val tvFechaCompactacion: TextView = view.findViewById(R.id.tvFechaCompactacion)
    private val btnEliminar: FloatingActionButton =
        view.findViewById(R.id.btnEliminarReporteCompactacion)
    private val btnVerReporteCilindros: FloatingActionButton =
        view.findViewById(R.id.btnVerReporteCompactacion)

    fun render(listaObras: ClaseObraCilindros) {

        tvItemObrasCompactacion.text = listaObras.Obra
        tvReporteCompactacion.text = listaObras.muestra.toString()
        tvCapaCompactacion.text = listaObras.elementoColado
        tvFechaCompactacion.text = listaObras.fecha

    }

    fun bind(
        obra: ClaseObraCilindros,
        onCilindroSelected: (Int) -> Unit,
        onItemDelete: (Int) -> Unit,
        onIntemVerReporteCilindros: (Int) -> Unit,
        mostrarBotones: Boolean
    ) {
        // Configura los elementos visuales con la información de la ClaseCala
        // ...

        // Configura el evento de clic en el elemento de lista (puedes mantenerlo si es necesario)
        itemView.setOnClickListener {
            onCilindroSelected(adapterPosition)
        }
        btnEliminar.setOnClickListener {
            onItemDelete(adapterPosition)
        }
        btnVerReporteCilindros.setOnClickListener {
            onIntemVerReporteCilindros(adapterPosition)
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
//            btnVerReporteCompactacion.visibility = View.VISIBLE
        } else {
            btnEliminar.visibility = View.GONE
//            btnVerReporteCompactacion.visibility = View.GONE


        }
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