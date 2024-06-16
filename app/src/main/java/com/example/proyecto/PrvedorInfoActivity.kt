package com.example.proyecto

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.ModelClasses.OrdenAdapter
import com.example.proyecto.ModelClasses.ProvedorData
import com.example.proyecto.ModelClasses.Utils
import com.example.proyecto.ui.provedor.order_add.AddOrderFragment.OrdenData
import com.google.gson.Gson

class PrvedorInfoActivity : AppCompatActivity() {

    private lateinit var provedor: ProvedorData
    private lateinit var tvProvedor: TextView
    private lateinit var tvmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvRFC: TextView
    private lateinit var rvOrden: RecyclerView
    private var ordenes: List<OrdenData> = listOf()
    private var filteredOrders: List<OrdenData> = listOf()
    private lateinit var ordenAdapter: OrdenAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prvedor_info)

        val gson = Gson()
        val estras = intent.extras
        val provedorJson = estras?.getString("provedor") ?: return

        provedor = gson.fromJson(provedorJson, ProvedorData::class.java)
        Toast.makeText(this, provedorJson, Toast.LENGTH_SHORT).show()

        tvProvedor = findViewById(R.id.tvNameProvedor)
        tvmail = findViewById(R.id.tvEmailProvedor)
        tvPhone = findViewById(R.id.tvNumeroProvedor)
        tvRFC = findViewById(R.id.tvRFCProvedor)
        rvOrden = findViewById(R.id.rvOrden)

        tvProvedor.text = "Proveedor: ${provedor.nombre}"
        tvmail.text = "Email: ${provedor.mail}"
        tvPhone.text = "Teléfono: ${provedor.phone}"
        tvRFC.text = "RFC: ${provedor.rfc}"

        ordenes = Utils.getOrdersFromPreferences(baseContext)

        if (ordenes.isEmpty()) {
            Toast.makeText(this, "Órdenes vacías", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, ordenes[0].provedor, Toast.LENGTH_SHORT).show()
        }

        rvOrden.layoutManager = LinearLayoutManager(this)
        ordenAdapter = OrdenAdapter(filteredOrders)
        rvOrden.adapter = ordenAdapter

        loadOrden()
    }

    private fun loadOrden() {
        filteredOrders = ordenes.filter { it.provedor == provedor.rfc }
        ordenAdapter.updateOrden(filteredOrders)
    }
}
