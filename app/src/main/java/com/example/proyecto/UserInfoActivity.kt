package com.example.proyecto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast

class UserInfoActivity : AppCompatActivity() {

    private lateinit var tvInfo: TextView
    private lateinit var btnDeleteCategory: Button
    private lateinit var mySQLConnection: MySQLConnection
    private lateinit var loadingOverlay: FrameLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        tvInfo = findViewById(R.id.tvInfo)
        btnDeleteCategory = findViewById(R.id.btnDeleteCategory)
        mySQLConnection = MySQLConnection(this)
        loadingOverlay = findViewById(R.id.loadingOverlay)

        val usuario: Usuario? = intent.getParcelableExtra("usuario")

        if (usuario != null) {
            val userInfo = "Nombre: ${usuario.nombre}\n" +
                    "Apellido: ${usuario.apellido}\n" +
                    "Edad: ${usuario.edad}\n" +
                    "Correo: ${usuario.correoElectronico}\n"

            tvInfo.text = userInfo
        } else {
            Toast.makeText(this, "Error al cargar la información del usuario", Toast.LENGTH_SHORT)
                .show()
            finish()
        }

        btnDeleteCategory.setOnClickListener {
            if (usuario != null) {
                deleteUserFromDatabase(usuario.correoElectronico)
            }
        }
    }

    private fun deleteUserFromDatabase(email: String) {
        loadingOverlay.visibility = FrameLayout.VISIBLE
        val query = "DELETE FROM usuarios WHERE correo = ?"
        mySQLConnection.insertDataAsync(query, email) { result ->
            if (result) {
                Toast.makeText(this, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al eliminar el usuario", Toast.LENGTH_SHORT).show()
            }
            loadingOverlay.visibility = FrameLayout.GONE
        }
    }
}