package com.example.proyecto.ui.venta.historial

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.proyecto.ModelClasses.Venta
import com.example.proyecto.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistorialVentaFragment : Fragment() {

    companion object {
        fun newInstance() = HistorialVentaFragment()
    }

    private lateinit var viewModel: HistorialVentaViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_historial_venta, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HistorialVentaViewModel::class.java)

        // Recuperar las ventas de SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("VentaPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val ventaListJson = sharedPreferences.getString("ventas", null)
        val type = object : TypeToken<MutableList<Venta>>() {}.type
        val ventaList: MutableList<Venta> = if (ventaListJson != null) {
            gson.fromJson(ventaListJson, type)
        } else {
            mutableListOf()
        }


        val linearLayout: LinearLayout = view?.findViewById(R.id.linearLayoutVenta) ?: return


        for (venta in ventaList) {
            val textViewVenta = TextView(context)
            textViewVenta.text =
                "Usuario: ${venta.idUsuario},\n" +
                        "Producto: ${venta.idProducto},\n" +
                        "Método de Pago: ${venta.metodoPago},\n" +
                        "Fecha: ${venta.fecha},\n" +
                        "Cantidad: ${venta.cantidad},\n" +
                        "Total: ${venta.total}"
            linearLayout?.addView(textViewVenta)
        }
    }

}