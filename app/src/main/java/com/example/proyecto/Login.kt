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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class Login : AppCompatActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private lateinit var loadingOverlay: FrameLayout
    private val validNfcTagId = "04314B0A276680"
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.spnProduct)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnLoginWithGoogle = findViewById<Button>(R.id.btnLoginWithGoogle)
        val btnExit = findViewById<Button>(R.id.btnExit)
        loadingOverlay = findViewById(R.id.loadingOverlay)
        val MySQLConnection = MySQLConnection(this)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC no está disponible en este dispositivo", Toast.LENGTH_SHORT).show()
            Log.v("NFC", "Este dispositivo no tiene soporte NFC")
        }

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Sign out to ensure account picker is shown every time
        googleSignInClient.signOut().addOnCompleteListener(this) {
            // Update UI accordingly if needed
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Debe rellenar todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                loadingOverlay.visibility = View.VISIBLE
                btnExit.isEnabled = false
                btnLogin.isEnabled = false
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
                    btnLogin.isEnabled = true
                    btnExit.isEnabled = true
                    loadingOverlay.visibility = View.GONE
                }
            }
        }

        btnLoginWithGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        btnExit.setOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            val email = account?.email
            if (email != null) {
                // Check if the user exists in the database
                val MySQLConnection = MySQLConnection(this)
                MySQLConnection.selectDataAsync(
                    "SELECT * FROM usuarios WHERE correo = ?",
                    email
                ) { user ->
                    if (user.isNotEmpty()) {
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
                        Toast.makeText(this, "No existe un usuario con este correo.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: ApiException) {
            Log.w("Google Sign-In", "signInResult:failed code=" + e.statusCode)
            Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
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
                    val tagId = it.id.joinToString("") { byte -> "%02X".format(byte) }
                    if (tagId == validNfcTagId) {
                        loginAsAdmin()

                    } else {
                        Toast.makeText(this, "Tarjeta NFC no válida", Toast.LENGTH_SHORT).show()
                    }
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
        intent.putExtra("user_id", 1)
        startActivity(intent)
    }

    private fun saveUserId(userId: Int) {
        val sharedPreferences = this.getSharedPreferences("sesion", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("user_id", userId)
        editor.apply()
    }
}