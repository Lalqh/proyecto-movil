package com.example.proyecto

import android.os.Bundle
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class horario_de_atencion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horario_de_atencion)

        val calendarView: CalendarView = findViewById(R.id.calendarView2)

        val calendar = Calendar.getInstance()
        calendar.set(2024, Calendar.JUNE, 1)
        calendarView.date = calendar.timeInMillis

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            if (selectedDate.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                val formattedDate = "$dayOfMonth/${month + 1}/$year"
                val message1 = "Fecha: $formattedDate\nDía disponible: Lunes"
                val message2 = "Horario: 9:00 am a 3:00 pm\nDirección: C. Nueva Escocia 1885"
                Toast.makeText(this, message1, Toast.LENGTH_SHORT).show()
                Toast.makeText(this, message2, Toast.LENGTH_LONG).show()
            } else {
                val formattedDate = "$dayOfMonth/${month + 1}/$year"
                val message = "Fecha: $formattedDate\nDía no disponible"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
