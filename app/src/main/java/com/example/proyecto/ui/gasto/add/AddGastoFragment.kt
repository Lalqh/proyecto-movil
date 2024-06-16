package com.example.proyecto.ui.gasto.add

import android.content.Context
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
import com.example.proyecto.ModelClasses.GastoData
import com.example.proyecto.ModelClasses.Utils
import com.example.proyecto.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

class AddGastoFragment : Fragment() {

    private lateinit var btnGuardarGasto: Button
    private lateinit var cancelar: Button
    private lateinit var edtTotalGasto: EditText
    private lateinit var edtDetallesGasto: EditText
    private lateinit var spnUsers: Spinner
    private lateinit var spnType: Spinner
    private lateinit var cal: CalendarView

    private lateinit var selectedUser: String
    private lateinit var selectedType: String
    private var selectedDate: Long = Calendar.getInstance().timeInMillis // Variable to store selected date

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_gasto, container, false)

        btnGuardarGasto = view.findViewById(R.id.btnGuardarGasto)
        cancelar = view.findViewById(R.id.btnCancelarGasto)
        edtTotalGasto = view.findViewById(R.id.edtTotalGasto)
        edtDetallesGasto = view.findViewById(R.id.edtDetallesGasto)
        spnUsers = view.findViewById(R.id.spnUserGasto)
        spnType = view.findViewById(R.id.spnGastoType)
        cal = view.findViewById(R.id.calGasto)

        initSpinnerUser()
        initSpinnerType()

        cal.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.timeInMillis
        }

        btnGuardarGasto.setOnClickListener {
            if (validarCampos()) {
                guardarGasto()
            } else {
                mostrarToast("Por favor, rellena todos los campos.")
            }
        }
        cancelar.setOnClickListener {
            limpiar()
        }
        return view
    }

    private fun guardarGasto() {
        val gasto = GastoData(selectedUser, selectedType, selectedDate.toString(), edtDetallesGasto.text.toString(), edtTotalGasto.text.toString())
        val sharedPreferences = requireContext().getSharedPreferences("GastoPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val gson = Gson()
        val productListJson = sharedPreferences.getString("gastos", null)
        val type = object : TypeToken<MutableList<GastoData>>() {}.type
        val gastoList: MutableList<GastoData> = if (productListJson != null) {
            gson.fromJson(productListJson, type)
        } else {
            mutableListOf()
        }

        gastoList.add(gasto)

        val newGastoListJson = gson.toJson(gastoList)
        editor.putString("gastos", newGastoListJson)

        editor.apply()
        mostrarToast("Se guardó un gasto")
        limpiar()
    }

    private fun limpiar() {
        val currentTimeMillis = Calendar.getInstance().timeInMillis
        cal.date = currentTimeMillis
        selectedDate = currentTimeMillis
        edtDetallesGasto.setText("")
        edtTotalGasto.setText("")
        spnUsers.setSelection(0)
        spnType.setSelection(0)
    }

    private fun initSpinnerUser() {
        val users = Utils.getUsersFromSharedPreferences(requireContext())
        if (users.isNotEmpty()) {
            selectedUser = users[0]
            val userArray = users.toTypedArray()

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

    private fun initSpinnerType() {
        val listaGastos = arrayOf(
            "Salarios y Beneficios del Personal",
            "Gastos de Alquiler",
            "Servicios Públicos",
            "Mantenimiento y Reparaciones",
            "Suministros de Oficina y Tienda",
            "Seguros",
            "Marketing y Publicidad",
            "Licencias y Permisos",
            "Tecnología y Software",
            "Gastos Financieros",
            "Gastos de Transporte",
            "Impuestos y Contribuciones",
            "Gastos de Limpieza y Seguridad",
            "Devoluciones y Pérdidas"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listaGastos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnType.adapter = adapter
        selectedType = listaGastos[0]

        spnType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedType = listaGastos[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun validarCampos(): Boolean {
        val totalGasto = edtTotalGasto.text.toString().trim()
        val detallesGasto = edtDetallesGasto.text.toString().trim()
        return totalGasto.isNotEmpty() && detallesGasto.isNotEmpty()
    }

    private fun mostrarToast(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }
}
