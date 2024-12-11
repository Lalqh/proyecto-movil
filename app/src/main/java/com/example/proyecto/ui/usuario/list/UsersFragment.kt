package com.example.proyecto.ui.usuario.list

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyecto.MySQLConnection
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
    private lateinit var mySQLConnection: MySQLConnection
    private lateinit var loadingOverlay: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_users, container, false)

        etSearchUsers = rootView.findViewById(R.id.etSearchUsers)
        btnBuscar = rootView.findViewById(R.id.btnBuscar)
        user1 = rootView.findViewById(R.id.user1)
        loadingOverlay = rootView.findViewById(R.id.loadingOverlay)
        mySQLConnection = MySQLConnection(requireContext())

        btnBuscar.setOnClickListener {
            val email = etSearchUsers.text.toString()
            searchUserByEmail(email)
        }

        // Load all users initially
        searchUserByEmail("")

        return rootView
    }

    @SuppressLint("SetTextI18n")
    private fun searchUserByEmail(email: String) {
        loadingOverlay.visibility = FrameLayout.VISIBLE
        if (email.isEmpty()) {
            loadAllUsers()
        } else {
            mySQLConnection.selectDataAsync(
                "SELECT * FROM usuarios WHERE correo = ?",
                email
            ) { users ->
                if (users.isNotEmpty()) {
                    val user = users[0]
                    val usuario = Usuario(
                        user["nombre"] ?: "",
                        user["apellido"] ?: "",
                        user["edad"]?.toInt() ?: 0,
                        user["correo"] ?: "",
                        "",
                        user["img"] ?: ""
                    )

                    user1.text = "Nombre: ${usuario.nombre}\nApellido: ${usuario.apellido}\nEdad:" +
                            " ${usuario.edad}\nCorreo: ${usuario.correoElectronico}"

                    user1.setOnClickListener {
                        val intent = Intent(requireContext(), UserInfoActivity::class.java)
                        intent.putExtra("usuario", usuario)
                        startActivity(intent)
                    }
                } else {
                    user1.text = "Usuario no encontrado"
                }
                loadingOverlay.visibility = FrameLayout.GONE
            }
        }
    }

    private fun loadAllUsers() {
        mySQLConnection.selectDataAsync(
            "SELECT * FROM usuarios"
        ) { users ->
            if (users.isNotEmpty()) {
                val userInfo = StringBuilder()
                for (user in users) {
                    val usuario = Usuario(
                        user["nombre"] ?: "",
                        user["apellido"] ?: "",
                        user["edad"]?.toInt() ?: 0,
                        user["correo"] ?: "",
                        "",
                        user["img"] ?: ""
                    )
                    userInfo.append("Nombre: ${usuario.nombre}\nApellido: ${usuario.apellido}\nEdad:" +
                            " ${usuario.edad}\nCorreo: ${usuario.correoElectronico}\n\n")
                }
                user1.text = userInfo.toString()
            } else {
                user1.text = "No hay usuarios disponibles"
            }
            loadingOverlay.visibility = FrameLayout.GONE
        }
    }
}