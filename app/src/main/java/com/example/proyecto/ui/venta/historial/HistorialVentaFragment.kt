package com.example.proyecto.ui.venta.historial

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.proyecto.R

class HistorialVentaFragment : Fragment() {

    companion object {
        fun newInstance() = HistorialVentaFragment()
    }

    private lateinit var viewModel: HistorialVentaViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_historial_venta, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HistorialVentaViewModel::class.java)
        // TODO: Use the ViewModel
    }

}