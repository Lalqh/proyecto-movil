package com.example.proyecto.ui.gasto.consultar

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.proyecto.R

class CheckGastoFragment : Fragment() {

    companion object {
        fun newInstance() = CheckGastoFragment()
    }

    private lateinit var viewModel: CheckGastoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_check_gasto, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CheckGastoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}