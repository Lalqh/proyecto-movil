package com.example.proyecto.ui.venta.historial

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.proyecto.MySQLConnection
import com.example.proyecto.R

class HistorialVentaFragment : Fragment() {

    companion object {
        fun newInstance() = HistorialVentaFragment()
    }

    private lateinit var viewModel: HistorialVentaViewModel
    private lateinit var mySQLConnection: MySQLConnection
    private lateinit var linearLayout: LinearLayout
    private lateinit var progressBar: ProgressBar

    data class Venta(
        val id: Int,
        val usuario: Int,
        val producto: Int,
        val metodo_pago: Int,
        val fecha: String,
        val cantidad: Int,
        val total: Float
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_historial_venta, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mySQLConnection = MySQLConnection(requireContext())
        linearLayout = view.findViewById(R.id.linearLayoutVenta)
        progressBar = view.findViewById(R.id.pbLoopSell)

        loadVentas()
    }

    private fun loadVentas() {
        progressBar.visibility = View.VISIBLE
        val query = "SELECT id, usuario, producto, metodo_pago, fecha, cantidad, total FROM ventas"
        mySQLConnection.selectDataAsync(query) { result ->
            progressBar.visibility = View.GONE
            if (result.isNotEmpty()) {
                val ventas = result.map {
                    Venta(
                        it["id"]?.toInt() ?: 0,
                        it["usuario"]?.toInt() ?: 0,
                        it["producto"]?.toInt() ?: 0,
                        it["metodo_pago"]?.toInt() ?: 0,
                        it["fecha"] ?: "",
                        it["cantidad"]?.toInt() ?: 0,
                        it["total"]?.toFloat() ?: 0f
                    )
                }
                updateVentas(ventas)
            } else {
                Toast.makeText(context, "No se encontraron ventas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateVentas(ventas: List<Venta>) {
        linearLayout.removeAllViews()
        for (venta in ventas) {
            val textViewVenta = TextView(requireContext())
            textViewVenta.text =
                "Usuario: ${venta.usuario}\n" +
                        "Producto: ${venta.producto}\n" +
                        "Método de Pago: ${venta.metodo_pago}\n" +
                        "Fecha: ${formatDate(venta.fecha)}\n" +
                        "Cantidad: ${venta.cantidad}\n" +
                        "Total: ${venta.total}"
            textViewVenta.setBackgroundResource(R.drawable.bordes)
            linearLayout.addView(textViewVenta)
        }
    }

    private fun formatDate(dateString: String): String {
        return dateString
    }
}