package com.example.proyecto.ModelClasses

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.proyecto.ui.producto.addcategoria.AddCategoriaFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Utils {
    private val PRODUCT_PREFS = "ProductPrefs"
    private val CATEGORIES_PREFS = "CategoriesPrefs"
    private val PROVEDOR_PREFS="ProvedorPrefs"
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
    fun getProvedoresFromPreferences(context: Context): List<ProvedorData> {
        val sharedPreferences = context.getSharedPreferences(PROVEDOR_PREFS, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("provedores", null)
        val type = object : TypeToken<List<ProvedorData>>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()
    }

    fun getUsersFromSharedPreferences(context: Context): List<String> {
        val sharedPreferences = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val allEntries = sharedPreferences.all
        val users = mutableListOf<String>()

        for ((key, _) in allEntries) {
            if (key.endsWith(".nombre")) {
                val userId = key.removeSuffix(".nombre").removePrefix("user_")
                val storedName = sharedPreferences.getString("$userId.nombre", "")
                users.add(storedName ?: "")
            }
        }
        return users
    }
    fun getUsersIDFromSharedPreferences(context: Context): List<String> {
        val sharedPreferences = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val allEntries = sharedPreferences.all
        val users = mutableListOf<String>()

        for ((key, _) in allEntries) {
            if (key.endsWith(".nombre")) {
                val userId = key.removeSuffix(".nombre").removePrefix("user_")
                users.add(userId)
            }
        }
        return users
    }
}