package com.example.proyecto.ModelClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.R
import java.util.Calendar

data class GastoData(
    val user: String,
    val type: String,
    val date: String,
    val detalles: String,
    val total: String
)

class GastoAdapter(private var gastos: List<GastoData>) : RecyclerView.Adapter<GastoAdapter.GastoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.gasto_item, parent, false)
        return GastoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GastoViewHolder, position: Int) {
        val gasto = gastos[position]
        holder.bind(gasto)
    }

    fun updateGastos(newGastos: List<GastoData>) {
        gastos = newGastos
        notifyDataSetChanged()
    }

    override fun getItemCount() = gastos.size

    class GastoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userTextView: TextView = itemView.findViewById(R.id.tvUserGastoI)
        private val typeTextView: TextView = itemView.findViewById(R.id.tvTypeGastoI)
        private val totalTextView: TextView = itemView.findViewById(R.id.tvTotalGastoI)
        private val dateTextView: TextView = itemView.findViewById(R.id.tvDateGastoI)
        private val detallesTextView: TextView = itemView.findViewById(R.id.tvDetallesGastoI)

        fun bind(gasto: GastoData) {
            userTextView.text = "Usuario: ${gasto.user}"
            typeTextView.text = "Tipo: ${gasto.type}"
            totalTextView.text = "Total: ${gasto.total}"

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = gasto.date.toLong()

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) // Note: Month is 0-based
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            dateTextView.text = "Fecha: ${year}/${month}/${day}"
            detallesTextView.text = "Detalles: ${gasto.detalles}"

        }
    }
}
