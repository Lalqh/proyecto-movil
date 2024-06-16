package com.example.proyecto.ui.gasto.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyecto.ModelClasses.Utils
import com.example.proyecto.R

class AddGastoFragment : Fragment() {

    private lateinit var btnGuardarGasto: Button
    private lateinit var edtTotalGasto: EditText
    private lateinit var edtDetallesGasto: EditText
    private lateinit var spnUsers:Spinner
    private lateinit var spnType:Spinner
    private lateinit var cal:CalendarView


    private lateinit var selectedUser:String
    private lateinit var selectedType:String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_gasto, container, false)

        btnGuardarGasto = view.findViewById(R.id.btnGuardarGasto)
        edtTotalGasto = view.findViewById(R.id.edtTotalGasto)
        edtDetallesGasto = view.findViewById(R.id.edtDetallesGasto)

        initSpinnerUser()

        btnGuardarGasto.setOnClickListener {
            if (validarCampos()) {
                guardarGasto()
            } else {
                mostrarToast("Por favor, rellena todos los campos.")
            }
        }
        return view
    }
    private fun initSpinnerUser(){
        val users=Utils.getUsersFromSharedPreferences(requireContext())
        if (users.isNotEmpty()){
            selectedUser=users[0]
            val userArray=users.toTypedArray()

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, userArray)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnUsers.adapter = adapter

            spnUsers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    selectedUser = users[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        }
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