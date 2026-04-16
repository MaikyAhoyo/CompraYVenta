package com.example.appcomprayventa.anuncios

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.appcomprayventa.adaptadores.AdaptadorImagenSeleccionada
import com.example.appcomprayventa.Constantes
import com.example.appcomprayventa.modelos.ModeloImagenSeleccionada
import com.example.appcomprayventa.R
import com.example.appcomprayventa.databinding.ActivityCrearAnuncioBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class CrearAnuncio : AppCompatActivity() {
    private lateinit var binding : ActivityCrearAnuncioBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog : ProgressDialog
    private var imagenUri : Uri?=null
    private lateinit var imagenSelecArrayList : ArrayList<ModeloImagenSeleccionada>
    private lateinit var adaptadorImagenSel : AdaptadorImagenSeleccionada
    private var imageUri: Uri?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearAnuncioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Por favor espere")
        progressDialog.setCanceledOnTouchOutside(false)

        imagenSelecArrayList = ArrayList()
        cargarImagenes()

        val adaptadorCat = ArrayAdapter(this, R.layout.item_categoria, Constantes.categorias)
        binding.Categoria.setAdapter(adaptadorCat)

        val adaptadorCon = ArrayAdapter(this, R.layout.item_condicion, Constantes.condiciones)
        binding.Condicion.setAdapter(adaptadorCon)

        binding.agregarImg.setOnClickListener {
            selec_imagen_de()
        }

        binding.BtnCrearAnuncio.setOnClickListener {
            crearAnuncio()
        }
    }

    private fun cargarImagenes() {
        adaptadorImagenSel = AdaptadorImagenSeleccionada(this, imagenSelecArrayList)
        binding.RVImagenes.adapter = adaptadorImagenSel
    }

    private fun selec_imagen_de(){
        val popupMenu = PopupMenu(this, binding.agregarImg)

        popupMenu.menu.add(Menu.NONE, 1, 1, "Cámara")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Galería")

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            val itemId = item.itemId
            if (itemId == 1) {
                // Funcionalidad para la Cámara
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    concederPermisosCamara.launch(arrayOf(android.Manifest.permission.CAMERA))
                } else {
                    concederPermisosCamara.launch(arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ))
                }
            } else if (itemId == 2) {
                // Funcionalidad para la Galería
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    imagenGaleria()
                } else {
                    concederPermisosAlamecenamiento.launch(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                }
            }
            return@setOnMenuItemClickListener true
        }
    }

    private val concederPermisosCamara =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) { resultado ->
            var concedidoTodos = true
            for (seConcede in resultado.values) {
                concedidoTodos = concedidoTodos && seConcede
            }

            if (concedidoTodos) {
                imagenCamara()
            } else {
                Toast.makeText(
                    this,
                    "No se concedieron permisos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val concederPermisosAlamecenamiento =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()) { esConcedido ->
            if (esConcedido) {
                imagenGaleria()
            } else {
                Toast.makeText(
                    this,
                    "El permiso de almacenamiento se denegó",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun imagenCamara(){
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Titulo_imagen")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Descripcion_imagen")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        resultadoCamara_ARL.launch(intent)
    }

    private val resultadoCamara_ARL =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){ resultado ->
            if(resultado.resultCode == RESULT_OK){
                val data = resultado.data
                imageUri = data!!.data
                agregarImagenALista(imageUri)
            } else {
                Toast.makeText(
                    this,
                    "La captura de imagen se canceló",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun imagenGaleria(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultadoGaleria_ARL.launch(intent)
    }

    private val resultadoGaleria_ARL =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){ resultado ->
            if(resultado.resultCode == RESULT_OK){
                val data = resultado.data
                imageUri = data!!.data
                agregarImagenALista(imageUri)
            } else {
                Toast.makeText(
                    this,
                    "La selección de imagen se canceló",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun agregarImagenALista(uri: Uri?) {
        if (uri != null) {
            val timestamp = "" + System.currentTimeMillis()

            val modeloImagen = ModeloImagenSeleccionada(
                id = timestamp,
                imagenUri = uri,
                imagenUrl = null,
                deInternet = false
            )

            imagenSelecArrayList.add(modeloImagen)
            adaptadorImagenSel.notifyDataSetChanged()
        } else {
            Toast.makeText(this, "Error: No se pudo obtener la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun crearAnuncio() {
        val marca = binding.EtMarca.text.toString().trim()
        val categoria = binding.Categoria.text.toString().trim()
        val condicion = binding.Condicion.text.toString().trim()
        val precio = binding.EtPrecio.text.toString().trim()
        val titulo = binding.EtTitulo.text.toString().trim()
        val descripcion = binding.EtDescripcion.text.toString().trim()

        if(marca.isEmpty()){
            binding.EtMarca.error = "Ingrese la marca"
            binding.EtMarca.requestFocus()
        } else if(categoria.isEmpty()){
            binding.Categoria.error = "Ingrese la categoria"
            binding.Categoria.requestFocus()
        } else if(condicion.isEmpty()){
            binding.Condicion.error = "Ingrese la condicion"
            binding.Condicion.requestFocus()
        } else if(precio.isEmpty()){
            binding.EtPrecio.error = "Ingrese el precio"
            binding.EtPrecio.requestFocus()
        } else if(titulo.isEmpty()){
            binding.EtTitulo.error = "Ingrese el titulo"
            binding.EtTitulo.requestFocus()
        } else if(descripcion.isEmpty()){
            binding.EtDescripcion.error = "Ingrese la descripcion"
            binding.EtDescripcion.requestFocus()
        } else if (imagenSelecArrayList.isEmpty()) {
            Toast.makeText(this, "Agregue al menos una imagen", Toast.LENGTH_SHORT).show()
        } else {
            subirAnuncio(marca, categoria, condicion, precio, titulo, descripcion)
        }
    }

    private fun subirAnuncio(marca: String, categoria: String, condicion: String, precio: String, titulo: String, descripcion: String) {
        progressDialog.setMessage("Subiendo información del anuncio...")
        progressDialog.show()

        val timestamp = Constantes.obtenerTiempoDis()
        val uid = firebaseAuth.uid

        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        val idAnuncio = ref.push().key
        val hashMap = HashMap<String, Any>()

        hashMap["idAnuncio"] = "$idAnuncio"
        hashMap["uid"] = "$uid"
        hashMap["marca"] = "$marca"
        hashMap["categoria"] = "$categoria"
        hashMap["condicion"] = "$condicion"
        hashMap["precio"] = "$precio"
        hashMap["titulo"] = "$titulo"
        hashMap["descripcion"] = "$descripcion"
        hashMap["timestamp"] = "$timestamp"
        hashMap["estado"] = Constantes.anuncio_disponible

        ref.child("$idAnuncio")
            .setValue(hashMap)
            .addOnSuccessListener {
                subirImagenesStorage(idAnuncio!!)
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this, "Error al crear anuncio: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun subirImagenesStorage(idAnuncio: String) {
        progressDialog.setMessage("Subiendo imágenes...")
        progressDialog.show()

        var imagenesSubidas = 0

        for (i in 0 until imagenSelecArrayList.size) {
            val modelo = imagenSelecArrayList[i]
            val nombreImagen = modelo.id
            val rutaImagen = "Anuncios/$idAnuncio/$nombreImagen"
            val storageReference = FirebaseStorage.getInstance().getReference(rutaImagen)
            storageReference.putFile(modelo.imagenUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    val uriTask = taskSnapshot.storage.downloadUrl
                    while (!uriTask.isSuccessful);
                    val urlImagenCargada = "${uriTask.result}"

                    if(uriTask.isSuccessful) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["id"] = nombreImagen
                        hashMap["imageUrl"] = urlImagenCargada

                        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
                        ref.child(idAnuncio).child("Imagenes").child(nombreImagen)
                            .setValue(hashMap)
                            .addOnSuccessListener {
                                imagenesSubidas++
                                if (imagenesSubidas == imagenSelecArrayList.size) {
                                    progressDialog.dismiss()
                                    Toast.makeText(
                                        this,
                                        "¡Anuncio publicado con éxito!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                }
                            }
                    }
                }
                .addOnFailureListener { e ->
                    imagenesSubidas++
                    Toast.makeText(this, "Falló una imagen: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}