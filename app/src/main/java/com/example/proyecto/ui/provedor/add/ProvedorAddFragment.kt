package com.example.proyecto.ui.provedor.add

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.proyecto.ModelClasses.ProvedorData
import com.example.proyecto.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProvedorAddFragment : Fragment() {


    companion object {
        fun newInstance() = ProvedorAddFragment()
    }

    private lateinit var viewModel: ProvedorAddViewModel
    private lateinit var nombre:EditText
    private lateinit var mail:EditText
    private lateinit var phone:EditText
    private lateinit var rfc:EditText
    private lateinit var add:Button
    private lateinit var cancel:Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_provedor_add, container, false)

        nombre=view.findViewById(R.id.edtNombreProveedor)
        mail=view.findViewById(R.id.edtCorreo)
        phone=view.findViewById(R.id.edtTelefono)
        rfc=view.findViewById(R.id.edtRFC)
        add=view.findViewById(R.id.btnRegistrarProveedor)
        cancel=view.findViewById(R.id.btnCancelarProveedor)
        //Toast.makeText(requireContext(), "Probando toast", Toast.LENGTH_SHORT).show()
        add.setOnClickListener{

            if (validateInputs()) {
                val nombre_ = nombre.text.toString()
                val mail_ = mail.text.toString()
                val phone_ = phone.text.toString()
                val rfc_ = rfc.text.toString()

                val provedor = ProvedorData(nombre_, mail_, phone_, rfc_)

                saveProvedor(provedor)
                Toast.makeText(requireContext(), "Provedor guardado", Toast.LENGTH_SHORT).show()
            }
        }
        cancel.setOnClickListener{
            Toast.makeText(requireContext(), "cancelo el registro", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProvedorAddViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun saveProvedor(provedor: ProvedorData) {
        val sharedPreferences = requireContext().getSharedPreferences("ProvedorPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val gson = Gson()
        val provedorListJson = sharedPreferences.getString("provedores", null)
        val type = object : TypeToken<MutableList<ProvedorData>>() {}.type
        val provedorList: MutableList<ProvedorData> = if (provedorListJson != null) {
            gson.fromJson(provedorListJson, type)
        } else {
            mutableListOf()
        }

        provedorList.add(provedor)

        val newProvedorListJson = gson.toJson(provedorList)
        editor.putString("provedores", newProvedorListJson)
        editor.apply()
    }

    private fun validateInputs(): Boolean {
        val nombre = nombre.text.toString().trim()
        val mail = mail.text.toString().trim()
        val phone = phone.text.toString().trim()
        val rfc = rfc.text.toString().trim()

        if (nombre.isEmpty()) {
            showToast("Por favor, ingrese el nombre del producto.")
            return false
        }

        if (mail.isEmpty()) {
            showToast("Por favor, ingrese el precio del producto.")
            return false
        }

        if (phone.isEmpty()) {
            showToast("Por favor, ingrese la descripción del producto.")
            return false
        }

        if (rfc.isEmpty()) {
            showToast("Por favor, ingrese el descuento del producto.")
            return false
        }



        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}