package com.example.proyecto.ui.gasto.consultar

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.ModelClasses.GastoAdapter
import com.example.proyecto.ModelClasses.GastoData
import com.example.proyecto.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CheckGastoFragment : Fragment() {

    companion object {
        fun newInstance() = CheckGastoFragment()
    }

    private lateinit var viewModel: CheckGastoViewModel

    private lateinit var rvGasto:RecyclerView

    private lateinit var gastoAdapter:GastoAdapter
    private var gastos: List<GastoData> = listOf()
    private var filteredGastos: List<GastoData> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_check_gasto, container, false)
        rvGasto=view.findViewById(R.id.rvGasto)

        rvGasto.layoutManager = LinearLayoutManager(requireContext())

        gastoAdapter = GastoAdapter(filteredGastos)
        rvGasto.adapter = gastoAdapter

        gastos = loadGastos()
        filteredGastos = gastos
        gastoAdapter.updateGastos(filteredGastos)


        return view
    }

    private fun loadGastos(): List<GastoData> {
        val sharedPreferences = requireContext().getSharedPreferences("GastoPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val gastoListJson = sharedPreferences.getString("gastos", null)
        val type = object : TypeToken<MutableList<GastoData>>() {}.type
        return if (gastoListJson != null) {
            gson.fromJson(gastoListJson, type)
        } else {
            mutableListOf()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CheckGastoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}