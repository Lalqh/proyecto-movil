package com.example.proyecto

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.proyecto.Utils.fetchData
import java.io.ByteArrayOutputStream

class UserInfoActivity : AppCompatActivity() {

    private lateinit var tvInfo: TextView
    private lateinit var btnDeleteCategory: Button
    private lateinit var btnEditImage: Button
    private lateinit var mySQLConnection: MySQLConnection
    private lateinit var loadingOverlay: FrameLayout
    private lateinit var imageView5: ImageView
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private var usuario: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val selectedImageUri = result.data!!.data
                if (selectedImageUri != null) {
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)
                        imageView5.setImageBitmap(bitmap)
                        val encodedImage = encodeImageToBase64(bitmap)

                        loadingOverlay.visibility = FrameLayout.VISIBLE

                        fetchData(this, encodedImage) { imageUrl ->
                            onFetchDataComplete(imageUrl, usuario?.correoElectronico)
                        }
                    } catch (e: Exception) {
                        Log.e("UserInfoActivity", "Error processing selected image", e)
                        Toast.makeText(this, "Error processing selected image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        tvInfo = findViewById(R.id.tvInfo)
        btnDeleteCategory = findViewById(R.id.btnDeleteCategory)
        mySQLConnection = MySQLConnection(this)
        loadingOverlay = findViewById(R.id.loadingOverlay)
        btnEditImage = findViewById(R.id.btnEditPhoto)
        imageView5 = findViewById(R.id.imageView5)

        usuario = intent.getParcelableExtra("usuario")

        if (usuario != null) {
            val userInfo = "Nombre: ${usuario!!.nombre}\n" +
                    "Apellido: ${usuario!!.apellido}\n" +
                    "Edad: ${usuario!!.edad}\n" +
                    "Correo: ${usuario!!.correoElectronico}\n"

            tvInfo.text = userInfo
            val imageUrl = usuario!!.img

            Glide.with(this)
                .load(imageUrl.toUri())
                .into(imageView5)

        } else {
            Toast.makeText(this, "Error al cargar la información del usuario", Toast.LENGTH_SHORT)
                .show()
            finish()
        }

        btnDeleteCategory.setOnClickListener {
            if (usuario != null) {
                deleteUserFromDatabase(usuario!!.correoElectronico)
            }
        }

        btnEditImage.setOnClickListener {
            editImage()
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

    private fun editImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun encodeImageToBase64(image: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun onFetchDataComplete(imageUrl: String?, correo: String?) {
        loadingOverlay.visibility = FrameLayout.GONE

        if (imageUrl != null && correo != null) {
            mySQLConnection.updateDataAsync(
                "UPDATE usuarios SET img = ? WHERE correo = ?",
                imageUrl,
                correo
            ) { result ->
                if (result) {
                    Toast.makeText(this, "Imagen guardada correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
        }
    }
}