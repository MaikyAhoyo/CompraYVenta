package com.example.appcomprayventa.adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appcomprayventa.R

class AdaptadorImagenCarrusel(
    private val context: Context,
    private val imagenesArrayList: ArrayList<String>
) : RecyclerView.Adapter<AdaptadorImagenCarrusel.HolderImagen>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImagen {
        val view = LayoutInflater.from(context).inflate(R.layout.item_imagen_carrusel, parent, false)
        return HolderImagen(view)
    }

    override fun onBindViewHolder(holder: HolderImagen, position: Int) {
        val modeloImageUrl = imagenesArrayList[position]

        try {
            Glide.with(context)
                .load(modeloImageUrl)
                .placeholder(R.drawable.item_imagen)
                .into(holder.imgCarrusel)
        } catch (e: Exception) {
        }
    }

    override fun getItemCount(): Int {
        return imagenesArrayList.size
    }

    inner class HolderImagen(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgCarrusel: ImageView = itemView.findViewById(R.id.imgCarrusel)
    }
}