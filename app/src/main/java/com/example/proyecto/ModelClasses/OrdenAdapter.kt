package com.example.proyecto.ModelClasses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.R
import com.example.proyecto.ui.provedor.order_add.AddOrderFragment.OrdenData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

class OrdenAdapter(
    private var ordenes: List<OrdenData>
) : RecyclerView.Adapter<OrdenAdapter.OrdenViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdenViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.orden_item, parent, false)
        return OrdenViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrdenViewHolder, position: Int) {
        val orden = ordenes[position]
        holder.bind(orden, position)
    }

    override fun getItemCount(): Int {
        return ordenes.size
    }

    fun updateOrden(newOrdenes: List<OrdenData>) {
        ordenes = newOrdenes
        notifyDataSetChanged()
    }

    inner class OrdenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvProvedor: TextView = itemView.findViewById(R.id.tvProvedorOrdI)
        private val tvProducto: TextView = itemView.findViewById(R.id.tvProductoOrdI)
        private val tvCantidad: TextView = itemView.findViewById(R.id.tvCantidadOrdI)
        private val tvMonto: TextView = itemView.findViewById(R.id.tvMontoOtdI)
        private val tvDeliveryDate: TextView = itemView.findViewById(R.id.tvDeliveryDateOrdI)
        private val tvPagado: TextView = itemView.findViewById(R.id.tvPagadoOrdI)

        fun bind(orden: OrdenData, position: Int) {
            val provedores = Utils.getProvedoresFromPreferences(itemView.context)
            var provedorName: String = ""
            for (item in provedores) {
                if (item.rfc == orden.provedor) {
                    provedorName = item.nombre
                    break
                }
            }

            tvProvedor.text = "Proveedor: $provedorName"
            tvProducto.text = "Producto: ${orden.producto}"
            tvCantidad.text = "Cantidad: ${orden.cantidad}"
            tvMonto.text = "Monto: ${orden.monto}"
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = orden.delivery.toLong()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            tvDeliveryDate.text = "Fecha de Entrega: $year/${month + 1}/$day"
            tvPagado.text = if (orden.pagado == "0") "pagado: false" else "pagado:true"

            itemView.setOnClickListener {
                val sharedPreferences = itemView.context.getSharedPreferences("OrderPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                val gson = Gson()

                // Obtener la lista de órdenes desde SharedPreferences
                val ordenesJson = sharedPreferences.getString("ordenes", null)
                val type = object : TypeToken<List<OrdenData>>() {}.type
                val ordenList: MutableList<OrdenData> = if (ordenesJson != null) {
                    gson.fromJson(ordenesJson, type)
                } else {
                    mutableListOf()
                }

                // Buscar y actualizar la orden específica
                for (item in ordenList) {
                    if (item == orden) {
                        item.pagado = if (item.pagado == "0") "1" else "0"
                        break
                    }
                }

                // Guardar la lista actualizada en SharedPreferences
                val newOrdenesJson = gson.toJson(ordenList)
                editor.putString("ordenes", newOrdenesJson)
                editor.apply()

                // Notificar al usuario que la orden fue actualizada
                Toast.makeText(itemView.context, "Orden actualizada", Toast.LENGTH_SHORT).show()

                // Actualizar el TextView de pagado
                orden.pagado = if (orden.pagado == "0") "1" else "0"
                tvPagado.text = if (orden.pagado == "0") "pagado: false" else "pagado:true"

                // Notificar al adaptador que el elemento ha cambiado
                notifyItemChanged(position)
            }
        }
    }
}
