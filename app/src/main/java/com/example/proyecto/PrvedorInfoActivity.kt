package com.example.proyecto

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.ModelClasses.OrdenAdapter
import com.example.proyecto.ModelClasses.ProvedorData
import com.example.proyecto.ModelClasses.Utils
import com.example.proyecto.ui.provedor.order_add.AddOrderFragment.OrdenData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
    private lateinit var del:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prvedor_info)

        val gson = Gson()
        val estras = intent.extras
        val provedorJson = estras?.getString("provedor") ?: return

        provedor = gson.fromJson(provedorJson, ProvedorData::class.java)

        tvProvedor = findViewById(R.id.tvNameProvedor)
        tvmail = findViewById(R.id.tvEmailProvedor)
        tvPhone = findViewById(R.id.tvNumeroProvedor)
        tvRFC = findViewById(R.id.tvRFCProvedor)
        rvOrden = findViewById(R.id.rvOrden)
        del=findViewById(R.id.btnDeleteProvedor)

        tvProvedor.text = "Proveedor: ${provedor.nombre}"
        tvmail.text = "Email: ${provedor.mail}"
        tvPhone.text = "Teléfono: ${provedor.phone}"
        tvRFC.text = "RFC: ${provedor.rfc}"

        ordenes = Utils.getOrdersFromPreferences(baseContext)


        rvOrden.layoutManager = LinearLayoutManager(this)
        ordenAdapter = OrdenAdapter(filteredOrders)
        rvOrden.adapter = ordenAdapter

        loadOrden()

        del.setOnClickListener{
            delete()
        }
    }

    private fun loadOrden() {
        filteredOrders = ordenes.filter { it.provedor == provedor.rfc }
        ordenAdapter.updateOrden(filteredOrders)
    }
    private fun delete() {
        // Obtener SharedPreferences y el editor
        val sharedPreferencesOrder = getSharedPreferences("OrderPrefs", Context.MODE_PRIVATE)
        val editorOrder = sharedPreferencesOrder.edit()
        val sharedPreferencesProvedor = getSharedPreferences("ProvedorPrefs", Context.MODE_PRIVATE)
        val editorProvedor = sharedPreferencesProvedor.edit()
        val gson = Gson()

        // Obtener la lista de proveedores desde SharedPreferences
        var provedores=Utils.getProvedoresFromPreferences(baseContext)

        // Obtener la lista de órdenes desde SharedPreferences

        var ordenesList= Utils.getOrdersFromPreferences(baseContext)

        // Filtrar la lista de proveedores para eliminar el proveedor específico
        val newProvedoresList = provedores.filter { it.rfc != provedor.rfc }

        // Filtrar la lista de órdenes para eliminar las órdenes relacionadas con el proveedor
        val newOrdenesList = ordenesList.filter { it.provedor != provedor.rfc }

        // Guardar las listas actualizadas en SharedPreferences
        val newProvedoresJson = gson.toJson(newProvedoresList)
        editorProvedor.putString("provedores", newProvedoresJson)

        val newOrdenesJson = gson.toJson(newOrdenesList)
        editorOrder.putString("ordenes", newOrdenesJson)

        editorOrder.apply()
        editorProvedor.apply()

        // Navegar de vuelta a main
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("is_admin", true)
        intent.putExtra("load_home_fragment", true)
        startActivity(intent)
    }

}
