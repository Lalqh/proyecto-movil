package com.example.proyecto.ui.usuario.add

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyecto.R
import com.example.proyecto.Usuario
import java.util.*

class AddUserFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var etName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etAge: EditText
    private lateinit var etMail: EditText
    private lateinit var etPassword: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_user, container, false)

        etName = view.findViewById(R.id.etName)
        etLastName = view.findViewById(R.id.etLastName)
        etAge = view.findViewById(R.id.etAge)
        etMail = view.findViewById(R.id.etMail)
        etPassword = view.findViewById(R.id.etPassword)
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)

        sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val lastName = etLastName.text.toString()
            val ageStr = etAge.text.toString()
            val email = etMail.text.toString()
            val password = etPassword.text.toString()

            if (name.isEmpty() || lastName.isEmpty() || ageStr.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Debe llenar todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                val age = ageStr.toInt()
                val user = Usuario(name, lastName, age, email, password)
                saveUser(user)
                Toast.makeText(requireContext(), "Usuario registrado", Toast.LENGTH_SHORT).show()
                clearFields()
            }
        }

        return view
    }

    private fun saveUser(user: Usuario) {
        val userId = UUID.randomUUID().toString()

        val editor = sharedPreferences.edit()
        editor.putString("$userId.nombre", user.nombre)
        editor.putString("$userId.apellido", user.apellido)
        editor.putInt("$userId.edad", user.edad)
        editor.putString("$userId.correoElectronico", user.correoElectronico)
        editor.putString("$userId.contrasena", user.contrasena)
        editor.apply()
    }

    private fun clearFields() {
        etName.text.clear()
        etLastName.text.clear()
        etAge.text.clear()
        etMail.text.clear()
        etPassword.text.clear()
    }
}
