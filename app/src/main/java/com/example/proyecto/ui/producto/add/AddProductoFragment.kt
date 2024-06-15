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
import com.example.proyecto.ModelClasses.ProductoData
import com.example.proyecto.ModelClasses.Utils
import com.example.proyecto.R
import com.example.proyecto.ui.producto.addcategoria.AddCategoriaFragment
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
    //private var categoriaSeleccionada: String = ""
    private lateinit var btnBuscarProducto: Button
    private lateinit var btnEliminarProducto: Button
    private lateinit var categoriaSeleccionada: AddCategoriaFragment.Category
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
        btnEliminarProducto = view.findViewById(R.id.btnCancelar)
        btnBuscarProducto = view.findViewById(R.id.btnBuscarProducto)

        setupSpinner()
        btnGuardarProducto.setOnClickListener {
            if (validateInputs()) {
                val nombre = edtNombre.text.toString()
                val precio = edtPrecio.text.toString()
                val descripcion = edtDescripcion.text.toString()
                val descuento = edtDescuento.text.toString()
                val stock = edtStock.text.toString()

                val producto = ProductoData(nombre, precio, descripcion, descuento, stock, categoriaSeleccionada.name)

                saveProduct(producto)
                Toast.makeText(requireContext(), "Producto guardado", Toast.LENGTH_SHORT).show()
            }
        }

        btnEliminarProducto.setOnClickListener {
            eliminarProducto()
        }
        btnBuscarProducto.setOnClickListener { buscarProducto() }

        return view
    }
    private fun setupSpinner() {
        val categories = Utils.getCategoriesFromPreferences(requireContext())

        if (categories.isNotEmpty()) {
            categoriaSeleccionada = categories[0]

            val categoryNames = categories.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnCategory.adapter = adapter

            spnCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    categoriaSeleccionada = categories[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }
    private fun buscarProducto() {
        val productName = edtNombre.text.toString()
        if (productName.isBlank()) {
            Toast.makeText(requireContext(), "Por favor ingrese el nombre del producto", Toast.LENGTH_SHORT).show()
            return
        }

        val productos = Utils.getProductosFromPreferences(requireContext())
        val producto = productos.find { it.nombre == productName }
        if (producto != null) {
            Toast.makeText(requireContext(), "Producto encontrado: ${producto.nombre} en categoría ${producto.categoria}", Toast.LENGTH_SHORT).show()
            // Mostrar los datos del producto en los EditText
            edtNombre.setText(producto.nombre)
            edtPrecio.setText(producto.precio)
            edtDescripcion.setText(producto.descripcion)
            edtDescuento.setText(producto.descuento)
            edtStock.setText(producto.stock)
            val categoryIndex = Utils.getCategoriesFromPreferences(requireContext()).indexOfFirst { it.name == producto.categoria }
            spnCategory.setSelection(categoryIndex)
        } else {
            Toast.makeText(requireContext(), "Producto no encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun eliminarProducto() {
        val productName = edtNombre.text.toString()
        if (productName.isBlank()) {
            Toast.makeText(requireContext(), "Por favor ingrese el nombre del producto", Toast.LENGTH_SHORT).show()
            return
        }

        val productos = Utils.getProductosFromPreferences(requireContext()).toMutableList()
        val iterator = productos.iterator()

        while (iterator.hasNext()) {
            val producto = iterator.next()
            if (producto.nombre == productName) {
                iterator.remove()
                Utils.saveProductosToPreferences(requireContext(),productos)
                Toast.makeText(requireContext(), "Producto eliminado: ${producto.nombre}", Toast.LENGTH_SHORT).show()
                return
            }
        }

        Toast.makeText(requireContext(), "Producto no encontrado", Toast.LENGTH_SHORT).show()
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

    private fun saveProduct(producto: ProductoData) {
        val sharedPreferences = requireContext().getSharedPreferences("ProductPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val gson = Gson()
        val productListJson = sharedPreferences.getString("productos", null)
        val type = object : TypeToken<MutableList<ProductoData>>() {}.type
        val productList: MutableList<ProductoData> = if (productListJson != null) {
            gson.fromJson(productListJson, type)
        } else {
            mutableListOf()
        }

        productList.add(producto)

        val newProductListJson = gson.toJson(productList)
        editor.putString("productos", newProductListJson)
        editor.apply()
        limpiar()
    }
    private fun limpiar(){
        edtNombre.setText("")
        edtPrecio.setText("")
        edtDescripcion.setText("")
        edtDescuento.setText("")
        edtStock.setText("")
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddProductoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}