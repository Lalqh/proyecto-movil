package com.example.proyecto.ModelClasses

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.proyecto.ui.producto.addcategoria.AddCategoriaFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Utils {
    private val PRODUCT_PREFS = "ProductPrefs"
    private val CATEGORIES_PREFS = "CategoriesPrefs"
    fun getCategoriesFromPreferences(context: Context): List<AddCategoriaFragment.Category> {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("categories", null)
        val type = object : TypeToken<List<AddCategoriaFragment.Category>>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()
    }
    fun getProductosFromPreferences(context: Context): List<ProductoData> {
        val sharedPreferences = context.getSharedPreferences(PRODUCT_PREFS, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("productos", null)
        val type = object : TypeToken<List<ProductoData>>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()
    }
     fun saveProductosToPreferences(context: Context,productos: List<ProductoData>) {
        val sharedPreferences = context.getSharedPreferences(PRODUCT_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(productos)
        editor.putString("productos", json)
        editor.apply()
    }
}