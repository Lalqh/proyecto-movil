package com.example.proyecto.ModelClasses

import android.provider.ContactsContract.CommonDataKinds.Phone
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.R

data class ProvedorData(
    var nombre: String,
    var mail: String,
    var phone: String,
    var rfc: String
)


class ProvedorAdapter(private var provedores:List<ProvedorData>):RecyclerView.Adapter<ProvedorAdapter.ProvedorViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProvedorAdapter.ProvedorViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.provedor_item,parent,false)
        return ProvedorViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: ProvedorViewHolder, position: Int) {
        val provedor = provedores[position]
        holder.bind(provedor)
    }
    fun updateProvedores(newProvedores: List<ProvedorData>) {
        provedores = newProvedores
        notifyDataSetChanged()
    }
    override fun getItemCount() = provedores.size

    class ProvedorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTextView: TextView = itemView.findViewById(R.id.tvProvedorI)
        private val mailTextView: TextView = itemView.findViewById(R.id.tvMailProvedorI)
        private val phoneTextView: TextView = itemView.findViewById(R.id.tvPhoneProvedorI)
        private val rfcTextView: TextView = itemView.findViewById(R.id.tvRFCProvedorI)

        fun bind(provedor: ProvedorData) {
            nombreTextView.text = "Nombre: ${provedor.nombre}"
            mailTextView.text = "email: ${provedor.mail}"
            phoneTextView.text = "telefono: ${provedor.phone}"
            rfcTextView.text = "RFC: ${provedor.rfc}"
            itemView.setOnClickListener{
                Toast.makeText(itemView.context, "TODO abrir info", Toast.LENGTH_SHORT).show()
            }
        }
    }
}