package com.example.appcomprayventa.adaptadores
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appcomprayventa.R
import com.example.appcomprayventa.modelos.Usuario

class AdaptadorUsuario (contexto: Context, listaUsuarios: List<Usuario>)
    : RecyclerView.Adapter<AdaptadorUsuario.ViewHolder?>() {

    private val context: Context

    private val listaUsuarios: List<Usuario>

    init {
        this.context = contexto
        this.listaUsuarios = listaUsuarios
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdaptadorUsuario.ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_usuario, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listaUsuarios.size
    }

    override fun onBindViewHolder(holder: AdaptadorUsuario.ViewHolder, position: Int) {
        val usuario: Usuario = listaUsuarios[position]
        holder.uid.text = usuario.uid
        holder.nombre.text = usuario.nombres
        holder.email.text = usuario.email
        Glide.with(context).load(usuario.imagen).placeholder(R.drawable.img_perfil).into(holder.imagen)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var uid: TextView
        var nombre: TextView
        var email: TextView
        var imagen: ImageView

        init {
            uid = itemView.findViewById(R.id.item_uid)
            nombre = itemView.findViewById(R.id.item_nombre)
            email = itemView.findViewById(R.id.item_email)
            imagen = itemView.findViewById(R.id.item_imagen)
        }
    }
}