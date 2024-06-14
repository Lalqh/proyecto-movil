package com.example.proyecto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.spnProduct)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnExit = findViewById<Button>(R.id.btnExit)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Debe rellenar todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                if (email == "administrador@gmail.com" && password == "123") {
                    Toast.makeText(this, "Bienvenido $email", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("load_home_fragment", true)
                    startActivity(intent)
                } else {
                    val user = getUserFromSharedPreferences(email, password)

                    if (user != null) {
                        Toast.makeText(this, "Bienvenido ${user.nombre}", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("load_home_fragment", true)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnExit.setOnClickListener {
            finish()
        }
    }

    private fun getUserFromSharedPreferences(email: String, password: String): Usuario? {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val storedEmail = sharedPreferences.getString("correoElectronico", "")
        val storedPassword = sharedPreferences.getString("contrasena", "")

        if (email == storedEmail && password == storedPassword) {
            val storedName = sharedPreferences.getString("nombre", "")
            val storedLastName = sharedPreferences.getString("apellido", "")
            val storedAge = sharedPreferences.getInt("edad", 0)

            return Usuario(storedName ?: "", storedLastName ?: "", storedAge, storedEmail ?: "", storedPassword ?: "")
        }
        return null
    }
}
