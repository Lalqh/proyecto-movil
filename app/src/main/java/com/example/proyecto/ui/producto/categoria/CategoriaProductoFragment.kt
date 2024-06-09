package com.example.proyecto.ui.producto.categoria

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.proyecto.R

class CategoriaProductoFragment : Fragment() {

    companion object {
        fun newInstance() = CategoriaProductoFragment()
    }

    private lateinit var viewModel: CategoriaProductoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_categoria_producto, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CategoriaProductoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}