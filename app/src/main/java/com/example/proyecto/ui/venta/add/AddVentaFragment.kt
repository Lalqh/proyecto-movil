package com.example.proyecto.ui.venta.add

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Spinner
import com.example.proyecto.ModelClasses.ProductoData
import com.example.proyecto.ModelClasses.Venta
import com.example.proyecto.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val opciones = arrayOf("Tarjeta", "Efectivo", "Transferencia")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinner: Spinner = view.findViewById(R.id.spnMetodoPago)
        spinner.adapter = adapter

        val usuarios = getAllUsersFromSharedPreferences().toTypedArray()
        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, usuarios)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinner2: Spinner = view.findViewById(R.id.spnUsuario)
        spinner2.adapter = adapter2

        val productos = getAllProductsFromSharedPreferences()
        val adapter3 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, productos.map { it.nombre })
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinner3: Spinner = view.findViewById(R.id.edtProducto)
        spinner3.adapter = adapter3

        val total: EditText = view.findViewById<EditText>(R.id.edtTotalVenta)
        val calendario: CalendarView = view.findViewById<CalendarView>(R.id.calendarView3)

        val btnGuardarVenta: Button = view.findViewById(R.id.btnGuardarVenta)
        btnGuardarVenta.setOnClickListener {
            val selectedUserIndex = spinner2.selectedItemPosition
            val selectedProductIndex = spinner3.selectedItemPosition

            val selectedUser = usuarios[selectedUserIndex]
            val selectedProduct = productos[selectedProductIndex]

            val idUsuario = selectedUser
            val idProducto = selectedProduct.nombre
            val metodoPago = spinner.selectedItem.toString()
            val fecha = calendario.date.toString()
            val cantidad = total.text.toString().toInt()
            val total = selectedProduct.precio.toFloat() * cantidad

            val venta = Venta(idUsuario, idProducto, metodoPago, fecha, cantidad, total)
            saveVenta(venta)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddVentaViewModel::class.java)
    }

    private fun getAllUsersFromSharedPreferences(): List<String> {
        val sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val allEntries = sharedPreferences.all
        val users = mutableListOf<String>()

        for ((key, _) in allEntries) {
            if (key.endsWith(".nombre")) {
                val userId = key.removeSuffix(".nombre").removePrefix("user_")
                val storedName = sharedPreferences.getString("$userId.nombre", "")
                users.add(storedName ?: "")
            }
        }
        return users
    }

    private fun getAllProductsFromSharedPreferences(): List<ProductoData> {
        val sharedPreferences = requireActivity().getSharedPreferences("ProductPrefs", Context.MODE_PRIVATE)
        val productListJson = sharedPreferences.getString("productos", null)
        val type = object : TypeToken<MutableList<ProductoData>>() {}.type
        val productList: MutableList<ProductoData> = if (productListJson != null) {
            Gson().fromJson(productListJson, type)
        } else {
            mutableListOf()
        }

        return productList
    }

    private fun saveVenta(venta: Venta) {
        val sharedPreferences = requireActivity().getSharedPreferences("VentaPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val gson = Gson()
        val ventaListJson = sharedPreferences.getString("ventas", null)
        val type = object : TypeToken<MutableList<Venta>>() {}.type
        val ventaList: MutableList<Venta> = if (ventaListJson != null) {
            gson.fromJson(ventaListJson, type)
        } else {
            mutableListOf()
        }

        ventaList.add(venta)

        val newVentaListJson = gson.toJson(ventaList)
        editor.putString("ventas", newVentaListJson)
        editor.apply()
    }

}