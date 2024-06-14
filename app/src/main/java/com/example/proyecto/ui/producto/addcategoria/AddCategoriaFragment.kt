package com.example.proyecto.ui.producto.addcategoria

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import com.example.proyecto.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class AddCategoriaFragment : Fragment() {
    data class Category(
        val name: String,
        val description: String,
        val isActive: Boolean
    )
    private lateinit var edtNameCategory: EditText
    private lateinit var edtDescripcionCategory: EditText
    private lateinit var rbActiveCategory: RadioButton
    private lateinit var btnAddCategory: Button
    private lateinit var btnCancelCategory: Button
    companion object {
        fun newInstance() = AddCategoriaFragment()
    }

    private lateinit var viewModel: AddCategoriaViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_categoria, container, false)

        edtNameCategory = view.findViewById(R.id.edtNameCategory)
        edtDescripcionCategory = view.findViewById(R.id.edtDescripcionCategory)
        rbActiveCategory = view.findViewById(R.id.rbActiveCategory)
        btnAddCategory = view.findViewById(R.id.btnAddCategory)
        btnCancelCategory = view.findViewById(R.id.btnCancelCategory)

        btnAddCategory.setOnClickListener {
            if (validateInputs()) {
                saveCategory()
            }
        }

        btnCancelCategory.setOnClickListener {
            // Clear the fields or handle cancel action
            edtNameCategory.text.clear()
            edtDescripcionCategory.text.clear()
            rbActiveCategory.isChecked = false
        }

        return view
    }
    private fun validateInputs(): Boolean {
        var isValid = true

        if (edtNameCategory.text.toString().trim().isEmpty()) {
            edtNameCategory.error = "Nombre de categoría es requerido"
            isValid = false
        }

        if (edtDescripcionCategory.text.toString().trim().isEmpty()) {
            edtDescripcionCategory.error = "Descripción es requerida"
            isValid = false
        }

        return isValid
    }

    private fun saveCategory() {
        val name = edtNameCategory.text.toString().trim()
        val description = edtDescripcionCategory.text.toString().trim()
        val isActive = rbActiveCategory.isChecked

        val newCategory = Category(name, description, isActive)

        val categories = getCategoriesFromPreferences(requireContext()).toMutableList()
        categories.add(newCategory)
        saveCategoriesToPreferences(requireContext(), categories)

        Toast.makeText(requireContext(), "Categoría guardada exitosamente", Toast.LENGTH_SHORT).show()
        edtNameCategory.text.clear()
        edtDescripcionCategory.text.clear()
        rbActiveCategory.isChecked = false
    }

    private fun saveCategoriesToPreferences(context: Context, categories: List<Category>) {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(categories)
        editor.putString("categories", json)
        editor.apply()
    }
    private fun getCategoriesFromPreferences(context: Context): List<Category> {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("categories", null)
        val type = object : TypeToken<List<Category>>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddCategoriaViewModel::class.java)
        // TODO: Use the ViewModel
    }

}