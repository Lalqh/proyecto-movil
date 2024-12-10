package com.example.proyecto.ui.venta.add

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.proyecto.MySQLConnection
import com.example.proyecto.R
import com.example.proyecto.QRViewActivity
import java.text.SimpleDateFormat
import java.util.*

class AddVentaFragment : Fragment() {

    companion object {
        fun newInstance() = AddVentaFragment()
        const val CHANNEL_ID = "venta_channel"
        const val NOTIFICATION_PERMISSION_CODE = 1001
    }

    private lateinit var viewModel: AddVentaViewModel
    private lateinit var mySQLConnection: MySQLConnection
    private lateinit var spinnerUsuario: Spinner
    private lateinit var spinnerProducto: Spinner
    private lateinit var spinnerMetodoPago: Spinner
    private lateinit var edtTotalVenta: EditText
    private lateinit var calendarView: CalendarView
    private lateinit var btnGuardarVenta: Button
    private lateinit var btnCancelarVenta: Button

    private var usuarios: List<Usuario> = listOf()
    private var productos: List<Producto> = listOf()

    data class Usuario(val id: Int, val nombre: String)
    data class Producto(val id: Int, val nombre: String, val precio: Float)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_venta, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createNotificationChannel()

        mySQLConnection = MySQLConnection(requireContext())

        spinnerUsuario = view.findViewById(R.id.spnUsuario)
        spinnerProducto = view.findViewById(R.id.edtProducto)
        spinnerMetodoPago = view.findViewById(R.id.spnMetodoPago)
        edtTotalVenta = view.findViewById(R.id.edtTotalVenta)
        calendarView = view.findViewById(R.id.calendarView3)
        btnGuardarVenta = view.findViewById(R.id.btnGuardarVenta)
        btnCancelarVenta = view.findViewById(R.id.btnCancelarVenta)

        loadUsuarios()
        loadProductos()

        val opcionesMetodoPago = arrayOf("Tarjeta", "Efectivo", "Transferencia")
        val adapterMetodoPago = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opcionesMetodoPago)
        adapterMetodoPago.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMetodoPago.adapter = adapterMetodoPago

        btnGuardarVenta.setOnClickListener {
            guardarVenta()
        }

        btnCancelarVenta.setOnClickListener {
            limpiarCampos()
            Toast.makeText(requireContext(), "Operación cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUsuarios() {
        val query = "SELECT id, nombre FROM usuarios"
        mySQLConnection.selectDataAsync(query) { result ->
            if (result.isNotEmpty()) {
                usuarios = result.map {
                    Usuario(
                        it["id"]?.toInt() ?: 0,
                        it["nombre"] ?: ""
                    )
                }
                val usuarioNombres = usuarios.map { it.nombre }
                val adapterUsuarios = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, usuarioNombres)
                adapterUsuarios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerUsuario.adapter = adapterUsuarios
            } else {
                Toast.makeText(context, "No se encontraron usuarios", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadProductos() {
        val query = "SELECT idProducto, nombreProducto, precio FROM producto"
        mySQLConnection.selectDataAsync(query) { result ->
            if (result.isNotEmpty()) {
                productos = result.map {
                    Producto(
                        it["idProducto"]?.toInt() ?: 0,
                        it["nombreProducto"] ?: "",
                        it["precio"]?.toFloat() ?: 0f
                    )
                }
                val productoNombres = productos.map { it.nombre }
                val adapterProductos = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, productoNombres)
                adapterProductos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerProducto.adapter = adapterProductos
            } else {
                Toast.makeText(context, "No se encontraron productos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarVenta() {
        try {
            val selectedUserIndex = spinnerUsuario.selectedItemPosition
            val selectedProductIndex = spinnerProducto.selectedItemPosition
            val cantidad = try {
                edtTotalVenta.text.toString().toInt()
            } catch (e: NumberFormatException) {
                -1
            }

            if (selectedUserIndex < 0 || selectedProductIndex < 0 || cantidad <= 0) {
                Toast.makeText(requireContext(), "Debes ingresar todos los campos correctamente", Toast.LENGTH_SHORT).show()
                return
            }

            val selectedUser = usuarios[selectedUserIndex]
            val selectedProduct = productos[selectedProductIndex]

            val idUsuario = selectedUser.id
            val idProducto = selectedProduct.id
            val metodoPagoTexto = spinnerMetodoPago.selectedItem.toString()
            val metodoPago = when (metodoPagoTexto) {
                "Tarjeta" -> 1
                "Efectivo" -> 2
                "Transferencia" -> 3
                else -> 0
            }

            val fechaSeleccionada = calendarView.date
            val fecha = formatDate(fechaSeleccionada)

            val totalVenta = selectedProduct.precio * cantidad

            val query = "INSERT INTO ventas (usuario, producto, metodo_pago, fecha, cantidad, total) VALUES (?, ?, ?, ?, ?, ?)"
            val params = arrayOf(idUsuario.toString(), idProducto.toString(), metodoPago.toString(), fecha, cantidad.toString(), totalVenta.toString())

            mySQLConnection.insertDataAsync(query, *params) { result ->
                if (result) {
                    Toast.makeText(requireContext(), "Venta guardada", Toast.LENGTH_SHORT).show()
                    limpiarCampos()
                    if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                        showNotification(selectedProduct.nombre, fecha)
                    } else {
                        requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_CODE)
                    }
                    if (metodoPago == 3) { // Transferencia
                        val intent = Intent(requireContext(), QRViewActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(requireContext(), "Error al guardar la venta", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Ocurrió un error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun limpiarCampos() {
        edtTotalVenta.setText("")
        spinnerUsuario.setSelection(0)
        spinnerMetodoPago.setSelection(0)
        spinnerProducto.setSelection(0)
        calendarView.date = System.currentTimeMillis()
    }

    private fun formatDate(dateInMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(productName: String, fecha: String) {
        val intent = Intent(requireContext(), AddVentaFragment::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Venta Agregada")
            .setContentText("La venta del producto $productName ha sido agregada correctamente el $fecha.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            with(NotificationManagerCompat.from(requireContext())) {
                notify(0, notificationBuilder.build())
            }
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                showNotification("Producto", formatDate(System.currentTimeMillis()))
            } else {
                Toast.makeText(requireContext(), "Permiso de notificación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}