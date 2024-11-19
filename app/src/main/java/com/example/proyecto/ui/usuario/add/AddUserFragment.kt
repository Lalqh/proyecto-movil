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
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.proyecto.MySQLConnection
import com.example.proyecto.R
import com.example.proyecto.Usuario
import com.example.proyecto.Utils.EncryptionUtils
import java.util.*

class AddUserFragment : Fragment() {

    private lateinit var etName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etAge: EditText
    private lateinit var etMail: EditText
    private lateinit var etPassword: EditText
    private lateinit var mySQLConnection: MySQLConnection

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
        mySQLConnection = MySQLConnection(requireContext())

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
            }
        }

        return view
    }

    private fun saveUser(user: Usuario) {
        val passwordEncrypted = EncryptionUtils.encryptPassword(user.contrasena)
        val query = "INSERT INTO usuarios (nombre, apellido, correo, contrasena, tipo_usuario, edad) VALUES (?, ?, ?, ?, ?, ?)"
        val params = arrayOf(user.nombre, user.apellido, user.correoElectronico, passwordEncrypted, "0", user.edad.toString())
        mySQLConnection.insertDataAsync(query, *params) { result ->
            if (result) {
                Toast.makeText(context, "Datos insertados correctamente", Toast.LENGTH_SHORT).show()
                clearFields()
            } else {
                Toast.makeText(context, "Error al insertar datos", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun clearFields() {
        etName.text.clear()
        etLastName.text.clear()
        etAge.text.clear()
        etMail.text.clear()
        etPassword.text.clear()
    }
}
