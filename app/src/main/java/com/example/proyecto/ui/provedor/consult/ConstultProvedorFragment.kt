package com.example.proyecto.ui.provedor.consult

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.ModelClasses.ProvedorAdapter
import com.example.proyecto.ModelClasses.ProvedorData
import com.example.proyecto.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ConstultProvedorFragment : Fragment() {

    companion object {
        fun newInstance() = ConstultProvedorFragment()
    }

    private lateinit var viewModel: ConstultProvedorViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var provedorAdapter: ProvedorAdapter
    private lateinit var searchBar: EditText
    private var provedores: List<ProvedorData> = listOf()
    private var filteredProvedores: List<ProvedorData> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_constult_provedor, container, false)

        searchBar = view.findViewById(R.id.etSearchProvedor)
        recyclerView = view.findViewById(R.id.recyclerViewProvedores)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        provedorAdapter = ProvedorAdapter(filteredProvedores)
        recyclerView.adapter = provedorAdapter

        provedores = loadProvedores()
        filteredProvedores = provedores

        provedorAdapter.updateProvedores(filteredProvedores)

        searchBar.addTextChangedListener {
            val searchText = it.toString()
            filterProvedores(searchText)
        }
        return view
    }

    private fun loadProvedores(): List<ProvedorData> {
        val sharedPreferences = requireContext().getSharedPreferences("ProvedorPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val provedorListJson = sharedPreferences.getString("provedores", null)
        val type = object : TypeToken<MutableList<ProvedorData>>() {}.type
        return if (provedorListJson != null) {
            gson.fromJson(provedorListJson, type)
        } else {
            mutableListOf()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ConstultProvedorViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun filterProvedores(query: String) {
        filteredProvedores = if (query.isEmpty()) {
            provedores
        } else {
            provedores.filter { it.nombre.contains(query, ignoreCase = true) }
        }
        provedorAdapter.updateProvedores(filteredProvedores)
    }

}