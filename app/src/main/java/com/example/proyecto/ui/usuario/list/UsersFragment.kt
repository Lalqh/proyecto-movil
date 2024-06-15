package com.example.proyecto.ui.usuario.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyecto.R
import com.example.proyecto.UserInfoActivity
import com.example.proyecto.Usuario

class UsersFragment : Fragment() {

    companion object {
        fun newInstance() = UsersFragment()
    }

    private lateinit var etSearchUsers: EditText
    private lateinit var btnBuscar: Button
    private lateinit var user1: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_users, container, false)

        etSearchUsers = rootView.findViewById(R.id.etSearchUsers)
        btnBuscar = rootView.findViewById(R.id.btnBuscar)
        user1 = rootView.findViewById(R.id.user1)

        btnBuscar.setOnClickListener {
            val email = etSearchUsers.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "Debe ingresar un correo electrónico", Toast.LENGTH_SHORT).show()
            } else {
                val user = getUserFromSharedPreferences(email)

                if (user != null) {
                    user1.text = "Nombre: ${user.nombre}\nApellido: ${user.apellido}\nEdad: ${user.edad}\nCorreo: ${user.correoElectronico}"

                    user1.setOnClickListener {
                        val intent = Intent(requireContext(), UserInfoActivity::class.java)
                        intent.putExtra("usuario", user)
                        startActivity(intent)
                    }
                } else {
                    user1.text = "Usuario no encontrado"
                }
            }
        }

        return rootView
    }

    private fun getUserFromSharedPreferences(email: String): Usuario? {
        val sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val allEntries = sharedPreferences.all

        for ((key, _) in allEntries) {
            if (key.endsWith(".correoElectronico")) {
                val storedEmail = sharedPreferences.getString(key, "")

                if (email == storedEmail) {
                    val userId = key.removeSuffix(".correoElectronico").removePrefix("user_")
                    val storedName = sharedPreferences.getString("$userId.nombre", "")
                    val storedLastName = sharedPreferences.getString("$userId.apellido", "")
                    val storedAge = sharedPreferences.getInt("$userId.edad", 0)
                    val storedPassword = sharedPreferences.getString("$userId.contrasena", "")

                    return Usuario(storedName ?: "", storedLastName ?: "", storedAge, storedEmail ?: "", storedPassword ?: "")
                }
            }
        }

        return null
    }
}
