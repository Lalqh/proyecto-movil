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
import com.example.proyecto.ModelClasses.ProductoData
import com.example.proyecto.ModelClasses.Venta
import com.example.proyecto.QRViewActivity
import com.example.proyecto.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class AddVentaFragment : Fragment() {

    companion object {
        fun newInstance() = AddVentaFragment()
        const val CHANNEL_ID = "venta_channel"
        const val NOTIFICATION_PERMISSION_CODE = 1001
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

        createNotificationChannel()

        val opciones = arrayOf("Tarjeta", "Efectivo", "Transferencia")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinner: Spinner = view.findViewById(R.id.spnMetodoPago)
        spinner.adapter = adapter

        val usuarios = getAllUsersFromSharedPreferences().toMutableList()
        usuarios.add(0, "Seleccione un usuario")
        usuarios.add(1, "Administrador")
        val usuariosArray = usuarios.toTypedArray()
        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, usuariosArray)
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
            try {
                val selectedUserIndex = spinner2.selectedItemPosition
                val selectedProductIndex = spinner3.selectedItemPosition
                val cantidad = try {
                    total.text.toString().toInt()
                } catch (e: NumberFormatException) {
                    -1
                }

                if (selectedUserIndex == 0 || selectedProductIndex < 0 || cantidad <= 0) {
                    Toast.makeText(requireContext(), "Debes ingresar todos los campos correctamente", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val selectedUser = usuarios[selectedUserIndex]
                val selectedProduct = productos[selectedProductIndex]

                val idUsuario = selectedUser
                val idProducto = selectedProduct.nombre
                val metodoPago = spinner.selectedItem.toString()

                val fechaSeleccionada = calendario.date
                val fecha = formatDate(fechaSeleccionada)

                val totalVenta = selectedProduct.precio.toFloat() * cantidad

                val venta = Venta(idUsuario, idProducto, metodoPago, fecha, cantidad, totalVenta)
                if (metodoPago === "Transferencia") {
                    venta.pagado = false
                }
                saveVenta(venta)
                Toast.makeText(requireContext(), "Venta guardada", Toast.LENGTH_SHORT).show()
                limpiarCampos()

                if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    showNotification(selectedProduct.nombre, fecha)
                } else {
                    requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_CODE)
                }

                if (metodoPago === "Transferencia") {
                    val intent = Intent(requireContext(), QRViewActivity::class.java)
                    startActivity(intent) // Iniciar la nueva actividad
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Ocurrió un error", Toast.LENGTH_SHORT).show()
            }
        }

        val btnCancelarVenta: Button = view.findViewById(R.id.btnCancelarVenta)
        btnCancelarVenta.setOnClickListener {
            limpiarCampos()
            Toast.makeText(requireContext(), "Operación cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddVentaViewModel::class.java)
    }

    private fun limpiarCampos() {
        view?.findViewById<EditText>(R.id.edtTotalVenta)?.setText("")
        view?.findViewById<Spinner>(R.id.spnUsuario)?.setSelection(0)
        view?.findViewById<Spinner>(R.id.spnMetodoPago)?.setSelection(0)
        view?.findViewById<Spinner>(R.id.edtProducto)?.setSelection(0)
        view?.findViewById<CalendarView>(R.id.calendarView3)?.date = System.currentTimeMillis()
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
        val ventaJson = gson.toJson(venta)

        // Guardar el JSON en SharedPreferences con la clave "venta"
        editor.putString("venta", ventaJson)
        //editor.apply() // Aplicar los cambios
        editor.apply()
    }

    private fun formatDate(dateInMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
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