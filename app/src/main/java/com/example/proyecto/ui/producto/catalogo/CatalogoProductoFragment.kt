package com.example.proyecto.ui.producto.catalogo

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.ModelClasses.Producto
import com.example.proyecto.ModelClasses.ProductoAdapter
import com.example.proyecto.ModelClasses.ProductoData
import com.example.proyecto.R
import com.example.proyecto.ui.producto.addcategoria.AddCategoriaFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CatalogoProductoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productoAdapter: ProductoAdapter
    private lateinit var editTextText: EditText
    private lateinit var spinner: Spinner
    private var productos: List<ProductoData> = listOf()
    private var filteredProductos: List<ProductoData> = listOf()
    companion object {
        fun newInstance() = CatalogoProductoFragment()
    }

    private lateinit var viewModel: CatalogoProductoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_catalogo_producto, container, false)
        editTextText = view.findViewById(R.id.editTextText)
        spinner = view.findViewById(R.id.spinner)
        recyclerView = view.findViewById(R.id.recyclerViewProductos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        productoAdapter = ProductoAdapter(filteredProductos)
        recyclerView.adapter = productoAdapter
        val categories = loadCategories()
        val stringCategories = categories.map { it.name }
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, stringCategories)
        spinner.adapter = spinnerAdapter

        // Cargar productos desde SharedPreferences
        productos = loadProducts()
        filteredProductos = productos
        productoAdapter.updateProductos(filteredProductos)

        // Configurar el filtro por nombre
        editTextText.addTextChangedListener {
            filterProductos()
        }

        // Configurar el filtro por categoría
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                filterProductos()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada
            }
        }

        return view
    }
    private fun loadProducts(): List<ProductoData> {
        val sharedPreferences = requireContext().getSharedPreferences("ProductPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val productListJson = sharedPreferences.getString("productos", null)
        val type = object : TypeToken<MutableList<ProductoData>>() {}.type
        return if (productListJson != null) {
            gson.fromJson(productListJson, type)
        } else {
            mutableListOf()
        }
    }
    private fun loadCategories(): List<AddCategoriaFragment.Category> {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val categoryListJson = sharedPreferences.getString("categories", null)
        val type = object : TypeToken<MutableList<AddCategoriaFragment.Category>>() {}.type
        return if (categoryListJson != null) {
            gson.fromJson(categoryListJson, type)
        } else {
            mutableListOf()
        }
    }
    private fun filterProductos() {
        val query = editTextText.text.toString().lowercase()
        val selectedCategory = spinner.selectedItem.toString()

        filteredProductos = productos.filter { producto ->
            producto.nombre.lowercase().contains(query) && (selectedCategory == "Todas" || producto.categoria == selectedCategory)
        }

        productoAdapter.updateProductos(filteredProductos)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CatalogoProductoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}