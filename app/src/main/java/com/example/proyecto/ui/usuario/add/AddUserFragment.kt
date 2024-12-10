package com.example.proyecto.ui.usuario.add

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.proyecto.MySQLConnection
import com.example.proyecto.R
import com.example.proyecto.Usuario
import com.example.proyecto.Utils.EncryptionUtils
import com.example.proyecto.Utils.fetchData
import java.io.ByteArrayOutputStream

class AddUserFragment : Fragment() {

    private lateinit var etName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etAge: EditText
    private lateinit var etMail: EditText
    private lateinit var etPassword: EditText
    private lateinit var userImage: ImageView
    private lateinit var mySQLConnection: MySQLConnection
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var btnImagen: Button
    private var userImageUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_add_user, container, false)

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val selectedImageUri = result.data!!.data
                if (selectedImageUri != null) {
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedImageUri)
                        userImage.setImageBitmap(bitmap)
                        val encodedImage = encodeImageToBase64(bitmap)
                        fetchData(requireContext(), encodedImage) { imageUrl ->
                            userImageUrl = imageUrl
                        }
                    } catch (e: Exception) {
                        Log.e("UserInfoActivity", "Error processing selected image", e)
                    }
                }
            }
        }

        etName = view.findViewById(R.id.etName)
        etLastName = view.findViewById(R.id.etLastName)
        etAge = view.findViewById(R.id.etAge)
        etMail = view.findViewById(R.id.etMail)
        etPassword = view.findViewById(R.id.etPassword)
        userImage = view.findViewById(R.id.ivLogo)
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)
        btnImagen = view.findViewById(R.id.btnImagen)
        mySQLConnection = MySQLConnection(requireContext())

        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val lastName = etLastName.text.toString()
            val ageStr = etAge.text.toString()
            val email = etMail.text.toString()
            val password = etPassword.text.toString()

            if (name.isEmpty() || lastName.isEmpty() || ageStr.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Debe llenar todos los campos", Toast.LENGTH_SHORT).show()
            } else if (userImageUrl == null) {
                Toast.makeText(requireContext(), "Debe seleccionar una imagen", Toast.LENGTH_SHORT).show()
            } else {
                val age = ageStr.toInt()
                val user = Usuario(name, lastName, age, email, password, userImageUrl!!)
                saveUser(user)
            }
        }

        btnImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        return view
    }

    private fun saveUser(user: Usuario) {
        val passwordEncrypted = EncryptionUtils.encryptPassword(user.contrasena)
        val query = "INSERT INTO usuarios (nombre, apellido, correo, contrasena, tipo_usuario, edad, img) VALUES (?, ?, ?, ?, ?, ?, ?)"
        val params = arrayOf(user.nombre, user.apellido, user.correoElectronico, passwordEncrypted, "0", user.edad.toString(), user.img)
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
        userImage.setImageResource(0)
        userImageUrl = null
    }

    private fun encodeImageToBase64(image: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}