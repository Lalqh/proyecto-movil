package com.example.proyecto.ui.gasto.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.proyecto.MySQLConnection
import com.example.proyecto.R
import java.text.SimpleDateFormat
import java.util.*

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
    private var selectedDate: String = ""
    private lateinit var mySQLConnection: MySQLConnection

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
        mySQLConnection = MySQLConnection(requireContext())

        initSpinnerUser()
        initSpinnerType()

        cal.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            selectedDate = format.format(calendar.time)
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
        val idUsuario = selectedUser.split(" - ")[0].toInt()
        val monto = edtTotalGasto.text.toString().toFloat()
        val detalle = edtDetallesGasto.text.toString()

        val query = "INSERT INTO gastos (id_usuario, monto, tipo, detalle, fecha) VALUES (?, ?, ?, ?, ?)"
        val params = arrayOf(idUsuario.toString(), monto.toString(), selectedType, detalle, selectedDate)
        mySQLConnection.insertDataAsync(query, *params) { result ->
            if (result) {
                mostrarToast("Gasto registrado correctamente")
                limpiar()
            } else {
                mostrarToast("Error al registrar el gasto")
            }
        }
    }

    private fun limpiar() {
        val currentTimeMillis = Calendar.getInstance().timeInMillis
        cal.date = currentTimeMillis
        selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentTimeMillis)
        edtDetallesGasto.setText("")
        edtTotalGasto.setText("")
        spnUsers.setSelection(0)
        spnType.setSelection(0)
    }

    private fun initSpinnerUser() {
        val query = "SELECT id, nombre FROM usuarios"
        mySQLConnection.selectDataAsync(query) { result ->
            val userList = result.map { "${it["id"]} - ${it["nombre"]}" }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, userList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnUsers.adapter = adapter

            if (userList.isNotEmpty()) {
                selectedUser = userList[0]
            }

            spnUsers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    selectedUser = userList[position]
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
        return totalGasto.isNotEmpty() && detallesGasto.isNotEmpty() && selectedDate.isNotEmpty()
    }

    private fun mostrarToast(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }
}