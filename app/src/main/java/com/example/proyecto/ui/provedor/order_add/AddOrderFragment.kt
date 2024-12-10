package com.example.proyecto.ui.provedor.order_add

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.proyecto.MySQLConnection
import com.example.proyecto.ModelClasses.ProductoData
import com.example.proyecto.ModelClasses.ProvedorData
import com.example.proyecto.ModelClasses.Utils
import com.example.proyecto.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddOrderFragment : Fragment() {

    data class OrdenData(
        val provedor: String,
        val producto: String,
        val cantidad: String,
        val monto: String,
        var pagado: String,
        val delivery: String
    )
    data class ProvedorData(
        val id: Int,
        val nombre: String,
        val mail: String,
        val phone: String,
        val rfc: String,
        val nit: String
    )
    data class ProductoData(
        val id: Int,
        val nombre: String,
        val precio: Float,
        val stock: Int,
        val categoriaId: Int,
        val descripcion: String,
        val img: String
    )
    companion object {
        fun newInstance() = AddOrderFragment()
    }

    private lateinit var viewModel: AddOrderViewModel
    private lateinit var spnProvedor: Spinner
    private lateinit var spnProducto: Spinner
    private lateinit var etCantidad: EditText
    private lateinit var etMonto: EditText
    private lateinit var cal: CalendarView
    private lateinit var add: Button
    private lateinit var cancel: Button

    private lateinit var selectedProvedor: ProvedorData
    private lateinit var selectedProduct: ProductoData
    private var selectedDate: Long = Calendar.getInstance().timeInMillis // Variable to store selected date

    private lateinit var mySQLConnection: MySQLConnection

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_order, container, false)
        spnProvedor = view.findViewById(R.id.spnProvedorOrd)
        spnProducto = view.findViewById(R.id.spnProductOrd)
        etCantidad = view.findViewById(R.id.edtCantidad)
        etMonto = view.findViewById(R.id.edtMontoOrd)
        cal = view.findViewById(R.id.cvOrderDate)
        add = view.findViewById(R.id.btnGuardarOrd)
        cancel = view.findViewById(R.id.btnCancelarOrd)

        mySQLConnection = MySQLConnection(requireContext())

        cal.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.timeInMillis
        }

        add.setOnClickListener {
            if (validateInputs()) {
                val cantidad = etCantidad.text.toString().trim()
                val monto = etMonto.text.toString().trim()
                val order = OrdenData(selectedProvedor.nombre, selectedProduct.nombre, cantidad, monto, "0", formatDate(selectedDate))
                saveOrder(order)
                saveOrderToDatabase(order)

                val calendar = Calendar.getInstance()
                calendar.timeInMillis = selectedDate

                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH) // Note: Month is 0-based
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                showToast("Se guardó la orden. Fecha de entrega: ${year}/${month + 1}/${day}")
            }
        }

        cancel.setOnClickListener {
            etCantidad.setText("")
            etMonto.setText("")
        }

        setupSpinnerProvedor()
        setupSpinnerProducto()

        return view
    }

    private fun validateInputs(): Boolean {
        val cantidad = etCantidad.text.toString().trim()
        val monto = etMonto.text.toString().trim()

        if (cantidad.isEmpty()) {
            showToast("Por favor, ingrese la cantidad de producto de la orden.")
            return false
        }

        if (monto.isEmpty()) {
            showToast("Por favor, ingrese el monto total de la orden.")
            return false
        }

        if (selectedProvedor == null) {
            showToast("No hay proveedores, registre el proveedor antes de registrar la orden.")
            return false
        }
        if (selectedProduct == null) {
            showToast("No hay productos, registre el producto antes de registrar la orden.")
            return false
        }

        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun saveOrder(order: OrdenData) {
        val sharedPreferences = requireContext().getSharedPreferences("OrderPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val gson = Gson()
        val orderListJson = sharedPreferences.getString("ordenes", null)
        val type = object : TypeToken<MutableList<OrdenData>>() {}.type
        val orderList: MutableList<OrdenData> = if (orderListJson != null) {
            gson.fromJson(orderListJson, type)
        } else {
            mutableListOf()
        }

        orderList.add(order)

        val newOrderListJson = gson.toJson(orderList)
        editor.putString("ordenes", newOrderListJson)
        editor.apply()
    }

    private fun saveOrderToDatabase(order: OrdenData) {
        val fecha = formatDate(selectedDate)
        val query = "INSERT INTO ordendecompra (cantidadSolicitada, fecha, montoCompra, Producto_id, Proveedor_id, pagado) VALUES (?, ?, ?, ?, ?, ?)"
        val params = arrayOf(order.cantidad, fecha, order.monto, selectedProduct.id.toString(), selectedProvedor.id.toString(), "0")

        mySQLConnection.insertDataAsync(query, *params) { result ->
            if (result) {
                showToast("Orden guardada en la base de datos.")
            } else {
                showToast("Error al guardar la orden en la base de datos.")
            }
        }
    }

    private fun setupSpinnerProvedor() {
        val query = "SELECT idProveedor, nombreProveedor FROM proveedor"
        mySQLConnection.selectDataAsync(query) { result ->
            if (result.isNotEmpty()) {
                val provedores = result.map {
                    ProvedorData(
                        it["idProveedor"]?.toInt() ?: 0,
                        it["nombreProveedor"] ?: "",
                        "", "", "", ""
                    )
                }
                selectedProvedor = provedores[0]

                val provedorNames = provedores.map { it.nombre }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, provedorNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnProvedor.adapter = adapter

                spnProvedor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        selectedProvedor = provedores[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
            } else {
                showToast("No se encontraron proveedores.")
            }
        }
    }

    private fun setupSpinnerProducto() {
        val query = "SELECT idProducto, nombreProducto FROM producto"
        mySQLConnection.selectDataAsync(query) { result ->
            if (result.isNotEmpty()) {
                val productos = result.map {
                    ProductoData(
                        it["idProducto"]?.toInt() ?: 0,
                        it["nombreProducto"] ?: "",
                        0f, 0, 0, "", ""
                    )
                }
                selectedProduct = productos[0]

                val productoNames = productos.map { it.nombre }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, productoNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnProducto.adapter = adapter

                spnProducto.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        selectedProduct = productos[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
            } else {
                showToast("No se encontraron productos.")
            }
        }
    }

    private fun formatDate(dateInMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddOrderViewModel::class.java)
        // TODO: Use the ViewModel
    }
}