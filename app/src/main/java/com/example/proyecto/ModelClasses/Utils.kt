package com.example.proyecto.ModelClasses

import android.content.Context
import com.example.proyecto.ui.producto.addcategoria.AddCategoriaFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Utils {
    fun getCategoriesFromPreferences(context: Context): List<AddCategoriaFragment.Category> {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("categories", null)
        val type = object : TypeToken<List<AddCategoriaFragment.Category>>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()
    }
}