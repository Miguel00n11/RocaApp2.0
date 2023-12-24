package com.miguelrodriguez.rocaapp20.Recycler

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.miguelrodriguez.rocaapp20.R

class ObrasMecanicaViewHolder(view:View):RecyclerView.ViewHolder(view) {
    private val tvItemObrasCompactacion: TextView =view.findViewById(R.id.tvItemObrasMecanica)
    private val tvReporteCompactacion: TextView =view.findViewById(R.id.tvItemReporteMecanica)
    private val tvCapaCompactacion: TextView =view.findViewById(R.id.tvItemCapaMecanica)
    private val tvFechaCompactacion: TextView =view.findViewById(R.id.tvItemFechaMecanica)
    private val btnEliminar: FloatingActionButton =
        view.findViewById(R.id.btnEliminarReporteMecanica)

    fun render(listaObrasMecancia: ClaseObraMecanica) {

        tvItemObrasCompactacion.text=listaObrasMecancia.Obra
        tvReporteCompactacion.text=listaObrasMecancia.reporte
        tvCapaCompactacion.text=listaObrasMecancia.capa
        tvFechaCompactacion.text=listaObrasMecancia.fecha

    }
    fun bind(obraMecanica: ClaseObraMecanica, onCalaSelected: (Int) -> Unit, onItemDelete: (Int) -> Unit) {
        // Configura los elementos visuales con la información de la ClaseCala
        // ...

        // Configura el evento de clic en el elemento de lista (puedes mantenerlo si es necesario)
        itemView.setOnClickListener {
            onCalaSelected(adapterPosition)
        }
        btnEliminar.setOnClickListener {
            onItemDelete(adapterPosition)
        }
        itemView.findViewById<FloatingActionButton>(R.id.btnEliminarReporteMecanica)
            .setOnClickListener {
                showDeleteConfirmationDialog(itemView.context) {
                    // Llama a onItemDelete solo si el usuario hace clic en "Sí"
                    if (it) {
                        onItemDelete(position)
                    }
                }

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