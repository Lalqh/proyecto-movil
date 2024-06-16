package com.example.proyecto.ui.provedor.order_add

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Spinner
import com.example.proyecto.ModelClasses.ProductoData
import com.example.proyecto.ModelClasses.ProvedorData
import com.example.proyecto.ModelClasses.Utils
import com.example.proyecto.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class AddOrderFragment : Fragment() {

    data class OrdenData(
        val provedor: String,
        val producto: String,
        val cantidad: String,
        val monto: String
    )
    companion object {
        fun newInstance() = AddOrderFragment()
    }

    private lateinit var viewModel: AddOrderViewModel
    private lateinit var spnProvedor:Spinner
    private lateinit var spnProducto:Spinner
    private lateinit var etCantidad:EditText
    private lateinit var etMonto:EditText
    private lateinit var cal: CalendarView
    private lateinit var add:Button
    private lateinit var cancel:Button

    private lateinit var selectedProvedor:ProvedorData
    private lateinit var selectedProduct:ProductoData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_add_order, container, false)
        spnProvedor=view.findViewById(R.id.spnProvedorOrd)
        spnProducto=view.findViewById(R.id.spnProductOrd)
        etCantidad=view.findViewById(R.id.edtCantidad)
        etMonto=view.findViewById(R.id.edtMontoOrd)
        cal=view.findViewById(R.id.cvOrderDate)
        add=view.findViewById(R.id.btnGuardarOrd)
        cancel=view.findViewById(R.id.btnCancelarOrd)

        setupSpinnerProcucto()
        setupSpinnerProvedor()

        return view
    }

    private fun saveOrder(order: OrdenData) {
        val sharedPreferences = requireContext().getSharedPreferences("OrderPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val gson = Gson()
        val provedorListJson = sharedPreferences.getString("ordenes", null)
        val type = object : TypeToken<MutableList<OrdenData>>() {}.type
        val provedorList: MutableList<OrdenData> = if (provedorListJson != null) {
            gson.fromJson(provedorListJson, type)
        } else {
            mutableListOf()
        }

        provedorList.add(order)

        val newProvedorListJson = gson.toJson(provedorList)
        editor.putString("ordenes", newProvedorListJson)
        editor.apply()
    }

    private fun setupSpinnerProvedor() {
        val procedores = Utils.getProvedoresFromPreferences(requireContext())

        if (procedores.isNotEmpty()) {
            selectedProvedor = procedores[0]

            val categoryNames = procedores.map { it.nombre }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnProvedor.adapter = adapter

            spnProvedor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    selectedProvedor = procedores[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }

    private fun setupSpinnerProcucto() {
        val productos = Utils.getProductosFromPreferences(requireContext())

        if (productos.isNotEmpty()) {
            selectedProduct = productos[0]

            val categoryNames = productos.map { it.nombre }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnProducto.adapter = adapter

            spnProducto.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    selectedProduct = productos[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddOrderViewModel::class.java)
        // TODO: Use the ViewModel
    }

}