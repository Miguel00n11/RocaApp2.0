package com.miguelrodriguez.rocaapp20.Recycler.Imagenes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.miguelrodriguez.rocaapp20.R

class ImageAdapter(private val imageList: List<String>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imagePath = imageList[position]

        // Cargar la imagen con Glide
        Glide.with(holder.itemView)
            .load(imagePath)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}
