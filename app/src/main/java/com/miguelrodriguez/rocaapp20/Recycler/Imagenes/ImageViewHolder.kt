package com.miguelrodriguez.rocaapp20.Recycler.Imagenes

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.miguelrodriguez.rocaapp20.R

class ImageViewHolder(itemView: View, private val onImageDelete: (Int) -> Unit) :
    RecyclerView.ViewHolder(itemView) {

    val imageView: ImageView = itemView.findViewById(R.id.imageView)
    val deleteButton: ImageView = itemView.findViewById(R.id.btnEliminarImagen)

    init {
        // Manejar clics en el botón de eliminación
        deleteButton.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onImageDelete(position)
            }
        }
    }
}
