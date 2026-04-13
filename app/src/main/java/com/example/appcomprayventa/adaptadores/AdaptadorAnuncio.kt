package com.example.appcomprayventa.adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appcomprayventa.R
import com.example.appcomprayventa.modelos.ModeloAnuncio
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdaptadorAnuncio(
    private val context: Context,
    private val anunciosArrayList: ArrayList<ModeloAnuncio>
) : RecyclerView.Adapter<AdaptadorAnuncio.HolderAnuncio>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderAnuncio {
        val view = LayoutInflater.from(context).inflate(R.layout.item_anuncio, parent, false)
        return HolderAnuncio(view)
    }

    override fun onBindViewHolder(holder: HolderAnuncio, position: Int) {
        val modelo = anunciosArrayList[position]

        val titulo = modelo.titulo ?: "Sin título"
        val precio = modelo.precio ?: "0.00"
        val condicion = modelo.condicion ?: ""
        val categoria = modelo.categoria ?: ""
        val estado = modelo.estado ?: ""

        holder.tvTitulo.text = titulo
        holder.tvPrecio.text = "$$precio"
        holder.tvCondicion.text = "$condicion • $categoria"
        holder.tvEstado.text = estado

        if (estado == "Disponible") {
            holder.tvEstado.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
        } else {
            holder.tvEstado.setBackgroundColor(android.graphics.Color.parseColor("#F44336"))
        }

        cargarPrimeraImagen(modelo, holder)

        holder.itemView.setOnClickListener {
            val idAnuncio = modelo.idAnuncio ?: ""
            if (idAnuncio.isNotEmpty()) {
                val intent = android.content.Intent(context, com.example.appcomprayventa.anuncios.DetalleAnuncio::class.java)
                intent.putExtra("idAnuncio", idAnuncio)
                context.startActivity(intent)
            }
        }
    }

    private fun cargarPrimeraImagen(modelo: ModeloAnuncio, holder: HolderAnuncio) {
        val idAnuncio = modelo.idAnuncio ?: ""
        if (idAnuncio.isEmpty() || idAnuncio == "null") {
            return
        }

        val ref = FirebaseDatabase.getInstance().getReference("Anuncios").child(idAnuncio).child("Imagenes")

        ref.limitToFirst(1).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (ds in snapshot.children) {
                        val imageUrl = "${ds.child("imageUrl").value}"
                        try {
                            Glide.with(context)
                                .load(imageUrl)
                                .placeholder(R.drawable.item_imagen)
                                .into(holder.imgAnuncio)
                        } catch (e: Exception) {
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun getItemCount(): Int {
        return anunciosArrayList.size
    }

    inner class HolderAnuncio(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgAnuncio: ShapeableImageView = itemView.findViewById(R.id.imgAnuncio)
        var tvTitulo: TextView = itemView.findViewById(R.id.tvTitulo)
        var tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        var tvCondicion: TextView = itemView.findViewById(R.id.tvCondicion)
        var tvEstado: TextView = itemView.findViewById(R.id.tvEstado)
    }
}