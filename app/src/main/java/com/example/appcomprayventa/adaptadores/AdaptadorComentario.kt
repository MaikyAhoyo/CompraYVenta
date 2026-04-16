package com.example.appcomprayventa.adaptadores

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appcomprayventa.R
import com.example.appcomprayventa.modelos.ModeloComentario
import com.example.appcomprayventa.modelos.ModeloRespuesta
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdaptadorComentario(
    private val context: Context,
    private val comentarioArrayList: ArrayList<ModeloComentario>,
    private val idAnuncio: String
) : RecyclerView.Adapter<AdaptadorComentario.HolderComentario>() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderComentario {
        val view = LayoutInflater.from(context).inflate(R.layout.item_comentario, parent, false)
        return HolderComentario(view)
    }

    override fun onBindViewHolder(holder: HolderComentario, position: Int) {
        val modelo = comentarioArrayList[position]

        holder.tvTextoComentario.text = modelo.comentario

        cargarInfoUsuario(modelo, holder)
        cargarRespuestas(modelo, holder)

        holder.btnResponder.setOnClickListener {
            dialogoResponder(modelo.idComentario)
        }
    }

    private fun cargarInfoUsuario(modelo: ModeloComentario, holder: HolderComentario) {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(modelo.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                holder.tvNombreComentario.text = "${snapshot.child("nombres").value}"
                val img = "${snapshot.child("urlImagenPerfil").value}"
                try {
                    Glide.with(context).load(img).placeholder(R.drawable.ic_imagen_perfil).into(holder.imgPerfilComentario)
                } catch (e: Exception) {}
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun cargarRespuestas(modelo: ModeloComentario, holder: HolderComentario) {
        val respuestasList = ArrayList<ModeloRespuesta>()
        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
            .child(idAnuncio).child("Comentarios").child(modelo.idComentario).child("Respuestas")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                respuestasList.clear()
                for (ds in snapshot.children) {
                    val modeloResp = ds.getValue(ModeloRespuesta::class.java)
                    if (modeloResp != null) respuestasList.add(modeloResp)
                }
                val adaptador = AdaptadorRespuesta(context, respuestasList)
                holder.rvRespuestas.layoutManager = LinearLayoutManager(context)
                holder.rvRespuestas.adapter = adaptador
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun dialogoResponder(idComentario: String) {
        val miUid = firebaseAuth.uid
        if (miUid == null) {
            Toast.makeText(context, "Inicia sesión para responder", Toast.LENGTH_SHORT).show()
            return
        }

        val view = LayoutInflater.from(context).inflate(R.layout.dialogo_responder, null)
        val etRespuesta = view.findViewById<EditText>(R.id.etRespuesta)

        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setTitle("Responder")
        builder.setPositiveButton("Enviar") { dialog, _ ->
            val respuestaTexto = etRespuesta.text.toString().trim()
            if (respuestaTexto.isNotEmpty()) {
                guardarRespuesta(idComentario, miUid, respuestaTexto)
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun guardarRespuesta(idComentario: String, miUid: String, respuesta: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
            .child(idAnuncio).child("Comentarios").child(idComentario).child("Respuestas")

        val idRespuesta = ref.push().key ?: return

        val hashMap = HashMap<String, Any>()
        hashMap["idRespuesta"] = idRespuesta
        hashMap["idComentario"] = idComentario
        hashMap["uid"] = miUid
        hashMap["respuesta"] = respuesta
        hashMap["timestamp"] = System.currentTimeMillis().toString()

        ref.child(idRespuesta).setValue(hashMap)
            .addOnSuccessListener { Toast.makeText(context, "Respuesta enviada", Toast.LENGTH_SHORT).show() }
    }

    override fun getItemCount() = comentarioArrayList.size

    inner class HolderComentario(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPerfilComentario: ShapeableImageView = itemView.findViewById(R.id.imgPerfilComentario)
        val tvNombreComentario: TextView = itemView.findViewById(R.id.tvNombreComentario)
        val tvTextoComentario: TextView = itemView.findViewById(R.id.tvTextoComentario)
        val btnResponder: TextView = itemView.findViewById(R.id.btnResponder)
        val rvRespuestas: RecyclerView = itemView.findViewById(R.id.rvRespuestas)
    }
}