package com.example.proyecto.ui.gasto.add

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.proyecto.R

class AddGastoFragment : Fragment() {

    companion object {
        fun newInstance() = AddGastoFragment()
    }

    private lateinit var viewModel: AddGastoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_gasto, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddGastoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}