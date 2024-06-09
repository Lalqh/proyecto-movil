package com.example.proyecto.ui.producto.addcategoria

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.proyecto.R

class AddCategoriaFragment : Fragment() {

    companion object {
        fun newInstance() = AddCategoriaFragment()
    }

    private lateinit var viewModel: AddCategoriaViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_categoria, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddCategoriaViewModel::class.java)
        // TODO: Use the ViewModel
    }

}