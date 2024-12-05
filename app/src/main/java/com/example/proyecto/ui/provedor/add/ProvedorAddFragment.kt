package com.example.proyecto.ui.provedor.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyecto.MySQLConnection
import com.example.proyecto.R

class ProvedorAddFragment : Fragment() {

    data class Proveedor(
        val nombreProveedor: String,
        val nitProveedor: String,
        val correoProveedor: String,
        val telefono: String
    )

    private lateinit var nombre: EditText
    private lateinit var mail: EditText
    private lateinit var phone: EditText
    private lateinit var rfc: EditText
    private lateinit var add: Button
    private lateinit var cancel: Button
    private lateinit var mySQLConnection: MySQLConnection

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_provedor_add, container, false)

        nombre = view.findViewById(R.id.edtNombreProveedor)
        mail = view.findViewById(R.id.edtCorreo)
        phone = view.findViewById(R.id.edtTelefono)
        rfc = view.findViewById(R.id.edtRFC)
        add = view.findViewById(R.id.btnRegistrarProveedor)
        cancel = view.findViewById(R.id.btnCancelarProveedor)
        mySQLConnection = MySQLConnection(requireContext())

        add.setOnClickListener {
            if (validateInputs()) {
                val nombre_ = nombre.text.toString()
                val mail_ = mail.text.toString()
                val phone_ = phone.text.toString()
                val rfc_ = rfc.text.toString()

                val proveedor = Proveedor(nombre_, rfc_, mail_, phone_)

                saveProveedor(proveedor)
            }
        }

        cancel.setOnClickListener {
            Toast.makeText(requireContext(), "Registro cancelado", Toast.LENGTH_SHORT).show()
            clearFields()
        }

        return view
    }

    private fun saveProveedor(proveedor: Proveedor) {
        val query = "INSERT INTO proveedor (nombreProveedor, nitProveedor, correoProveedor, telefono) VALUES (?, ?, ?, ?)"
        val params = arrayOf(proveedor.nombreProveedor, proveedor.nitProveedor, proveedor.correoProveedor, proveedor.telefono)
        mySQLConnection.insertDataAsync(query, *params) { result ->
            if (result) {
                Toast.makeText(context, "Proveedor guardado correctamente", Toast.LENGTH_SHORT).show()
                clearFields()
            } else {
                Toast.makeText(context, "Error al guardar proveedor", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputs(): Boolean {
        val nombre = nombre.text.toString().trim()
        val mail = mail.text.toString().trim()
        val phone = phone.text.toString().trim()
        val rfc = rfc.text.toString().trim()

        if (nombre.isEmpty()) {
            showToast("Por favor, ingrese el nombre del proveedor.")
            return false
        }

        if (mail.isEmpty()) {
            showToast("Por favor, ingrese el email.")
            return false
        }

        if (phone.isEmpty()) {
            showToast("Por favor, ingrese el número telefónico.")
            return false
        }

        if (rfc.isEmpty()) {
            showToast("Por favor, ingrese el RFC.")
            return false
        }

        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun clearFields() {
        nombre.text.clear()
        mail.text.clear()
        phone.text.clear()
        rfc.text.clear()
    }
}