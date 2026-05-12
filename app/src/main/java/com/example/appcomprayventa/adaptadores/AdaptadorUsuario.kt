package com.example.appcomprayventa.adaptadores
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appcomprayventa.R
import com.example.appcomprayventa.chat.ChatActivity
import com.example.appcomprayventa.modelos.Usuario

class AdaptadorUsuario(
    private val contexto: Context,
    private val listaUsuarios: List<Usuario>
) : RecyclerView.Adapter<AdaptadorUsuario.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(contexto).inflate(R.layout.item_usuario, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listaUsuarios.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val usuario = listaUsuarios[position]
        holder.uid.text = usuario.uid
        holder.nombre.text = usuario.nombres
        holder.email.text = usuario.email

        Glide.with(contexto)
            .load(usuario.imagen)
            .placeholder(R.drawable.ic_imagen_perfil)
            .into(holder.imagen)

        holder.itemView.setOnClickListener {
            val intent = Intent(contexto, ChatActivity::class.java)
            intent.putExtra("uid", holder.uid.text)
            Toast.makeText(contexto,
                "Has seleccionado al usuario: ${holder.nombre.text}",
                Toast.LENGTH_SHORT).show()
            contexto.startActivity(intent)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var uid: TextView = itemView.findViewById(R.id.item_uid)
        var nombre: TextView = itemView.findViewById(R.id.item_nombre)
        var email: TextView = itemView.findViewById(R.id.item_email)
        var imagen: ImageView = itemView.findViewById(R.id.item_imagen)
    }
}