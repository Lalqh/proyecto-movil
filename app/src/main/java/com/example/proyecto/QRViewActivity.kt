package com.example.proyecto

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.proyecto.ModelClasses.Venta
import com.google.gson.Gson

class QRViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrview)

        val ventaGuardada = obtenerVenta()

    }

    private fun obtenerVenta(): Venta? {
        val sharedPreferences = getSharedPreferences("VentaPrefs", Context.MODE_PRIVATE)
        val gson = Gson()

        // Obtener el JSON guardado
        val ventaJson = sharedPreferences.getString("venta", null)

        // Si hay un JSON guardado, convertirlo a un objeto Venta
        return if (ventaJson != null) {
            gson.fromJson(ventaJson, Venta::class.java)
        } else {
            null // Si no hay datos guardados
        }
    }
}