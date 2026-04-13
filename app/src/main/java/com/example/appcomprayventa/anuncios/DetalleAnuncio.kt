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

class DetalleAnuncio : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleAnuncioBinding
    private var idAnuncio = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleAnuncioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idAnuncio = intent.getStringExtra("idAnuncio") ?: ""

        binding.btnRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        cargarDetalles()

        binding.btnEliminar.setOnClickListener {
            Toast.makeText(this, "Función eliminar pendiente", Toast.LENGTH_SHORT).show()
        }

        binding.btnMarcarVendido.setOnClickListener {
            Toast.makeText(this, "Función marcar vendido pendiente", Toast.LENGTH_SHORT).show()
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

                        binding.tvTitulo.text = titulo
                        binding.tvPrecio.text = "$$precio"
                        binding.tvDescripcion.text = descripcion
                        binding.tvCondicion.text = condicion
                        binding.tvMarca.text = marca
                        binding.tvCategoria.text = categoria

                        cargarImagenPrincipal()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@DetalleAnuncio, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun cargarImagenPrincipal() {
        val ref = FirebaseDatabase.getInstance().getReference("Anuncios").child(idAnuncio).child("Imagenes")
        ref.limitToFirst(1).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (ds in snapshot.children) {
                        val imageUrl = "${ds.child("imageUrl").value}"
                        try {
                            Glide.with(this@DetalleAnuncio)
                                .load(imageUrl)
                                .placeholder(R.drawable.item_imagen)
                                .into(binding.imgPrincipal)
                        } catch (e: Exception) { }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}