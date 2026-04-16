package com.example.appcomprayventa.adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appcomprayventa.R
import com.example.appcomprayventa.modelos.ModeloRespuesta
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdaptadorRespuesta(private val context: Context, private val respuestaList: ArrayList<ModeloRespuesta>) :
    RecyclerView.Adapter<AdaptadorRespuesta.HolderRespuesta>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderRespuesta {
        val view = LayoutInflater.from(context).inflate(R.layout.item_comentario, parent, false)
        return HolderRespuesta(view)
    }

    override fun onBindViewHolder(holder: HolderRespuesta, position: Int) {
        val modelo = respuestaList[position]
        holder.tvTexto.text = modelo.respuesta

        holder.btnResp.visibility = View.GONE

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(modelo.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                holder.tvNombre.text = "${snapshot.child("nombres").value}"
                val img = "${snapshot.child("urlImagenPerfil").value}"
                Glide.with(context).load(img).placeholder(R.drawable.ic_imagen_perfil).into(holder.imgP)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun getItemCount() = respuestaList.size

    inner class HolderRespuesta(v: View) : RecyclerView.ViewHolder(v) {
        val tvNombre = v.findViewById<TextView>(R.id.tvNombreComentario)
        val tvTexto = v.findViewById<TextView>(R.id.tvTextoComentario)
        val imgP = v.findViewById<ShapeableImageView>(R.id.imgPerfilComentario)
        val btnResp = v.findViewById<TextView>(R.id.btnResponder)
    }
}