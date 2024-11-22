package com.example.proyecto

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.Utils.EncryptionUtils

class Login : AppCompatActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private lateinit var loadingOverlay: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.spnProduct)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnExit = findViewById<Button>(R.id.btnExit)
        loadingOverlay = findViewById(R.id.loadingOverlay)
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
                loadingOverlay.visibility = View.VISIBLE
                MySQLConnection.selectDataAsync(
                    "SELECT * FROM usuarios WHERE correo = ?",
                    email
                ) { user ->

                    if (user.isNotEmpty()) {
                        // validar contrasña
                        val pass = user[0]["contrasena"]
                        val passOk = EncryptionUtils.checkPassword(password, pass.toString())

                        if (passOk) {
                            val tipo_usaurio = user[0]["tipo_usuario"]
                            val id = user[0]["id"].toString().toInt()
                            val nombre = user[0]["nombre"]

                            if (tipo_usaurio == "1") {
                                loginAsAdmin()
                                saveUserId(id)
                            } else {
                                Toast.makeText(this, "Bienvenido ${nombre}", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                intent.putExtra("is_admin", false)
                                intent.putExtra("load_home_fragment", true)
                                saveUserId(id)
                                startActivity(intent)
                            }
                        } else {
                            Toast.makeText(this, "La contraseña ingresada no es correcta.", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Toast.makeText(this, "No existe un usuario con este correo.", Toast.LENGTH_SHORT).show()
                    }
                    loadingOverlay.visibility = View.GONE
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

    private fun saveUserId(userId: Int) {
        val sharedPreferences = this.getSharedPreferences("sesion", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("user_id", userId)
        editor.apply()
    }
}