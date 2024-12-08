package com.example.proyecto.ui.producto.add

import android.content.Context
import android.content.Intent
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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.proyecto.ModelClasses.Producto
import com.example.proyecto.ModelClasses.ProductoData
import com.example.proyecto.ModelClasses.Utils
import com.example.proyecto.MySQLConnection
import com.example.proyecto.R
import com.example.proyecto.ui.producto.addcategoria.AddCategoriaFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.integration.android.IntentIntegrator

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
    private lateinit var scanProduct: Button
    private lateinit var code: TextView
    private lateinit var mySQLConnection: MySQLConnection

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_producto, container, false)
        mySQLConnection = MySQLConnection(requireContext())
        edtNombre = view.findViewById(R.id.edtNombre)
        edtPrecio = view.findViewById(R.id.edtPrecio)
        edtDescripcion = view.findViewById(R.id.edtDescripcion)

        edtStock = view.findViewById(R.id.edtStock)
        spnCategory = view.findViewById(R.id.spnCategory)
        btnGuardarProducto = view.findViewById(R.id.btnGuardarProducto)
        btnEliminarProducto = view.findViewById(R.id.btnCancelar)
        btnBuscarProducto = view.findViewById(R.id.btnBuscarProducto)
        scanProduct = view.findViewById(R.id.btnProductQR)
        code = view.findViewById(R.id.tvQR)

        scanResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val intentResult = IntentIntegrator.parseActivityResult(
                result.resultCode, result.data
            )


            if (intentResult != null) {
                if (intentResult.contents == null) {
                    Toast.makeText(requireContext(), "Lectura cancelada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Código Leído", Toast.LENGTH_SHORT).show()
                    code.text = intentResult.contents
                }
            } else {
                Toast.makeText(requireContext(), "Error al escanear el código", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        scanProduct.setOnClickListener { escanearCodigo() }

        setupSpinner()
        btnGuardarProducto.setOnClickListener {
            if (validateInputs()) {
                val nombre = edtNombre.text.toString()
                val precio = edtPrecio.text.toString()
                val descripcion = edtDescripcion.text.toString()
                val descuento = "0"
                val stock = edtStock.text.toString()

                val producto = ProductoData(
                    nombre,
                    precio,
                    descripcion,
                    descuento,
                    stock,
                    categoriaSeleccionada.name,
                    "code here"
                )

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
        val query = "SELECT idCategoria, nombreCategoria, descripcion, activo FROM categoria"
        var categories = emptyList<AddCategoriaFragment.Category>()
        mySQLConnection.selectDataAsync(query) { result ->
            if (result.isNotEmpty()) {
                categories = result.map {
                    AddCategoriaFragment.Category(
                        it["nombreCategoria"] ?: "",
                        it["descripcion"] ?: "",
                        it["activo"]?.toInt() == 1
                    )
                }
                updateSpinner(categories)
            } else {
                Toast.makeText(requireContext(), "No se encontraron categorías", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun updateSpinner(categories: List<AddCategoriaFragment.Category>) {
        if (categories.isNotEmpty()) {
            categoriaSeleccionada = categories[0]

            val categoryNames = categories.map { it.name }
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnCategory.adapter = adapter

            spnCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    categoriaSeleccionada = categories[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }
    private fun buscarProducto() {
        val productName = edtNombre.text.toString()
        if (productName.isBlank()) {
            Toast.makeText(
                requireContext(),
                "Por favor ingrese el nombre del producto",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val productos = Utils.getProductosFromPreferences(requireContext())
        val producto = productos.find { it.nombre == productName }
        if (producto != null) {
            Toast.makeText(
                requireContext(),
                "Producto encontrado: ${producto.nombre} en categoría ${producto.categoria}",
                Toast.LENGTH_SHORT
            ).show()
            // Mostrar los datos del producto en los EditText
            edtNombre.setText(producto.nombre)
            edtPrecio.setText(producto.precio)
            edtDescripcion.setText(producto.descripcion)
            //edtDescuento.setText(producto.descuento)
            edtStock.setText(producto.stock)
            val categoryIndex = Utils.getCategoriesFromPreferences(requireContext())
                .indexOfFirst { it.name == producto.categoria }
            spnCategory.setSelection(categoryIndex)
        } else {
            Toast.makeText(requireContext(), "Producto no encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun eliminarProducto() {
        val productName = edtNombre.text.toString()
        if (productName.isBlank()) {
            Toast.makeText(
                requireContext(),
                "Por favor ingrese el nombre del producto",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val productos = Utils.getProductosFromPreferences(requireContext()).toMutableList()
        val iterator = productos.iterator()

        while (iterator.hasNext()) {
            val producto = iterator.next()
            if (producto.nombre == productName) {
                iterator.remove()
                Utils.saveProductosToPreferences(requireContext(), productos)
                Toast.makeText(
                    requireContext(),
                    "Producto eliminado: ${producto.nombre}",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }

        Toast.makeText(requireContext(), "Producto no encontrado", Toast.LENGTH_SHORT).show()
    }

    private fun validateInputs(): Boolean {
        val nombre = edtNombre.text.toString().trim()
        val precio = edtPrecio.text.toString().trim()
        val descripcion = edtDescripcion.text.toString().trim()
        val descuento = "0"
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
        val sharedPreferences =
            requireContext().getSharedPreferences("ProductPrefs", Context.MODE_PRIVATE)
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

    private fun limpiar() {
        edtNombre.setText("")
        edtPrecio.setText("")
        edtDescripcion.setText("")
        //edtDescuento.setText("")
        edtStock.setText("")
    }

    // Función para iniciar el escaneo
    private fun escanearCodigo() {
        val intentIntegrator = IntentIntegrator.forSupportFragment(this)
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        intentIntegrator.setPrompt("Lector de códigos")
        intentIntegrator.setCameraId(0)
        intentIntegrator.setBeepEnabled(true)
        intentIntegrator.setBarcodeImageEnabled(true)

        // Iniciar escaneo usando el launcher
        scanResultLauncher.launch(intentIntegrator.createScanIntent())
    }

    /*override*/ fun onActivityResultDeprecated(requestCode: Int, resultCode: Int, data: Intent?) {
//Instancia para recibir el resultado (Lectura de código)

        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
//Validar que no este vacia
        if (intentResult != null) {
//Validar leyo información
            if (intentResult.contents == null) {
//Mensaje informativo - no hubo datos
                Toast.makeText(requireContext(), "Lectura cancelada", Toast.LENGTH_SHORT).show()
            } else {
//Mensaje informativo - si hubo datos
                Toast.makeText(requireContext(), "Codigo Leido", Toast.LENGTH_SHORT).show()
//Colocar el codigo en la caja de texto
                code.setText(intentResult.contents)
            } //if-else = null
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }//if-else !=null
    }//onActivityResult

    private lateinit var scanResultLauncher: ActivityResultLauncher<Intent>
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddProductoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}