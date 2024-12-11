package com.example.proyecto.ui.producto.add

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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
import com.example.proyecto.Utils.fetchData
import com.example.proyecto.ui.producto.addcategoria.AddCategoriaFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.integration.android.IntentIntegrator
import java.io.ByteArrayOutputStream

class AddProductoFragment : Fragment() {

    companion object {
        fun newInstance() = AddProductoFragment()
    }
    data class Category(
        val id:Int,
        val name: String,
        val description: String,
        val isActive: Boolean
    )

    private lateinit var viewModel: AddProductoViewModel
    private lateinit var edtNombre: EditText
    private lateinit var edtPrecio: EditText
    private lateinit var edtDescripcion: EditText
    private lateinit var edtDescuento: EditText
    private lateinit var edtStock: EditText
    private lateinit var spnCategory: Spinner
    private lateinit var btnGuardarProducto: Button
    private lateinit var btnCancelar: Button
    private lateinit var btnImagen:Button

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    private var prodImageUrl: String? = null

    //private var categoriaSeleccionada: String = ""
    private lateinit var btnBuscarProducto: Button
    private lateinit var btnEliminarProducto: Button
    private lateinit var categoriaSeleccionada:Category
    private lateinit var scanProduct: Button
    private lateinit var code: TextView
    private lateinit var mySQLConnection: MySQLConnection
    private lateinit var prodImage: ImageView

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
        btnImagen=view.findViewById(R.id.btnCargarImagen)
        code = view.findViewById(R.id.tvQR)
        prodImage=view.findViewById(R.id.ivProductoImagen)


        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val selectedImageUri = result.data!!.data
                if (selectedImageUri != null) {
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedImageUri)
                        prodImage.setImageBitmap(bitmap)
                        val encodedImage = encodeImageToBase64(bitmap)
                        fetchData(requireContext(), encodedImage) { imageUrl ->
                            if (imageUrl != null) {
                                prodImageUrl = imageUrl
                            } else {
                                Toast.makeText(requireContext(), "Error al cargar la imagen. Intente nuevamente.", Toast.LENGTH_SHORT).show()
                            }
                        }

                    } catch (e: Exception) {
                        Log.e("UserInfoActivity", "Error processing selected image", e)
                    }
                }
            }
        }
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
            if (prodImageUrl == null) {
                Toast.makeText(requireContext(), "La imagen aún no se ha cargado. Por favor, espere.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (validateInputs()) {
                val producto = ProductoData(
                    nombre = edtNombre.text.toString(),
                    precio = edtPrecio.text.toString(),
                    descripcion = edtDescripcion.text.toString(),
                    descuento = "0",
                    stock = edtStock.text.toString(),
                    categoria = categoriaSeleccionada.name,
                    code = code.text.toString(),
                    img = prodImageUrl!!
                )

                saveProductToDatabase(producto)
                Toast.makeText(requireContext(), "Producto guardado", Toast.LENGTH_SHORT).show()
            }
        }


        btnEliminarProducto.setOnClickListener {
            eliminarProducto()
        }
        btnBuscarProducto.setOnClickListener { buscarProducto() }
        btnImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        return view
    }

    private fun setupSpinner() {
        val query = "SELECT idCategoria, nombreCategoria, descripcion, activo FROM categoria"
        var categories = emptyList<Category>()
        mySQLConnection.selectDataAsync(query) { result ->
            if (result.isNotEmpty()) {
                categories = result.map {
                    Category(
                        it["idCategoria"]?.toInt()?:-1,
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

    private fun updateSpinner(categories: List<Category>) {
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

        if (prodImageUrl==null){
            showToast("Por favor, ingrese una imagen.")
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
    private fun saveProductToDatabase(producto: ProductoData) {
        val query = """
        INSERT INTO producto (nombreProducto, precio, stock, Categoria_id, descripcion, img,qrcode) 
        VALUES (?, ?, ?, ?, ?, ?,?)
    """.trimIndent()

        val params = arrayOf(
            producto.nombre,
            producto.precio,
            producto.stock,
            categoriaSeleccionada.id.toString(), // Cambia según la estructura de `Category`
            producto.descripcion,
            producto.img, // Si no se maneja una imagen, puedes pasar un valor vacío o nulo,
            producto.code
        )

        mySQLConnection.insertDataAsync(query, *params) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Producto guardado en la base de datos", Toast.LENGTH_SHORT).show()
                limpiar() // Limpia los campos del formulario
            } else {
                Toast.makeText(requireContext(), "Error al guardar el producto", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun encodeImageToBase64(image: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP) // Asegúrate de usar Base64.NO_WRAP para evitar saltos de línea
    }


}