package com.example.proyecto.ui.provedor.consult

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.MySQLConnection
import com.example.proyecto.R

class ConstultProvedorFragment : Fragment() {

    companion object {
        fun newInstance() = ConstultProvedorFragment()
    }

    private lateinit var viewModel: ConstultProvedorViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBar: EditText
    private lateinit var mySQLConnection: MySQLConnection
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

        mySQLConnection = MySQLConnection(requireContext())

        loadProvedores()

        searchBar.addTextChangedListener {
            val searchText = it.toString()
            filterProvedores(searchText)
        }
        return view
    }

    private fun loadProvedores() {
        val query = "SELECT nombreProveedor, nitProveedor, correoProveedor, telefono FROM proveedor"
        mySQLConnection.selectDataAsync(query) { result ->
            if (result.isNotEmpty()) {
                provedores = result.map {
                    ProvedorData(
                        it["nombreProveedor"] ?: "",
                        it["nitProveedor"] ?: "",
                        it["correoProveedor"] ?: "",
                        it["telefono"] ?: ""
                    )
                }
                filteredProvedores = provedores
                updateRecyclerView(filteredProvedores)
            } else {
                Toast.makeText(context, "No se encontraron proveedores", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateRecyclerView(provedores: List<ProvedorData>) {
        recyclerView.adapter = object : RecyclerView.Adapter<ProvedorViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProvedorViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.provedor_item, parent, false)
                return ProvedorViewHolder(view)
            }

            override fun onBindViewHolder(holder: ProvedorViewHolder, position: Int) {
                val provedor = provedores[position]
                holder.nombre.text = provedor.nombre
                holder.mail.text = provedor.correo
                holder.phone.text = provedor.telefono
                holder.rfc.text = provedor.nit
            }

            override fun getItemCount(): Int {
                return provedores.size
            }
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
        updateRecyclerView(filteredProvedores)
    }

    data class ProvedorData(
        val nombre: String,
        val nit: String,
        val correo: String,
        val telefono: String
    )

    class ProvedorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.tvProvedorI)
        val mail: TextView = itemView.findViewById(R.id.tvMailProvedorI)
        val phone: TextView = itemView.findViewById(R.id.tvPhoneProvedorI)
        val rfc: TextView = itemView.findViewById(R.id.tvRFCProvedorI)
    }
}