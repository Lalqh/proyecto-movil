package com.example.proyecto.ui.gasto.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyecto.R

class AddGastoFragment : Fragment() {

    private lateinit var btnGuardarGasto: Button
    private lateinit var edtTotalGasto: EditText
    private lateinit var edtDetallesGasto: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_gasto, container, false)

        btnGuardarGasto = view.findViewById(R.id.btnGuardarGasto)
        edtTotalGasto = view.findViewById(R.id.edtTotalGasto)
        edtDetallesGasto = view.findViewById(R.id.edtDetallesGasto)

        btnGuardarGasto.setOnClickListener {
            if (validarCampos()) {
                guardarGasto()
            } else {
                mostrarToast("Por favor, rellena todos los campos.")
            }
        }
        return view
    }

    private fun validarCampos(): Boolean {
        val totalGasto = edtTotalGasto.text.toString().trim()
        val detallesGasto = edtDetallesGasto.text.toString().trim()
        return totalGasto.isNotEmpty() && detallesGasto.isNotEmpty()
    }

    private fun guardarGasto() {
    }

    private fun mostrarToast(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }
}