package com.example.proyecto.ui.producto.catalogo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.ModelClasses.ProductoAdapter
import com.example.proyecto.ModelClasses.ProductoData
import com.example.proyecto.MySQLConnection
import com.example.proyecto.R
import com.example.proyecto.ui.producto.categoria.CategoriaProductoFragment.Category

class CatalogoProductoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productoAdapter: ProductoAdapter
    private lateinit var editTextText: EditText
    private lateinit var spinner: Spinner
    private var productos: List<ProductoData> = listOf()
    private var filteredProductos: List<ProductoData> = listOf()
    private lateinit var mySQLConnection: MySQLConnection
    private var categories: List<Category> = listOf()
    private var filteredCategories: List<Category> = listOf()

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
        mySQLConnection = MySQLConnection(requireContext())
        productoAdapter = ProductoAdapter(filteredProductos)
        recyclerView.adapter = productoAdapter
        loadCategories()
        val spinnerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        spinner.adapter = spinnerAdapter

        productos = loadProducts()
        filteredProductos = productos
        productoAdapter.updateProductos(filteredProductos)


        editTextText.addTextChangedListener {
            filterProductos()
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                filterProductos()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada
            }
        }

        return view
    }

    private fun loadProducts(): List<ProductoData> {
        mySQLConnection.selectDataAsync(
            "SELECT p.*, c.nombreCategoria " +
                    "FROM producto as p " +
                    "INNER JOIN categoria as c " +
                    "ON p.Categoria_id = c.idCategoria;"
        )
        { result ->
            if (result.isNotEmpty()) {
                productos = result.map {
                    ProductoData(
                        nombre = it["nombreProducto"] ?: "",
                        precio = it["precio"] ?: "",
                        descripcion = it["descripcion"] ?: "",
                        categoria = it["nombreCategoria"] ?: "",
                        stock = it["stock"] ?: "",
                        code = it["codigo"] ?: "",
                        img = it["img"] ?: ""
                    )
                }
                filteredProductos = productos
                productoAdapter.updateProductos(filteredProductos)
            } else {
                Toast.makeText(context, "No se encontraron productos", Toast.LENGTH_SHORT).show()
            }
        }
        return productos
    }

    private fun loadCategories() {
        val query = "SELECT idCategoria, nombreCategoria, descripcion, activo FROM categoria"
        mySQLConnection.selectDataAsync(query) { result ->
            if (result.isNotEmpty()) {
                categories = result.map {
                    Category(
                        it["idCategoria"]?.toInt() ?: 0,
                        it["nombreCategoria"] ?: "",
                        it["descripcion"] ?: "",
                        it["activo"]?.toInt() == 1
                    )
                }
                categories = categories.plusElement(Category(0, "Todas", "", true))
                val spinnerAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    categories.map { it.name })
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = spinnerAdapter
            } else {
                Toast.makeText(context, "No se encontraron categorías", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun filterProductos() {
        val query = editTextText.text.toString().lowercase()
        val selectedCategory = spinner.selectedItem?.toString()

        filteredProductos = productos.filter { producto ->
            producto.nombre.lowercase()
                .contains(query) && (selectedCategory == "Todas" || producto.categoria == selectedCategory)
        }

        productoAdapter.updateProductos(filteredProductos)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CatalogoProductoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}