package com.example.proyecto

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {

    private var nfcAdapter: NfcAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.spnProduct)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnExit = findViewById<Button>(R.id.btnExit)
        val MySQLConnection = MySQLConnection(this)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC no está disponible en este dispositivo", Toast.LENGTH_SHORT).show()
            Log.v("NFC", "Este dispositivo no tiene soporte NFC")
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Debe rellenar todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                MySQLConnection.selectDataAsync(
                    "SELECT * FROM usuarios WHERE correo = ? AND contrasena = ?",
                    email, password
                ) { user ->
                    if (user.isNotEmpty()) {
                        // Verificar si el usuario es administrador
                        val isAdmin = user[0]["tipo_usuario"] == "1"
                        if (isAdmin) {
                            loginAsAdmin()
                        }
                    } else {
                        Toast.makeText(this, "El correo o la contraseña son incorrectos.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnExit.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, filters, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        try {
            if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
                val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
                tag?.let {
                    loginAsAdmin()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al leer la tarjeta NFC", Toast.LENGTH_SHORT).show()
            Log.e("NFC", "Error al leer la tarjeta NFC", e)
        }
    }

    private fun loginAsAdmin() {
        Toast.makeText(this, "Bienvenido Administrador", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("is_admin", true)
        intent.putExtra("load_home_fragment", true)
        startActivity(intent)
    }

    private fun getUserFromSharedPreferences(email: String, password: String): Usuario? {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val allEntries = sharedPreferences.all

        for ((key, _) in allEntries) {
            if (key.endsWith(".correoElectronico")) {
                val storedEmail = sharedPreferences.getString(key, "")
                val userId = key.removeSuffix(".correoElectronico").removePrefix("user_")
                val storedPassword = sharedPreferences.getString("$userId.contrasena", "")

                if (email == storedEmail && password == storedPassword) {
                    val storedName = sharedPreferences.getString("$userId.nombre", "")
                    val storedLastName = sharedPreferences.getString("$userId.apellido", "")
                    val storedAge = sharedPreferences.getInt("$userId.edad", 0)

                    return Usuario(storedName ?: "", storedLastName ?: "", storedAge, storedEmail ?: "", storedPassword ?: "")
                }
            }
        }
        return null
    }
}