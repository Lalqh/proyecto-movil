package com.example.proyecto.ui.venta.reporte

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.proyecto.R

class ReporteVentaFragment : Fragment() {

    companion object {
        fun newInstance() = ReporteVentaFragment()
    }

    private lateinit var viewModel: ReporteVentaViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reporte_venta, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ReporteVentaViewModel::class.java)
        // TODO: Use the ViewModel
    }

}