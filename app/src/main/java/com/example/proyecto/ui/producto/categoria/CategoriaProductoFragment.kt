package com.example.proyecto.ui.producto.categoria

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.proyecto.MySQLConnection
import com.example.proyecto.R

class CategoriaProductoFragment : Fragment() {

    companion object {
        fun newInstance() = CategoriaProductoFragment()
    }

    private lateinit var scrollView: ScrollView
    private lateinit var linearLayout: LinearLayout
    private lateinit var searchBar: EditText
    private lateinit var viewModel: CategoriaProductoViewModel
    private lateinit var mySQLConnection: MySQLConnection
    private var categories: List<Category> = listOf()
    private var filteredCategories: List<Category> = listOf()

    data class Category(
        val id: Int,
        val name: String,
        val description: String,
        val isActive: Boolean
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_categoria_producto, container, false)
        scrollView = view.findViewById(R.id.scrollViewCategories)
        linearLayout = scrollView.findViewById(R.id.linearLayoutCategories)
        searchBar = view.findViewById(R.id.etSearchCategory)
        mySQLConnection = MySQLConnection(requireContext())

        loadCategories()

        searchBar.addTextChangedListener {
            val searchText = it.toString()
            filterCategories(searchText)
        }

        return view
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
                filteredCategories = categories
                updateCategories(filteredCategories)
            } else {
                Toast.makeText(context, "No se encontraron categorías", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateCategories(categories: List<Category>) {
        linearLayout.removeAllViews()
        for (category in categories) {
            val textView = TextView(requireContext())
            textView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 15)
            }
            val isActiveText = if (category.isActive) "Sí" else "No"
            textView.text = "Nombre: ${category.name}\nDescripción: ${category.description}\nActivo: $isActiveText"
            textView.setBackgroundResource(R.drawable.bordes)
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_arrow_right_24, 0, 0, 0)
            textView.setTextColor(resources.getColor(R.color.white))

            linearLayout.addView(textView)
        }
    }

    private fun filterCategories(query: String) {
        filteredCategories = if (query.isEmpty()) {
            categories
        } else {
            categories.filter { it.name.contains(query, ignoreCase = true) }
        }
        updateCategories(filteredCategories)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CategoriaProductoViewModel::class.java)
        // TODO: Use the ViewModel
    }
}