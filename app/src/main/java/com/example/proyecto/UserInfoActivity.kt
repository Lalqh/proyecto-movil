package com.example.proyecto

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class UserInfoActivity : AppCompatActivity() {

    private lateinit var tvInfo: TextView
    private lateinit var btnDeleteCategory: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        tvInfo = findViewById(R.id.tvInfo)
        btnDeleteCategory = findViewById(R.id.btnDeleteCategory)

        val usuario: Usuario? = intent.getParcelableExtra("usuario")

        if (usuario != null) {
            val userInfo = "Nombre: ${usuario.nombre}\n" +
                    "Apellido: ${usuario.apellido}\n" +
                    "Edad: ${usuario.edad}\n" +
                    "Correo: ${usuario.correoElectronico}\n" +
                    "Contraseña: ${usuario.contrasena}"

            tvInfo.text = userInfo
        } else {
            Toast.makeText(this, "Error al cargar la información del usuario", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnDeleteCategory.setOnClickListener {
            if (usuario != null) {
                deleteUserFromSharedPreferences(usuario.correoElectronico)
                Toast.makeText(this, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun deleteUserFromSharedPreferences(email: String) {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val allEntries = sharedPreferences.all

        for ((key, _) in allEntries) {
            if (key.endsWith(".correoElectronico")) {
                val storedEmail = sharedPreferences.getString(key, "")

                if (email == storedEmail) {
                    editor.remove(key)
                    val userId = key.removeSuffix(".correoElectronico").removePrefix("user_")
                    editor.remove("$userId.nombre")
                    editor.remove("$userId.apellido")
                    editor.remove("$userId.edad")
                    editor.remove("$userId.contrasena")
                }
            }
        }
        editor.apply()
    }
}
