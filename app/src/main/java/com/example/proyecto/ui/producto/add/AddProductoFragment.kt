package com.example.proyecto.ui.producto.add

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
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.example.proyecto.ModelClasses.Producto
import com.example.proyecto.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AddProductoFragment : Fragment() {

    companion object {
        fun newInstance() = AddProductoFragment()
    }

    private lateinit var viewModel: AddProductoViewModel
    private lateinit var edtNombre: EditText
    private lateinit var edtPrecio: EditText
    private lateinit var edtDescripcion: EditText
    private lateinit var edtDescuento: EditText
    private lateinit var edtStock: EditText
    private lateinit var spnCategory: Spinner
    private lateinit var btnGuardarProducto: Button
    private lateinit var btnCancelar: Button
    private var categoriaSeleccionada: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_producto, container, false)

        edtNombre = view.findViewById(R.id.edtNombre)
        edtPrecio = view.findViewById(R.id.edtPrecio)
        edtDescripcion = view.findViewById(R.id.edtDescripcion)
        edtDescuento = view.findViewById(R.id.edtDescuento)
        edtStock = view.findViewById(R.id.edtStock)
        spnCategory = view.findViewById(R.id.spnCategory)
        btnGuardarProducto = view.findViewById(R.id.btnGuardarProducto)
        btnCancelar = view.findViewById(R.id.btnCancelar)

        val categories = resources.getStringArray(R.array.categorias)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
        spnCategory.adapter = adapter
        spnCategory.setSelection(0)
        categoriaSeleccionada = categories[0]

        spnCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                categoriaSeleccionada = categories[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnGuardarProducto.setOnClickListener {
            if (validateInputs()) {
                val nombre = edtNombre.text.toString()
                val precio = edtPrecio.text.toString()
                val descripcion = edtDescripcion.text.toString()
                val descuento = edtDescuento.text.toString()
                val stock = edtStock.text.toString()

                val producto = Producto(nombre, precio, descripcion, descuento, stock, categoriaSeleccionada)

                saveProduct(producto)
                Toast.makeText(requireContext(), "Producto guardado", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancelar.setOnClickListener {
            Toast.makeText(requireContext(), "Acción cancelada", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun validateInputs(): Boolean {
        val nombre = edtNombre.text.toString().trim()
        val precio = edtPrecio.text.toString().trim()
        val descripcion = edtDescripcion.text.toString().trim()
        val descuento = edtDescuento.text.toString().trim()
        val stock = edtStock.text.toString().trim()

        if (nombre.isEmpty()) {
            showToast("Por favor, ingrese el nombre del producto.")
            return false
        }

        if (precio.isEmpty()) {
            showToast("Por favor, ingrese el precio del producto.")
            return false
        }

        if (descripcion.isEmpty()) {
            showToast("Por favor, ingrese la descripción del producto.")
            return false
        }

        if (descuento.isEmpty()) {
            showToast("Por favor, ingrese el descuento del producto.")
            return false
        }

        if (stock.isEmpty()) {
            showToast("Por favor, ingrese el stock del producto.")
            return false
        }

        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun saveProduct(producto: Producto) {
        val sharedPreferences = requireContext().getSharedPreferences("ProductPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Retrieve existing products
        val gson = Gson()
        val productListJson = sharedPreferences.getString("productos", null)
        val type = object : TypeToken<MutableList<Producto>>() {}.type
        val productList: MutableList<Producto> = if (productListJson != null) {
            gson.fromJson(productListJson, type)
        } else {
            mutableListOf()
        }

        // Add new product to the list
        productList.add(producto)

        // Save the updated list back to SharedPreferences
        val newProductListJson = gson.toJson(productList)
        editor.putString("productos", newProductListJson)
        editor.apply()
    }

    private fun loadProducts(): List<Producto> {
        val sharedPreferences = requireContext().getSharedPreferences("ProductPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val productListJson = sharedPreferences.getString("productos", null)
        val type = object : TypeToken<MutableList<Producto>>() {}.type
        return if (productListJson != null) {
            gson.fromJson(productListJson, type)
        } else {
            mutableListOf()
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddProductoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}