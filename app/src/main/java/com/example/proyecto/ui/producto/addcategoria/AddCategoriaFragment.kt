package com.example.proyecto.ui.producto.addcategoria

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.proyecto.MySQLConnection
import com.example.proyecto.R

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
    private lateinit var mySQLConnection: MySQLConnection

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
        mySQLConnection = MySQLConnection(requireContext())

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
        val isActive = if (rbActiveCategory.isChecked) 1 else 0

        val query = "INSERT INTO categoria (nombreCategoria, descripcion, activo) VALUES (?, ?, ?)"
        val params = arrayOf(name, description, isActive.toString())

        mySQLConnection.insertDataAsync(query, *params) { result ->
            if (result) {
                Toast.makeText(requireContext(), "Categoría guardada exitosamente", Toast.LENGTH_SHORT).show()
                edtNameCategory.text.clear()
                edtDescripcionCategory.text.clear()
                rbActiveCategory.isChecked = false
            } else {
                Toast.makeText(requireContext(), "Error al guardar categoría", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddCategoriaViewModel::class.java)
        // TODO: Use the ViewModel
    }
}