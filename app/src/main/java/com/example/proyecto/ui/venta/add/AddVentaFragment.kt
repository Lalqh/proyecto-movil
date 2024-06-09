package com.example.proyecto.ui.venta.add

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.proyecto.R

class AddVentaFragment : Fragment() {

    companion object {
        fun newInstance() = AddVentaFragment()
    }

    private lateinit var viewModel: AddVentaViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_venta, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddVentaViewModel::class.java)
        // TODO: Use the ViewModel
    }

}