package com.example.appcomprayventa.fragmentos

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.appcomprayventa.adaptadores.AdaptadorUsuario
import com.example.appcomprayventa.databinding.FragmentChatsBinding
import com.example.appcomprayventa.modelos.Usuario

class FragmentChats : Fragment() {
    private lateinit var binding: FragmentChatsBinding

    private lateinit var mContext: Context
    private var usuarioAdapador: AdaptadorUsuario? = null
    private var usuarioLista: List<Usuario> ?= null

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChatsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}