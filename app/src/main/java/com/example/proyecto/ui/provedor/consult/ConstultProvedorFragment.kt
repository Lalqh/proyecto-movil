package com.example.proyecto.ui.provedor.consult

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.proyecto.R

class ConstultProvedorFragment : Fragment() {

    companion object {
        fun newInstance() = ConstultProvedorFragment()
    }

    private lateinit var viewModel: ConstultProvedorViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_constult_provedor, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ConstultProvedorViewModel::class.java)
        // TODO: Use the ViewModel
    }

}