package com.example.proyecto.ui.gasto.consultar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.MySQLConnection
import com.example.proyecto.R

class CheckGastoFragment : Fragment() {

    companion object {
        fun newInstance() = CheckGastoFragment()
    }

    private lateinit var rvGasto: RecyclerView
    private lateinit var gastoAdapter: GastoAdapter
    private var gastos: List<GastoData> = listOf()
    private lateinit var mySQLConnection: MySQLConnection

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_check_gasto, container, false)
        rvGasto = view.findViewById(R.id.rvGasto)
        rvGasto.layoutManager = LinearLayoutManager(requireContext())

        mySQLConnection = MySQLConnection(requireContext())
        loadGastos()

        return view
    }

    private fun loadGastos() {
        val query = """
            SELECT g.idGasto, g.id_usuario, g.monto, g.tipo, g.detalle, g.fecha, u.nombre 
            FROM gastos g 
            JOIN usuarios u ON g.id_usuario = u.id
        """
        mySQLConnection.selectDataAsync(query) { result ->
            gastos = result.map {
                GastoData(
                    idGasto = it["idGasto"]?.toInt() ?: 0,
                    idUsuario = it["id_usuario"]?.toInt() ?: 0,
                    monto = it["monto"]?.toFloat() ?: 0f,
                    tipo = it["tipo"] ?: "",
                    detalle = it["detalle"] ?: "",
                    fecha = it["fecha"] ?: "",
                    nombreUsuario = it["nombre"] ?: ""
                )
            }
            gastoAdapter = GastoAdapter(gastos)
            rvGasto.adapter = gastoAdapter
        }
    }

    data class GastoData(
        val idGasto: Int,
        val idUsuario: Int,
        val monto: Float,
        val tipo: String,
        val detalle: String,
        val fecha: String,
        val nombreUsuario: String
    )

    class GastoAdapter(private var gastos: List<GastoData>) : RecyclerView.Adapter<GastoAdapter.GastoViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastoViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.gasto_item, parent, false)
            return GastoViewHolder(view)
        }

        override fun onBindViewHolder(holder: GastoViewHolder, position: Int) {
            val gasto = gastos[position]
            holder.bind(gasto)
        }

        override fun getItemCount(): Int = gastos.size

        fun updateGastos(newGastos: List<GastoData>) {
            gastos = newGastos
            notifyDataSetChanged()
        }

        class GastoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val tvUserGastoI: TextView = itemView.findViewById(R.id.tvUserGastoI)
            private val tvTypeGastoI: TextView = itemView.findViewById(R.id.tvTypeGastoI)
            private val tvTotalGastoI: TextView = itemView.findViewById(R.id.tvTotalGastoI)
            private val tvDateGastoI: TextView = itemView.findViewById(R.id.tvDateGastoI)
            private val tvDetallesGastoI: TextView = itemView.findViewById(R.id.tvDetallesGastoI)

            fun bind(gasto: GastoData) {
                tvUserGastoI.text = gasto.nombreUsuario
                tvTypeGastoI.text = gasto.tipo
                tvTotalGastoI.text = gasto.monto.toString()
                tvDateGastoI.text = gasto.fecha
                tvDetallesGastoI.text = gasto.detalle
            }
        }
    }
}