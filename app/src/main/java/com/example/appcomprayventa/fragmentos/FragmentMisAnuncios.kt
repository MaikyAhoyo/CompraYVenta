package com.example.appcomprayventa.fragmentos

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appcomprayventa.adaptadores.AdaptadorAnuncio
import com.example.appcomprayventa.databinding.FragmentMisAnunciosBinding
import com.example.appcomprayventa.modelos.ModeloAnuncio
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentMisAnuncios : Fragment() {

    private var _binding: FragmentMisAnunciosBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mContext: Context

    private lateinit var anunciosArrayList: ArrayList<ModeloAnuncio>
    private lateinit var adaptadorAnuncio: AdaptadorAnuncio

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMisAnunciosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        cargarMisAnuncios()
    }

    private fun cargarMisAnuncios() {
        anunciosArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        ref.orderByChild("uid").equalTo(firebaseAuth.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    anunciosArrayList.clear()

                    if (snapshot.exists()) {
                        for (ds in snapshot.children) {
                            val modelo = ds.getValue(ModeloAnuncio::class.java)
                            if (modelo != null) {
                                anunciosArrayList.add(modelo)
                            }
                        }

                        adaptadorAnuncio = AdaptadorAnuncio(mContext, anunciosArrayList)
                        binding.RVMisAnuncios.layoutManager = LinearLayoutManager(mContext)
                        binding.RVMisAnuncios.adapter = adaptadorAnuncio
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(mContext, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}