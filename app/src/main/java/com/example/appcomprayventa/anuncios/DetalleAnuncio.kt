package com.example.appcomprayventa.anuncios

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.appcomprayventa.R
import com.example.appcomprayventa.databinding.ActivityDetalleAnuncioBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appcomprayventa.adaptadores.AdaptadorComentario
import com.example.appcomprayventa.adaptadores.AdaptadorImagenCarrusel
import com.example.appcomprayventa.modelos.ModeloComentario
import com.google.firebase.auth.FirebaseAuth

class DetalleAnuncio : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleAnuncioBinding
    private var idAnuncio = ""
    private lateinit var imagenesArrayList: ArrayList<String>
    private lateinit var adaptadorImagen: AdaptadorImagenCarrusel
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var comentarioArrayList: ArrayList<ModeloComentario>
    private lateinit var adaptadorComentario: AdaptadorComentario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleAnuncioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        idAnuncio = intent.getStringExtra("idAnuncio") ?: ""

        binding.btnRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        cargarDetalles()
        cargarLikesYDislikes()
        cargarComentarios()

        binding.btnLike.setOnClickListener { interactuarLike() }
        binding.btnDislike.setOnClickListener { interactuarDislike() }
        binding.btnEliminar.setOnClickListener { eliminarAnuncio() }
        binding.btnMarcarVendido.setOnClickListener { toggleVendido() }

        binding.btnEnviarComentario.setOnClickListener { publicarComentario() }
    }

    private fun cargarComentarios() {
        comentarioArrayList = ArrayList()
        binding.rvComentarios.layoutManager = LinearLayoutManager(this)

        val ref = FirebaseDatabase.getInstance().getReference("Anuncios").child(idAnuncio).child("Comentarios")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                comentarioArrayList.clear()
                for (ds in snapshot.children) {
                    val modelo = ds.getValue(ModeloComentario::class.java)
                    if (modelo != null) {
                        comentarioArrayList.add(modelo)
                    }
                }
                adaptadorComentario = AdaptadorComentario(this@DetalleAnuncio, comentarioArrayList, idAnuncio)
                binding.rvComentarios.adapter = adaptadorComentario
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetalleAnuncio, "Error al cargar comentarios: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun publicarComentario() {
        val textoComentario = binding.etComentario.text.toString().trim()
        val miUid = firebaseAuth.uid

        if (textoComentario.isEmpty()) {
            Toast.makeText(this, "Escribe un comentario antes de enviar", Toast.LENGTH_SHORT).show()
            return
        }

        if (miUid == null) {
            Toast.makeText(this, "Debes iniciar sesión para comentar", Toast.LENGTH_SHORT).show()
            return
        }

        val ref = FirebaseDatabase.getInstance().getReference("Anuncios").child(idAnuncio).child("Comentarios")
        val idComentario = ref.push().key ?: return

        val hashMap = HashMap<String, Any>()
        hashMap["idComentario"] = idComentario
        hashMap["uid"] = miUid
        hashMap["comentario"] = textoComentario
        hashMap["timestamp"] = System.currentTimeMillis().toString()

        ref.child(idComentario).setValue(hashMap).addOnSuccessListener {
            binding.etComentario.setText("") // Limpiamos el EditText
            Toast.makeText(this, "Comentario publicado", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error al publicar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarDetalles() {
        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        ref.child(idAnuncio)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val titulo = "${snapshot.child("titulo").value}"
                        val precio = "${snapshot.child("precio").value}"
                        val descripcion = "${snapshot.child("descripcion").value}"
                        val condicion = "${snapshot.child("condicion").value}"
                        val marca = "${snapshot.child("marca").value}"
                        val categoria = "${snapshot.child("categoria").value}"
                        val uidAnuncio = "${snapshot.child("uid").value}"
                        val uidUsuario = firebaseAuth.currentUser?.uid
                        val estado = "${snapshot.child("estado").value}"

                        binding.tvTitulo.text = titulo
                        binding.tvPrecio.text = "$$precio"
                        binding.tvDescripcion.text = descripcion
                        binding.tvCondicion.text = condicion
                        binding.tvMarca.text = marca
                        binding.tvCategoria.text = categoria

                        cargarImagenes()

                        if (uidUsuario == uidAnuncio) {
                            binding.btnLikeDislike.visibility = View.GONE
                            if (estado == "Vendido") {
                                binding.btnMarcarVendido.text = "Marcar como Disponible"
                                binding.btnMarcarVendido.backgroundTintList = ColorStateList.valueOf(
                                    ContextCompat.getColor(this@DetalleAnuncio, R.color.white)
                                )
                                binding.btnMarcarVendido.strokeColor = ColorStateList.valueOf(Color.parseColor("#4CAF50"))
                                binding.btnMarcarVendido.strokeWidth = 3
                                binding.btnMarcarVendido.setTextColor(Color.parseColor("#4CAF50"))
                            } else {
                                binding.btnMarcarVendido.text = "Marcar como Vendido"
                                binding.btnMarcarVendido.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4CAF50"))
                                binding.btnMarcarVendido.strokeWidth = 0
                                binding.btnMarcarVendido.setTextColor(ContextCompat.getColor(this@DetalleAnuncio, R.color.white))
                            }
                        } else {
                            binding.btnEliminar.visibility = View.GONE
                            binding.btnMarcarVendido.visibility = View.GONE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@DetalleAnuncio, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun cargarImagenes() {
        imagenesArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Anuncios").child(idAnuncio).child("Imagenes")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                imagenesArrayList.clear()

                if (snapshot.exists()) {
                    for (ds in snapshot.children) {
                        val imageUrl = "${ds.child("imageUrl").value}"
                        imagenesArrayList.add(imageUrl)
                    }

                    adaptadorImagen = AdaptadorImagenCarrusel(this@DetalleAnuncio, imagenesArrayList)
                    binding.viewPagerImagenes.adapter = adaptadorImagen
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun cargarLikesYDislikes() {
        val refAnuncio = FirebaseDatabase.getInstance().getReference("Anuncios").child(idAnuncio)

        refAnuncio.child("Likes").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.tvLikesCount.text = "${snapshot.childrenCount}"
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        refAnuncio.child("Dislikes").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.tvDislikesCount.text = "${snapshot.childrenCount}"
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun interactuarLike() {
        val uidUsuario = firebaseAuth.currentUser?.uid ?: return
        val refLikes = FirebaseDatabase.getInstance().getReference("Anuncios").child(idAnuncio).child("Likes")
        val refDislikes = FirebaseDatabase.getInstance().getReference("Anuncios").child(idAnuncio).child("Dislikes")

        refLikes.child(uidUsuario).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    refLikes.child(uidUsuario).removeValue()
                } else {
                    refLikes.child(uidUsuario).setValue(true)
                    refDislikes.child(uidUsuario).removeValue()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun interactuarDislike() {
        val uidUsuario = firebaseAuth.currentUser?.uid ?: return
        val refLikes = FirebaseDatabase.getInstance().getReference("Anuncios").child(idAnuncio).child("Likes")
        val refDislikes = FirebaseDatabase.getInstance().getReference("Anuncios").child(idAnuncio).child("Dislikes")

        refDislikes.child(uidUsuario).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    refDislikes.child(uidUsuario).removeValue()
                } else {
                    refDislikes.child(uidUsuario).setValue(true)
                    refLikes.child(uidUsuario).removeValue()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun eliminarAnuncio() {
        val ref = FirebaseDatabase.getInstance().getReference("Anuncios").child(idAnuncio)
        ref.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Anuncio eliminado con éxito", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al eliminar el anuncio: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun toggleVendido() {
        val ref = FirebaseDatabase.getInstance().getReference("Anuncios").child(idAnuncio)
        ref.child("estado").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val estadoActual = snapshot.value.toString()
                    val nuevoEstado = if (estadoActual == "Disponible") "Vendido" else "Disponible"

                    ref.child("estado").setValue(nuevoEstado)
                        .addOnSuccessListener {
                            Toast.makeText(this@DetalleAnuncio, "Estado actualizado a $nuevoEstado", Toast.LENGTH_SHORT).show()

                            if (nuevoEstado == "Vendido") {
                                binding.btnMarcarVendido.text = "Marcar como Disponible"
                                binding.btnMarcarVendido.backgroundTintList = ColorStateList.valueOf(
                                    ContextCompat.getColor(this@DetalleAnuncio, R.color.white)
                                )
                                binding.btnMarcarVendido.strokeColor = ColorStateList.valueOf(Color.parseColor("#4CAF50"))
                                binding.btnMarcarVendido.strokeWidth = 3
                                binding.btnMarcarVendido.setTextColor(Color.parseColor("#4CAF50"))

                            } else {
                                binding.btnMarcarVendido.text = "Marcar como Vendido"
                                binding.btnMarcarVendido.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4CAF50"))
                                binding.btnMarcarVendido.strokeWidth = 0
                                binding.btnMarcarVendido.setTextColor(ContextCompat.getColor(this@DetalleAnuncio, R.color.white))
                            }
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetalleAnuncio, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}