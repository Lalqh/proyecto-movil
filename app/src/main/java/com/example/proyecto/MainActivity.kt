package com.example.proyecto

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.proyecto.ModelClasses.NotificationActionReceiver
import com.example.proyecto.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import java.util.Objects
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    companion object {
        const val NOTIFICATION_PERMISSION_CODE = 1001
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val handler = Handler(Looper.getMainLooper())
    private val notificationMessages = listOf(
        "¡Aprovecha la promoción! 20% de descuento en todos los productos mexicanos.",
        "¡Venta nocturna! Descuentos especiales en productos seleccionados.",
        "¡Nuevo lanzamiento! Prueba nuestros nuevos productos mexicanos.",
        "¡Oferta especial! Compra 2 y lleva 3 en productos seleccionados.",
        "¡Evento especial! Ven y disfruta de nuestras degustaciones gratuitas.",
        "¡Promoción limitada! 15% de descuento en productos de temporada.",
        "¡Descuento exclusivo! 10% de descuento en tu primera compra.",
        "¡Gran venta! Hasta 50% de descuento en productos seleccionados.",
        "¡Evento de fin de semana! Ofertas especiales solo por este fin de semana.",
        "¡Promoción de verano! Descuentos en productos frescos y naturales."
    )
    private val notificationInterval = 10000L // 10 seconds
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController
        mediaPlayer = MediaPlayer.create(this, R.raw.background_music)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_usuarios_añadir,
                R.id.nav_usuarios_consultar,
                R.id.nav_proveedores_consultar,
                R.id.nav_proveedores_añadir,
                R.id.nav_proveedores_ordenar_compra,
                R.id.nav_productos_catalogo,
                R.id.nav_productos_añadir,
                R.id.nav_productos_añadir_oferta,
                R.id.nav_productos_añadir_categoria,
                R.id.nav_productos_consultar_categoria,
                R.id.nav_ventas_añadir,
                R.id.nav_ventas_consultar_historial,
                R.id.nav_gastos_añadir,
                R.id.nav_gastos_consultar,
                R.id.nav_horario
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val isAdmin = intent.getBooleanExtra("is_admin", false)
        adjustMenu(navView, isAdmin)

        createNotificationChannel()
        //scheduleNotifications()
        handler.post(notificationCheckRunnable)
    }

    private fun adjustMenu(navView: NavigationView, isAdmin: Boolean) {
        val menu = navView.menu

        menu.findItem(R.id.nav_usuarios_añadir).isVisible = isAdmin
        menu.findItem(R.id.nav_usuarios_consultar).isVisible = isAdmin
        menu.findItem(R.id.nav_proveedores_consultar).isVisible = isAdmin
        menu.findItem(R.id.nav_proveedores_añadir).isVisible = isAdmin
        menu.findItem(R.id.nav_proveedores_ordenar_compra).isVisible = isAdmin
        menu.findItem(R.id.nav_productos_catalogo).isVisible = isAdmin
        menu.findItem(R.id.nav_productos_añadir).isVisible = isAdmin
        menu.findItem(R.id.nav_productos_añadir_categoria).isVisible = isAdmin
        menu.findItem(R.id.nav_productos_consultar_categoria).isVisible = isAdmin
        menu.findItem(R.id.nav_gastos_añadir).isVisible = isAdmin
        menu.findItem(R.id.nav_gastos_consultar).isVisible = isAdmin
        menu.findItem(R.id.nav_horario)?.isVisible = !isAdmin
        menu.findItem(R.id.ubicacion)?.isVisible = !isAdmin
        menu.findItem(R.id.ubi_proveedores)?.isVisible = isAdmin
    }

    override fun onPause() {
        super.onPause()
        if(::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }
    override fun onResume() {
        super.onResume()
        if(::mediaPlayer.isInitialized && !mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("promo_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scheduleNotifications() {
        val notificationInterval = 60000L

        handler.post(object : Runnable {
            override fun run() {
                showRandomNotification()
                handler.postDelayed(this, notificationInterval)
            }
        })
    }

    private fun showRandomNotification() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val randomMessage = notificationMessages[Random.nextInt(notificationMessages.size)]

            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val notificationBuilder = NotificationCompat.Builder(this, "promo_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Promoción Especial")
                .setContentText(randomMessage)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(this)) {
                notify(Random.nextInt(), notificationBuilder.build())
            }
        } else {
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_CODE
            )
        }
    }

    private val notificationCheckRunnable = object : Runnable {
        override fun run() {
            checkForNewNotifications()
            handler.postDelayed(this, 60000L)
        }
    }

    private fun checkForNewNotifications() {
        val MySQLConnection = MySQLConnection(this)
        val userId = getUserId()
        //Toast.makeText(this, "Revisando notificaciones...", Toast.LENGTH_SHORT).show()
        Log.d("Notificaciones", "Revisando notificaciones para el usuario $userId")
        /*
        Codigo para insertar notificación XD
        MySQLConnection.insertDataAsync(
            "INSERT INTO notificacion (usuario_destino, titulo, descripcion) VALUES (?, ?, ?)",
            *arrayOf(userId.toString(), "Notificación de prueba", "Este es un mensaje de prueba"),
            callback = { success ->
                if (success) {
                    Toast.makeText(this, "Notificación de prueba enviada", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "Error al enviar notificación de prueba", Toast.LENGTH_SHORT).show()
                }
            }
        )*/
        MySQLConnection.selectDataAsync(
            "SELECT id, titulo, descripcion FROM notificacion WHERE usuario_destino = ? and leida = 0",
            userId.toString()
        ) { notifications ->
            notifications.forEach { notification ->
                val title = notification["titulo"].toString()
                val message = notification["descripcion"].toString()
                val id = notification["id"]?.toInt() ?: -1
                Toast.makeText(this, "Nueva notificación: $title", Toast.LENGTH_SHORT).show()
                showNotification(id, title, message)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(id: Int, title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val markAsReadIntent = Intent(this, NotificationActionReceiver::class.java).apply {
            putExtra("notification_id", id)
            putExtra("user_id", getUserId())
        }
        val markAsReadPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, id, markAsReadIntent, PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, "promo_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_mark_as_read, "Leida", markAsReadPendingIntent)

        with(NotificationManagerCompat.from(this)) {
            notify(id, notificationBuilder.build())
        }
    }

    private fun getUserId(): Int {
        val sharedPreferences = this.getSharedPreferences("sesion", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("user_id", -1)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                showRandomNotification()
            } else {
                Toast.makeText(this, "Permiso de notificación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}