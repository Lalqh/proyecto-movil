package com.example.proyecto.ui.horario

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import com.example.proyecto.R
import java.util.Calendar

class HorarioFragment : Fragment() {

    private lateinit var viewModel: HorarioViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_horario, container, false)

        val calendarView: CalendarView = view.findViewById(R.id.cvDate)
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
                Toast.makeText(view.context, "$message1", Toast.LENGTH_LONG).show()
                Toast.makeText(view.context, "$message2", Toast.LENGTH_LONG).show()
            } else {
                val formattedDate = "$dayOfMonth/${month + 1}/$year"
                val message = "Fecha: $formattedDate\nDía no disponible"
                Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HorarioViewModel::class.java)
        // TODO: Use the ViewModel
    }
}